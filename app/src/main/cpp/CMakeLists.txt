# CMake configuration for NoghreSod native code (NDK)
# Compiles C++ code for secure key storage

cmake_minimum_required(VERSION 3.18.1)
project("noghresod_keys" VERSION 1.0.0)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility=hidden -fvisibility-inlines-hidden")

# Add OpenSSL for encryption
find_package(OpenSSL REQUIRED)

# Source files for native library
set(NOGHRESOD_NATIVE_SOURCES
    src/keys.cpp
    src/obfuscation.cpp
    src/encryption.cpp
    src/device_binding.cpp
)

# Create shared library
add_library(
    noghresod_keys
    SHARED
    ${NOGHRESOD_NATIVE_SOURCES}
)

# Include directories
target_include_directories(noghresod_keys PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/include)

# Link OpenSSL
target_link_libraries(noghresod_keys PRIVATE OpenSSL::Crypto)

# Link Android log library
find_library(log-lib log)
target_link_libraries(noghresod_keys PRIVATE ${log-lib})

# Enable Link Time Optimization (LTO) for better obfuscation
if(CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    target_compile_options(noghresod_keys PRIVATE -flto)
    target_link_options(noghresod_keys PRIVATE -flto)
endif()

# Strip symbols from release build
if(CMAKE_BUILD_TYPE MATCHES Release)
    target_link_options(noghresod_keys PRIVATE -Wl,--strip-all)
endif()
