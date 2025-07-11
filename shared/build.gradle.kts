plugins {
    kotlin("jvm") // Or 'java-library' if models are plain Java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    // Gson is used by companion and wear modules for serializing these models,
    // but the models themselves don't need to depend on Gson unless they use annotations from it.
    // api("com.google.code.gson:gson:2.8.9") // Use 'api' if Gson annotations were used in models
}
