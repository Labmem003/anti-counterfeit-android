# anti-counterfeit-android
模拟器检测，多开检测，Hook检测，Root检测。


4个Java类，直接拷贝使用即可。
## 模拟器检测
 boolean isEmulator = EmulatorDetector.isEmulator();
## 多开检测
 boolean isVirtual = CheckVirtual.isRunInVirtual(context);
## Hook检测
 boolean isHook = CheckHook.isHook(context);
## Root检测
 boolean isRoot = CheckRoot.isDeviceRooted();
 
 
 参考：
 
 [Android虚拟机多开检测](https://www.jianshu.com/p/216d65d9971e)
 
 [Android Java层的anti-hooking技巧](http://www.droidsec.cn/android-java%E5%B1%82%E7%9A%84anti-hooking%E6%8A%80%E5%B7%A7/)
 
 [Android反调试之 AntiEmulator 检测安卓模拟器](http://blog.csdn.net/earbao/article/details/51455564)
