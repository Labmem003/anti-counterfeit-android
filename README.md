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
 
## AccessibilityService检测
TODO...

 ------------------------------
 
 近两年，Android端的虚拟化技术和群控技术发展急速，带来很多好玩产品和便利工具。但是作为App开发者就头疼了，恶意用户（比如不文明用户、比如刷单）利用这些技术，作恶门槛低得不知道哪里去。我们需要思考怎么识别和防御了。
下文介绍一些简单但是有效的恶意用户识别（方便后续封号）方案。
#  Anti 模拟器
这个很容易理解，模拟出来的机器，每次模拟的时候生成的设备ID，只存在模拟器使用的生命周期里。可能下一次模拟时又不一样了。
应对方法：主要是检测运行模拟器的一些特征，比如驱动文件，Build类内的硬件讯息等。
比如Build类内有模拟器的字串，明显就是模拟器：
```java
 public static boolean isEmulatorAbsoluly() {
        if (Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("sdk_x86") ||
                Build.PRODUCT.contains("sdk_google") ||
                Build.PRODUCT.contains("Andy") ||
                Build.PRODUCT.contains("Droid4X") ||
                Build.PRODUCT.contains("nox") ||
                Build.PRODUCT.contains("vbox86p")) {
            return true;
        }
        if (Build.MANUFACTURER.equals("Genymotion") ||
                Build.MANUFACTURER.contains("Andy") ||
                Build.MANUFACTURER.contains("nox") ||
                Build.MANUFACTURER.contains("TiantianVM")) {
            return true;
        }
        if (Build.BRAND.contains("Andy")) {
            return true;
        }
        if (Build.DEVICE.contains("Andy") ||
                Build.DEVICE.contains("Droid4X") ||
                Build.DEVICE.contains("nox") ||
                Build.DEVICE.contains("vbox86p")) {
            return true;
        }
        if (Build.MODEL.contains("Emulator") ||
                Build.MODEL.equals("google_sdk") ||
                Build.MODEL.contains("Droid4X") ||
                Build.MODEL.contains("TiantianVM") ||
                Build.MODEL.contains("Andy") ||
                Build.MODEL.equals("Android SDK built for x86_64") ||
                Build.MODEL.equals("Android SDK built for x86")) {
            return true;
        }
        if (Build.HARDWARE.equals("vbox86") ||
                Build.HARDWARE.contains("nox") ||
                Build.HARDWARE.contains("ttVM_x86")) {
            return true;
        }
        if (Build.FINGERPRINT.contains("generic/sdk/generic") ||
                Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                Build.FINGERPRINT.contains("Andy") ||
                Build.FINGERPRINT.contains("ttVM_Hdragon") ||
                Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
                Build.FINGERPRINT.contains("vbox86p") ||
                Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
            return true;
        }
        return false;
    }
```
还有的特征只是疑似，但不确定，对于这些特征，可以集合起来做一个疑似度评分，评分达到一定程度就标记为模拟器：
```java
     int newRating = 0;
        if (rating < 0) {
            if (Build.PRODUCT.contains("sdk") ||
                    Build.PRODUCT.contains("Andy") ||
                    Build.PRODUCT.contains("ttVM_Hdragon") ||
                    Build.PRODUCT.contains("google_sdk") ||
                    Build.PRODUCT.contains("Droid4X") ||
                    Build.PRODUCT.contains("nox") ||
                    Build.PRODUCT.contains("sdk_x86") ||
                    Build.PRODUCT.contains("sdk_google") ||
                    Build.PRODUCT.contains("vbox86p")) {
                newRating++;
            }

            if (Build.MANUFACTURER.equals("unknown") ||
                    Build.MANUFACTURER.equals("Genymotion") ||
                    Build.MANUFACTURER.contains("Andy") ||
                    Build.MANUFACTURER.contains("MIT") ||
                    Build.MANUFACTURER.contains("nox") ||
                    Build.MANUFACTURER.contains("TiantianVM")) {
                newRating++;
            }

            if (Build.BRAND.equals("generic") ||
                    Build.BRAND.equals("generic_x86") ||
                    Build.BRAND.equals("TTVM") ||
                    Build.BRAND.contains("Andy")) {
                newRating++;
            }

            if (Build.DEVICE.contains("generic") ||
                    Build.DEVICE.contains("generic_x86") ||
                    Build.DEVICE.contains("Andy") ||
                    Build.DEVICE.contains("ttVM_Hdragon") ||
                    Build.DEVICE.contains("Droid4X") ||
                    Build.DEVICE.contains("nox") ||
                    Build.DEVICE.contains("generic_x86_64") ||
                    Build.DEVICE.contains("vbox86p")) {
                newRating++;
            }

            if (Build.MODEL.equals("sdk") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.equals("google_sdk") ||
                    Build.MODEL.contains("Droid4X") ||
                    Build.MODEL.contains("TiantianVM") ||
                    Build.MODEL.contains("Andy") ||
                    Build.MODEL.equals("Android SDK built for x86_64") ||
                    Build.MODEL.equals("Android SDK built for x86")) {
                newRating++;
            }

            if (Build.HARDWARE.equals("goldfish") ||
                    Build.HARDWARE.equals("vbox86") ||
                    Build.HARDWARE.contains("nox") ||
                    Build.HARDWARE.contains("ttVM_x86")) {
                newRating++;
            }

            if (Build.FINGERPRINT.contains("generic/sdk/generic") ||
                    Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                    Build.FINGERPRINT.contains("Andy") ||
                    Build.FINGERPRINT.contains("ttVM_Hdragon") ||
                    Build.FINGERPRINT.contains("generic_x86_64") ||
                    Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
                    Build.FINGERPRINT.contains("vbox86p") ||
                    Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
                newRating++;
            }

            try {
                String opengl = android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_RENDERER);
                if (opengl != null) {
                    if (opengl.contains("Bluestacks") ||
                            opengl.contains("Translator")
                            )
                        newRating += 10;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                File sharedFolder = new File(Environment
                        .getExternalStorageDirectory().toString()
                        + File.separatorChar
                        + "windows"
                        + File.separatorChar
                        + "BstSharedFolder");

                if (sharedFolder.exists()) {
                    newRating += 10;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            rating = newRating;
        }
        return rating > 3;//不能再少了，否则有可能误判，若增减了新的嫌疑度判定属性，要重新评估该值
```

#  Anti 多开
多开麻烦在于真机多开，具备真机特征，模拟器的检测就失效了，因为它就是真机。
应对方法：普通的软多开，一般绕不过uid，还是用宿主的。因此，如果满足同一uid下的两个进程对应的包名，在"/data/data"下有两个私有目录，则违背了系统 "只为一个应用创建唯一一个私有目录"的设定，则该应用被多开了。
```java
public static boolean isRunInVirtual() {

    String filter = getUidStrFormat();

    String result = exec("ps");
    if (result == null || result.isEmpty()) {
        return false;
    }

    String[] lines = result.split("\n");
    if (lines == null || lines.length <= 0) {
        return false;
    }

    int exitDirCount = 0;

    for (int i = 0; i < lines.length; i++) {
        if (lines[i].contains(filter)) {
            int pkgStartIndex = lines[i].lastIndexOf(" ");
            String processName = lines[i].substring(pkgStartIndex <= 0
                    ? 0 : pkgStartIndex + 1, lines[i].length());
            File dataFile = new File(String.format("/data/data/%s",
                    processName, Locale.CHINA));
            if (dataFile.exists()) {
                exitDirCount++;
            }
        }
    }

    return exitDirCount > 1;
}
```
这个方法是在简书 JZaratustra 大佬的文章里学到的：[Android虚拟机多开检测](https://www.jianshu.com/p/216d65d9971e)。
但是有一些多开，比如小米自带的多开这种，进程好像都是隔离的独立uid的，暂时没有好办法识别。

#  Anti Hook
不多说了，方法都被你Hook了，你就是大爷，你说啥就是啥。
应对方法：检测是否安装了xposed相关应用，检测调用栈道的可疑方法，检测并不应该native的native方法，通过/proc/[pid]/maps检测可疑的共享对象或者JAR。
### 检测是否安装了xposed相关应用
```java
PackageManager packageManager = context.getPackageManager();
List applicationInfoList  = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
 
for(ApplicationInfo applicationInfo : applicationInfoList) {
    if(applicationInfo.packageName.equals("de.robv.android.xposed.installer")) {
        Log.wtf("HookDetection", "Xposed found on the system.");
    }
    if(applicationInfo.packageName.equals("com.saurik.substrate")) {
        Log.wtf("HookDetection", "Substrate found on the system.");
    }
}
```
### 检测调用栈道的可疑方法
```
  try {
            throw new Exception("blah");
        } catch (Exception e) {
            int zygoteInitCallCount = 0;
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                if (stackTraceElement.getClassName().equals("com.android.internal.os.ZygoteInit")) {
                    zygoteInitCallCount++;
                    if (zygoteInitCallCount == 2) {
                        Log.wtf("HookDetection", "Substrate is active on the device.");
                        isHook = true;
                    }
                }
                if (stackTraceElement.getClassName().equals("com.saurik.substrate.MS$2") &&
                        stackTraceElement.getMethodName().equals("invoked")) {
                    Log.wtf("HookDetection", "A method on the stack trace has been hooked using Substrate.");
                    isHook = true;
                }
                if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge") &&
                        stackTraceElement.getMethodName().equals("main")) {
                    Log.wtf("HookDetection", "Xposed is active on the device.");
                    isHook = true;
                }
                if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge") &&
                        stackTraceElement.getMethodName().equals("handleHookedMethod")) {
                    Log.wtf("HookDetection", "A method on the stack trace has been hooked using Xposed.");
                    isHook = true;
                }

            }
        }
```
### 通过/proc/[pid]/maps检测可疑的共享对象或者JAR：
```java
 try {
            Set<String> libraries = new HashSet();
            String mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps";
            BufferedReader reader = new BufferedReader(new FileReader(mapsFilename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(".so") || line.endsWith(".jar")) {
                    int n = line.lastIndexOf(" ");
                    libraries.add(line.substring(n + 1));
                }
            }
            for (String library : libraries) {
                if (library.contains("com.saurik.substrate")) {
                    Log.wtf("HookDetection", "Substrate shared object found: " + library);
                    isHook = true;
                }
                if (library.contains("XposedBridge.jar")) {
                    Log.wtf("HookDetection", "Xposed JAR found: " + library);
                    isHook = true;
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.wtf("HookDetection", e.toString());
        }
```
注意，只要针对这几个检测相关函数Hook，就反反Hook了。很容易绕过。

#  服务器分析数据相似性
可用于识别设备的标识有很多，除了Android ID，还有imei、mac、pseduo_id，aaid，gsf_id等。由于谷歌是反对唯一绝对追踪用户的，所以这些id或难或简单都是可能被修改的。比如，通过adb命令就可以无root直接修改Android ID。但是，这些标识全部都修改的话还是优点麻烦的。客户端可以把这些id都上报给服务器，服务器再结合地理位置、ip等其他信息做一个相似度判定，可以找出一些疑似同一恶意用户的账号。
# SD卡存储自制ID
如果你有SD卡写权限的话，按自己的规则生成id并加密，在自己应用私有目录之外的隐蔽地方偷偷写成一个隐藏文件（只要在文件名或者文件夹名字前加一个点号即可）。只要生成过一次，就以这个为准，无论用户修改设备信息注册多少个马甲，都能识别为同一设备用户。
# 手机号短信认证
所有登录用户都必须绑定手机号。从产品流程上提高了马甲成本，但是也提高了用户注册门槛。

当然了，以上方法只能防小白不防大师，这些方法很容易就可以被有经验的逆向人员绕过。
写出来，是希望能集思广益，获得更多的反制思路，提高恶意分子伪造设备的成本。（其实是希望碰到大佬指点，提高下本不成器菜鸟的知识水平😄）有更深入实践的同学，求评论，求私信。

参考文章：

[Android反调试之 AntiEmulator 检测安卓模拟器](http://blog.csdn.net/earbao/article/details/51455564)

[基于文件特征的Android模拟器检测](https://mp.weixin.qq.com/s/sl33d2pnyLMJ-fUY_DfBDw)

[Android Java层的anti-hooking技巧](http://www.droidsec.cn/android-java%E5%B1%82%E7%9A%84anti-hooking%E6%8A%80%E5%B7%A7/)

[Android虚拟机多开检测](https://www.jianshu.com/p/216d65d9971e)

参考Demo: 

[anti-counterfeit-android](https://github.com/Labmem003/anti-counterfeit-android)。
