plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
    implementation("com.badlogicgames.gdx:gdx:${Versions.gdx}")
    implementation("com.badlogicgames.ashley:ashley:${Versions.ashley}")
    implementation("com.badlogicgames.box2dlights:box2dlights:${Versions.box2dLight}")
    implementation("com.badlogicgames.gdx:gdx-box2d:${Versions.gdx}")
    api("io.github.libktx:ktx-app:${Versions.ktx}")
    implementation("io.github.libktx:ktx-ashley:${Versions.ktx}")
    implementation("io.github.libktx:ktx-assets-async:${Versions.ktx}")
    implementation("io.github.libktx:ktx-math:${Versions.ktx}")
    implementation("io.github.libktx:ktx-graphics:${Versions.ktx}")
    implementation("io.github.libktx:ktx-log:${Versions.ktx}")
}

java {
    sourceCompatibility = Versions.java
    targetCompatibility = Versions.java
}
