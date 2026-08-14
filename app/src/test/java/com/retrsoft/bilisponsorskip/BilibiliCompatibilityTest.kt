package com.retrsoft.bilisponsorskip

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BilibiliCompatibilityTest {
    @Test
    fun includesLegacy732VideoIdentityEndpoints() {
        val legacy = BilibiliCompatibility.videoIdentityHookTargets.single {
            it.className == "com.bapis.bilibili.app.playurl.v1.PlayURLMoss"
        }

        assertEquals(setOf("playView", "playURL"), legacy.methodNames)
    }

    @Test
    fun recognizesLegacy732PlayerSeekWidgets() {
        val legacyClassNames = listOf(
            "com.bilibili.playerbizcommon.widget.control.PlayerSeekWidget",
            "com.bilibili.playerbizcommon.widget.control.seekbar.PlayerSeekWidget2",
            "com.bilibili.playerbizcommonv2.widget.seek.PlayerSeekWidget",
            "com.bilibili.playerbizcommonv2.widget.seek.PlayerSeekWidget2",
        )

        legacyClassNames.forEach { className ->
            assertTrue(className, BilibiliCompatibility.isPlayerProgressClass(className))
        }
        assertTrue(
            BilibiliCompatibility.isPlayerProgressClass(
                "com.bilibili.playerbizcommonv2.widget.seek.v3.PlayerSeekWidget3",
            ),
        )
    }
}
