#include "maya.h"
#include "client_logger.h"

jint InitializeMaya() {
    // Comment or uncomment the following line to enable/disable ImGui rendering
    // RenderImGui will be called in eglSwapBuffers_hook if uncommented
    // RenderImGui(display, surface);

    // Perform hooking
    if (JNI_DoHooks() != JNI_OK) {
        return JNI_ERR;
    }

    ClientLog("Native","Maya","Logger Setup Success");
    return JNI_OK;
}