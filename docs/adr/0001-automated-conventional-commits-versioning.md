# Automated Conventional Commits Versioning

We decided to use Git tags as the source of truth for the base version and calculate the next version automatically using Conventional Commits. This replaces the Maven snapshot-based versioning to support automated releases on pushes to `main` without manual POM updates.
