package com.nstl.securitysdkcore.urischeme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.nstl.securitysdkcore.SecuritySDKInit;
import com.nstl.securitysdkcore.config.IntentUriScheme;

/**
 * Created by daizhongyin on 2018/5/4.
 * Uri scheme类似 zhifubao://web?url=http://aaa.bbb.com/xxx，其中参数容易被攻击者篡改以达到某种目的，为此，安全sdk定义IntentUriSchemeFilter类来提供Uri Scheme安全拦截的功能，作为临时补丁和恶意行为的拦截
 */

public class IntentUriSchemeFilter {
    private Context context = null;
    public IntentUriSchemeFilter(Context context){
        this.context = context;
    }

    /**
     * 对传入的IntentUri Scheme进行校验，true表示合法，false表示Uri参数非法
     * @param uriScheme                     外部传入的Uri字符串
     * @param validateIntentUriScheme       server端配置下发的IntentUriScheme拦截策略
     * @return
     */
    public boolean validate(String uriScheme, IValidateIntentUriScheme validateIntentUriScheme){
        boolean flag = false;
        Uri uri = Uri.parse(uriScheme);
        String str = SecuritySDKInit.getInstance(context).getConfigStringValueByKey(SecuritySDKInit.INTENTURISCHEMELIST);
        IntentUriScheme intentUriScheme = JSON.parseObject(str, IntentUriScheme.class);
        flag = validateIntentUriScheme.validateUri(uri, intentUriScheme);
        return flag;
    }
}
