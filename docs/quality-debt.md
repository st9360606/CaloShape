# Android lint quality-debt register

Baseline: `:app:lintDevDebug` report reviewed on 2026-07-19. The report has
242 warnings and no lint errors. These warnings are intentionally classified
instead of being suppressed or cleared through an unsafe dependency upgrade.

## Release-blocking review items

| Issue | Count | Required disposition before production release |
| --- | ---: | --- |
| `MissingPermission` | Resolved | The app declares `SCHEDULE_EXACT_ALARM`; runtime availability is guarded with `canScheduleExactAlarms()` and falls back to inexact scheduling. |
| `HardwareIds` | Resolved | The app uses a generated, app-local UUID instead of a hardware identifier. |
| `AcceptsUserCertificates` / `InsecureBaseConfiguration` | Resolved | Production permits only system certificates and blocks cleartext; debug uses `debug-overrides` for local certificate interception and scopes cleartext to `10.0.2.2`. |
| `CredentialManagerSignInWithGoogle` | Lint false-positive review | The implementation parses `CustomCredential` with `GoogleIdTokenCredential.createFrom`; verify this again when upgrading the Credential Manager dependency. |

## Planned non-blocking remediation

| Issue family | Count | Plan |
| --- | ---: | --- |
| Dependency version suggestions (`GradleDependency`, `AndroidGradlePluginVersion`) | 76 | Upgrade only in separately reviewed dependency batches, with device and release-build verification. |
| Version catalog placement (`UseTomlInstead`) | 56 | Consolidate versions while touching each dependency group; do not mix with product changes. |
| Copy and resource cleanup (`PluralsCandidate`, `TypographyDashes`, `Typos`, `UnusedTranslation`) | 66 | Resolve during the localization pass with translator review. |
| Resource and API compatibility (`UnusedResources`, `UnusedAttribute`, `ObsoleteSdkInt`) | 33 | Remove or move version-specific XML/resources in small, individually tested changes. |
| Compose layout/style (`DisableBaselineAlignment`, `UseOfNonLambdaOffsetOverload`, `ModifierParameter`) | 10 | Address with screen-level visual regression checks. |

This register is not a lint baseline and does not suppress warnings. Re-run
`./gradlew :app:lintDevDebug` after each remediation batch and update the
counts here.
