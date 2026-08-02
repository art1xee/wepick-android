# wepick-android — Fix Plan

Companion to [AUDIT.md](AUDIT.md). Ordered by risk/impact — tackle in phases rather than all at once.

## Phase 1 — Security (do this first)

1. **Rotate the TMDB key** at themoviedb.org (generate a new API key, revoke the old one — assume the current one is burned since it's in git history).
2. **Stop hardcoding it**: in `app/build.gradle.kts:36`, remove the `?: "f0d0bc12560c00cff720536f062f5463"` fallback. Make the build fail loudly if `TMDB_API_KEY` isn't set in `local.properties`, instead of silently substituting a default.
3. **Add the new key** to your local `local.properties` only (never commit it).
4. **Untrack `local.properties` from git**: `git rm --cached local.properties`, confirm it's still listed in `.gitignore`, commit the removal.
5. **Gate the logging interceptor**: in `RetrofitClient.kt`, change `HttpLoggingInterceptor.Level.BODY` to something like `if (BuildConfig.DEBUG) Level.BODY else Level.NONE` in both places (lines ~16-18 and ~28-30).

## Phase 2 — Correctness bugs (quick, high-value)

6. **Fix the password validation** in `SignupScreen.kt:76-77` — flip `password.length < 6` to `password.length >= 6` (or whatever the actual minimum should be).
7. **Fix the copy bug** in `MainScreen.kt:107` — replace the reused `R.string.login_email_hint` with a proper name-field string resource (add one to `strings.xml` if it doesn't exist).
8. **Swap hardcoded strings for existing resources** in `SignupScreen.kt` (lines ~200-201, 221, 255, 261) — use the `signup_confirm_password_label`, `signup_button`, etc. resources that already exist in `strings.xml` instead of literals.

## Phase 3 — Error handling

9. In `ContentViewModel.kt` (lines 75, 117, 151, 160, 194), replace each `println(e)` with `Log.e(TAG, "...", e)` and set an error/loading state (e.g. a sealed `UiState` or a simple `errorMessage: State<String?>`) that the screen can observe and show ("Failed to load — retry" UI) instead of silently showing stale/empty data.

## Phase 4 — Dependency cleanup

10. In `app/build.gradle.kts`, delete the duplicate lines 117-119 (`androidx.credentials`, `androidx.credentials.play.services.auth`, `googleid` — already declared at 95-97).
11. In `gradle/libs.versions.toml`, pick one version for `foundation`/`foundationVersion` and delete the other alias; same for `androidx-runtime-livedata` vs `androidx-compose-runtime-livedata`. Update `build.gradle.kts` references accordingly.
12. Move the raw-literal `coil-compose` and `material-icons-extended` dependencies into the version catalog (bump coil to the catalog's `2.7.0` while at it, and rebuild/retest since it's a version jump).
13. Remove `androidx.benchmark.traceprocessor` unless macrobenchmark trace analysis is actually needed.

## Phase 5 — Structural cleanup (lower urgency, do when touching that code anyway)

14. Delete the leftover comment in `HomeScreen.kt:86` and the unused `NavHost`/`composable` imports in `MainScaffold.kt`.
15. Remove dead code: unused `getGenreIdForApi` and `Paging.JIKAN_MAX_PAGES` in `ContentViewModel.kt`/`Constants.kt`.
16. Fix the `Anime` branch in `ContentViewModel.kt:40,225` passing the Jikan base URL as an unused `apiKey` param — drop the param from that call path.
17. Move shared form components (`FormTextFields`, `EmailTextField`, `PasswordTextField`, `LoginDivider`) out of `LoginScreen.kt` into `ui/components/`, alongside `RetroButtons.kt`.
18. Either implement or remove the empty `SettingScreen.kt` stub.

## Phase 6 — Testing (ongoing, not a one-shot)

19. Replace the two template test files with real unit tests. Start with pure-logic pieces that don't need Android context (e.g. the fixed password validation logic, genre-matching logic in `PlayerViewModel`).
20. This will be much easier once ViewModels aren't hard-wired to `FirebaseAuth.getInstance()`/`RetrofitClient` — consider introducing simple constructor injection (pass dependencies in, default to the real singleton) so tests can substitute fakes, even without a full DI framework.
</content>
