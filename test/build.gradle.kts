plugins {
    `java-library`
}

group = "dev.mbo"

dependencies {
    implementation(platform(libs.library.bom))
    implementation("dev.mbo:kotlin-logging")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://s01.oss.sonatype.org/content/groups/public")
    }
    mavenCentral()
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
        options.isIncremental = true
    }

    withType<Test> {
        useJUnitPlatform()
    }

    wrapper {
        // https://gradle.org/releases/
        gradleVersion = "8.10.2"
        distributionType = Wrapper.DistributionType.BIN
    }
}