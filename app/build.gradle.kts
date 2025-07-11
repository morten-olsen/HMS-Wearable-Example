import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library") // Changed to library
    id("kotlin-android")
    id("com.huawei.agconnect") // This might be specific to the watch app or not needed if companion handles all AGConnect services
    // Consider removing safeargs, parcelize, kapt if not used by the new watch app logic directly
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        // applicationId is not typically used in library modules, but can be if it's a feature module.
        // For a watch app, this will be different. For now, let's assume it will be the wear module.
        // applicationId = "com.fprieto.hms.wearable.watch"
        minSdkVersion(AndroidSettings.minSdk)
        targetSdkVersion(AndroidSettings.targetSdk)
        versionCode = AndroidSettings.appVersionCode
        versionName = AndroidSettings.appVersionName
        resConfigs ("en","zh-rCN") // May need adjustment for watch resources
    }
    buildFeatures {
        viewBinding = true // May or may not be used by watch UI
    }
    buildToolsVersion(AndroidSettings.buildTools)
    compileSdkVersion(AndroidSettings.compileSdk)
    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    // peerPkgName and peerFingerPrint might be relevant for Wear Engine communication. Keep for now.
    val properties = gradleLocalProperties(rootDir)
    val peerPkgName: String = properties.getProperty("peerPkgName") ?: ""
    val peerFingerPrint: String = properties.getProperty("peerFingerprint") ?: ""

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false // Adjust for watch app
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // Watch app will need its own proguard rules
            )
            isDebuggable = false
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug") // Watch app will have its own signing
            isDebuggable = true
            // These buildConfigFields might be specific to how the watch app communicates or identifies itself
            buildConfigField("String", "peerPkgName", peerPkgName)
            buildConfigField("String", "peerFingerPrint", peerFingerPrint)
        }
    }

    // Signing configs will likely be different for the watch app (HAP)
    // For now, keeping it, but this section will need significant changes for HarmonyOS build.
    val keyAliasProperty: String = gradleLocalProperties(rootDir).getProperty("keyAlias")
    val keyPasswordProperty: String = gradleLocalProperties(rootDir).getProperty("keyPassword")
    val storePasswordProperty: String = gradleLocalProperties(rootDir).getProperty("storePassword")

    signingConfigs {
        getByName("debug") {
            keyAlias = keyAliasProperty
            keyPassword = keyPasswordProperty
            storeFile = file("../keystore/debug.keystore")
            storePassword = storePasswordProperty
        }
    }
}

dependencies {
    fun kapt(definition: Any) = "kapt"(definition)
    fun implementation(definition: Any) = "implementation"(definition)
    fun testImplementation(definition: Any) = "testImplementation"(definition)
    fun androidTestImplementation(definition: Any) = "androidTestImplementation"(definition)

    // Keep only dependencies relevant for the watch app.
    // UI, navigation, coroutines, and Wear Engine are likely needed.
    // Network, Dagger, Retrofit, OkHttp, ExoPlayer (full version) might be removed or replaced with watch-specific alternatives.

    implementation(Dependencies.AndroidX.coreKtx) // Likely needed
    implementation(Dependencies.AndroidX.appCompat) // For base Activity/Fragment if using AndroidX on HarmonyOS
    implementation(Dependencies.Kotlin.jdk8)
    implementation(Dependencies.Kotlin.coroutines)

    implementation(Dependencies.Huawei.wearengine) // Essential for watch communication
    implementation(Dependencies.Huawei.hmsBase)   // If any base HMS services are used directly by watch

    // The following are likely NOT needed in the watch app as companion handles them:
    // implementation(Dependencies.AndroidX.fragmentKtx)
    // implementation(Dependencies.AndroidX.lifecycleLivedataKtx)
    // annotationProcessor(Dependencies.AndroidX.lifecycleCompiler)
    // implementation(Dependencies.AndroidX.archComponents)
    // implementation(Dependencies.AndroidX.lifeCycleCommon)
    // implementation(Dependencies.AndroidX.material) // Might be replaced with HarmonyOS UI components
    // implementation(Dependencies.AndroidX.constraintlayout)
    // implementation(Dependencies.AndroidX.Navigation.fragmentKtx)
    // implementation(Dependencies.AndroidX.Navigation.uiKtx)
    // implementation(Dependencies.Huawei.health) // If health data is not directly accessed by watch
    // implementation(Dependencies.Huawei.hiHealth) // If health data is not directly accessed by watch
    // implementation(Dependencies.ExoPlayer.core) // Watch might use simpler media player or a lite version
    // implementation(Dependencies.ExoPlayer.ui)
    // implementation(Dependencies.timber) // Could be kept for logging
    // implementation(Dependencies.gson) // If data models are shared and serialized

    // Dagger might be overkill for watch app, or a simpler DI might be used.
    // kapt(Dependencies.Dagger.daggerCompiler)
    // implementation(Dependencies.Dagger.daggerAndroid)
    // implementation(Dependencies.Dagger.daggerAndroidSupport)
    // kapt(Dependencies.Dagger.daggerAndroidProcessor)

    // Network libraries are not needed as companion handles network.
    // implementation(Dependencies.Retrofit.retrofit)
    // implementation(Dependencies.Retrofit.converterGson)
    // implementation(Dependencies.OkHttp.okhttp)
    // implementation(Dependencies.OkHttp.loggingInterceptor)

    // Test dependencies would also change based on watch app's needs
    testImplementation(kotlin("test"))
    testImplementation(TestDependencies.kotlinxCoroutines)
    testImplementation(TestDependencies.JUnit.junit)
    // ... other relevant test dependencies
}
