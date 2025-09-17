package org.example.wan.android.presentation.feature.web

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import org.example.wan.android.data.model.CacheData
import org.example.wan.android.data.model.WebPage
import org.example.wan.android.util.dateFormat
import org.example.wan.android.util.fromJson
import org.example.wan.android.util.logd
import org.example.wan.android.util.loge
import org.example.wan.android.util.toJson
import kotlinx.coroutines.flow.catch
import java.util.Date

class WebPageRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val HISTORY_KEY = stringPreferencesKey("history")
        val BOOKMARK_KEY = stringPreferencesKey("bookmark")
    }

    // 查询数据
    suspend fun searchWebPage(url: String, isBookmark: Boolean = false, result: (List<WebPage>) -> Unit) {
        try {
            dataStore.edit { data ->
//                data.clear()
//                return@edit
                // 读取当前的 WebPage 列表
                val key = if (isBookmark) org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.BOOKMARK_KEY else org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.HISTORY_KEY
                val json = data[key]
                val cacheData = fromJson<CacheData>(json)
                val currentList = cacheData?.pageList ?: emptyList()
/*
                logd(
                    "WebPage: currentList = ${
                        currentList.map {
                            "${it.time} [${it.title}](${it.url})"
                        }.toJson()
                    }"
                )
*/

                // 查询结果
                val resultList = currentList.filter { it.url == url }

                logd(
                    "WebPage: resultList = ${
                        resultList.map {
                            "${it.time} [${it.title}](${it.url})"
                        }.toJson()
                    }"
                )

                result(resultList)
            }
        } catch (e: Exception) {
            loge(e)
            result(emptyList())
        }
    }

    // 增加数据
    suspend fun addWebPage(url: String, title: String, author: String? = null, isBookmark: Boolean = false) {
        val time = dateFormat.format(Date())
        val webPage = WebPage(
            url = url,
            title = title,
            author = author,
            time = time,
        )
        try {
            dataStore.edit { data ->
//                data.clear()
//                return@edit
                // 读取当前的 WebPage 列表
                val key = if (isBookmark) org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.BOOKMARK_KEY else org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.HISTORY_KEY
                val json = data[key]
                val cacheData = fromJson<CacheData>(json)
                val currentList = cacheData?.pageList ?: emptyList()
/*
                logd(
                    "WebPage: currentList = ${
                        currentList.map {
                            "${it.time} [${it.title}](${it.url})"
                        }.toJson()
                    }"
                )
*/

                // 插入到前面
                val updatedList = currentList
                    .toMutableList()
                    .apply {
                        add(0, webPage)
                    }
                logd(
                    "WebPage: updatedList = ${
                        updatedList.map {
                            "${it.time} [${it.title}](${it.url})"
                        }.toJson()
                    }"
                )

                // 保存新的列表
                val newCacheData = CacheData(pageList = updatedList)
                data[key] = newCacheData.toJson() ?: ""
            }
        } catch (e: Exception) {
            loge(e)
        }
    }

    // 更新数据
    suspend fun updateWebPage(url: String, title: String, author: String? = null, isBookmark: Boolean = false) {
        val time = dateFormat.format(Date())
        val webPage = WebPage(
            url = url,
            title = title,
            author = author,
            time = time,
        )
        try {
            dataStore.edit { data ->
//                data.clear()
//                return@edit
                // 读取当前的 WebPage 列表
                val key = if (isBookmark) org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.BOOKMARK_KEY else org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.HISTORY_KEY
                val json = data[key]
                val cacheData = fromJson<CacheData>(json)
                val currentList = cacheData?.pageList ?: emptyList()
/*
                logd(
                    "WebPage: currentList = ${
                        currentList.map {
                            "${it.time} [${it.title}](${it.url})"
                        }.toJson()
                    }"
                )
*/

                // 过滤掉指定对象并插入到前面
                val updatedList = currentList.filterNot { it.url == webPage.url }
                    .toMutableList()
                    .apply {
                        add(0, webPage)
                    }
                logd(
                    "WebPage: updatedList = ${
                        updatedList.map {
                            "${it.time} [${it.title}](${it.url})"
                        }.toJson()
                    }"
                )

                // 保存新的列表
                val newCacheData = CacheData(pageList = updatedList)
                data[key] = newCacheData.toJson() ?: ""
            }
        } catch (e: Exception) {
            loge(e)
        }
    }

    // 删除数据
    suspend fun removeWebPageByUrl(url: String, isBookmark: Boolean = false) {
        dataStore.edit { data ->
            // 读取当前的 WebPage 列表
            val key = if (isBookmark) org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.BOOKMARK_KEY else org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.HISTORY_KEY
            val json = data[key]
            val cacheData = fromJson<CacheData>(json)
            val currentList = cacheData?.pageList ?: emptyList()
/*
            logd(
                "WebPage: currentList = ${
                    currentList.map {
                        "${it.time} [${it.title}](${it.url})"
                    }.toJson()
                }"
            )
*/

            // 过滤掉指定对象
            val updatedList = currentList.filterNot { it.url == url }
            logd(
                "WebPage: updatedList = ${
                    updatedList.map {
                        "${it.time} [${it.title}](${it.url})"
                    }.toJson()
                }"
            )

            // 保存新的列表
            val newCacheData = CacheData(pageList = updatedList)
            data[key] = newCacheData.toJson() ?: ""
        }
    }

    // 删除数据
    suspend fun removeWebPageByTitle(title: String, isBookmark: Boolean = false) {
        dataStore.edit { data ->
            // 读取当前的 WebPage 列表
            val key = if (isBookmark) org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.BOOKMARK_KEY else org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.HISTORY_KEY
            val json = data[key]
            val cacheData = fromJson<CacheData>(json)
            val currentList = cacheData?.pageList ?: emptyList()
/*
            logd(
                "WebPage: currentList = ${
                    currentList.map {
                        "${it.time} [${it.title}](${it.url})"
                    }.toJson()
                }"
            )
*/

            // 过滤掉指定对象
            val updatedList = currentList.filterNot { it.title == title }
            logd(
                "WebPage: updatedList = ${
                    updatedList.map {
                        "${it.time} [${it.title}](${it.url})"
                    }.toJson()
                }"
            )

            // 保存新的列表
            val newCacheData = CacheData(pageList = updatedList)
            data[key] = newCacheData.toJson() ?: ""
        }
    }

    // 读取数据 订阅模式
    suspend fun observeWebPageList(isBookmark: Boolean = false, listener: (List<WebPage>) -> Unit) {
        dataStore.data
            .catch {
                loge(it)
            }
            .collect { data ->
                // 读取当前的 WebPage 列表
                val key = if (isBookmark) org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.BOOKMARK_KEY else org.example.wan.android.presentation.feature.web.WebPageRepository.Companion.HISTORY_KEY
                val json = data[key]
                val cacheData = fromJson<CacheData>(json)
                val currentList = cacheData?.pageList ?: emptyList()
/*
                logd(
                    "WebPage: currentList = ${
                        currentList.map {
                            "${it.time} [${it.title}](${it.url})"
                        }.toJson()
                    }"
                )
*/

                listener.invoke(currentList)
            }
    }

}