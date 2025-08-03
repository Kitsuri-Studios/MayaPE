//
// Created by mrjar on 8/2/2025.
//
#include "maya.h"

eglSwapBuffers_t eglSwapBuffers_original = nullptr;

EGLBoolean eglSwapBuffers_hook(EGLDisplay display, EGLSurface surface) {
    if (eglGetCurrentDisplay() != display || eglGetCurrentSurface(EGL_DRAW) != surface) {
        LOGE("EGL context mismatch — skipping ImGui render");
        return eglSwapBuffers_original(display, surface);
    }

    // Comment or uncomment this line in main.cpp to enable/disable ImGui
    RenderImGui(display, surface);

    return eglSwapBuffers_original(display, surface);
}

jint JNI_DoHooks() {
    void *eglSwapBuffers_sym = DobbySymbolResolver(EGL_L, "eglSwapBuffers");
    if (!eglSwapBuffers_sym) {
        LOGE("Failed to resolve eglSwapBuffers symbol");
        return JNI_ERR;
    }

    if (DobbyHook(eglSwapBuffers_sym,
                  reinterpret_cast<void *>(eglSwapBuffers_hook),
                  reinterpret_cast<void **>(&eglSwapBuffers_original)) != 0) {
        LOGE("Failed to hook eglSwapBuffers");
        return JNI_ERR;
    }

    LOGI("Successfully hooked eglSwapBuffers");
    return JNI_OK;
}