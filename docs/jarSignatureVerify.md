## JarSignatureVerifier的使用指南
JarSignatureVerifier类是为了保证JAR签名和内容一致性，防止攻击者合法签名文件，但是.class文件和签名信息不一致，从而绕过签名校验，导致任意代码执行。

### 1、调用JarSignatureVerifier.verifyJar(String jarPath)

业务方只需要调用JarSignatureVerifier.verifyJar(String jarPath)来校验jar签名一致性，true表示一致，false表示jar被修改过。

### 2、实例demo
    ```
        String filePath = "D:\\Documents\\keystore\\commons-collections4-4.1_sign.jar";
        JarSignatureVerifier verifier = new JarSignatureVerifier()
        boolean verified = verifier.verifyJar(filePath);
        if (verified)
            System.out.println("verified OK.");
            //读取jar签名，通过进行
        else {
            System.out.println("jar is not verified.");
        }
    ```