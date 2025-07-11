buildscript {
    repositories {
        google()
        jcenter()
        maven("https://developer.huawei.com/repo/")
    }
    dependencies {
        classpath(BuildDependencies.androidGradle)
        classpath(BuildDependencies.safeArgs)
        classpath(BuildDependencies.kotlinGradlePlugin)
        classpath(BuildDependencies.agConnect)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral() // Added Maven Central
        jcenter() // Keeping jcenter for now, but typically listed after google/mavenCentral
        maven("https://developer.huawei.com/repo/")
    }
}

task("clean") {
    delete(rootProject.buildDir)
}
