//
// Created by Lodingglue on 8/11/2025.
//

#ifndef LOGGER_H
#define LOGGER_H

#ifdef __cplusplus
extern "C" {
#endif

__attribute__((visibility("default")))
void ClientLog(const char* threadName, const char* tag, const char* message);

#ifdef __cplusplus
}
#endif

#endif // LOGGER_H
