import org.jetbrains.compose.compose

plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.episodeimages.api)
                implementation(projects.data.episodes.api)
                implementation(projects.data.seasondetails.api)

                api(compose("org.jetbrains.compose.runtime:runtime"))
                api(compose("org.jetbrains.compose.runtime:runtime-saveable"))

                implementation(libs.kotlinx.collections)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.voyager.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.data.episodeimages.testing)
                implementation(projects.data.seasondetails.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}