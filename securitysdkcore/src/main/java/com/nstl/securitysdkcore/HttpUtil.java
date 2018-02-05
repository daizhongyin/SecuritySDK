package com.nstl.securitysdkcore;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by plldzy on 17-11-15.
 * 进行Http请求:get和post
 * 推荐使用https，如果是自定义证书，可以调用CryptAndHttps中的api进行通信
 */

public class HttpUtil {
    public static String doGet(final String url){
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                BufferedReader br = null;
                InputStreamReader isr = null;
                URLConnection urlConnection = null;
                try{
                    URL url1 = new URL(url);
                    urlConnection = url1.openConnection();
                    urlConnection.connect();
                    isr = new InputStreamReader(urlConnection.getInputStream());        //输入流
                    br = new BufferedReader(isr);
                    String line = null;
                    while( (line = br.readLine()) != null){
                        sb.append(line);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        if(br != null){
                            br.close();
                        }
                        if(isr != null){
                            isr.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try{
            s = task.get();
        }catch(Exception e){
            e.printStackTrace();
        }
        return s;
    }
    //post会提交相关参数，如cookie等账号信息，所以需要params参数
    public static String doPost(final String url, final Map<String, String> params){
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL postUrl = new URL(url);
                    connection = (HttpURLConnection) postUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);         //连接超时时间
                    connection.setReadTimeout(8000);            //读取超时时间
                    //发送post请求必须设置
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                    StringBuilder request = new StringBuilder();
                    for(String key: params.keySet()){
                        request.append(key + "=" + URLEncoder.encode(params.get(key), "UTF-8") + "&");
                    }
                    outputStream.write(request.toString().getBytes());             //写入请求参数
                    outputStream.flush();
                    outputStream.close();
                    if(connection.getResponseCode() == 200){
                        InputStream inputStream = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line = null;
                        while( (line = reader.readLine()) != null ){
                            sb.append(line);
                        }
                        //System.out.println(sb);
                    }
                }catch (Exception e){
                 e.printStackTrace();
                }finally {
                    try{
                        if(reader != null){
                            reader.close();
                        }
                        if(connection != null){
                            connection.disconnect();            //断开链接，释放资源
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try{
            s = task.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }
}
