plugins {
    kotlin("multiplatform") version Versions.kotlin apply (false)
    id("com.android.application") version Versions.agp apply (false)
}

subprojects {
    version = "1.1"
    repositories {
        jcenter()
        google()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
