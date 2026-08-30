# module_debugtools proguard rules

# OkHttp 拦截器不被混淆
-keep class com.zjmok.debugtools.BaseUrlInterceptor { *; }
