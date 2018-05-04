## SafeZipFile的使用指南

### 1、SafeZipFile实例化

SafeZipFile实例化是需要三个参数：APK或下载ZIP文件、Context上下文、md5Sig 要校验的签名证书的MD5值(可以通过keytool工具来查看)。

### 2、调用SafeZipFile.isLegalZipFile()

调用函数isLegalZipFile()判断zipFile是否是合法的文件即可，判断合法后，即可进行代码的加载执行;