import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
}

/*
 * Copyright 2017-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

/*
 * Stream copied this script from kotlinx.serialization project
 */

kotlin {
    explicitApi()
    jvm {
        withJava()
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
    }
    jvmToolchain(8)

    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "10s"
                }
            }
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            sourceMap = true
            moduleKind = JsModuleKind.MODULE_UMD
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {

    }
    if (HostManager.hostIsMac) {
        // According to https://kotlinlang.org/docs/native-target-support.html
        // Tier 1
        macosX64()
        macosArm64()
        iosSimulatorArm64()
        iosX64()
        // Tier 2
        watchosSimulatorArm64()
        watchosX64()
        watchosArm32()
        watchosArm64()
        tvosSimulatorArm64()
        tvosX64()
        tvosArm64()
        iosArm64()
        // Tier 3
        watchosDeviceArm64()
    }

    // Tier 2
    linuxX64()
    linuxArm64()



    mingwX64()
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()



    // setup tests running in RELEASE mode
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.test(listOf(NativeBuildType.RELEASE))
    }
    targets.withType<KotlinNativeTargetWithTests<*>>().configureEach {
        testRuns.create("releaseTest") {
            setExecutionSourceFrom(binaries.getTest(NativeBuildType.RELEASE))
        }
    }
}