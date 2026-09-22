package com.operit.spike

import android.app.Activity
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Spike 1 验证目标：
 * 1. compose:true 触发编译器插件对 @Composable 的变换（不依赖 UI 运行）
 * 2. 树内 androidx.compose.runtime_runtime 依赖解析
 *
 * 仅依赖 runtime 单库，刻意不引 foundation/material，
 * 让失败面收敛到插件链路本身而不是 UI 依赖图。
 */
class SpikeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

@Composable
fun SpikeContent(initial: Int): Int {
    val state = remember { mutableStateOf(initial) }
    return state.value
}
