package com.nstl.securitysdkcore.config;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lin on 2017/12/13.
 */

public class ConfigFileToObject {
    //配置文件的路径
    private static String fileName = "";
    /**
     * 获取反序列化后的对象
     * @param context
     * @return
     */
    public static SecuritySDKConfig getSecuritySDKConfig(Context context){
        //从文件中读取json数据进行反序列化
        InputStream abpath = context.getClass().getResourceAsStream("/assets/config.json");
        String jsonStr = "";
        try {
            jsonStr = new String(InputStreamToByte(abpath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecuritySDKConfig securitySDKConfig =  JSON.parseObject(jsonStr,SecuritySDKConfig.class);
        return securitySDKConfig;
    }

    /**
     * @param is
     * @return
     * @throws IOException
     */
    private static byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

}
