package com.example.wan.android.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * 这里更推荐使用 let、takeNotNull
 * ```
 * Any?.let { }
 *
 * Any.takeNotNull { }
 * ```
 */
@OptIn(ExperimentalContracts::class)
fun Any?.isNotNull(): Boolean {
    contract {
        // 告诉编译器，返回 true 时，调用者一定不会为 null，可以减少 `?` `!!` 的使用
        returns(true) implies (this@isNotNull != null)
    }
    return this != null
}

@OptIn(ExperimentalContracts::class)
fun Any?.isNull(): Boolean {
    contract {
        // 告诉编译器，返回 false 时，调用者一定不会为 null，可以减少 `?` `!!` 的使用
        returns(false) implies (this@isNull != null)
    }
    return this == null
}

/**
 * this != null 时执行 block
 */
@OptIn(ExperimentalContracts::class)
fun <T, R> T?.takeNotNull(block: (T) -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (this == null) {
        return null
    }
    return block(this)
}

/**
 * this == null 时执行 block
 */
@OptIn(ExperimentalContracts::class)
fun <R> Any?.takeNull(block: () -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (this != null) {
        return null
    }
    return block()
}
