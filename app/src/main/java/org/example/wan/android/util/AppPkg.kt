package org.example.wan.android.util

enum class AppPkg(val pkg: String, val app: String) {

    WanAndroid("org.example.wan.android", "WanAndroid"), // 本 APP, test 自定义 Scheme

    WeChat("com.tencent.mm", "微信"), // 适配打开 weixin 协议

    // 以下是浏览器 可适配打开 http(s) 协议
    Browser("com.android.browser", "浏览器"),
    Chrome("com.android.chrome", "Chrome"),
    Edge("com.microsoft.emmx", "Edge"),
    Firefox("org.mozilla.firefox", "Firefox"),
    XBrowser("com.mmbox.xbrowser", "X浏览器"),
    Via("mark.via", "Via"),
    Quark("com.quark.browser", "夸克"),
    Oupeng("com.oupeng.mini.android", "欧朋浏览器"), // Opera mini 的中国定制版
    UCMobile("com.UCMobile", "UC浏览器"),
    UCMobileLite("com.ucmobile.lite", "UC浏览器极速版"),
    UCMobileIntl("com.ucmobile.intl", "UC浏览器国际版"),
    QQBrowser("com.tencent.mtt", "QQ浏览器"),
    Baidu("com.baidu.searchbox", "百度"),
    BaiduLite("com.baidu.searchbox.lite", "百度极速版"),
    BaiduBrowser("com.baidu.browser.apps", "百度浏览器"),
    QihooBrowser360("com.qihoo.browser", "360浏览器"),
    QihooContents360("com.qihoo.contents", "360极速浏览器"),

    // 以下是应用商店
    GooglePlay("com.android.vending", "GooglePlay"),
    ApkPure("com.apkpure.aegon", "ApkPure"),
    FDroid("org.fdroid.fdroid", "F-Droid"),
    CoolApk("com.coolapk.market", "酷安"),
    QQMarket("com.tencent.android.qqdownloader", "QQ应用宝"),
    HuaweiMarket("com.huawei.appmarket", "华为应用商店"),
    XiaomiMarket("com.xiaomi.market", "小米应用商店"),
    OppoMarket("com.heytap.market", "OPPO应用商店"),
    OppoMarketOld("com.oppo.market", "OPPO应用商店-旧版"),
    VivoMarket("com.bbk.appstore", "VIVO应用商店"),

    // 以下是邮件应用
    Email("com.android.email", "Email"),
    Gmail("com.google.android.gm", "Gmail"),
    MailRu("ru.mail.mailapp", "MailRu"),
    Outlook("com.microsoft.office.outlook", "Outlook"),
    YahooMail("com.yahoo.mobile.client.android.mail", "Yahoo Mail"),
    BlueMail("me.bluemail.mail", "BlueMail"),
    ProtonMail("ch.protonmail.android", "ProtonMail"),
    Spark("com.readdle.spark", "Spark Email"),
    SamsungEmail("com.samsung.android.email.provider", "Samsung Email"),
    EdisonMail("com.easilydo.mail", "Edison Mail"),
    NineEmail("com.ninefolders.hd3", "Nine Email"),
    AquaMail("org.kman.AquaMail", "Aqua Mail"),
    NetEaseMail("com.netease.mobimail", "网易邮箱"),
    NetEaseMail2("com.netease.mail", "网易邮箱"),
    QQMail("com.tencent.androidqqmail", "QQ邮箱"),
    AliyunMail("com.alibaba.ailabs.tg", "阿里云邮箱"),
    SinaMail("com.sina.mail", "新浪邮箱"),
    Mail139("cn.cj.pe", "139邮箱"),

    // Others
    LibChecker("com.absinthe.libchecker", "LibChecker"), // 这个不是应用商店 但他适配了 market 协议
    Termux("com.termux", "Termux"),

}