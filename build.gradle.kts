plugins {
    kotlin("multiplatform") version Versions.kotlin apply (false)
    id("com.android.application") version Versions.agp apply (false)
    id("org.gretty") version Versions.gretty apply (false)
}

subprojects {
    version = "1.3"
    repositories {
        mavenCentral()
        maven { url = uri("https://s01.oss.sonatype.org") }
        mavenLocal()
        google()
        gradlePluginPortal()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }
}
