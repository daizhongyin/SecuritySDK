# SecuritySDK
安全SDK，通过该sdk为Android APP提供一系列安全防护功能，如基本对抗防护、漏洞防护、威胁情报收集等功能。

##配置文件的更新和获取(配置文件格式，参考根目录下的config.json)：
	1）配置文件：securitysdk_config.json(通过preferences方式存储)，是基于json格式的内容，它提供了4类拦截规则：webview的拦截规则、intent uri拦截规则、应用和插件更新的url白名单和应用插件调用的过滤拦截规则
	2）通过SecuritySDKInit.syncConfig(url, params)方法进行配置文件的更新，新版本的更新取决于设置的time_out和version，当更新间隔时间到了，就回去服务器请求新的版本号，如果版本号大于本地版本号，就会进行规则更新；
	3）获得拦截规则：先通过SecuritySDKInit.getConfigStringValueByKey(key)获得本地存储的配置文件json内容，然后自己通过fastjson讲字符串在转换成SecuritySDKConfig中的相关拦截规则对象。示例代码如下：
	String str = SecuritySDKInit.getConfigStringValueByKey(SecuritySDKInit.WEBVIEWCONFIG);
	WebviewConfig webviewConfig = JSON.parseObject(str, WebviewConfig.class);
##SafeZipFile
	提供函数isLegalZipFile()判断zipFile是否是合法的文件。

##SafeWebView
	android自带webview组件存在诸多不安全设计，容易被攻击者利用，因此安全sdk提供一个相对安全的safewebview组件。
	考虑到兼容性和开发量，safewebview从Github上jsbridge的思想继承而来。
	链接：https://github.com/lzyzsd/JsBridge
###(1)SafeWebview类中相关实例变量作用如下：
	1）MiInterface是接口IMethodInvokeInterface的实现类，这个类的主要作用是业务方实现的js调用java对象的方法和参数，以这两个成员变量由业务方给定和实现。
	2）WebViewConfig类
	从配置文件中读取webview的配置策略：url黑、白名单，需要阻止JS执行的方法名和方法参数，参数格式以Map的方式执行。
	3）MethodAndArgJson
	Js传递的需要调用的方法名和参数，参数以Map的方式传递
###(2)类中函数说明：
	1）构造函数
	获得WebViewSetting、MethodMap和MiInterface接口实例化对象，并实例化WebViewClient，然后调用init函数。
	2）Init函数
		1、读取本地配置文件中url白/黑名单，以及MethodName和argArg的黑名单列表(WebViewConfigBean)，并设置webviewClient；同时关闭如下两个方法：
		setAllowFileAccessFromFileURLs(false);
		setAllowUniversalAccessFromFileURLs(false);
		2、读取系统版本号，判断当前系统版本号，如果发现API<=16，则移除默认三个接口并且禁止添加自定义的JS接口。移除如下3个接口：
		removeJavascriptInterface("searchBoxJavaBridge_");
		removeJavascriptInterface("accessibility");
		removeJavascriptInterface("accessibilityTraversal");
	3）Verify函数
	判断调用的Url是否在配置名单允许的范围内。
	4）VerifyMethodOrArg函数
	校验需要交互的方法名和参数是否合法，其中黑名单列表在init方法中获得，如果需要交互的方法、和参数在黑名单中(黑名单由服务器下发)，则停止执行。
	5）重载的loadUrl函数：先调用verify函数，然后再加载显示URL。
	6）实现BridgeWebView相关的BridgeHandler的handler方法：
实现的handler方法，主要是调用VerifyMethodOrArg函数校验js传递过来的函数名与参数是否是允许执行，允许执行则调用IMethodInvokeInterface接口的具体实现类来获取数据，然后返回给js
	7）	重载的rigisterHandler方法
	注册handler方法
###(3)使用过程：
	（1）	构造类实现iMethodInvokeInterface接口
	（2）	初始化SafeWebView
        safeWebView = new SafeWebView(this,iMethodInvokeInterface);
	（3）设置页面反馈方式
        setContentView(safeWebView);
	（4）加载Url
        safeWebView.loadUrl("file:///android_asset/javascript.html");

	（5）注册js调用的handlerName: submitFromWeb
 		safeWebView.registerHandler("submitFromWeb");
	（6）加载的js需要包含如下：
		1、执行初始化操作，并向java对象注册js对象，用于java调取js的方法
		function connectWebViewJavascriptBridge(callback) {
            if (window.WebViewJavascriptBridge) {
                callback(WebViewJavascriptBridge)
            } else {
                document.addEventListener(
                    'WebViewJavascriptBridgeReady'
                    , function() {
                        callback(WebViewJavascriptBridge)
                    },
                    false
                );
            }
        }

        connectWebViewJavascriptBridge(function(bridge) {
            bridge.init(function(message, responseCallback) {
                responseCallback("");
            });
            bridge.registerHandler("functionInJs", function(data, responseCallback) {
                document.getElementById("show").innerHTML = ("data from Java: = " + data);
                var responseData = "Javascript Says Right back aka!";
                responseCallback(responseData);
            });
        })
 		2、js调用java对象的方法
		window.WebViewJavascriptBridge.callHandler(
                'submitFromWeb'
                , {"argMapJson":{1:"baidu.com",2:"hahah"},"methodName":"openurl"}
                , function(responseData) {
                   ///java对象返回的数据                }
            );
		其中，{"argMapJson":{1:"baidu.com",2:"hahah"},"methodName":"openurl"}，为iMethodInvokeInterface接口需要的函数名和参数列表
		3、java调用js的方法
	   safeWebView.callHandler("functionInJs", "data from Java", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                // TODO Auto-generated method stub
            }
        });
	其中的functionJs 为步骤1注册的handler名称，可以任意指定。
##NativeCoreUtil
	提供debugPresent（检测应用是否处于调试中）、runInEmulator（检测应用是否处于模拟器）、rePackage（重打包检查）、
	detectInject（检测应用是否被xposed，substrate等框架注入）、isExisSUAndExecute（是否被root）、getRemoteAppSign（获取远程应用的签名）6个函数
##BinderSecurityUtil
    提供函数checkClientSign方法，在方法内部获取客户端的包名和签名，传给远端校验。需要注意的是该方法只能放在服务中的接口实现中调用，具体使用见Demo中Myservice。
##UpgradeTool
	提供应用安全升级或安全加载功能，具体吐下：
	（1）UpgradeModel
			1、包含databean：应用升级的信息，如描述信息，下载apk的url地址、是否强制安装，应用的版本（1.0.1），开发版本号，以及经过加密的md5Sign
			2、code 和 msg 暂时为扩展字段，待后面增加描述
	（2）ISafeInstall安装接口类，业务需要实现的
			getVerCode 为获取当前应用的开发版本，用于对比升级
			install 为业务需要实现的如何安装等功能
			getErrMsg 获取升级应用时产生的错误消息
	（3）UpgradeTool
			对外只导出upgrade方法，流程如下：
			判断下载apk的url是否在白名单内 -> 判断开发版本号是否需要升级 -> 下载apk -> 调用ISafeInstall的具体实现类的Install方法
###使用方法
	（1）实现ISafeInstall接口类
	（2）初始化UpgradeTool对象，传入参数
			Context，savePath，fileName，接口类的实现类对象
	（3）调用upgrade方法即可。
###相关事项
    （1）securicysdkcore是安全sdk的核心功能实现，app是测试安全sdk的demo apk
     (2)项目成员：daizy(daizhongyin@126.com)、张林(97615274@qq.com)、唐海洋(ffthy@qq.com)。如有任何问题，欢迎随时联系我们。



