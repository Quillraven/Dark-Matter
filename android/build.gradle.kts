plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion = "android-31"
    sourceSets {
        named("main") {
            java.srcDirs("src/main/kotlin")
            assets.srcDirs(project.file("../assets"))
            jniLibs.srcDirs("libs")
        }
    }

    defaultConfig {
        applicationId = "com.github.quillraven.darkmatter"
        minSdk = 19
        targetSdk = 31
        versionCode = 3
        versionName = "${project.version}"
        multiDexEnabled = true
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
        isCoreLibraryDesugaringEnabled = true
    }
}

val natives: Configuration by configurations.creating

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation(project(":core"))
    implementation(kotlin("stdlib"))
    implementation("com.badlogicgames.gdx:gdx-backend-android:${Versions.gdx}")

    natives("com.badlogicgames.gdx:gdx-platform:${Versions.gdx}:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:${Versions.gdx}:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:${Versions.gdx}:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:${Versions.gdx}:natives-x86_64")
}

// Called every time gradle gets executed, takes the native dependencies of
// the natives configuration, and extracts them to the proper libs/ folders
// so they get packed with the APK.
tasks.register("copyAndroidNatives") {
    doFirst {
        natives.files.forEach { jar ->
            val outputDir = file("libs/" + jar.nameWithoutExtension.substringAfterLast("natives-"))
            outputDir.mkdirs()
            copy {
                from(zipTree(jar))
                into(outputDir)
                include("*.so")
            }
        }
    }
}

tasks.whenTaskAdded {
    if ("package" in name) {
        dependsOn("copyAndroidNatives")
    }
}
