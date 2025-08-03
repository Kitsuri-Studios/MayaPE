#include "maya.h"

jint InitializeMaya() {
    // Comment or uncomment the following line to enable/disable ImGui rendering
    // RenderImGui will be called in eglSwapBuffers_hook if uncommented
    // RenderImGui(display, surface);

    // Perform hooking
    if (JNI_DoHooks() != JNI_OK) {
        return JNI_ERR;
    }

    return JNI_OK;
}