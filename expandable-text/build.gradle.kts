plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "io.github.halilozel1903.expandabletext"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":expandable-text-core"))

    implementation(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

mavenPublishing {
    publishToMavenCentral()
    // Sign only when a key is configured (Maven Central); JitPack and local builds stay unsigned.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }
    // JitPack serves artifacts under com.github.<user>.<repo>.
    val jitpackGroup = "com.github.halilozel1903.compose-expandable-text".takeIf { System.getenv("JITPACK") == "true" }
    coordinates(groupId = jitpackGroup, artifactId = "compose-expandable-text")
    pom {
        name.set("Compose Expandable Text")
        description.set("Expandable read more text for Jetpack Compose: collapsed line limit, inline 'more' link, animated expand, links, hashtags and accessibility.")
    }
}
