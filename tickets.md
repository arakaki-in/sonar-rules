# Tickets: CI/CD & Release Improvements

This file lists the vertical-slice tickets created on GitHub for resolving various CI/CD, release, and packaging issues.

Work the **frontier**: any ticket whose blockers are all done. For a purely linear chain that means top to bottom.

---

## 1. [CodeQL] Fix Python File Exclusions and Verify Advanced Setup
**What to build:** Excludes all Python files from CodeQL static analysis to eliminate false-positive alerts on test fixtures in GitHub Code Scanning.

**Blocked by:** None — can start immediately.

**GitHub Issue:** [#13](https://github.com/arakaki-in/sonar-rules/issues/13)

- [ ] Update `.github/codeql/codeql-config.yml` to ignore all Python files (`**/*.py`) and the entire `src/test/resources/checks/**` folder.
- [ ] Verify that `.github/workflows/codeql.yml` correctly references the configuration file.
- [ ] Ensure GitHub Code Scanning is configured to use "Advanced Setup" (via the workflow) rather than "Default Setup" which bypasses exclusions.

---

## 2. [Java] Review and Address CodeQL Java Quality/Security Issues
**What to build:** Audits and resolves pending Java quality/security alerts flagged by CodeQL in the plugin codebase to ensure a clean security and quality report.

**Blocked by:** None — can start immediately.

**GitHub Issue:** [#14](https://github.com/arakaki-in/sonar-rules/issues/14)

- [ ] Review Java check classes under `src/main/java/com/arakakiin/sonar/python/checks/` and helper utilities for any CodeQL warnings/issues.
- [ ] Fix identified Java security or quality issues while preserving all existing rule functionality and tests.
- [ ] Ensure `mvn clean test` and spotless format check pass cleanly.

---

## 3. [Release] Refactor automatic Version Bumping using Package Management Best Practices
**What to build:** Refactor the release automatic version bumping mechanism to prevent major bumps from also bumping the minor version, adhering to industry-standard package management best practices.

**Blocked by:** None — can start immediately.

**GitHub Issue:** [#15](https://github.com/arakaki-in/sonar-rules/issues/15)

- [ ] Refactor `.github/scripts/bump-version.sh` and the `release.yml` workflow.
- [ ] Implement version bumping based on industry best practices (e.g., parsing Conventional Commits from git log or utilizing a workflow release trigger input) to support major, minor, and patch bumps dynamically.
- [ ] Ensure that a major version bump does not trigger an unexpected minor bump.
- [ ] Ensure alignment with standard GitHub release tag practices.

---

## 4. [SBOM] Generate CycloneDX Software Bill of Materials (SBOM) and Publish to Releases
**What to build:** Generate a CycloneDX SBOM (Software Bill of Materials) in JSON format during compilation, and attach it to each new GitHub Release.

**Blocked by:** None — can start immediately.

**GitHub Issue:** [#16](https://github.com/arakaki-in/sonar-rules/issues/16)

- [ ] Add and configure `cyclonedx-maven-plugin` in `pom.xml`.
- [ ] Ensure the plugin runs during the packaging phase and generates a valid SBOM (e.g. `bom.json`).
- [ ] Update `release.yml` to automatically upload the generated SBOM file to the GitHub Release.

---

## 5. [Deployment] Configure Maven Deployment to GitHub Packages
**What to build:** Publish built Maven plugin artifacts to the GitHub Packages Maven registry automatically during the release pipeline and link them to the release.

**Blocked by:** None — can start immediately.

**GitHub Issue:** [#17](https://github.com/arakaki-in/sonar-rules/issues/17)

- [ ] Ensure `pom.xml` contains the correct distribution configuration for GitHub Packages.
- [ ] Update `release.yml` to authenticate via `GITHUB_TOKEN` and deploy the built plugin package using `mvn deploy`.
- [ ] Verify that packages are successfully published and linked to the repository.

---

## 6. [UX] Professionalize Workflow Pipeline Names
**What to build:** Update workflow names and descriptions to sound highly professional, clean, and appropriate for a public repository.

**Blocked by:** None — can start immediately.

**GitHub Issue:** [#18](https://github.com/arakaki-in/sonar-rules/issues/18)

- [ ] Rename the `name` field of workflows in `ci.yml`, `_build.yml`, `codeql.yml`, and `release.yml` to professional titles (e.g., removing "(Reusable)" and similar markers).
- [ ] Ensure clean, descriptive job names display in the GitHub Actions tab.

---

## 7. [Signing] Professionalize Package Signing using Cosign or Maven GPG Plugin
**What to build:** Enhance artifact signing to adhere to modern security practices (e.g., using Sigstore/Cosign keyless signing or standardizing GPG signing via the Maven GPG plugin).

**Blocked by:** All other tickets:
- [#13](https://github.com/arakaki-in/sonar-rules/issues/13)
- [#14](https://github.com/arakaki-in/sonar-rules/issues/14)
- [#15](https://github.com/arakaki-in/sonar-rules/issues/15)
- [#16](https://github.com/arakaki-in/sonar-rules/issues/16)
- [#17](https://github.com/arakaki-in/sonar-rules/issues/17)
- [#18](https://github.com/arakaki-in/sonar-rules/issues/18)

**GitHub Issue:** [#19](https://github.com/arakaki-in/sonar-rules/issues/19)

- [ ] Implement artifact signing for the built JARs and SBOMs.
- [ ] Align the signing method with best practices for professional package/code signing (e.g., keyless signing using GitHub OIDC / Sigstore / Cosign, or a standardized maven-gpg-plugin profile configuration).
- [ ] Ensure signed signatures are attached to the release and packages.
