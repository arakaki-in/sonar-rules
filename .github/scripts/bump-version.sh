#!/bin/bash
set -e

# Retrieve the current version in pom.xml as backup/default
pom_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Current version in pom.xml: $pom_version"

# Retrieve the latest git tag matching v*
latest_tag=$(git describe --tags --abbrev=0 --match "v*" 2>/dev/null || true)

if [ -z "$latest_tag" ]; then
  echo "No Git tag found. Using pom.xml version as base."
  # If it ends with -SNAPSHOT, strip it
  if [[ "$pom_version" == *-SNAPSHOT ]]; then
    base_version="${pom_version%-SNAPSHOT}"
  else
    base_version="$pom_version"
  fi
  # Scan all commits since beginning of history
  commits=$(git log --oneline 2>/dev/null || echo "")
else
  echo "Latest Git tag found: $latest_tag"
  base_version="${latest_tag#v}"
  # Scan commits since the latest tag
  commits=$(git log "${latest_tag}..HEAD" --oneline 2>/dev/null || echo "")
fi

echo "Base version to bump from: $base_version"

# Determine bump type based on Conventional Commits
bump_type="patch"
if [ -n "$commits" ]; then
  echo "Analyzing commits since last tag..."
  # Define regex patterns for matching Conventional Commits
  # Note: storing regex in variables avoids syntax errors in Bash [[ ... =~ ... ]]
  major_pattern='^[a-zA-Z0-9_-]+(\([^)]+\))?\!:'
  minor_pattern='^feat(\([^)]+\))?:'

  while IFS= read -r line; do
    # Check for breaking change: type(scope)!: or type!: or BREAKING CHANGE anywhere in message
    if [[ "$line" =~ $major_pattern ]] || [[ "$line" == *"BREAKING CHANGE"* ]] || [[ "$line" == *"BREAKING CHANGE:"* ]]; then
      bump_type="major"
      echo "  Found breaking change: $line"
      break # Major has highest precedence, stop scanning
    elif [[ "$line" =~ $minor_pattern ]]; then
      if [ "$bump_type" != "major" ]; then
        bump_type="minor"
        echo "  Found feature: $line"
      fi
    fi
  done <<< "$commits"
else
  echo "No new commits found since last tag. Defaulting to patch bump."
fi

echo "Determined bump type: $bump_type"

# Parse base version into major, minor, patch
if [[ "$base_version" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
  major="${BASH_REMATCH[1]}"
  minor="${BASH_REMATCH[2]}"
  patch="${BASH_REMATCH[3]}"
elif [[ "$base_version" =~ ^([0-9]+)\.([0-9]+)$ ]]; then
  major="${BASH_REMATCH[1]}"
  minor="${BASH_REMATCH[2]}"
  patch=0
else
  major="$base_version"
  minor=0
  patch=0
fi

# Apply version bump
if [ "$bump_type" = "major" ]; then
  new_version="$((major + 1)).0.0"
elif [ "$bump_type" = "minor" ]; then
  new_version="$major.$((minor + 1)).0"
else
  new_version="$major.$minor.$((patch + 1))"
fi

echo "New version: $new_version"

# Set the new version in pom.xml using maven versions plugin
mvn versions:set -DnewVersion="$new_version" -DgenerateBackupPoms=false

# Output to GITHUB_OUTPUT if running in GitHub Actions
if [ -n "$GITHUB_OUTPUT" ]; then
  echo "new_version=$new_version" >> "$GITHUB_OUTPUT"
fi
