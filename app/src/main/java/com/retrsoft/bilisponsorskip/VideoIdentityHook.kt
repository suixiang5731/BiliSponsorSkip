package com.retrsoft.bilisponsorskip

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Proxy

internal class VideoIdentityHook(
    private val classLoader: ClassLoader,
    private val controller: SkipController,
) {
    fun install() {
        val handlerClass = XposedHelpers.findClassIfExists(MOSS_HANDLER_CLASS, classLoader)
        var hookedMethodCount = 0
        BilibiliCompatibility.videoIdentityHookTargets.forEach { target ->
            val mossClass = XposedHelpers.findClassIfExists(target.className, classLoader)
                ?: return@forEach
            target.methodNames.forEach { methodName ->
                hookedMethodCount += hookRequestMethod(mossClass, methodName, handlerClass)
            }
        }
        if (hookedMethodCount == 0) error("no supported Bilibili video identity method found")
    }

    private fun hookRequestMethod(
        mossClass: Class<*>,
        methodName: String,
        handlerClass: Class<*>?,
    ): Int {
        val methods = mossClass.declaredMethods.filter { it.name == methodName }
        if (methods.isEmpty()) {
            Log.d("$methodName is not present in this Bilibili version")
            return 0
        }

        methods.forEach { method ->
            XposedBridge.hookMethod(method, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    param.args.firstOrNull()?.let(::readRequest)
                    if (handlerClass != null) {
                        val handlerIndex = param.args.indexOfFirst { argument ->
                            argument != null && handlerClass.isInstance(argument)
                        }
                        if (handlerIndex >= 0) {
                            val original = param.args[handlerIndex] ?: return
                            param.args[handlerIndex] = wrapResponseHandler(original, handlerClass)
                        }
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    param.result?.let(::readResponse)
                }
            })
        }
        Log.d("hooked ${mossClass.simpleName}.$methodName (${methods.size} overload(s))")
        return methods.size
    }

    private fun readRequest(request: Any) {
        val vod = request.callOrNull("getVod")
        val cid = vod?.longOrNull("getCid") ?: request.longOrNull("getCid") ?: return
        val directBvid = request.stringOrNull("getBvid").orEmpty()
        val aid = vod?.longOrNull("getAid") ?: request.longOrNull("getAid")
        val bvid = directBvid.takeIf(String::isNotBlank)
            ?: aid?.takeIf { it > 0 }?.let(BvId::fromAid)
            ?: return
        controller.updateVideo(bvid, cid.toString())
    }

    private fun readResponse(response: Any?) {
        val playArc = response?.callOrNull("getPlayArc") ?: return
        val cid = playArc.longOrNull("getCid") ?: return
        val bvid = playArc.stringOrNull("getBvid")?.takeIf(String::isNotBlank)
            ?: playArc.longOrNull("getAid")?.takeIf { it > 0 }?.let(BvId::fromAid)
            ?: return
        controller.updateVideo(bvid, cid.toString())
    }

    private fun wrapResponseHandler(original: Any, handlerClass: Class<*>): Any =
        Proxy.newProxyInstance(classLoader, arrayOf(handlerClass)) { _, method, args ->
            if (method.name == "onNext") readResponse(args?.firstOrNull())
            if (args == null) method.invokeUnwrapped(original) else method.invokeUnwrapped(original, *args)
        }

    private companion object {
        const val MOSS_HANDLER_CLASS = "com.bilibili.lib.moss.api.MossResponseHandler"
    }
}
