package com.example.wan.android.util

/**
 * kotlin 1.8.22 缺少了这个比较
 */
public fun <T> compareByDescending(vararg selectors: (T) -> Comparable<*>?): Comparator<T> {
    require(selectors.size > 0)
    return Comparator { a, b -> compareValuesByImpl(b, a, selectors) }
}

private fun <T> compareValuesByImpl(
    a: T,
    b: T,
    selectors: Array<out (T) -> Comparable<*>?>
): Int {
    for (fn in selectors) {
        val v1 = fn(a)
        val v2 = fn(b)
        val diff = compareValues(v1, v2)
        if (diff != 0) return diff
    }
    return 0
}
