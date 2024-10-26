plugins {
    `java-library` // we don't want to create a jar but found no other working solution so far
    signing // required for maven central
    `maven-publish`
    // https://plugins.gradle.org/plugin/io.spring.dependency-management
    id("io.spring.dependency-management") version "1.1.6"
}

group = "dev.mbo"

dependencyManagement {
    dependencies {
        dependency("dev.mbo:kotlin-logging:1.0.0")
        dependency("dev.mbo:spring-kotlin-cache:1.0.0")
        dependency("dev.mbo:spring-kotlin-error:1.0.0")
        dependency("dev.mbo:spring-kotlin-jpa:1.0.0")
        dependency("dev.mbo:spring-kotlin-reflection:1.0.0")
        dependency("dev.mbo:spring-kotlin-s3:1.0.0")
        dependency("dev.mbo:spring-kotlin-smtp:1.0.0")
        dependency("dev.mbo:spring-kotlin-templating:1.0.0")
        dependency("dev.mbo:spring-kotlin-validation:1.0.0")
        dependency("dev.mbo:spring-kotlin-web:1.0.0")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

// using "artifact bomZip" makes the project a pom packaging
tasks.register<Zip>("bomZip") {
    group = "build"
    description = "create zip from bom"
    archiveFileName.set("bom-${project.version}.zip")
    from(layout.buildDirectory.dir("publications/maven"))
    include("*.xml")
    dependsOn("generatePomFileForMavenPublication")
}

// disable gradle-metadata
tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = project.findProperty("ossrhUsername") as String?
                password = project.findProperty("ossrhPassword") as String?
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks["bomZip"])

            pom {
                name.set("library-bom")
                description.set("BOM for applications using my libs.")
                url.set("https://mbo.dev")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                scm {
                    url.set("https://github.com/mbogner/library-bom")
                    connection.set("git@github.com:mbogner/library-bom.git")
                    developerConnection.set("git@github.com:mbogner/library-bom.git")
                }
                developers {
                    developer {
                        id.set("mbo")
                        name.set("Manuel Bogner")
                        email.set("outrage_breath.0t@icloud.com")
                        organization.set("mbo.dev")
                        organizationUrl.set("https://mbo.dev")
                        timezone.set("Europe/Vienna")
                        roles.set(listOf("developer", "architect"))
                    }
                }
                organization {
                    name.set("mbo.dev")
                    url.set("https://mbo.dev")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

tasks.wrapper {
    // https://gradle.org/releases/
    gradleVersion = "8.10.2"
    distributionType = Wrapper.DistributionType.BIN
}