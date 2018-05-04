package com.nstl.securitysdkcore.urischeme;

import android.net.Uri;

import com.nstl.securitysdkcore.config.IntentUriScheme;

/**
 * Created by daizhongyin on 2018/5/4.
 */
//Uri scheme类似 zhifubao://web?url=http://aaa.bbb.com/xxx
public interface IValidateIntentUriScheme {
    /**
     * 业务方需要实现的Intent Uri Scheme的校验策略，其中根据IntentUriScheme.type字段来表示是黑名单策略，还是白名单策略，建议白名单策略
     * 比如uri scheme可以调用内置浏览器，打开url，因此可以增加url白名单限制，如 zhifubao://web?url=http://aaa.bbb.com/xxx，我们对url的参数进行白名单校验，禁止打开非支付宝域名下的url。
     * 也可以限制uri 可以打开的协议，比如禁止传入tel://10086这种拨打电话的uri scheme
     * @param uri
     * @param uriScheme
     * @return
     */
    boolean validateUri(Uri uri, IntentUriScheme uriScheme);
}
