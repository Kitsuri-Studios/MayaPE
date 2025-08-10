//
// Created by Lodingglue on 8/11/2025.
//


#include "logger.h"
#include <jni.h>

extern JavaVM* g_vm;
extern jclass g_nativeManagerClass;
extern jmethodID g_logFromNativeMethod;

__attribute__((visibility("default")))
extern "C"
void ClientLog(const char* threadName, const char* tag, const char* message) {
    if (!g_vm || !g_nativeManagerClass || !g_logFromNativeMethod) return;

    JNIEnv* env = nullptr;
    bool didAttach = false;

    if (g_vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        if (g_vm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            return;
        }
        didAttach = true;
    }

    jstring jThreadName = env->NewStringUTF(threadName);
    jstring jTag = env->NewStringUTF(tag);
    jstring jMessage = env->NewStringUTF(message);

    env->CallStaticVoidMethod(g_nativeManagerClass, g_logFromNativeMethod, jThreadName, jTag, jMessage);

    env->DeleteLocalRef(jThreadName);
    env->DeleteLocalRef(jTag);
    env->DeleteLocalRef(jMessage);

    if (didAttach) {
        g_vm->DetachCurrentThread();
    }
}

