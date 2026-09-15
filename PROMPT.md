Я разрабатываю Android-приложение на Kotlin + Jetpack Compose + Firebase Authentication + Firestore.

В проекте я недавно делал рефакторинг: хотел придерживаться подхода, при котором один `Composable Screen` использует один основной `ViewModel`. После рефакторинга `ProfileSettingScreen` появились проблемы с авторизацией.

## Главная проблема

Есть два связанных бага:

### 1. Выход из аккаунта

Когда пользователь находится в `ProfileSettingScreen` и нажимает:

**Profile Settings → Sign Out → Confirm**

Firebase действительно выполняет `auth.signOut()`.

Это подтверждается Logcat:

```text
FirebaseAuth: Notifying id token listeners about a sign-out event.
FirebaseAuth: Notifying auth state listeners about a sign-out event.
```

То есть Firebase считает пользователя вышедшим.

Но UI приложения не переходит на `LoginScreen`.

Вместо этого приложение остаётся/возвращается на `HomeScreen`.

В логах после выхода появляется что-то вроде:

```text
AuthDebug: Текущее состояние: AuthState$Authenticated
AuthDebug: Переход на Home
```

То есть Firebase уже считает пользователя неавторизованным, но другой механизм приложения всё ещё считает его `Authenticated`.

### 2. После этого невозможно удалить аккаунт

Из-за того, что UI не перешёл на LoginScreen и пользователь визуально остаётся авторизованным, кажется, что аккаунт всё ещё активен.

Но Firebase уже сделал `signOut()`.

Поэтому при попытке удалить аккаунт вызывается:

```kotlin
auth.currentUser
```

и он оказывается `null`.

В Logcat:

```text
DELETE_ACC_ERROR: user don`t found
```

Ошибка возникает примерно здесь:

```kotlin
val user = auth.currentUser
    ?: throw IllegalStateException("user don`t found")
```

Таким образом, проблема удаления аккаунта является следствием некорректной синхронизации состояния авторизации и UI, а не самостоятельной проблемой удаления.

---

# Важный момент

Сейчас в проекте, судя по коду, существует ДВА механизма состояния авторизации:

### Механизм №1

`AuthViewModel`

Он содержит что-то вроде:

```kotlin
private val _authState = MutableLiveData<AuthState>()
val authState: LiveData<AuthState> = _authState
```

и используется для определения:

```text
Authenticated → Home
Unauthenticated → Login
```

### Механизм №2

`UserRepository`

В нём уже существует:

```kotlin
val authStateFlow: Flow<Boolean>
```

который основан на Firebase:

```kotlin
FirebaseAuth.AuthStateListener
```

То есть он должен отражать реальное состояние:

```text
FirebaseAuth.currentUser != null
```

Проблема, вероятно, заключается в том, что `ProfileSettingViewModel` вызывает:

```kotlin
userRepository.signOut()
```

который напрямую вызывает:

```kotlin
FirebaseAuth.signOut()
```

но состояние, которое использует глобальная навигация (`AuthViewModel.authState`), корректно не синхронизируется.

В результате получается:

```text
Firebase:
Unauthenticated

        ↓

UserRepository.authStateFlow:
Unauthenticated

        ↓

AuthViewModel:
Authenticated   ← ПРОБЛЕМА

        ↓

Navigation:
HomeScreen
```

Нужно найти точную причину этой рассинхронизации и исправить архитектуру.

---

# Как приложение ДОЛЖНО работать

## При запуске приложения

Firebase должен быть единственным источником истины относительно факта авторизации.

Если:

```kotlin
FirebaseAuth.currentUser != null
```

то приложение должно проверить профиль пользователя.

Если профиль существует:

```text
Splash/Loading
      ↓
HomeScreen
```

Если пользователь авторизован, но профиль ещё не создан/не заполнен:

```text
Splash/Loading
      ↓
ProfileSetupScreen
```

Если:

```kotlin
FirebaseAuth.currentUser == null
```

то:

```text
Splash/Loading
      ↓
LoginScreen
```

---

# При обычном входе

Пользователь вводит данные или использует Google Sign-In.

После успешной авторизации:

```text
LoginScreen
     ↓
FirebaseAuth
     ↓
Auth state changes
     ↓
AuthViewModel / global auth state
     ↓
проверка профиля
     ↓
HomeScreen или ProfileSetupScreen
```

---

# При выходе из аккаунта

Пользователь нажимает:

```text
Profile Settings
      ↓
Sign Out
      ↓
Confirmation Dialog
      ↓
Confirm
```

После подтверждения:

```kotlin
FirebaseAuth.signOut()
```

должен изменить реальное состояние Firebase.

После этого `authStateFlow` должен эмитить:

```kotlin
false
```

Глобальное состояние авторизации должно стать:

```text
Unauthenticated
```

И приложение должно автоматически перейти:

```text
ProfileSettingScreen
        ↓
LoginScreen
```

При этом из Back Stack не должно остаться возможности нажать Back и вернуться в защищённые экраны приложения.

То есть желательно:

```kotlin
navController.navigate(Login) {
    popUpTo(...)
    launchSingleTop = true
}
```

---

# При удалении аккаунта

Удаление аккаунта — отдельная операция.

Пользователь должен оставаться авторизованным до момента выполнения удаления.

Правильная последовательность:

```text
Profile Settings
      ↓
Delete Account
      ↓
Confirmation
      ↓
Firebase Auth user существует
      ↓
Удаление документа users/{uid} из Firestore
      ↓
Удаление Firebase Auth account
      ↓
Firebase Auth state становится unauthenticated
      ↓
Navigation реагирует
      ↓
LoginScreen
```

Важно:

**Нельзя делать `signOut()` перед `deleteAccount()`.**

Если пользователь сначала вышел:

```kotlin
auth.signOut()
```

то:

```kotlin
auth.currentUser == null
```

и:

```kotlin
user.delete()
```

уже невозможно выполнить.

---

# Что нужно проверить

Пожалуйста, НЕ начинай сразу переписывать код.

Сначала проанализируй архитектуру и найди источник рассинхронизации.

Мне нужно проверить следующие файлы.

## 1. `AuthViewModel.kt`

Особенно:

* `_authState`
* `authState`
* `init`
* Firebase `AuthStateListener`
* `signOut`
* login
* Google Sign-In
* signup
* `verifyUserProfile`
* все места, где выполняется:

```kotlin
_authState.value = ...
```

Нужно понять, кто является источником истины для `AuthState`.

---

## 2. `ProfileSettingViewModel.kt`

Особенно:

```kotlin
signOut()
deleteAccount()
```

Проверить, не вызывает ли он авторизацию/выход независимо от глобального `AuthViewModel`.

Также проверить, не создаётся ли несколько экземпляров ViewModel.

---

## 3. `ProfileSettingScreen.kt`

Проверить:

* как создаётся `ProfileSettingViewModel`
* как вызывается `signOut()`
* как вызывается `deleteAccount()`
* `LaunchedEffect`
* navigation
* обработку `isSignedOut`
* обработку `isDeletedAccount`
* состояние диалогов
* нет ли прямого обращения к `FirebaseAuth`
* нет ли второго `AuthViewModel`

Особенно важно проверить, не вызывается ли одновременно:

```text
ProfileSettingViewModel.signOut()
```

и

```text
AuthViewModel.signOut()
```

---

## 4. `UserRepository.kt`

Проверить интерфейс:

```kotlin
val currentUserId: String?
val isGoogleAuth: Boolean
val authStateFlow: Flow<Boolean>
suspend fun signOut(): Result<Unit>
suspend fun deleteAccount(): Result<Unit>
```

Нужно понять, правильно ли разделены:

* состояние авторизации
* sign out
* delete account.

---

## 5. `FirebaseUserRepository.kt`

Это один из самых важных файлов.

Проверить:

```kotlin
authStateFlow
signOut()
deleteAccount()
currentUserId
```

Особенно реализацию:

```kotlin
FirebaseAuth.AuthStateListener
```

и:

```kotlin
auth.currentUser
```

Нужно убедиться, что `authStateFlow` действительно эмитит `false` после:

```kotlin
auth.signOut()
```

и `true` после успешной авторизации.

Также проверить удаление аккаунта.

Правильная логика должна быть примерно:

```kotlin
val user = auth.currentUser
    ?: throw IllegalStateException("User not found")

db.collection("users")
    .document(user.uid)
    .delete()
    .await()

user.delete().await()
```

При этом не нужно выполнять `signOut()` перед `delete()`.

---

## 6. Navigation / `NavGraph.kt`

Проверить:

* где создаётся `AuthViewModel`
* как определяется стартовый экран
* кто реагирует на `AuthState`
* где выполняется:

```kotlin
navigate(Home)
```

и:

```kotlin
navigate(Login)
```

Особенно найти код, который пишет в Logcat:

```text
AuthDebug
```

и:

```text
Переход на Home
```

Именно этот код, скорее всего, продолжает считать пользователя `Authenticated`.

---

## 7. `MainActivity.kt`

Если там находится root `NavHost`, `AuthViewModel` или логика определения стартового экрана — обязательно проверить его.

Нужно определить, где именно находится глобальный auth state.

---

## 8. Все места использования `AuthViewModel`

Поищи по проекту:

```text
AuthViewModel
```

и:

```text
authState
```

и:

```text
AuthState.Authenticated
```

и:

```text
AuthState.Unauthenticated
```

Также найти все:

```text
FirebaseAuth.getInstance()
```

и:

```text
Firebase.auth
```

Мне важно понять, не создаются ли несколько независимых экземпляров/источников состояния.

---

# Главное архитектурное требование

Не нужно просто "заставить навигацию работать".

Нужно сделать так, чтобы в приложении был **один источник истины для состояния авторизации**.

Предпочтительная схема:

```text
             FirebaseAuth
                  │
                  ▼
          AuthStateListener
                  │
                  ▼
        UserRepository.authStateFlow
                  │
                  ▼
            AuthViewModel
                  │
                  ▼
        Global Auth State
                  │
          ┌───────┴────────┐
          ▼                ▼
       Login             Home
```

`ProfileSettingViewModel` при этом не должен самостоятельно решать, куда навигировать.

Он должен только выполнить:

```kotlin
userRepository.signOut()
```

После чего Firebase изменяет auth state.

Глобальная система авторизации замечает изменение и сама переводит приложение на Login.

То же самое должно происходить после удаления аккаунта.

---

# Важное требование к исправлению

Не делай огромный рефакторинг всего проекта.

Сначала:

1. Найди точную причину бага.
2. Покажи цепочку событий, из-за которой возникает:

```text
Firebase = unauthenticated
AuthViewModel = authenticated
UI = Home
```

3. Укажи конкретные файлы и строки/методы, которые нужно изменить.
4. Объясни, почему именно они вызывают проблему.
5. Предложи минимальный исправляющий патч.
6. Не ломай существующую авторизацию, Google Sign-In, регистрацию и Profile Setup.
7. Не добавляй дублирующие механизмы auth state.
8. Не используй навигацию внутри Repository или ViewModel.
9. UI должен реагировать на состояние, а не ViewModel напрямую управлять `NavController`.

После этого предоставь готовый код изменённых методов/файлов.

Главная цель:

```text
FirebaseAuth
     ↓
единый auth state
     ↓
Navigation
```

И после `signOut()` приложение гарантированно должно оказаться на `LoginScreen`.

После `deleteAccount()` пользователь также должен оказаться на `LoginScreen`, а его Firestore-профиль и Firebase Auth аккаунт должны быть удалены.

---

# Что было сделано (итог, 2026-09-14)

Разобрались с багом и попутно нашли ещё несколько. Ниже — короткий отчёт: что было не так, что сделали, в каких файлах.

## 1. Баг с рассинхронизацией auth state (главный)

**Было:** `AuthViewModel._authState` обновлялся только вручную, из своих же методов (`login`, `signup`, `signout` и т.д.). Он не слушал реальные события Firebase. Когда `ProfileSettingViewModel` делал sign out через `UserRepository` (в обход `AuthViewModel`), Firebase реально разлогинивал, а `AuthViewModel` об этом не узнавал — оставался `Authenticated`. `LoginScreen` видел старое значение и сам перебрасывал обратно на `Home`. После этого `deleteAccount()` падал с `user don't found`, потому что `auth.currentUser` уже был `null`.

**Сделали:** в `AuthViewModel.kt` заменили одноразовую проверку на постоянный `FirebaseAuth.AuthStateListener` — теперь `_authState` обновляется при любом реальном изменении, независимо от того, кто вызвал `signOut()`. Убрали мёртвый код (`deleteAccount(onSuccess, onError)`, который никто не вызывал) и повторные вызовы `verifyUserProfile()` после логина (стали не нужны).

Файлы: `viewmodel/AuthViewModel.kt`, `screens/HomeScreen.kt` (навигация на Login при сайнауте с Home тоже не чистила back stack — починили).

## 2. Не было стартового экрана-проверки

**Было:** приложение всегда стартовало с `LoginScreen`, даже если пользователь уже был залогинен — на долю секунды мигал логин, потом перебрасывало дальше.

**Сделали:** добавили `SplashScreen.kt` — экран-спиннер, который смотрит на `authState` и сразу ведёт на `Home` / `ProfileSetup` / `Login`, с очисткой back stack.

Файлы: новый `screens/SplashScreen.kt`, `navigation/ScreenNav.kt` (добавили роут), `navigation/NavHost.kt` (стартовый экран теперь `Splash`).

## 3. В Profile Settings не было данных пользователя

**Было:** в `ProfileSettingViewModel.loadUserProfile()` результат запроса профиля (`UserProfile`) получали, но никуда не записывали — только `isLoading = false`. Поэтому `ProfileInfoBlock` (аватар, имя, юзернейм) всегда был пустой.

**Сделали:** добавили `userProfile = profile` в `_uiState.copy(...)`. Нашли баг, сравнив с `ProfileSetupViewModel.fetchUserProfile()`, где это было сделано правильно.

Файл: `viewmodel/profile_view_model/ProfileSettingViewModel.kt`.

## 4. Клавиатура: не было тап-дисмисса на части экранов, дублирование кода

**Было:** `Login`/`Signup`/`ForgotPassword` уже умели закрывать клавиатуру тапом вне поля, а `ProfileSetup` — нет. Каждый экран реализовывал это у себя копипастой.

**Сделали:** сначала добавили тап-дисмисс в `ProfileSetupScreen.kt`, потом вынесли этот механизм один раз на уровень `MainScaffold.kt` (там проходит контент всех экранов) и убрали копии из 5 файлов. `imePadding()` (отступ под клавиатуру) пришлось оставить/вернуть на уровне каждого экрана рядом с `verticalScroll` — если вынести его туда же, наверх, при открытии клавиатуры появлялась полоска фона (баг с рассинхронизацией анимации layout).

Файлы: `screens/MainScaffold.kt` (тап-дисмисс глобально), `LoginScreen.kt`, `SignupScreen.kt`, `ForgotPasswordScreen.kt`, `ProfileEditScreen.kt`, `ProfileSetupScreen.kt` (у всех — только `imePadding()` рядом со скроллом, без своего тап-дисмисса).

## Коммиты

Разбили на 5 отдельных коммитов в ветке `test` (плюс 3 файла — ваши более ранние незакоммиченные правки, тоже вынесли отдельным коммитом):

```
bd42004 FIX: AuthViewModel now listens to real FirebaseAuth state so SignOut/DeleteAccount from ProfileSettingScreen no longer leaves user stuck on Home
f37854c FEAT: add SplashScreen to route Home/ProfileSetup/Login based on real auth state on app start
223e833 FIX: ProfileSettingViewModel.loadUserProfile() not storing fetched profile, ProfileInfoBlock showed no user data
e9ed178 refactor: move keyboard tap-to-dismiss to MainScaffold globally, keep imePadding per-screen next to verticalScroll to avoid gap above keyboard
da046fb ref: simplify FirebaseUserRepository deleteAccount/signOut and wire ProfileSettingScreen sign-out navigation
```

PR-заголовок: `Fix auth state desync (sign out/delete account) + profile data + keyboard UX`.
