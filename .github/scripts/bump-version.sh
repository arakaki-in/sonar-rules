#!/bin/bash
set -e

# Retrieve the current version
version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Current version: $version"

# If it ends with -SNAPSHOT, strip it
if [[ "$version" == *-SNAPSHOT ]]; then
  new_version="${version%-SNAPSHOT}"
else
  # If it is a release version, increment the last digit
  if [[ "$version" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
    major="${BASH_REMATCH[1]}"
    minor="${BASH_REMATCH[2]}"
    patch="${BASH_REMATCH[3]}"
    new_version="$major.$minor.$((patch + 1))"
  elif [[ "$version" =~ ^([0-9]+)\.([0-9]+)$ ]]; then
    major="${BASH_REMATCH[1]}"
    minor="${BASH_REMATCH[2]}"
    new_version="$major.$((minor + 1))"
  else
    new_version="$version.1"
  fi
fi

echo "New version: $new_version"

# Set the new version in pom.xml using maven versions plugin
mvn versions:set -DnewVersion="$new_version" -DgenerateBackupPoms=false

# Output to GITHUB_OUTPUT if running in GitHub Actions
if [ -n "$GITHUB_OUTPUT" ]; then
  echo "new_version=$new_version" >> "$GITHUB_OUTPUT"
fi
