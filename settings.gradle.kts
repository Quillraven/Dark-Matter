include(":core", ":desktop", ":android", ":teavm")

pluginManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://s01.oss.sonatype.org") }
        mavenLocal()
        google()
        gradlePluginPortal()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}
