package com.tools.payhelper.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncrptyUtil {

    /**
     * 加密算法/加密模式/填充类型 本例采用AES加密，ECB加密模式，PKCS5Padding填充
     */
    private static final String CIPHER_MODE = "AES/ECB/PKCS5Padding";
    /**
     * 设置加密字符集 本例采用 UTF-8 字符集
     */
    private static final String CHARACTER = "UTF-8";

    /**
     * 设置加密密码处理长度。 不足此长度补0；
     */
    private static final int PWD_SIZE = 16;

    private static final String ENCRY_ALGORITHM = "AES";

    public static String AES_KEY = "mtizndu2zmfkc2zh";

    private static byte[] base64DecodeChars = null;
    private static final char[] base64EncodeChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    static {
        byte[] v0 = new byte[0x80];
        v0[0] = -1;
        v0[1] = -1;
        v0[2] = -1;
        v0[3] = -1;
        v0[4] = -1;
        v0[5] = -1;
        v0[6] = -1;
        v0[7] = -1;
        v0[8] = -1;
        v0[9] = -1;
        v0[0xA] = -1;
        v0[0xB] = -1;
        v0[0xC] = -1;
        v0[0xD] = -1;
        v0[0xE] = -1;
        v0[0xF] = -1;
        v0[0x10] = -1;
        v0[0x11] = -1;
        v0[0x12] = -1;
        v0[0x13] = -1;
        v0[0x14] = -1;
        v0[0x15] = -1;
        v0[0x16] = -1;
        v0[0x17] = -1;
        v0[0x18] = -1;
        v0[0x19] = -1;
        v0[0x1A] = -1;
        v0[0x1B] = -1;
        v0[0x1C] = -1;
        v0[0x1D] = -1;
        v0[0x1E] = -1;
        v0[0x1F] = -1;
        v0[0x20] = -1;
        v0[0x21] = -1;
        v0[0x22] = -1;
        v0[0x23] = -1;
        v0[0x24] = -1;
        v0[0x25] = -1;
        v0[0x26] = -1;
        v0[0x27] = -1;
        v0[0x28] = -1;
        v0[0x29] = -1;
        v0[0x2A] = -1;
        v0[0x2B] = 0x3E;
        v0[0x2C] = -1;
        v0[0x2D] = -1;
        v0[0x2E] = -1;
        v0[0x2F] = 0x3F;
        v0[0x30] = 0x34;
        v0[0x31] = 0x35;
        v0[0x32] = 0x36;
        v0[0x33] = 0x37;
        v0[0x34] = 0x38;
        v0[0x35] = 0x39;
        v0[0x36] = 0x3A;
        v0[0x37] = 0x3B;
        v0[0x38] = 0x3C;
        v0[0x39] = 0x3D;
        v0[0x3A] = -1;
        v0[0x3B] = -1;
        v0[0x3C] = -1;
        v0[0x3D] = -1;
        v0[0x3E] = -1;
        v0[0x3F] = -1;
        v0[0x40] = -1;
        v0[0x42] = 1;
        v0[0x43] = 2;
        v0[0x44] = 3;
        v0[0x45] = 4;
        v0[0x46] = 5;
        v0[0x47] = 6;
        v0[0x48] = 7;
        v0[0x49] = 8;
        v0[0x4A] = 9;
        v0[0x4B] = 0xA;
        v0[0x4C] = 0xB;
        v0[0x4D] = 0xC;
        v0[0x4E] = 0xD;
        v0[0x4F] = 0xE;
        v0[0x50] = 0xF;
        v0[0x51] = 0x10;
        v0[0x52] = 0x11;
        v0[0x53] = 0x12;
        v0[0x54] = 0x13;
        v0[0x55] = 0x14;
        v0[0x56] = 0x15;
        v0[0x57] = 0x16;
        v0[0x58] = 0x17;
        v0[0x59] = 0x18;
        v0[0x5A] = 0x19;
        v0[0x5B] = -1;
        v0[0x5C] = -1;
        v0[0x5D] = -1;
        v0[0x5E] = -1;
        v0[0x5F] = -1;
        v0[0x60] = -1;
        v0[0x61] = 0x1A;
        v0[0x62] = 0x1B;
        v0[0x63] = 0x1C;
        v0[0x64] = 0x1D;
        v0[0x65] = 0x1E;
        v0[0x66] = 0x1F;
        v0[0x67] = 0x20;
        v0[0x68] = 0x21;
        v0[0x69] = 0x22;
        v0[0x6A] = 0x23;
        v0[0x6B] = 0x24;
        v0[0x6C] = 0x25;
        v0[0x6D] = 0x26;
        v0[0x6E] = 0x27;
        v0[0x6F] = 0x28;
        v0[0x70] = 0x29;
        v0[0x71] = 0x2A;
        v0[0x72] = 0x2B;
        v0[0x73] = 0x2C;
        v0[0x74] = 0x2D;
        v0[0x75] = 0x2E;
        v0[0x76] = 0x2F;
        v0[0x77] = 0x30;
        v0[0x78] = 0x31;
        v0[0x79] = 0x32;
        v0[0x7A] = 0x33;
        v0[0x7B] = -1;
        v0[0x7C] = -1;
        v0[0x7D] = -1;
        v0[0x7E] = -1;
        v0[0x7F] = -1;
        EncrptyUtil.base64DecodeChars = v0;
    }

    public EncrptyUtil() {
        super();
    }

    private static int decode(char arg3) {
        int v0;
        if (arg3 < 0x41 || arg3 > 0x5A) {
            if (arg3 >= 0x61 && arg3 <= 0x7A) {
                return arg3 - 0x47;
            }

            if (arg3 >= 0x30 && arg3 <= 0x39) {
                return arg3 + 4;
            }

            switch (arg3) {
                case 43: {
                    return 0x3E;
                }
                case 47: {
                    return 0x3F;
                }
                case 61: {
                    return 0;
                }
            }

            throw new RuntimeException("unexpected code: " + arg3);
//            return 0;
        } else {
            v0 = arg3 - 0x41;
        }

        return v0;
    }

    public static final String decode(String arg14, String arg15) {
        int v3;
        int v2;
        int v1;
        int v0;
        int v8;
        String v10;
        byte[] v5;
        try {
            v5 = arg14.getBytes(arg15);
        } catch (UnsupportedEncodingException v6) {
            v6.printStackTrace();
            v10 = null;
            return v10;
        }

        int v9 = v5.length;
        ByteArrayOutputStream v4 = new ByteArrayOutputStream(((int) ((((double) v9)) * 0.67)));
        int v7 = 0;
        while (v7 < v9) {
            do {
                v8 = v7;
                if (v8 < v9) {
                    v7 = v8 + 1;
                    v0 = EncrptyUtil.base64DecodeChars[v5[v8]];
                    if (v7 < v9) {
                        if (v0 == 0xFFFFFFFF) {
                            continue;
                        }

                        break;
                    }
                } else {
                    v0 = 0xFFFFFFFF;
                    v7 = v8;
                }

                if (v0 == 0xFFFFFFFF) {
                    break;
                }
            }
            while (true);

            do {
                v8 = v7;
                if (v8 < v9) {
                    v7 = v8 + 1;
                    v1 = EncrptyUtil.base64DecodeChars[v5[v8]];
                    if (v7 < v9) {
                        if (v1 == 0xFFFFFFFF) {
                            continue;
                        }

                        break;
                    }
                } else {
                    v1 = 0xFFFFFFFF;
                    v7 = v8;
                }

                if (v1 == 0xFFFFFFFF) {
                    break;
                }
            }
            while (true);

            v4.write(v0 << 2 | (v1 & 0x30) >>> 4);
            do {
                v8 = v7;
                if (v8 < v9) {
                    v7 = v8 + 1;
                    v2 = v5[v8];
                    if (v2 == 0x3D) {
                        v2 = 0xFFFFFFFF;
                    } else {
                        v2 = EncrptyUtil.base64DecodeChars[v2];
                        if (v7 < v9) {
                            if (v2 == 0xFFFFFFFF) {
                                continue;
                            }

                            break;
                        }
                    }
                } else {
                    v2 = 0xFFFFFFFF;
                    v7 = v8;
                }

                if (v2 == 0xFFFFFFFF) {
                    break;
                }
            }
            while (true);

            v4.write((v1 & 0xF) << 4 | (v2 & 0x3C) >>> 2);
            do {
                v8 = v7;
                if (v8 < v9) {
                    v7 = v8 + 1;
                    v3 = v5[v8];
                    if (v3 == 0x3D) {
                        v3 = 0xFFFFFFFF;
                    } else {
                        v3 = EncrptyUtil.base64DecodeChars[v3];
                        if (v3 == 0xFFFFFFFF) {
                            continue;
                        }

                        break;
                    }
                } else {
                    v3 = 0xFFFFFFFF;
                    v7 = v8;
                }

                if (v3 == 0xFFFFFFFF) {
                    break;
                }
            }
            while (true);

            v4.write((v2 & 3) << 6 | v3);
        }

        try {
            v10 = v4.toString(arg15);
        } catch (UnsupportedEncodingException v6) {
            v6.printStackTrace();
            v10 = null;
        }

        return v10;
    }

    private static void decode(String arg6, OutputStream arg7) throws IOException {
        int v5 = 0x3D;
        int v0 = 0;
        int v1 = arg6.length();
        while (true) {
            if (v0 < v1 && arg6.charAt(v0) <= 0x20) {
                ++v0;
                continue;
            }

            if (v0 != v1) {
                int v2 = (EncrptyUtil.decode(arg6.charAt(v0)) << 0x12) + (EncrptyUtil.decode(arg6.charAt(v0 + 1)) << 0xC) + (EncrptyUtil.decode(arg6.charAt(v0 + 2)) << 6) + EncrptyUtil.decode(arg6.charAt(v0 + 3));
                arg7.write(v2 >> 0x10 & 0xFF);
                if (arg6.charAt(v0 + 2) != v5) {
                    arg7.write(v2 >> 8 & 0xFF);
                    if (arg6.charAt(v0 + 3) != v5) {
                        arg7.write(v2 & 0xFF);
                        v0 += 4;
                        continue;
                    }
                }
            }

            return;
        }
    }

    public static byte[] decode(String arg7) {
        ByteArrayOutputStream v0 = new ByteArrayOutputStream();
        try {
            EncrptyUtil.decode(arg7, ((OutputStream) v0));
        } catch (IOException v2) {
            throw new RuntimeException();
        }

        byte[] v1 = v0.toByteArray();
        try {
            v0.close();
        } catch (IOException v3) {
            System.err.println("Error while decoding BASE64: " + v3.toString());
        }

        return v1;
    }

    public static final String encode(String arg1, String arg2) {
        return EncrptyUtil.encode(arg1, arg2, 0);
    }

    public static final String encode(String arg16, String arg17, int arg18) {
        int v0;
        int v3;
        byte[] v1;
        try {
            v1 = arg16.getBytes(arg17);
        } catch (UnsupportedEncodingException v2) {
            v2.printStackTrace();
            String v12 = null;
            return v12;
        }

        int v6 = v1.length;
        int v9 = ((int) Math.ceil((((double) v6)) * 1.36));
        int v11 = arg18 > 0 ? v9 / arg18 : 0;
        StringBuffer v8 = new StringBuffer(v9 + v11);
        int v7 = v6 % 3;
        int v5 = v6 - v7;
        int v4;
        for (v4 = 0; v4 < v5; ++v4) {
            v3 = v4 + 1;
            int v12_1 = (v1[v4] & 0xFF) << 0x10;
            v4 = v3 + 1;
            v0 = v12_1 | (v1[v3] & 0xFF) << 8 | v1[v4] & 0xFF;
            v8.append(EncrptyUtil.base64EncodeChars[v0 >> 0x12]);
            v8.append(EncrptyUtil.base64EncodeChars[v0 >> 0xC & 0x3F]);
            v8.append(EncrptyUtil.base64EncodeChars[v0 >> 6 & 0x3F]);
            v8.append(EncrptyUtil.base64EncodeChars[v0 & 0x3F]);
        }

        if (v7 == 1) {
            v0 = v1[v4] & 0xFF;
            v8.append(EncrptyUtil.base64EncodeChars[v0 >> 2]);
            v8.append(EncrptyUtil.base64EncodeChars[(v0 & 3) << 4]);
            v8.append("==");
        } else if (v7 == 2) {
            v0 = (v1[v4] & 0xFF) << 8 | v1[v4 + 1] & 0xFF;
            v8.append(EncrptyUtil.base64EncodeChars[v0 >> 0xA]);
            v8.append(EncrptyUtil.base64EncodeChars[v0 >> 4 & 0x3F]);
            v8.append(EncrptyUtil.base64EncodeChars[(v0 & 0xF) << 2]);
            v8.append("=");
        }

        if (v11 > 0) {
            char v10 = '\n';
            for (v3 = arg18; v3 < v8.length(); v3 = v3 + arg18 + 1) {
                v8.insert(v3, v10);
            }
        }

        return v8.toString();
    }

    public static String encode(byte[] arg8) {
        int v1;
        int v4 = arg8.length;
        StringBuffer v0 = new StringBuffer(arg8.length * 3 / 2);
        int v2 = v4 - 3;
        int v3;
        for (v3 = 0; v3 <= v2; v3 += 3) {
            v1 = (arg8[v3] & 0xFF) << 0x10 | (arg8[v3 + 1] & 0xFF) << 8 | arg8[v3 + 2] & 0xFF;
            v0.append(EncrptyUtil.legalChars[v1 >> 0x12 & 0x3F]);
            v0.append(EncrptyUtil.legalChars[v1 >> 0xC & 0x3F]);
            v0.append(EncrptyUtil.legalChars[v1 >> 6 & 0x3F]);
            v0.append(EncrptyUtil.legalChars[v1 & 0x3F]);
        }

        if (v3 == 0xFFFFFFFE + v4) {
            v1 = (arg8[v3] & 0xFF) << 0x10 | (arg8[v3 + 1] & 0xFF) << 8;
            v0.append(EncrptyUtil.legalChars[v1 >> 0x12 & 0x3F]);
            v0.append(EncrptyUtil.legalChars[v1 >> 0xC & 0x3F]);
            v0.append(EncrptyUtil.legalChars[v1 >> 6 & 0x3F]);
            v0.append("=");
        } else if (v3 == 0xFFFFFFFF + v4) {
            v1 = (arg8[v3] & 0xFF) << 0x10;
            v0.append(EncrptyUtil.legalChars[v1 >> 0x12 & 0x3F]);
            v0.append(EncrptyUtil.legalChars[v1 >> 0xC & 0x3F]);
            v0.append("==");
        }

        return v0.toString();
    }


    /**
     * 生成MD5
     *
     * @param message
     * @return 返回字符串全大写
     */
    public static String getMD5(String message) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // 创建一个md5算法对象
            byte[] messageByte = message.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte); // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte); // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if (num < 0) {
                num += 256;
            }
            if (num < 16) {
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

    /**
     * 对字符串BASE64加密
     *
     * @param s
     * @return
     */
    public static String getBASE64(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Base64.encodeToString(s.getBytes("utf-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] pwdHandler(String password) throws UnsupportedEncodingException {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuffer sb = new StringBuffer(PWD_SIZE);
        sb.append(password);
        while (sb.length() < PWD_SIZE) {
            sb.append("0");
        }
        if (sb.length() > PWD_SIZE) {
            sb.setLength(PWD_SIZE);
        }
        data = sb.toString().getBytes("UTF-8");
        return data;
    }

    public static String encryptBase64(String clearText, String password) {
        try {
            byte[] cipherTextBytes = encrypt(clearText.getBytes(CHARACTER), pwdHandler(password));
            String cipherText = Base64.encodeToString(cipherTextBytes, Base64.NO_WRAP);
            return cipherText;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt(byte[] clearTextBytes, byte[] pwdBytes) {
        try {
            // 1 获取加密密钥
            SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);

            // 2 获取Cipher实例
            Cipher cipher = Cipher.getInstance(CIPHER_MODE);

            // 查看数据块位数 默认为16（byte） * 8 =128 bit
//	            System.out.println("数据块位数(byte)：" + cipher.getBlockSize());

            // 3 初始化Cipher实例。设置执行模式以及加密密钥
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            // 4 执行
            byte[] cipherTextBytes = cipher.doFinal(clearTextBytes);

            // 5 返回密文字符集
            return cipherTextBytes;

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * BASE64解密
     *
     * @param s
     * @return
     */
    public static String decodeBASE64(String s) {
        if (s == null) {
            return null;
        }
        // 解码
        try {
            byte[] asBytes = Base64.decode(s, Base64.DEFAULT);
            return new String(asBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 对字符串数据进行MD5运算
     *
     * @param data
     * @return 返回字符串全小写
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            StringBuffer buf = new StringBuffer();
            byte[] bits = md.digest();
            for (int i = 0; i < bits.length; i++) {
                int a = bits[i];
                if (a < 0) a += 256;
                if (a < 16) buf.append("0");
                buf.append(Integer.toHexString(a));
            }
            return buf.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 对字符串数据进行SHA1哈希散列运算
     *
     * @param data
     * @return
     */
    public static String sha1(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }


}

