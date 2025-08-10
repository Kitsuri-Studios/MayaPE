//
// Created by mrjar on 8/11/2025.
//

#ifndef CLIENT_LOGGER_H
#define CLIENT_LOGGER_H

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Logs a message from native code to the Java side.
 * Requires that JNI_OnLoad has run, or that the init function has been called manually.
 *
 * @param threadName Name of the thread sending the log.
 * @param tag        Log tag (like Android Log tags).
 * @param message    Log message content.
 */
__attribute__((visibility("default")))
void ClientLog(const char* threadName, const char* tag, const char* message);

/**
 * Optional: If the host Lib doesn’t trigger JNI_OnLoad,
 * you can call this manually to set up the logger.
 *
 * @param vm  Pointer to JavaVM instance.
 * @return 0 on success, non-zero on error.
 */
__attribute__((visibility("default")))
int ClientLog_Init(void* vm);

#ifdef __cplusplus
}
#endif

#endif // CLIENT_LOGGER_H
