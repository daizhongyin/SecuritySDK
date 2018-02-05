package com.nstl.securitysdk;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.nstl.securitysdkcore.BinderSecurityUtil;


public class MyService extends Service {
    public String TAG="MyService";
    public MyService() {
    }
    private IBinder binder = new IMyAidlInterface.Stub(){

        @Override
        public String getInfoFromCli(String s) throws RemoteException {
           Boolean flag= BinderSecurityUtil.checkClientSig(getApplicationContext());
            int pid =Binder.getCallingPid();
          //  String ss=getAppNameByPID(getApplicationContext(),pid);
          //  Log.d(TAG, "onBind+pid: "+ss);
            return "hello from service";
        }
    };
    @Override
    public IBinder onBind(Intent intent)  {
        // TODO: Return the communication channel to the service.
        String callingApp = getApplicationContext().getPackageManager().getNameForUid(Binder.getCallingUid());
        Log.d(TAG, "onBind: "+callingApp);
//        if(callingApp.equals("com.nstl.securitysdk")){
//            Log.d(TAG, "包名正确 ");
//            return binder;
//        }
//        Log.d(TAG, "包名不在白名单内");
//       return  null;
//        int pid =Binder.getCallingPid();
//         String ss=getAppNameByPID(getApplicationContext(),pid);
//        Log.d(TAG, "onBind+pid: "+ss);
        return  binder;
    }
//    public static String getAppNameByPID(Context context, int pid){
//        ActivityManager manager;
//        manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//
//        for(ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()){
//            if(processInfo.pid == pid){
//                return processInfo.processName;
//            }
//        }
//        return "";
//    }
}

