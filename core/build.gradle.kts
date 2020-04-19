plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt") version Versions.detekt
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
    implementation("com.badlogicgames.gdx:gdx:${Versions.gdx}")
    implementation("com.badlogicgames.ashley:ashley:${Versions.ashley}")
    api("io.github.libktx:ktx-app:${Versions.ktx}")
    implementation("io.github.libktx:ktx-actors:${Versions.ktx}")
    implementation("io.github.libktx:ktx-ashley:${Versions.ktx}")
    implementation("io.github.libktx:ktx-assets-async:${Versions.ktx}")
    implementation("io.github.libktx:ktx-collections:${Versions.ktx}")
    implementation("io.github.libktx:ktx-math:${Versions.ktx}")
    implementation("io.github.libktx:ktx-graphics:${Versions.ktx}")
    implementation("io.github.libktx:ktx-log:${Versions.ktx}")
    implementation("io.github.libktx:ktx-scene2d:${Versions.ktx}")
    implementation("io.github.libktx:ktx-style:${Versions.ktx}")
}

java {
    sourceCompatibility = Versions.java
    targetCompatibility = Versions.java
}

detekt {
    failFast = true
    buildUponDefaultConfig = true
    config = files("${rootProject.projectDir}/config/detekt.yml")

    reports {
        html.enabled = true
        xml.enabled = false
        txt.enabled = false
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.jvm
        }
    }
    withType<io.gitlab.arturbosch.detekt.Detekt> {
        jvmTarget = Versions.jvm
    }
}
