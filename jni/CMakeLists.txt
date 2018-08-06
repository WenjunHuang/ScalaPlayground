cmake_minimum_required(VERSION 3.8)
project(jni)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/target/native/libs")
find_package(Java REQUIRED)
find_package(Jni REQUIRED)
set(BOOST_ROOT $ENV{BOOST_ROOT})
find_package(Boost REQUIRED
        COMPONENTS filesystem system thread coroutine)

include_directories(${Boost_INCLUDE_DIRS})
include_directories(${JNI_INCLUDE_DIRS})
include_directories(./target/native/include)

file(GLOB SOURCE_FILES src/main/cpp/*.cpp)
add_library(${PROJECT_NAME} SHARED ${SOURCE_FILES})
