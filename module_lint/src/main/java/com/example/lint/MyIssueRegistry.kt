package com.example.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.example.lint.detector.GlideDetector
import com.example.lint.detector.LogDetector
import com.example.lint.detector.ToastDetector
import com.example.lint.detector.UtilcodeToastDetector

// 在 build.gradle 使用
@Suppress("unused", "UnstableApiUsage")
class MyIssueRegistry : IssueRegistry() {

    // 3.x
//    override val vendor: Vendor = Vendor(
//        vendorName = "zjmok",
//        feedbackUrl = "https://github.com/zjmok/WanAndroid/issues",
//        contact = "dev.mzx@gmail.com"
//    )
//
    override val issues: List<Issue> = listOf(
        GlideDetector.ISSUE,
        LogDetector.ISSUE,
        ToastDetector.ISSUE,
        UtilcodeToastDetector.ISSUE,
    ) // 将所有自定义 Lint 的 ISSUE 列出

}