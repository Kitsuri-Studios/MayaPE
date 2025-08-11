#include "maya.h"


bool g_ImGuiInitialized = false;


void InitImGui() {
    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGui_ImplOpenGL3_Init("#version 300 es");
    ImGui::StyleColorsDark();
    g_ImGuiInitialized = true;
    LOGI("ImGui initialized (GLES3 mode)");
}

void RenderImGui(EGLDisplay display, EGLSurface surface) {
    if (!g_ImGuiInitialized) {
        InitImGui();
    }

    EGLint width, height;
    eglQuerySurface(display, surface, EGL_WIDTH, &width);
    eglQuerySurface(display, surface, EGL_HEIGHT, &height);

    ImGuiIO& io = ImGui::GetIO();
    io.DisplaySize = ImVec2((float)width, (float)height);

    ImGui_ImplOpenGL3_NewFrame();
    ImGui::NewFrame();
    ImGui::Begin("Hooked");
    ImGui::End();
    ImGui::Render();
    ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
}




