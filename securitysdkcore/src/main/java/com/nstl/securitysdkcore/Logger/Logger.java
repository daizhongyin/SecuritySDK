package com.nstl.securitysdkcore.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final String FILE_PATH = "./securitySdk.log";

    /**
     * 向日志文件写入
     * @param err_msg
     * @param err_no
     * @param level
     */
    public static void writeToFile(String err_msg ,int err_no, String level){

        String time = "[ " +dateFormat.format(new Date()) + " ] : ";
        String basicInfo = level + " error_no = " + err_no + " error_msg = ";
        String fileName = "in FileName = " + getFileName() + " ";
        String className = " ClassName = " + getClassName() + " ";
        String methodName = " MethodName = " + getMethodName() + " ";
        String lineNumber = " LineNumber = " + getLineNumber() + " ";

        StringBuilder log = new StringBuilder();
        log.append(time);
        log.append(basicInfo);
        log.append(fileName);
        log.append(className);
        log.append(methodName);
        log.append(lineNumber);
        log.append("\r\n");

        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(FILE_PATH, true);
            writer.write(log.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param str
     * @param errno
     */
    public static void warning(String str, int errno){
        Logger.writeToFile(str, errno, LogLevel.DEBUG_WARNING);
    }

    /**
     *
     * @param str
     * @param errno
     */
    public static void error(String str, int errno){
        Logger.writeToFile(str, errno, LogLevel.DEBUG_ERROR);
    }

    /**
     *
     * @param str
     * @param errno
     */
    public static void trace(String str, int errno){
        Logger.writeToFile(str, errno, LogLevel.DEBUG_TRACE);
    }

    /**
     *
     * @param str
     * @param errno
     */
    public static void info(String str, int errno){
        Logger.writeToFile(str, errno, LogLevel.DEBUG_INFO);
    }

    private static int originStackIndex = 3;


    /**
     * 文件名
     * @return
     */
    public static String getFileName() {
        return Thread.currentThread().getStackTrace()[originStackIndex].getFileName();
    }

    /**
     * 类名
     * @return
     */
    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[originStackIndex].getClassName();
    }

    /**
     * 方法名
     * @return
     */
    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[originStackIndex].getMethodName();
    }

    /**
     * 行号
     * @return
     */
    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[originStackIndex].getLineNumber();
    }
}