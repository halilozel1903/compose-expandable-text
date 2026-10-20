import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    explicitApi()
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    testImplementation(libs.kotlin.test)
}

mavenPublishing {
    publishToMavenCentral()
    // Sign only when a key is configured (Maven Central); JitPack and local builds stay unsigned.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }
    // JitPack serves artifacts under com.github.<user>.<repo>.
    val jitpackGroup = "com.github.halilozel1903.compose-expandable-text".takeIf { System.getenv("JITPACK") == "true" }
    coordinates(groupId = jitpackGroup, artifactId = "compose-expandable-text-core")
    pom {
        name.set("Compose Expandable Text Core")
        description.set("Platform independent logic behind compose-expandable-text: word boundary truncation, link, mention and hashtag detection, and a small LRU cache.")
    }
}
