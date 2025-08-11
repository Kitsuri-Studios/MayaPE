cmake_minimum_required(VERSION 3.22.1)
project("mc")

set(CMAKE_CXX_STANDARD 23)

file(GLOB IMGUI_SOURCES
        include/imgui/*.cpp
        include/imgui/backends/imgui_impl_opengl3.cpp
)

file(GLOB AND64_SOURCES
        src/libs/And64InlineHook/*.cpp
)

add_library(${CMAKE_PROJECT_NAME} SHARED
        src/main.cpp
        src/hooks.cpp
        src/imgui_setup.cpp
        src/jni_main.cpp
        ${IMGUI_SOURCES}
        ${AND64_SOURCES}
)

add_subdirectory(hxo-loader)
add_subdirectory(Shoki)

target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        include
        src/libs/And64InlineHook
)

target_link_libraries(${CMAKE_PROJECT_NAME} PRIVATE
        android
        EGL
        GLESv3
        log
        Shoki
)

target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
        IMGUI_IMPL_OPENGL_ES3
)
