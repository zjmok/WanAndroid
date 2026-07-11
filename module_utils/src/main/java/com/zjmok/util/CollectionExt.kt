package com.zjmok.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun Collection<*>?.isNotNullAndNotEmpty(): Boolean {
    contract {
        // 告诉编译器，返回 true 时，调用者一定不会为 null，可以减少 `?` `!!` 的使用
        returns(true) implies (this@isNotNullAndNotEmpty != null)
    }
    return this.isNullOrEmpty().not()
}
