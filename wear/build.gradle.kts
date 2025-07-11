// Conceptual HarmonyOS build.gradle.kts for the 'wear' module
// Actual syntax and plugins will depend on HarmonyOS SDK and DevEco Studio

plugins {
    id("com.huawei.ohos.app") // Example HarmonyOS application plugin
    id("kotlin") // If using Kotlin for HarmonyOS (Java is also common)
}

ohos {
    // HarmonyOS specific configuration
    compileSdkVersion 5 // Example API version
    defaultConfig {
        compatibleSdkVersion 4 // Example
        targetDeviceType "wearable" // Specify device type
        // bundleName and abilityName configurations
        bundleName = "com.fprieto.hms.wearable.audiobookshelfhr" // Unique bundle name for HarmonyOS app
    }
    buildTypes {
        release {
            proguardEnabled false // Configure Proguard/obfuscation as needed
            // signingConfigName "release" // Configure signing for release HAP
        }
        debug {
            // signingConfigName "debug"
        }
    }
    // Signing configurations for HAP
    // signingConfigs {
    //     debug {
    //         storeFile file('../keystore/harmony_debug.p12') // Example path
    //         storePassword 'password'
    //         keyAlias 'alias'
    //         keyPassword 'password'
    //         signAlg 'SHA256withECDSA'
    //         profile file('../keystore/harmony_debug.p7b') // Example path
    //         certFingerprint 'FINGERPRINT_HERE'
    //     }
    // }
}

dependencies {
    implementation(project(":shared")) // If shared module contains common code/models

    // HarmonyOS SDK dependencies
    // implementation "com.huawei.ohos:harmonyos_sdk:X.Y.Z" // Example, actual dependencies vary
    // implementation "com.huawei.ohos:WearableUI:X.Y.Z" // For UI components

    // Include Kotlin standard library if using Kotlin
    implementation(kotlin("stdlib-jdk8"))

    // Communication library for watch-phone interaction if not part of core SDK
    // implementation "com.huawei.hms:wearengine-harmonyos:X.Y.Z" // Fictional example

    // GSON for JSON serialization if used in shared models and needed directly on watch
    implementation("com.google.code.gson:gson:2.8.9")

    // Timber for logging (if a HarmonyOS compatible version/adapter exists or use HarmonyOS HiLog)
    // implementation "com.jakewharton.timber:timber:5.0.1"

    // Test dependencies
    // testImplementation "junit:junit:4.13.2"
}
