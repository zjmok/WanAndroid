package com.zjmok.util

import java.net.URLEncoder

/**
 * 将 Map 转换为查询字符串。
 * 例如：mapOf("name" to "Android Google", "age" to "999") 转换为 "name=Android%20Google&age=999"。
 *
 * @param encode 是否对键和值进行 URL 编码。默认为 true，在绝大多数情况下都应启用。
 * @return 构造好的查询字符串。如果 Map 内容为空，则返回空字符串 ""。
 */
fun Map<String, String>.toParamsString(encode: Boolean = true): String {
    // 处理空值 Map
    if (this.isEmpty()) return ""

    return this.entries
        .joinToString("&") { (key, value) ->
            // 关键步骤：对键和值进行 URL 编码，确保特殊字符（如 &, %, 空格, 中文）被正确转义
            val encodedKey = if (encode) URLEncoder.encode(key, "UTF-8") else key
            val encodedValue = if (encode) URLEncoder.encode(value, "UTF-8") else value
            "$encodedKey=$encodedValue" // 拼接成 "key=value" 格式
        }
}
