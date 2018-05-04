## 应用和插件安全更新的简介

### UpgradeTool目录

	提供应用和插件安全下载和更新功能，具体类功能简介如下：

	（1）UpgradeModel

			1、包含databean：应用升级的信息，如描述信息，下载apk的url地址、是否强制安装，应用的版本（1.0.1），开发版本号，要下载文件的MD5值、以及经过服务端私钥签名后的MD5值

			2、code 和 msg 暂时为扩展字段，待后面增加描述

	（2）ISafeInstall安装接口类，业务需要实现的

			getVerCode 为获取当前应用的开发版本，用于对比升级

			install 为业务需要实现的如何安装等功能

			getErrMsg 获取升级应用时产生的错误消息

	（3）UpgradeTool

	        UpgradeTool实例化时需要4个参数:

	            publicKey RSA公钥，用来验证DataBean中的singnedVerifyCode是否和verifyCode一致，防止下载的非法劫持

                savePath  保存路径

                fileName  保存的文件名

                iSafeInstall  业务实现的安装接口

## 应用和插件安全更新的使用指南

### 1、实现ISafeInstall接口类

该接口是业务方用于实现插件或者应用更新安装的逻辑，比如移动最新版的插件到私有目录，或者调用系统api进行应用升级。

### 2、初始化UpgradeTool对象，传入参数：(Context，publicKey，savePath，fileName，接口类的实现类对象);

### 3、调用upgrade方法即可

业务方下载更新或者相关插件时，只需要调用upgrade方法，流程如下：判断开发版本号是否需要升级 -> 下载apk -> 调用ISafeInstall的具体实现类的Install方法