## 安全webview的使用指南

### 1、申明和实例化

布局文件中的申明

```
    <com.example.safeappdemo.webview.SafeWebView
        android:id="@+id/my_webview"
        android:layout_width="match_parent"
        android:layout_height="400dp" >
    </com.example.safeappdemo.webview.SafeWebView>
```

### 2、在Java中注册一个handler，以便JS调用

```
webView.registerHandler("submitFromWeb");
```

### 3、实现IMethodInvokeInterface接口，其中data是js传递到java层的调用参数

```
public interface IMethodInvokeInterface {
        String  dispatch(String data);
    }
```

### 4、 进行webview的初始化，并加载url

```
webView.init(this, invokeInterface);
webView.loadUrl("http://www.baidu.com|file:///android_asset/javascript.html");
```

### 5、 Js页面，调用上述webview注册的接口

```
WebViewJavascriptBridge.callHandler('submitFromWeb', {'param': str1}, function(responseData) {
    document.getElementById("show").innerHTML = "send get responseData from java, data = " + responseData
    });
```

### 安全webview的相关注意事项如下

    1）	当前webview使用file协议时，只允许加载本地私有目录：/android_asset和android_res这两个目录下的html文件;

    2）	安全webview对于可以使用的JS交互接口的url进行了限制，只允许百度域下的url调用，业务方需要根据当前业务场景进行修改。

    3）关于在javascript中注册handler，java中调用的方式，可以参考JSBridge中的使用说明，安全webview也支持。https://github.com/lzyzsd/JsBridge.