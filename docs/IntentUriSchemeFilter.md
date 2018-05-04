## Intent Uri Scheme安全策略拦截的使用指南

### 1、实现接口IValidateIntentUriScheme.validateUri

业务方需要实现的Intent Uri Scheme的校验策略，其中根据IntentUriScheme.type字段来表示是黑名单策略，还是白名单策略，建议白名单策略,黑名单存在被绕过的情况。

比如uri scheme可以调用内置浏览器，打开url，因此可以增加url白名单限制，如 zhifubao://web?url=http://aaa.bbb.com/xxx，我们对url的参数进行白名单校验，禁止打开非支付宝域名下的url。

### 2、调用IntentUriSchemeFilter.validate(String uriStr, IValidateIntentUriScheme iv)

true表示合法，false表示Uri参数非法；uriStr是外部传入的Uri字符串