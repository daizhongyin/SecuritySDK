package com.nstl.securitysdkcore.UpgradeTool;
/**
 * Created by Lin on 2017/12/27.
 */

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.nstl.securitysdkcore.HelpUtil;
import com.nstl.securitysdkcore.Util.VerifyUtil;

import java.io.File;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;


/**
 * UpgradeTool类
 * 提供安全下载apk并安装的功能
 */
public class UpgradeTool {

    //用户传入的接口
    private UpgradeModel upgradeModel;

    //下载任务的id
    private long mTaskId;

    //下载管理器
    DownloadManager downloadManager;

    //保存的路径和文件名
    private String savePath;
    private String fileName;

    //用户实现的接口
    private ISafeInstall iSafeInstall;

    //上下文信息
    private Context mContext;

    //注册接收广播接收器
    private BroadcastReceiver receiver;

    //RSA公钥，用来验证DataBean中的singnedVerifyCode是否和verifyCode一致，防止非法劫持
    private String publicKey = null;

    /**
     * @param mContext     上下文
     * @param publicKey    RSA公钥，用来验证DataBean中的singnedVerifyCode是否和verifyCode一致，防止非法劫持
     * @param savePath     保存路径
     * @param fileName     保存的文件名
     * @param iSafeInstall 业务实现的安装接口
     */
    public UpgradeTool(Context mContext, String publicKey, UpgradeModel upgradeModel, String savePath, String fileName, ISafeInstall iSafeInstall) {
        this.upgradeModel = upgradeModel;
        if( savePath == null || savePath.isEmpty()){
            this.savePath = "/download";
        }else{
            this.savePath = savePath;
        }
        if( fileName == null || fileName.isEmpty()){
            this.fileName = "apk_upgrade.apk";
        }else{
            this.fileName = fileName;
        }
        this.iSafeInstall = iSafeInstall;
        this.mContext = mContext;
        this.publicKey = publicKey;
        //注册广播接收器，接受下载之后的广播
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //检查下载状态
                checkDownloadStatus();
            }
        };
    }

    /**
     * 检查下载的状态，根据不同的状态设置不同的getErrMessage
     *
     */
    private void checkDownloadStatus() {
        try {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mTaskId);
            Cursor cursor = downloadManager.query(query);

            if (cursor == null) {
                //todo 打印日志
                iSafeInstall.getErrMsg("下载状态不可知");
                return;
            }
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        this.iSafeInstall.getErrMsg("download apk is pause");
                        break;
                    case DownloadManager.STATUS_PENDING:
                        this.iSafeInstall.getErrMsg("download apk is pending");
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        this.iSafeInstall.getErrMsg("download apk is running");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        //下载完成,先校验下载的应用和签名是否合法，然后安装APK
                        if (checkPkgSign( savePath, fileName)) {
                            this.iSafeInstall.install(mContext, savePath, fileName, this.upgradeModel.getDataBean().getIsForce());
                        } else {
                            this.iSafeInstall.getErrMsg("download apk is not allowed");
                        }
                        break;
                    case DownloadManager.STATUS_FAILED:
                        this.iSafeInstall.getErrMsg("download apk is failed");
                        break;
                }
            }
        } catch (Exception e) {
            //todo 写入log
            return;
        }
    }

    /**
     * 下载的apk文件
     * @param mContext 上下文
     * @param url      下载文件的url
     * @param savePath 下载文件的路径
     * @param fileName 下载后的文件名
     */
    private void downloadAPK(Context mContext, String url, String savePath, String fileName) {
        String apkUpgradeUrl = url;
        if (apkUpgradeUrl == null || apkUpgradeUrl.isEmpty()) {
            //todo 打印日志，提示url为空
            iSafeInstall.getErrMsg("url is empty");
            return;
        }
        apkUpgradeUrl = apkUpgradeUrl.trim();
        if (!apkUpgradeUrl.startsWith("http")) {
            apkUpgradeUrl = "http://" + apkUpgradeUrl;
        }
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(" ");
            request.setDescription(" ");

            //在通知栏显示下载进度
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request
                        .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            //设置启用wifi
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            //设置保存在sdcard上的路径
            request.setDestinationInExternalPublicDir(savePath, fileName);
            downloadManager = (DownloadManager)
                    mContext.getSystemService(Context.DOWNLOAD_SERVICE);

            //进入下载队列
            this.mTaskId = downloadManager.enqueue(request);

            //注册广播接收者，监听下载状态
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            mContext.registerReceiver(receiver, intentFilter);

        } catch (Exception e) {
            //todo 打印日志
            return;
        }
        return ;
    }

    /**
     * upgrade方法，对外提供调用的方法
     */
    public void upgrade() {
        int verCode = this.iSafeInstall.getVerCode();
        // TODO: 2017/12/31
        if (this.upgradeModel.getDataBean().getVercode() <= verCode) {
            //不需要执行升级
            this.iSafeInstall.getErrMsg("current version is latest");
            return;
        }
        //downLoad apk
        this.downloadAPK(mContext, this.upgradeModel.getDataBean().getDownUrl(), this.savePath, this.fileName);
        return ;
    }

    /**
     * @param savePath
     * @param fileName
     * @return
     */
    private boolean checkPkgSign(String savePath, String fileName) {
        if( savePath == null || savePath.isEmpty()){
            return false;
        }
        if( fileName == null || fileName.isEmpty()){
            return false;
        }
        String apkFilePath = getSDPath() + savePath + File.separator + fileName;

        //判断文件是否存在
        try
        {
            File file = new File(apkFilePath);
            if(!file.exists())
            {
                iSafeInstall.getErrMsg("下载的文件找不到...");
                return false;
            }
        }
        catch (Exception e)
        {
            //todo 打印log
            return false;
        }
        //生成的签名
        String signCode = this.upgradeModel.getDataBean().getSingnedVerifyCode();

        //被签的原文
        String code = this.upgradeModel.getDataBean().getVerifyCode();
        //验证签名信息
        if (
                VerifyUtil.verifySignedVerifyCode(this.publicKey, signCode, code) &&
                VerifyUtil.fileMd5Check(apkFilePath, code)
                )
        {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
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
     * 使用公钥用来验证DataBean中的singnedVerifyCode是否和verifyCode一致，防止下载被非法劫持
     * @param signCode 签值
     * @param code
     * @return
     */
    private boolean verifySignedVerifyCode(String signCode, String code) {
        boolean flag = false;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(this.publicKey.getBytes());
            PublicKey pKey = kf.generatePublic(keySpec);
            java.security.Signature signature = java.security.Signature.getInstance("SHA1withRSA");
            signature.initVerify(pKey);
            signature.update(code.getBytes());
            flag = signature.verify(signCode.getBytes());
        } catch (Exception e) {
            //todo 打印日志
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 检查下载的apk或者文件的MD5信息是否跟文件自身匹配
     * @param md5Code 下载文件的MD5值
     * @return true, 下载内容MD5验证合法，false MD5验证非法
     */
    private boolean apkSignCheck(String apkFilePath, String md5Code) {
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
