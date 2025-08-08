cmake_minimum_required(VERSION 3.22.1)
project("mc")

set(CMAKE_CXX_STANDARD 23)

# ImGui sources (from cpp/include/imgui/)
file(GLOB IMGUI_SOURCES
        include/imgui/*.cpp
        include/imgui/backends/imgui_impl_opengl3.cpp  # Only GLES3 backend
)

add_library(${CMAKE_PROJECT_NAME} SHARED
        src/main.cpp
        src/hooks.cpp
        src/imgui_setup.cpp
        src/jni_main.cpp
        ${IMGUI_SOURCES}
)

add_subdirectory(hxo-loader)
add_subdirectory(ELFChecker)
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        include  # Points to cpp/include/
)

# Dobby hooking library
include(cmake/CPM.cmake)
CPMAddPackage(
        NAME Dobby
        GIT_REPOSITORY https://github.com/Lodingglue/Dobby
        GIT_TAG        master
)

target_link_libraries(${CMAKE_PROJECT_NAME} PRIVATE
        android
        dobby
        EGL
        GLESv3  # MCPE uses GLES3
        log
)

# Explicitly enable GLES3 for ImGui
target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
        IMGUI_IMPL_OPENGL_ES3
)