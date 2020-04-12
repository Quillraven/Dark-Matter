plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${Versions.gdx}")
    implementation("com.badlogicgames.gdx:gdx-platform:${Versions.gdx}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${Versions.gdx}:natives-desktop")
}

java {
    sourceCompatibility = Versions.java
    targetCompatibility = Versions.java
}

val assetsDir = rootProject.files("assets")
sourceSets {
    main {
        resources.srcDir(assetsDir)
    }
}

tasks.register<Jar>("dist") {
    from(files(sourceSets.main.get().output.classesDirs))
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    from(assetsDir)

    manifest {
        attributes["Main-Class"] = "com.github.quillraven.darkmatter.desktop.launcherKt"
    }
}
