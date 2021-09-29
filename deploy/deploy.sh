#!/usr/bin/env bash
# Find the most recent version via git tags
PREVIOUS_VERSION=$(npx git-semver-tags | head -n 1);
# Using conventional commits paradigm, check the merge commit message on master to set up a new version deployment
if [ -n "$PREVIOUS_VERSION" ]; then BUMP_TYPE=$(npx -p conventional-changelog-angular -p conventional-recommended-bump conventional-recommended-bump --preset angular);
    NEXT_VERSION=$(npx semver -i $BUMP_TYPE ${PREVIOUS_VERSION});
    # Add a message to console with the next version number
    echo "Versioning artifacts with version $NEXT_VERSION";
    # Sets the new version for the release and deploys using the maven release profile
    mvn -U --no-transfer-progress versions:set --define newVersion=${NEXT_VERSION};
    echo "Performing release for version $NEXT_VERSION";
    mvn -U --no-transfer-progress -P release -Dtag=${NEXT_VERSION} deploy scm:tag;
  else
    echo "A problem was encountered when determining the version bump. Please check logs and try again.";
    return 1;
fi