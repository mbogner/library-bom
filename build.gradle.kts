import java.time.Year

plugins {
    `java-library` // we don't want to create a jar but found no other working solution so far
    id("maven-publish")
    // https://plugins.gradle.org/plugin/io.spring.dependency-management
    id("io.spring.dependency-management") version "1.1.7"
    // https://plugins.gradle.org/plugin/net.researchgate.release
    id("net.researchgate.release") version "3.1.0"
    // https://plugins.gradle.org/plugin/org.jreleaser
    id("org.jreleaser") version "1.19.0"
}

group = "dev.mbo"
val year = Year.now().value

dependencyManagement {
    dependencies {
        dependency("dev.mbo:kotlin-logging:2025.7.1")
        dependency("dev.mbo:kotlin-encryption:2025.7.1")
        dependency("dev.mbo:spring-kotlin-cache:2025.7.1")
        dependency("dev.mbo:spring-kotlin-reflection:2025.7.1")
        dependency("dev.mbo:spring-kotlin-error:2025.7.1")
        dependency("dev.mbo:spring-kotlin-jpa:2025.7.1")
        dependency("dev.mbo:spring-kotlin-s3:2025.7.1")
        dependency("dev.mbo:spring-kotlin-validation:2025.7.1")
        dependency("dev.mbo:spring-kotlin-smtp:2025.7.1")
        dependency("dev.mbo:spring-kotlin-templating:2025.7.1")
        dependency("dev.mbo:spring-kotlin-web:2025.7.1")
        dependency("dev.mbo:spring-kotlin-aop-logging:2025.7.1")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks {
    withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }

    named("jar") {
        enabled = false
    }

    named("jreleaserFullRelease") {
        dependsOn("publish")
    }

    named("afterReleaseBuild") {
        dependsOn("publishToMavenLocal", "jreleaserFullRelease")
    }

    named<Wrapper>("wrapper") {
        gradleVersion = "8.14.3"
        distributionType = Wrapper.DistributionType.BIN
    }
}

jreleaser {
    project {
        name.set("library-bom")
        description.set("Spring Boot BOM for mbo.dev projects")
        longDescription.set("Simple BOM (Bill of Materials) for libraries used in mbo.dev projects.")
        license.set("Apache-2.0")
        copyright.set("\u00a9 $year mbo.dev")
        authors.set(listOf("Manuel Bogner"))
        tags.set(listOf("spring", "boot", "bom", "dependencies", "kotlin"))
        links {
            homepage.set("https://mbo.dev")
            documentation.set("https://github.com/mbogner/library-bom")
        }
    }
    signing {
        active.set(org.jreleaser.model.Active.ALWAYS)
        armored.set(true)
    }
    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    active.set(org.jreleaser.model.Active.ALWAYS)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    snapshotSupported.set(true)
                    stagingRepository("${layout.buildDirectory.get()}/staging-deploy")
                }
            }
        }
    }
    release {
        github {
            tagName.set("{{projectVersion}}")
            releaseName.set("{{projectVersion}}")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name.set("library-bom")
                description.set("BOM for applications using my libs.")
                url.set("https://mbo.dev")
                packaging = "pom"

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
                        roles.set(listOf("developer", "architect"))
                        timezone.set("Europe/Vienna")
                    }
                }
                organization {
                    name.set("mbo.dev")
                    url.set("https://mbo.dev")
                }
            }

            // prevent Gradle from attaching the default JAR
            suppressAllPomMetadataWarnings()
        }
    }
    repositories {
        maven {
            name = "staging"
            url = uri("${layout.buildDirectory.get()}/staging-deploy")
        }
    }
}
