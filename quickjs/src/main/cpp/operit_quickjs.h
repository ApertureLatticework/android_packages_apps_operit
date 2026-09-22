#pragma once
// 树内（Soong）编译用：Gradle 侧由 quickjs/src/main/cpp/CMakeLists.txt
// 在 build 目录生成同名头并指向 git fetch 的源；本静态头指向包内收源副本。
#include "quickjs-upstream/quickjs.h"
