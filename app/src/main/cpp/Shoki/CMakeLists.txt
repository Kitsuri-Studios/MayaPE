cmake_minimum_required(VERSION 3.22.1)
project("Shoki")
add_library(Shoki SHARED
        logger.cpp
        jni_bridge.cpp
        ElfTool.cpp
)
target_link_libraries(Shoki android log)
