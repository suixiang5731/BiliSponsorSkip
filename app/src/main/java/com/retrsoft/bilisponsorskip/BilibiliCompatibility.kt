package com.retrsoft.bilisponsorskip

internal data class VideoIdentityHookTarget(
    val className: String,
    val methodNames: Set<String>,
)

internal object BilibiliCompatibility {
    val videoIdentityHookTargets = listOf(
        VideoIdentityHookTarget(
            className = "com.bapis.bilibili.app.playerunite.v1.PlayerMoss",
            methodNames = setOf("executePlayViewUnite", "playViewUnite"),
        ),
        VideoIdentityHookTarget(
            className = "com.bapis.bilibili.app.playurl.v1.PlayURLMoss",
            methodNames = setOf("playView", "playURL"),
        ),
    )

    private val playerProgressClassNames = setOf(
        "com.bilibili.playerbizcommon.widget.control.PlayerSeekWidget",
        "com.bilibili.playerbizcommon.widget.control.seekbar.PlayerSeekWidget2",
        "com.bilibili.playerbizcommonv2.widget.seek.PlayerSeekWidget",
        "com.bilibili.playerbizcommonv2.widget.seek.PlayerSeekWidget2",
        "com.bilibili.playerbizcommonv2.widget.seek.v3.PlayerSeekWidget3",
        "com.bilibili.video.story.view.StorySeekBar",
    )

    fun isPlayerProgressClass(className: String): Boolean = className in playerProgressClassNames
}
