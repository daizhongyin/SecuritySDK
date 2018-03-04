package com.nstl.securitysdkcore.UpgradeTool;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.alibaba.fastjson.JSON;
import com.nstl.securitysdkcore.HelpUtil;
import com.nstl.securitysdkcore.config.ConfigFileToObject;

import java.io.File;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Lin on 2017/12/27.
 */

public class UpgradeTool {

    private UpgradeModel upgradeModel;
    private long mTaskId;
    private DownloadManager downloadManager;
    private String savePath;
    private String fileName;
    private ISafeInstall iSafeInstall;
    private Context mContext;
    private BroadcastReceiver receiver;
    private String publicKey = null;            //RSA公钥，用来验证DataBean中的singnedVerifyCode是否和verifyCode一致，防止非法劫持

    private String TAG = "TEST";

    /**
     *
     * @param mContext  上下文
     * @param publicKey RSA公钥，用来验证DataBean中的singnedVerifyCode是否和verifyCode一致，防止非法劫持
     * @param savePath  保存路径
     * @param fileName  保存的文件名
     * @param iSafeInstall  业务实现的安装接口
     */
    public UpgradeTool( Context mContext,String publicKey, UpgradeModel upgradeModel, String savePath ,String fileName, ISafeInstall iSafeInstall ){
        //请求json数据
        //反序列化为UpgradeModel
        //这是测试的请求字符串，此处应为一个http请求
        // TODO: 2017/12/31 下面需要实现和服务器的安全通信，获取apk下载需要的信息 
        String jsonStr = "{\n" +
                "  \"code\": 100001,\n" +
                "  \"dataBean\": {\n" +
                "    \"description\": \"this is update\",\n" +
                "    \"downUrl\": \"http://192.168.12.139/download.apk\",\n" +
                "    \"isForce\": \"isForce\",\n" +
                "    \"md5SignCode\": \"xxxxxx\",\n" +
                "    \"vercode\": 7,\n" +
                "    \"version\": \"1.0.1\"\n" +
                "  },\n" +
                "  \"msg\": \"download apk\"\n" +
                "}";
        this.upgradeModel = upgradeModel;
        this.savePath = savePath;
        this.fileName = fileName;
        this.iSafeInstall = iSafeInstall;
        this.mContext = mContext;
        this.publicKey = publicKey;

        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //执行下载完成后的操作
                checkDownloadStatus();
            }
        };
    }

    /**
     * 检查下载的状态
     */
    private void checkDownloadStatus(){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                // TODO: 2017/12/31 不同的处理状态
                case DownloadManager.STATUS_PAUSED:
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成,先校验下载的应用和签名是否合法，然后安装APK
                    if(checkPkgSign(mContext, savePath, fileName)){
                        this.iSafeInstall.install(mContext,savePath,fileName,this.upgradeModel.getDataBean().getIsForce());
                    }else{
                        this.iSafeInstall.getErrMsg("download apk is success");
                    }

                    break;
                case DownloadManager.STATUS_FAILED:
                    this.iSafeInstall.getErrMsg("download apk is failed");
                    break;
            }
        }
    }

    /**
     * 下载的apk文件
     * @param mContext  上下文
     * @param url   下载文件的url
     * @param savePath 下载文件的路径
     * @param fileName  下载后的文件名
     */
    private void downloadAPK(Context mContext,String url,String savePath, String fileName){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //漫游网络是否可以下载，是否可以考虑业务来传递
        request.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置
        //此时可以考虑
        // TODO: 2017/12/31 判断是否放在sd上面去 
        request.setDestinationInExternalPublicDir(savePath,fileName);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径
        //将下载请求加入下载队列
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * upgrade方法，对外提供调用的方法
     */
    public void upgrade(){
        int verCode = this.iSafeInstall.getVerCode();
        // TODO: 2017/12/31
        if(this.upgradeModel.getDataBean().getVercode() <= verCode){
            //不需要执行升级
            this.iSafeInstall.getErrMsg("current version is latest");
            return;
        }
        //downLoad apk
        this.downloadAPK(mContext,this.upgradeModel.getDataBean().getDownUrl(),this.savePath,this.fileName);
    }
    /**
     *
     * @param context
     * @param savePath
     * @param fileName
     * @return
     */
    private boolean checkPkgSign(Context context,String savePath,String fileName){
        String apkFilePath = savePath + File.separator + fileName;
        String signCode = this.upgradeModel.getDataBean().getSingnedVerifyCode();   //生成的签名
        String code = this.upgradeModel.getDataBean().getVerifyCode();      //被签的原文
        //验证签名信息
        if(verifySignedVerifyCode(signCode, code)&& this.apkSignCheck(mContext,apkFilePath,code)){
            return true;
        }else{
            return false;
        }
    }
    //使用公钥用来验证DataBean中的singnedVerifyCode是否和verifyCode一致，防止下载被非法劫持
    private boolean verifySignedVerifyCode(String signCode, String code){
        boolean flag = false;
        try{
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(this.publicKey.getBytes());
            PublicKey pKey = kf.generatePublic(keySpec);
            java.security.Signature signature = java.security.Signature.getInstance("SHA1withRSA");
            signature.initVerify(pKey);
            signature.update(code.getBytes());
            flag = signature.verify(signCode.getBytes());
        }catch (Exception e){
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 检查下载的apk或者文件的MD5信息是否跟文件自身匹配
     * @param ct
     * @param md5Code           下载文件的MD5值
     * @return              true,下载内容MD5验证合法，false MD5验证非法
     */
    private boolean apkSignCheck(Context ct, String apkFilePath, String md5Code){
        //2.读取APK中的META-INF目录下的签名文件，验证一致性
        boolean flag = false;
        if (TextUtils.isEmpty(apkFilePath))
            return flag;
        File downloadFile = new File(apkFilePath);
        String downloadFileMD5 = HelpUtil.getFileMD5(downloadFile);
        flag = md5Code.equals(downloadFileMD5);
        return flag;
    }


}
