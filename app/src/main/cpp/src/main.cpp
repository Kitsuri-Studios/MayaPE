#include "maya.h"
#include "misc/client_logger.h"


jint InitializeMaya() {
    if (JNI_DoHooks() != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_OK;
}
