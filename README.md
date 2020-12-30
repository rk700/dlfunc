dlfunc
----------------

[![Build Status](https://github.com/rk700/dlfunc/workflows/Android%20CI/badge.svg)](https://github.com/rk700/dlfunc/actions)
[![Maven](https://badgen.net/maven/v/metadata-url/https/dl.bintray.com/rk700/maven/io/github/rk700/dlfunc/maven-metadata.xml)](https://dl.bintray.com/rk700/maven/io/github/rk700/dlfunc/)

## Introduction

Dynamic linking functions(`dlopen`, `dlsym`) in recent Android versions are restricted with caller address checked. This project can be used as a workaround by forging caller address. 

The basic idea is to simply set `dlopen`/`dlsym` as JNI functions, which are actually called by trampoline code in `libart.so` and can be used to trick the linker.

In this way, neither `/proc/self/maps` file nor ELF parsing is needed, and hopefully symbol hash table could be utilized during resolution for faster symbol lookup.


## Setup

The library is built with the new feature of Android Gradle Plugin for [native code dependency](https://developer.android.com/studio/build/native-dependencies), which would add [prefab](https://google.github.io/prefab/) modules into the `.aar` file.

To use the library, first make sure that Android Gradle Plugin version 4.0+ is used. Then add the library as a dependency:

```
implementation 'io.github.rk700:dlfunc:0.1.0'
```

Put the following lines into the android block of the module's `build.gradle` file to enable prefab:

```
    buildFeatures {
        prefab true
    }
```

In `CMakeLists.txt` file, add the following lines to expose the `dlfunc` library to native code:

```
find_package(dlfunc REQUIRED CONFIG)

target_link_libraries( # Specifies the target library.
                       app

                       # Links the dlfunc library to the target library.
                       dlfunc::dlfunc
)
```

Android NDK [sample for prefab](https://github.com/android/ndk-samples/tree/master/prefab/prefab-dependency) also provides an example for importing a prefab library.

## Usage

First, include the header file `dlfunc.h` in the native code:

```c
#include "dlfunc.h"
```

Then run the function `dlfunc_init` for initialization.

```c
    if(dlfunc_init(env) != JNI_OK) {
        LOGE("dlfunc init failed");
        return;
    }
```

Now just call `dlfunc_dlopen` and `dlfunc_dlsym` simply as calling `dlopen`/`dlsym`, except that the `JNIEnv *env` is passed in as the first argument:

```c
    void *handle = dlfunc_dlopen(env, "libart.so", RTLD_LAZY);
    LOGI("libart handle is %p", handle);
    if(handle != NULL) {
        void *ptr = dlfunc_dlsym(env, handle, "MterpCheckBefore");
        LOGI("MterpCheckBefore is at %p", ptr);
    }
```
## License

`dlfunc` is distributed under Apache License 2.0.
