package com.yunos.tv.core.util;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class RSASign {
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    /// private RSASignature signature = new RSASignature();
    public static String sign(String content, String privateKey, String charset )
    {
        try
        {
            PKCS8EncodedKeySpec priPKCS8 	= new PKCS8EncodedKeySpec( Base64.decode(privateKey,Base64.DEFAULT ) );
            KeyFactory keyf 				= KeyFactory.getInstance("RSA");
            PrivateKey priKey 				= keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update( content.getBytes(charset) );

            byte[] signed = signature.sign();

            return Base64.encodeToString (signed,Base64.DEFAULT);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptWithPubkey(final byte[] pubKeyBytes, final String plain) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(pubKeyBytes));
        byte[] byteData = plain.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedByteData = cipher.doFinal(byteData);
        return Base64.encodeToString(encryptedByteData, Base64.DEFAULT);
    }

    public static String decryptWithPrivatekey(final byte[] privateKeyBytes, final String encoded) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedData = Base64.decode(encoded, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes);
    }
}

