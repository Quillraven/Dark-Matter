plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt") version Versions.detekt
}

dependencies {
    api(kotlin("stdlib"))
    api("com.badlogicgames.gdx:gdx:${Versions.gdx}")
    api("com.badlogicgames.ashley:ashley:${Versions.ashley}")
    api("io.github.libktx:ktx-app:${Versions.ktx}")
    api("io.github.libktx:ktx-actors:${Versions.ktx}")
    api("io.github.libktx:ktx-ashley:${Versions.ktx}")
    api("io.github.libktx:ktx-assets:${Versions.ktx}")
    api("io.github.libktx:ktx-collections:${Versions.ktx}")
    api("io.github.libktx:ktx-math:${Versions.ktx}")
    api("io.github.libktx:ktx-graphics:${Versions.ktx}")
    api("io.github.libktx:ktx-i18n:${Versions.ktx}")
    api("io.github.libktx:ktx-log:${Versions.ktx}")
    api("io.github.libktx:ktx-preferences:${Versions.ktx}")
    api("io.github.libktx:ktx-scene2d:${Versions.ktx}")
    api("io.github.libktx:ktx-style:${Versions.ktx}")
}

java {
    sourceCompatibility = Versions.java
    targetCompatibility = Versions.java
}

detekt {
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
