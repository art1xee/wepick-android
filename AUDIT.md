# wepick-android — Project Audit

Date: 2026-07-28

## Overall rating: 5.5/10 — solid early-stage hobby project, but with real bugs and a live leaked API key

It's a working Compose app with a reasonable MVVM shape (ViewModels + Compose state), consistent patterns in the game/matching flow, and up-to-date dependencies. It loses points for a genuinely leaked secret, an inverted validation bug, silent error handling, and zero real test coverage.

## 1. Project structure & architecture

Loosely MVVM: four ViewModels (`AuthViewModel`, `MainViewModel`, `ContentViewModel`, `PlayerViewModel` in `app/src/main/java/com/example/wepick/viewmodel/`) hold state via `LiveData` (Auth) or Compose `mutableStateOf`/`mutableStateListOf` (the others), with ~19 screen composables under `app/src/main/java/com/example/wepick/screens/`. No repository layer — `ContentViewModel` calls `RetrofitClient.instanceTmdb`/`instanceJikan` directly, and `AuthViewModel` calls `FirebaseAuth.getInstance()` directly. No DI framework (no Hilt/Koin); ViewModels are threaded by hand through every screen/nav destination (`navigation/NavHost.kt`), which means no testing seams (see §4).

There are two visible "eras" of code:
- The gameplay flow (`MainScreen`, `SelectionScreen`, `PartnerScreen`, `FriendNameScreen`, `GenresScreen`, `SummaryScreen`, `MatchScreen`, `OverlayMenu`) is consistent: same parameter ordering `(navController, viewModel, modifier, playerVM[, contentVM])`, string resources used almost everywhere, business logic correctly pushed into ViewModels rather than composables.
- The newer auth flow (`LoginScreen`, `SignupScreen`, `ForgotPasswordScreen`, `HomeScreen`, `SettingScreen`) doesn't follow that convention (different param signatures) and is rougher — see §3. `screens/SettingScreen.kt` is a literal empty composable body, wired into nav and the bottom bar but does nothing.

Shared form UI (`FormTextFields`, `EmailTextField`, `PasswordTextField`, `LoginDivider`) is defined inside `screens/LoginScreen.kt:256-461` and consumed from `SignupScreen.kt`/`ForgotPasswordScreen.kt` via same-package visibility, rather than living in `ui/components/` alongside `RetroButtons.kt` — an organizational inconsistency.

## 2. Security concerns

- **Confirmed**: `app/build.gradle.kts:36` hardcodes a fallback TMDB key (`f0d0bc12560c00cff720536f062f5463`) directly in the build script. Since the current `local.properties` has no `TMDB_API_KEY` entry, this fallback is the key actually compiled into every build today. `git log -p -- app/build.gradle.kts` shows this same literal key checked in across multiple historical commits — it's a real key with a live history trail, not just a placeholder.
- `data/network/RetrofitClient.kt:16-18` and `:28-30` set `HttpLoggingInterceptor.Level.BODY` unconditionally (no `BuildConfig.DEBUG` gate), so full request/response bodies — including the `api_key` query parameter — get written to Logcat in release builds too.
- `local.properties` is tracked in git (committed in `5e738f9`) despite the file's own header comment saying it must not be checked in, and despite `.gitignore` listing it. Today it only contains `sdk.dir`, but the pattern is wrong for when a real `TMDB_API_KEY` is added there. Recommend `git rm --cached local.properties`.
- `app/google-services.json` is tracked, but that's normal/expected for Firebase Android apps — not a defect.
- `app/build.gradle.kts:45` — `isMinifyEnabled = false` in `release`. Combined with the hardcoded key baked into `BuildConfig`, there's no obfuscation at all for a public release build. `proguard-rules.pro` is the untouched stock template.
- `AndroidManifest.xml`: only `INTERNET` permission, `MainActivity` is `exported=true` (correct/required as the launcher activity), no other components declared, no cleartext traffic issues.
- **Real validation bug**: `screens/SignupScreen.kt:76-77`:
  ```kotlin
  val isPasswordValid =
      password.isNotEmpty() && password == confirmPassword && password.length < 6
  ```
  This requires the password to be *shorter* than 6 characters to be accepted — backwards from the presumably-intended "at least 6 characters" rule. A single-character password passes; a normal 8-character password is rejected.
- `viewmodel/AuthViewModel.kt:111` hardcodes the Google OAuth `serverClientId` inline — not sensitive, but should live in a resource/BuildConfig for maintainability.
- Low priority: `AndroidManifest.xml:7` `allowBackup="true"` with the stock, empty `xml/backup_rules.xml` — not urgent given no sensitive local storage today (just a language preference in `util/LocalSettings.kt`), but worth revisiting once real user data accumulates.

## 3. Code quality

- **Error handling is silent-fail by design.** `viewmodel/ContentViewModel.kt:75,117,151,160,194` catch network exceptions and only `println(...)` them — no `Log.e`, no user-facing error state. On failure, the UI just keeps showing stale/empty results with no indication anything went wrong.
- **Dead / misleading code**: `ContentViewModel.kt:40,225` call `loadContent(ContentType.Anime, BuildConfig.JIKAN_BASE_URL)`, passing the Jikan *base URL* as the `apiKey` parameter, but the `Anime` branch never uses it — copy-paste artifact. `getGenreIdForApi` (`ContentViewModel.kt:207-216`) and `Paging.JIKAN_MAX_PAGES` (`util/Constants.kt:27`) are unused.
- **Hardcoded strings bypassing existing resources**: `screens/SignupScreen.kt:200-201,221,255,261` hardcode `"Confirm password"`, `"repeat password"`, `"Create"`, `"Already have an account, "`, `"Login"` even though `res/values/string.xml` already defines matching resources — `LoginScreen.kt`, one file over, is fully resource-driven.
- **Resource misuse bug**: `screens/MainScreen.kt:107` reuses `R.string.login_email_hint` ("Enter email") as the label for what is actually the user's *name* field — a real, user-visible copy bug.
- Leftover comment: `screens/HomeScreen.kt:86` — refactor residue that should be deleted.
- Unused/dead imports in `screens/MainScaffold.kt:33,35,37` (`NavHost`, `composable`), likely copy-pasted from `navigation/NavHost.kt`.
- State-management style is inconsistent: three ViewModels use idiomatic Compose `State`, but `AuthViewModel` alone uses `LiveData` + `observeAsState()`.
- Comments/UI copy mix Russian/Ukrainian/English (e.g. `navigation/ScreenNav.kt:4,8`) — fine for solo work, worth flagging for future collaborators.

## 4. Testing

Effectively zero real coverage. `app/src/test/java/com/example/wepick/ExampleUnitTest.kt` and `app/src/androidTest/java/com/example/wepick/ExampleInstrumentedTest.kt` are the unmodified Android Studio template tests. No ViewModel logic, no Composable UI tests, no network/mapping tests exist. Compounded by the lack of DI/repository seams — `AuthViewModel` and `ContentViewModel` hold hard dependencies on `FirebaseAuth.getInstance()` and the `RetrofitClient` singleton, so they can't easily be unit-tested without a refactor first.

## 5. Dependencies

- **Confirmed duplicate declarations** in `app/build.gradle.kts`: `androidx.credentials`, `androidx.credentials.play.services.auth`, and `googleid` are each `implementation(...)`'d twice — lines 95-97 and again 117-119, verbatim.
- `gradle/libs.versions.toml` has two catalog entries resolving to the *same* artifact at *different* pinned versions: `foundation = "1.10.1"` vs `foundationVersion = "1.10.4"`, both `androidx.compose.foundation:foundation`. Both aliases are applied in `build.gradle.kts:113-114`.
- Similarly, `androidx-runtime-livedata` (no pinned version) and `androidx-compose-runtime-livedata` (pinned `1.11.4`) are two aliases for the same artifact, and both are used (`build.gradle.kts:102` and `:116`).
- `coil-compose` is declared as a raw literal `implementation("io.coil-kt:coil-compose:2.4.0")` instead of via the catalog, even though the catalog defines `coilCompose = "2.7.0"` unused — version drift. `material-icons-extended:1.5.4` is likewise a raw literal bypassing the catalog.
- `androidx.benchmark.traceprocessor` (`build.gradle.kts:112`) is an unusual dependency for a simple Compose app — a macrobenchmark trace-analysis library, likely accidental.
- Pinned versions themselves (Kotlin 2.3.0, AGP 8.13.2, Compose BOM 2026.01.00, Firebase BOM 34.15.0) are current, not stale.

## 6. AndroidManifest.xml

Minimal and largely fine: single `INTERNET` permission, one exported launcher `MainActivity` (correct/required), no other manifest components, `allowBackup=true` with stock/empty backup rules (low risk given no sensitive local storage today). Nothing alarming.

## Top priorities to fix first

1. **Rotate the TMDB API key and stop hardcoding it.** `app/build.gradle.kts:36` bakes a real key into the build script, present across multiple prior commits in git history. Rotate the key at TMDB, remove the fallback literal, fail the build if `local.properties`/CI secret isn't set, and `git rm --cached local.properties`.
2. **Fix the inverted password-length check** in `screens/SignupScreen.kt:76-77` (`password.length < 6` should be `>= 6`) — it currently blocks valid passwords and accepts trivially weak ones.
3. **Disable verbose OkHttp body logging in release builds** (`data/network/RetrofitClient.kt:16-18,28-30`) — gate `HttpLoggingInterceptor.Level.BODY` behind `BuildConfig.DEBUG`.
4. **Surface network failures to the user** instead of `println`-and-swallow in `viewmodel/ContentViewModel.kt` (lines 75, 117, 151, 194) — expose an error/empty state the UI can react to, and replace `println` with proper logging.
5. **Clean up the dependency list**: remove the duplicate `credentials`/`googleid` implementations, collapse the duplicate `foundation`/`runtime-livedata` catalog aliases, and route the raw-literal `coil-compose`/`material-icons-extended` dependencies through the version catalog.
</content>
