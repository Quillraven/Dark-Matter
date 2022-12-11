plugins {
    application
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${Versions.gdx}")
    implementation("com.badlogicgames.gdx:gdx-platform:${Versions.gdx}:natives-desktop")
}

java {
    sourceCompatibility = Versions.java
    targetCompatibility = Versions.java
}

application {
    mainClass.set("com.github.quillraven.darkmatter.desktop.LauncherKt")
}

val assetsDir = rootProject.files("assets")
sourceSets {
    main {
        resources.srcDir(assetsDir)
    }
}

tasks {
    named<Jar>("jar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(files(sourceSets.main.get().output.classesDirs))
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

        archiveBaseName.set("Dark Matter")

        manifest {
            attributes["Main-Class"] = application.mainClass.get()
        }
    }
}
