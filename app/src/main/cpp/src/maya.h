//
// Created by mrjar on 8/2/2025.
//
#ifndef MAYA_H
#define MAYA_H

#include <android/log.h>
#include <EGL/egl.h>
#include <dlfcn.h>
#include <dobby.h>
#include <jni.h>
#include <string>

#include "../include/imgui/imgui.h"
#include "../include/imgui/backends/imgui_impl_opengl3.h"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "Maya", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "Maya", __VA_ARGS__)

#define MINECRAFTPE_L "libminecraftpe.so"
#define HXO_L "libhxo.so"
#define EGL_L "libEGL.so"

typedef EGLBoolean(*eglSwapBuffers_t)(EGLDisplay display, EGLSurface surface);
extern eglSwapBuffers_t eglSwapBuffers_original;
extern bool g_ImGuiInitialized;

// Functions from imgui_setup.cpp
void InitImGui();
void RenderImGui(EGLDisplay display, EGLSurface surface);

// Functions from hooks.cpp
EGLBoolean eglSwapBuffers_hook(EGLDisplay display, EGLSurface surface);
jint JNI_DoHooks();

// Function from main.cpp
jint InitializeMaya();

#endif // MAYA_H