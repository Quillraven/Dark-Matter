plugins {
    kotlin("multiplatform") version Versions.kotlin apply (false)
    id("com.android.application") version Versions.agp apply (false)
}

subprojects {
    version = "0.0.1-SNAPSHOT"
    repositories {
        jcenter()
        google()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
