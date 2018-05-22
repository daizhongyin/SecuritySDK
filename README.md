# SecuritySDK
----------

**SecuritySDK**是为Android APP提供一系列安全防护功能，包括但不限于：基础加固对抗防护、典型漏洞防护方案和代码、威胁情报收集等功能。其中securitysdkcore是项目核心功能代码，app只是securitysdkcore的测试demo apk。项目成员：daizy(daizhongyin@126.com)、张林(97615274@qq.com)、唐海洋(ffthy@qq.com)。如有任何问题，欢迎随时联系我们。

**典型漏洞防护方案和代码**包括：安全webview、应用IPC通信安全、应用和插件更新安全、插件调用安全、ZIPFile读取压缩文件的安全性、Intent Uri Scheme安全、插件plugin调用安全、Jar签名效验验证。

**基础加固对抗防护**包括：反调试、模拟器检测、重打包检测、进程注入检测、HOOK框架检测。

**威胁情报收集**包括：当前设备上安装的所有包名+签名、连接WIFI、是否root、HOOK框架检测、设备位置信息、基站信息。

## SecuritySDK的配置文件设置与使用

 由于漏洞防护中的很多能力依赖于运行时拦截，所以**配置文件**的更新和获取是SecuritySDK的使用前提，配置文件格式和内容，可以参考[示例](./securitysdkcore/config.json)，配置文件由server端生成，SecuritySDK获取到本地后，反序列化后使用。

 1）配置文件config.json(通过preferences方式存储)，是基于json格式的内容，它提供了4类拦截规则：webview的拦截规则、intent uri拦截规则、应用插件调用的过滤拦截规则。

 2）通过SecuritySDKInit.syncConfig(url, params)方法进行配置文件的更新，新版本的更新取决于设置的time_out和version，当更新间隔时间到了，就回去服务器请求新的版本号，如果版本号大于本地版本号，就会进行规则更新；

 3）获得拦截规则：先通过SecuritySDKInit.getConfigStringValueByKey(key)获得本地存储的配置文件json内容，然后自己通过fastjson讲字符串在转换成SecuritySDKConfig中的相关拦截规则对象。示例代码如下：

 ```java
    String str = SecuritySDKInit.getConfigStringValueByKey(SecuritySDKInit.WEBVIEWCONFIG);
    WebviewConfig webviewConfig = JSON.parseObject(str, WebviewConfig.class);
 ```

## 典型漏洞防护方案、代码和使用说明

* [安全webview](./docs/safewebview.md)——[代码](./securitysdkcore/src/main/java/com/nstl/securitysdkcore/webview/SafeWebView.java)

    安全webview是针对webview常见的远程命令执行漏洞，file域攻击、高危接口对外和中间人劫持，这几个高危漏洞设计的安全webview，可以有效的解决上述几个高危漏洞。同时参考了微信、支付宝和github上的开源代码(JSBridge)，做到了有效性、兼容性和安全性并存。

* [应用IPC通信安全](./docs/BinderSecurityUtil.md)——[代码](./securitysdkcore/src/main/java/com/nstl/securitysdkcore/BinderSecurityUtil.java)

    IPC通信安全是针对应用进程间，进行数据通信时设计的方案。应用A和应用B进行数据通信时，业务方需要使用Binder异步接口进行，不要使用开放端口，然后再每个开放的Binder通信接口中，调用BinderSecurityUtil. checkClientSig()进行调用方的签名认证，签名认证可以在本地，也可以在云端进行。

* [应用和插件安全更新](./docs/safeupgrade.md)——[代码](./securitysdkcore/src/main/java/com/nstl/securitysdkcore/UpgradeTool/UpgradeTool.java)

    应用和插件安全更新功能提供：应用和插件安全更新的能力，防止应用或插件更新升级时，被劫持或者下载的apk/zip被串改，从而导致任意代码执行的漏洞。

* [ZIPFile读取APK文件安全](./docs/SafeZipFile.md)——[代码](./securitysdkcore/src/main/java/com/nstl/securitysdkcore/reinforce/SafeZipFile.java)

    为了保证ZIPFile读取APK文件的安全性，确保ZipFile文件内不包含../，并且只有一个Dex(避免恶意代码加载执行漏洞)，此外还需要确保APK中的签名和业务方提供的签名信息一致，防止签名绕过漏洞。

* [Intent Uri Scheme安全](./docs/IntentUriSchemeFilter.md)——[代码](./securitysdkcore/src/main/java/com/nstl/securitysdkcore/urischeme/IntentUriSchemeFilter.java)

    Uri scheme类似 bainuo://web?url=http://aaa.bbb.com/xxx，其中参数容易被攻击者篡改以达到某种目的，为此，安全sdk定义IntentUriSchemeFilter类来提供Uri Scheme安全拦截的功能，作为临时补丁和恶意行为的拦截，类似web安全中的WAF。

* [插件plugin调用安全](./docs/PluginInvokeValidate.md)——[代码](./securitysdkcore/src/main/java/com/nstl/securitysdkcore/PluginInvokeValidate.java)

    插件调用安全是通过增加拦截类，根据配置文件(config)中的拦截规则，对恶意插件调用进行拦截；拦截规则提供了插件包名拦截、插件方法名拦截、调用的类名、方法参数和Intent数据的拦截.

* [jar签名效验](./docs/jarSignatureVerify.md)——[代码](./securitysdkcore/src/main/java/com/nstl/securitysdkcore/reinforce/JarSignatureVerifier.java)

    JarSignatureVerifier类是为了保证JAR签名和内容一致性，防止攻击者合法签名文件，但是.class文件和签名信息不一致，从而绕过签名校验，导致任意代码执行。

## 基础加固对抗防护

### NativeCoreUtil

	提供debugPresent（检测应用是否处于调试中）、runInEmulator（检测应用是否处于模拟器）、rePackage（重打包检查）、detectInject（检测应用是否被xposed，substrate等框架注入）、isExisSUAndExecute（是否被root）、getRemoteAppSign（获取远程应用的签名）6个函数


### AndroidSo 加解密

	核心加解密过程如下：

		1、首先获取.so 在内存中加载的基址

		2、因为android 系统对于.so的加载是基于段的，dynamic段中包含了多个节区，有hash,dynstr(函数的名字在该节区中)，符号表

		3、由于hash表和dynstr表  节区 的类型不唯一，有可能是别的，所以在dynamic段中寻找

		4、hash表中存储了符号表中对应的索引，通过函数名进行哈希运算得到索引，然后去符号表中，符号表的结构是1、dynstr中的索引 2、地址 3、大小

		5、通过符号表中的第一个字节获取dynstr中对应的字符串 ，然后比对，比对成功则进行加密解密

	使用过程

    	（1）业务方将解密的核心代码加入到自己的native代码中，指定SO_NAME（生成的SO名称），FUNC_NAME（加密函数名），RCE_KEY(加密密钥)，RC4_KEY_LEN（加密密钥的长度），生成so
    	注：密钥分发和保存

    	（2）将so进行加密，指定加密的函数名、加密的密钥（此时加密SO的RC4密钥需要与解密的密钥一致），加密的so文件路径（EncodeSo类中RC4_KEY,funcName,so_path）

    	（3）业务方在Build.gradle中指定加载so的方式，如Jnilibs、libs等等，将加密后的so放到指定的路径下即可

    	（4）在需要调用jni的地方， static {
            System.loadLibrary("加密后的so库名称");
        }

	参考：

		http://blog.csdn.net/jiangwei0910410003/article/details/49966719

		http://bbs.pediy.com/thread-191649.htm

### sdk各文件及其相关API说明

	1、Rc4Util.h Rc4Util.cpp（RC4加密算法的实现文件）

	注：
		因为涉及到修改内存操作，目前暂考虑同位加密，为此选取了RC4加密算法，RC4加密算法原理这里暂不介绍，请自行google。

	2、 getSign.cpp getSign.h(获取签名）

	 通过NativeCoreUtil类中的getRemoteAppSign函数进行调用。传入上下文和包名，返回远程调用的签名。本地签名哈希暂时写在getSign.cpp文件中。

    3、插件调用拦截。使用PluginInvokeValidate 类中的validate函数进行拦截，初始化安全策略文件写在键intercept_plugin_invoke中。

    4、安全通信 调用接口为CryptAndHttps类中的getHttpsUrlConnection函数。使用前 必须先配置服务器证书，证书文件放在Assets目录。具体使用时见MainActivity中的Https登录按钮。

## 威胁情报收集
    todo


