//
// Created by mrjar on 8/2/2025.
//

#include "maya.h"

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    if (!dlopen(MINECRAFTPE_L, RTLD_LAZY)) {
        LOGE("Failed to dlopen %s", MINECRAFTPE_L);
        return JNI_ERR;
    }

    if (!dlopen(HXO_L, RTLD_LAZY)) {
        LOGE("Failed to dlopen %s", HXO_L);
        return JNI_ERR;
    }

    if (!dlopen(EGL_L, RTLD_LAZY)) {
        LOGE("Failed to dlopen %s", EGL_L);
        return JNI_ERR;
    }

    if (InitializeMaya() != JNI_OK) {
        return JNI_ERR;
    }

    LOGI("MAya loaded successfully");
    return JNI_VERSION_1_6;
}
