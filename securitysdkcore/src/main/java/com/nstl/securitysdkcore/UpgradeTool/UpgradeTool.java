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
import com.nstl.securitysdkcore.config.ConfigFileToObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    private String TAG = "TEST";

    /**
     *
     * @param mContext  上下文
     * @param savePath  保存路径
     * @param fileName  保存的文件名
     * @param iSafeInstall  业务实现的安装接口
     */
    public UpgradeTool( Context mContext,String savePath ,String fileName, ISafeInstall iSafeInstall ){
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
        this.upgradeModel = JSON.parseObject(jsonStr,UpgradeModel.class);
        this.savePath = savePath;
        this.fileName = fileName;
        this.iSafeInstall = iSafeInstall;
        this.mContext = mContext;

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
                    //下载完成安装APK
                    this.iSafeInstall.install(mContext,savePath,fileName,this.upgradeModel.getDataBean().getIsForce());
                    this.iSafeInstall.getErrMsg("download apk is success");
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

        //判断请求的host 是否在白名单内,不在的话，调用接口的xxx方法
        if( !ConfigFileToObject.getSecuritySDKConfig(mContext).getUpdateAndDownloadUrlWhiteList().contains(this.upgradeModel.getDataBean().getDownUrl())){
            this.iSafeInstall.getErrMsg("utl is not exist in whilelist");
        }
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

        String apkFilePath = getSDPath() + savePath + "\\" + fileName;
        //此时应该是加密后的sign，应该对其进行解密
        String sign = this.upgradeModel.getDataBean().getMd5SignCode();
        if(this.apkSignCheck(mContext,apkFilePath,sign )){
            return true;
        }
        return false;
    }

    /**
     * 获取sd卡的路径信息
     * @return
     */
    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }
    /**
     * 检查apk的签名
     * @param ct
     * @param sig
     * @return
     */
    private boolean apkSignCheck(Context ct, String apkFilePath, String sig){
        //2.读取APK中的META-INF目录下的签名文件，验证一致性
        boolean flag = true;
        if (TextUtils.isEmpty(apkFilePath))
            return true;
        PackageManager pm = ct.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_SIGNATURES);
        if(pi != null){
            Signature[] signatures = pi.signatures;
            if(signatures != null && signatures.length > 0){
                String signature =  encryptionMD5(signatures[0].toByteArray());
                if(signature.equals(sig))
                    flag = false;
            }
        }
        return flag;
    }

    /**
     * md5签名
     * @param byteStr
     * @return
     */
    private  String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }



}
