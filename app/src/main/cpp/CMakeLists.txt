cmake_minimum_required(VERSION 3.10)
project(NoghreSod)

set(CMAKE_CXX_STANDARD 17)

add_library(
    noghresod-native
    SHARED
    native-keys.cpp
)

target_include_directories(
    noghresod-native
    PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
)