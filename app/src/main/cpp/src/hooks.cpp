//
// Created by mrjar on 8/2/2025.
//
#include "maya.h"

eglSwapBuffers_t eglSwapBuffers_original = nullptr;

EGLBoolean eglSwapBuffers_hook(EGLDisplay display, EGLSurface surface) {
    RenderImGui(display, surface);
    return eglSwapBuffers_original(display, surface);
}

jint JNI_DoHooks() {
    void* eglHandle = dlopen("libEGL.so", RTLD_NOW);
    if (!eglHandle) {
        LOGE("Failed to open libEGL.so");
        return JNI_ERR;
    }

    void* sym = dlsym(eglHandle, "eglSwapBuffers");
    if (!sym) {
        LOGE("Failed to find eglSwapBuffers symbol");
        return JNI_ERR;
    }
    eglSwapBuffers_original = (eglSwapBuffers_t)sym;

    A64HookFunction((void*)eglSwapBuffers_original, (void*)eglSwapBuffers_hook, (void**)&eglSwapBuffers_original);
    __builtin___clear_cache((char*)eglSwapBuffers_original, (char*)eglSwapBuffers_original + sizeof(void*));


    LOGI("Hooked eglSwapBuffers successfully");
    return JNI_OK;
}