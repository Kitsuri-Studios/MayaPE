cmake_minimum_required(VERSION 3.22.1)
project("ElfHelper")
add_library(ElfHelper SHARED ElfTool.cpp)
target_link_libraries(ElfHelper android log)
