buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.42")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.7.10")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.1")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}