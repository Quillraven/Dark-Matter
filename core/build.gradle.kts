plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
    implementation("com.badlogicgames.gdx:gdx:${Versions.gdx}")
    implementation("com.badlogicgames.ashley:ashley:${Versions.ashley}")
    api("io.github.libktx:ktx-app:${Versions.ktx}")
    implementation("io.github.libktx:ktx-ashley:${Versions.ktx}")
    implementation("io.github.libktx:ktx-assets-async:${Versions.ktx}")
    implementation("io.github.libktx:ktx-collections:${Versions.ktx}")
    implementation("io.github.libktx:ktx-math:${Versions.ktx}")
    implementation("io.github.libktx:ktx-graphics:${Versions.ktx}")
    implementation("io.github.libktx:ktx-log:${Versions.ktx}")
}

java {
    sourceCompatibility = Versions.java
    targetCompatibility = Versions.java
}
