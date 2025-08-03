package com.mojang.minecraftpe

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import java.io.File
import java.lang.reflect.Method


@SuppressLint("MissingSuperCall")
class Launcher : MainActivity() {
    @SuppressLint("DiscouragedPrivateApi")
    override fun onCreate(bundle: Bundle?) {
        try {
            val addAssetPath: Method = assets.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
            intent.getStringArrayListExtra("APKS")?.forEach { addAssetPath.invoke(assets, it) }
            super.onCreate(bundle)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    init {
        System.loadLibrary("mc")
    }
}

