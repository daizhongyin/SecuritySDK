## SafeZipFile的使用指南
为了保证ZIPFile读取APK文件的安全性，确保ZipFile文件内不包含../，并且只有一个Dex(避免恶意代码加载执行漏洞)，此外还需要确保APK中的签名和业务方提供的签名信息一致，防止签名绕过漏洞；
### 1、SafeZipFile实例化

SafeZipFile实例化是需要三个参数：APK或下载ZIP文件、Context上下文、md5Sig 要校验的签名证书的MD5值(可以通过keytool工具来查看，非apk的普通zipfile可以不用该签名参数)。

### 2、调用SafeZipFile.isLegalZipFile()

业务方只需要调用函数isZipFileValid(boolean validateSig),判断zipFile是否是合法的文件即可,true表示zipfile文件合法，false非法;其中参数validateSig 是否对zipfile文件进行签名校验，true表示进行，false表示不用进行zip文件的签名校验。