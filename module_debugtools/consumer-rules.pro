# module_debugtools consumer rules

# 保持 DebugBaseUrl 不被混淆（SharedPreferences 反射需要）
-keep class com.zjmok.debugtools.DebugBaseUrl { *; }

# 保持 DebugFloatService 不被混淆（子类继承需要）
-keep class com.zjmok.debugtools.DebugFloatService { *; }
