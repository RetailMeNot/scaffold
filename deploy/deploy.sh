#!/usr/bin/env bash
# Update GPG creds
gpg --allow-secret-key-import --import deploy/seckey.key
gpg --import deploy/pubkey.key
# Update the git config with the gpg key
echo "default-key $GPG_KEYNAME" > $HOME/.gnupg/gpg.conf
git config --global user.signingkey $GPG_KEYNAME
# Copy over the settings.xml file
cp .travis.settings.xml $HOME/.m2/settings.xml
# Add ssh key
eval "$(ssh-agent -s)"
chmod 600 deploy/deploy_rsa
ssh-add deploy/deploy_rsa
# Find the most recent version via git tags
OLD_VERSION=$(npx git-semver-tags | head -n 1)
# Using conventional commits paradigm, check the merge commit message on master to set up a new version deployment
if [ -n "$OLD_VERSION" ]; then BUMP_TYPE=$(npx -p conventional-changelog-angular -p conventional-recommended-bump conventional-recommended-bump --preset angular);
    NEXT_VERSION=$(npx semver -i $BUMP_TYPE ${OLD_VERSION});
    # Add a message to console with the next version number
    echo "Versioning artifacts with version $NEXT_VERSION";
    # Sets the new version for the release and deploys using the maven release profile
    mvn versions:set --define newVersion=${NEXT_VERSION};
    echo "Performing release for version $NEXT_VERSION";
    mvn -Prelease -Dtag=${NEXT_VERSION} deploy scm:tag;
  else
    echo "A problem was encountered when determining the version bump. Please check logs.";
    return 1;
  fi
