# library-bom

This is the source of `dev.mbo:library-bom`. This is meant for projects using my libraries and want to
stick to the latest versions.

Attention! This bom must not be used inside the libraries because that would be a circular dependency then.

## Release

see https://github.com/mbogner/spring-boot-bom for more details.

### Build

Local:
```shell
./gradlew clean signMavenPublication publishToMavenLocal
```

see `~/.m2/repository/dev/mbo/spring-kotlin-s3` for the created content

Upload:
```shell
./gradlew clean signMavenPublication publishToMavenLocal publish
```

see https://s01.oss.sonatype.org/content/groups/public/dev/mbo/

### Web Process

https://s01.oss.sonatype.org