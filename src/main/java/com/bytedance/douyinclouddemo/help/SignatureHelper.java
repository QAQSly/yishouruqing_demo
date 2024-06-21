package com.bytedance.douyinclouddemo.help;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@RestController
public class SignatureHelper {

   
    public static PrivateKey loadPrivateKeyFromPemV2(String privateKeyPath, ResourceLoader resourceLoader) throws Exception {
        Resource resource = resourceLoader.getResource(privateKeyPath);
        try (InputStream inputStream = resource.getInputStream()) {
            String pemContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            // 去除PEM包装器，只保留Base64编码的部分
            String base64Key = extractBase64Key(pemContent);
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
    }

    private static String extractBase64Key(String pemContent) {
        // 匹配PEM文件中的Base64编码部分
        Pattern pattern = Pattern.compile("(?<=-----BEGIN PRIVATE KEY-----\\n).*?(?=-----END PRIVATE KEY-----)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pemContent);
        if (matcher.find()) {
            return matcher.group().replaceAll("\\s", ""); // 移除所有空白字符
        }
        throw new IllegalArgumentException("Invalid PEM format");
    }

    public static String createSignature(String signatureStr, String privateKeyPath, ResourceLoader resourceLoader) throws Exception {
        PrivateKey privateKey = loadPrivateKeyFromPemV2(privateKeyPath, resourceLoader );
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(signatureStr.getBytes());
        byte[] encodedSig = sig.sign();
        return Base64.getEncoder().encodeToString(encodedSig);
    }
}
