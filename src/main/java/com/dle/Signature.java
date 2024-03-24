package com.dle;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Signature {

    public static List<NameValuePair> queryParametersGenerate(Map<String, String> parameters) {
        List<NameValuePair> result = new ArrayList<>();
        for (Map.Entry<String, String> e : parameters.entrySet()) {
//            if (e.getKey().equals("access_token")) continue;
            BasicNameValuePair query = new BasicNameValuePair(e.getKey(), e.getValue());
            result.add(query);
        }
        return result;
    }

    public static String calculate(String secret, Map<String, String> parameters, String path, String contentType, String body) {
        // Extract all query parameters excluding "sign" and "access_token"
        List<String> keys = new ArrayList<>();
        for (String key : parameters.keySet()) {
            if (!key.equals("sign") && !key.equals("access_token")) {
                keys.add(key);
            }
        }

        // Sort the keys in alphabetical order
        Collections.sort(keys);

        // Concatenate all the parameters in the format of {key}{value}
        StringBuilder input = new StringBuilder();
        for (String key : keys) {
            input.append(key).append(parameters.get(key));
        }

        // Append the request path
        input.insert(0, path);

        // If the request header Content-type is not "multipart/form-data," append the body to the end
        if (!"multipart/form-data".equals(contentType)) {
            input.append(body);
        }

        // Wrap the string generated in the previous steps with the App secret
        input.insert(0, secret);
        input.append(secret);

        return generateSHA256(input.toString(), secret);
    }

    private static String generateSHA256(String input, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKey);

            byte[] macData = sha256_HMAC.doFinal(input.getBytes(StandardCharsets.UTF_8));

            // Encode the digest byte stream in hexadecimal
            return bytesToHex(macData);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
