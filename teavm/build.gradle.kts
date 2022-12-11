import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    java
    kotlin("jvm")
    id("org.gretty")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation("org.teavm:teavm-cli:${Versions.teamvmCli}")
    implementation("com.github.xpenatan.gdx-teavm:backend-web:${Versions.gdxWebToolsVersion}")
    implementation("com.github.xpenatan.gdx-teavm:backend-teavm:${Versions.gdxWebToolsVersion}")
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

gretty {
    contextPath = "/"
    extraResourceBase("build/dist/webapp")
}

val buildJavaScript = tasks.register<JavaExec>("buildJavaScript") {
    dependsOn(tasks.classes)
    mainClass.set("com.github.quillraven.darkmatter.teavm.LauncherKt")
    setClasspath(sourceSets.main.get().runtimeClasspath)
}

tasks.named("build").dependsOn(buildJavaScript)

tasks.register("run") {
    dependsOn(buildJavaScript, ":teavm:jettyRun")
}
