package sshkey;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//below 4 packages are from ibm jdk
//oracle have the same classes but in different package
import com.ibm.misc.BASE64Decoder;
import com.ibm.misc.BASE64Encoder;
import com.ibm.security.util.DerInputStream;
import com.ibm.security.util.DerValue;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PPKConvert {

	public static void main(String[] args) {
		String filePath = "C:\\temp\\id_rsa";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String keyinfo = "";
			String line = null;
			// 去掉文件头尾的注释信息
			while ((line = br.readLine()) != null) {
				if (line.indexOf("---") == -1) {
					keyinfo += line;
				}
			}
			// 密钥信息用 BASE64 编码加密过，需要先解密
			byte[] decodeKeyinfo = (new BASE64Decoder()).decodeBuffer(keyinfo);
			// 使用 DerInputStream 读取密钥信息
			DerInputStream dis = new DerInputStream(decodeKeyinfo);
			// 密钥不含 otherPrimeInfos 信息，故只有 9 段
			DerValue[] ders = dis.getSequence(9);
			// 依次读取 RSA 因子信息
			int version = ders[0].getBigInteger().intValue();
			BigInteger modulus = ders[1].getBigInteger();
			BigInteger publicExponent = ders[2].getBigInteger();
			BigInteger privateExponent = ders[3].getBigInteger();
			BigInteger primeP = ders[4].getBigInteger();
			BigInteger primeQ = ders[5].getBigInteger();
			BigInteger primeExponentP = ders[6].getBigInteger();
			BigInteger primeExponentQ = ders[7].getBigInteger();
			BigInteger crtCoefficient = ders[8].getBigInteger();

			//below are generate public key string for ppk
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			String keyAlgo = "ssh-rsa";
			// 写入该元素长度信息
			bos.write(convertIntToByteArray(keyAlgo.length()));
			// 写入该元素的字节信息
			bos.write(keyAlgo.getBytes());
			bos.write(convertIntToByteArray(publicExponent.toByteArray().length));
			bos.write(publicExponent.toByteArray());
			bos.write(convertIntToByteArray(modulus.toByteArray().length));
			bos.write(modulus.toByteArray());
			bos.flush();
			String keyInfo = (new BASE64Encoder()).encode(bos.toByteArray());
			//the raw info is used for ppk mac generation 
			byte[] ppkPublicRaw = bos.toByteArray();
			String ppkPubKeyEncrypted = Base64.getEncoder().encodeToString(ppkPublicRaw);
			bos.close();

			//auto break the key , each line should only have 64 chars
			int publicKeyLine = ppkPubKeyEncrypted.length() % 64 == 0 ? ppkPubKeyEncrypted.length() / 64
					: ppkPubKeyEncrypted.length() / 64 + 1;

			String ppkPublicOut = "";
			for (int i = 0; i < publicKeyLine; i++) {
				if ((i + 1) * 64 < ppkPubKeyEncrypted.length()) {
					ppkPublicOut += ppkPubKeyEncrypted.substring(i * 64, i * 64 + 64) + "\n";
				} else {
					ppkPublicOut += ppkPubKeyEncrypted.substring(i * 64)+"\n";
				}
			}
			// System.out.println(ppkPublicOut);

			//below is to build ppk private key string
			bos = new ByteArrayOutputStream();
			bos.write(convertIntToByteArray(privateExponent.toByteArray().length));
			bos.write(privateExponent.toByteArray());
			bos.write(convertIntToByteArray(primeP.toByteArray().length));
			bos.write(primeP.toByteArray());
			bos.write(convertIntToByteArray(primeQ.toByteArray().length));
			bos.write(primeQ.toByteArray());
			bos.write(convertIntToByteArray(crtCoefficient.toByteArray().length));
			bos.write(crtCoefficient.toByteArray());
			bos.flush();
			byte[] ppkPrivRaw = bos.toByteArray();
			String ppkPrivKeyencrypted = Base64.getEncoder().encodeToString(bos.toByteArray());
			bos.close();

			//auto line break for private key 
			int privateKeyLine = ppkPrivKeyencrypted.length() % 64 == 0 ? ppkPrivKeyencrypted.length() / 64
					: ppkPrivKeyencrypted.length() / 64 + 1;

			String ppkPrivateOut = "";
			for (int i = 0; i < privateKeyLine; i++) {
				if ((i + 1) * 64 < ppkPrivKeyencrypted.length()) {
					ppkPrivateOut += ppkPrivKeyencrypted.substring(i * 64, i * 64 + 64) + "\n";
				} else {
					ppkPrivateOut += ppkPrivKeyencrypted.substring(i * 64)+"\n";
				}
			}
			
			// System.out.println(ppkPrivateOut);
			String ppkOut = "";
			String algName = "ssh-rsa";
			String encryption = "none";
			String comment = "convert by java";
			
			//below is to build mac string and run hmac sha1 encryption
			bos = new ByteArrayOutputStream();
			bos.write(convertIntToByteArray(algName.length()));
			bos.write(algName.getBytes());
			bos.write(convertIntToByteArray(encryption.length()));
			bos.write(encryption.getBytes());
			bos.write(convertIntToByteArray(comment.length()));
			bos.write(comment.getBytes());
			bos.write(convertIntToByteArray(ppkPublicRaw.length));
			bos.write(ppkPublicRaw);
			bos.write(convertIntToByteArray(ppkPrivRaw.length));
			bos.write(ppkPrivRaw);
			bos.flush();
			byte[] macRaw = bos.toByteArray();
			bos.close();
			byte[] macByte = HmacSHA1Encrypt(macRaw, "putty-private-key-file-mac-key");
			//System.out.println(toHexString(macByte));
			
			//build ppk key
			ppkOut += String.format("PuTTY-User-Key-File-2: %s\n", algName);
			ppkOut += String.format("Encryption: %s\n",encryption);
			ppkOut += String.format("Comment: %s\n", comment);
			ppkOut += String.format("Public-Lines: %d\n", publicKeyLine);
			ppkOut += ppkPublicOut;
			ppkOut += String.format("Private-Lines: %d\n", privateKeyLine);
			ppkOut += ppkPrivateOut;
			ppkOut += String.format("Private-MAC: %s", toHexString(macByte));
			System.out.println(ppkOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] convertIntToByteArray(int value) {
		byte[] bytevalue = new byte[4];
		bytevalue[0] = (byte) ((value >> 24) & 0xFF);
		bytevalue[1] = (byte) ((value >> 16) & 0xFF);
		bytevalue[2] = (byte) ((value >> 8) & 0xFF);
		bytevalue[3] = (byte) (value & 0xFF);
		return bytevalue;
	}

	public static byte[] HmacSHA1Encrypt(byte[] encryptText, String encryptKey) throws Exception {
		String MAC_NAME = "HmacSHA1";
		String ENCODING = "UTF-8";
		
		// 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
		SecretKey secretKey = new SecretKeySpec(encode(encryptKey), MAC_NAME);
		// 生成一个指定 Mac 算法 的 Mac 对象
		Mac mac = Mac.getInstance(MAC_NAME);
		// 用给定密钥初始化 Mac 对象
		mac.init(secretKey);

		//byte[] text = encryptText.getBytes(ENCODING);
		// 完成 Mac 操作
		return mac.doFinal(encryptText);
	}

	public static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();

		for (byte b : bytes) {
			formatter.format("%02x", b);
		}

		return formatter.toString();
	}

    public static byte[] encode(String str) throws NoSuchAlgorithmException{
        if (str == null) {
            return null;
        }
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes());
            return messageDigest.digest();
    }
}
