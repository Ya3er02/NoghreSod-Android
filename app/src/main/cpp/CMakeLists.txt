cmake_minimum_required(VERSION 3.18.1)
project("noghresod_secure")

# C++ standard configuration
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Specify supported ABIs (critical for Android)
set(ANDROID_ABI armeabi-v7a arm64-v8a x86 x86_64)

# Create native library
add_library(noghresod_secure SHARED
    native-keys.cpp
)

# Link Android log library
find_library(log-lib log)
target_link_libraries(noghresod_secure ${log-lib})

# Set optimization flags
if(CMAKE_BUILD_TYPE STREQUAL "Release")
    target_compile_options(noghresod_secure PRIVATE -O3)
else()
    target_compile_options(noghresod_secure PRIVATE -g)
endif()

# Enable position-independent code for security
set_target_properties(noghresod_secure PROPERTIES
    POSITION_INDEPENDENT_CODE ON
)
