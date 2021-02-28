## Release Instructions (don't use release plugin and bypass staging repo)

``` bash
release_version=1.0.6
new_version=1.0.7

./mvnw versions:set -DnewVersion=${release_version}
./mvnw clean deploy -P release-sign-artifacts
./mvnw versions:set -DnewVersion=${new_version}-SNAPSHOT
```
