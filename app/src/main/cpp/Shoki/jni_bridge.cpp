//
// Created by Lodingglue on 8/11/2025.
//

#include <jni.h>

JavaVM* g_vm = nullptr;
jclass g_nativeManagerClass = nullptr;
jmethodID g_logFromNativeMethod = nullptr;

extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* /*reserved*/) {
    g_vm = vm;
    JNIEnv* env = nullptr;

    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass localClass = env->FindClass("io/kitsuri/mayape/manager/NativeManager");
    if (!localClass) {
        return JNI_ERR;
    }

    g_nativeManagerClass = reinterpret_cast<jclass>(env->NewGlobalRef(localClass));
    env->DeleteLocalRef(localClass);

    g_logFromNativeMethod = env->GetStaticMethodID(
            g_nativeManagerClass,
            "logFromNative",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
    );
    if (!g_logFromNativeMethod) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
