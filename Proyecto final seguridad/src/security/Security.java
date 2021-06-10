package security;
import java.io.Serializable;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Security implements Serializable{
private static final long serialVersionUID = 1L;
private static final String secretKeyAES = "anzelo";
private static final String saltAES = "anzelo1234";

public Security() { }
	    
	    public String getAES(String data) {
	        try {
	            byte[] iv = new byte[16];
	            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
	            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	            KeySpec keySpec = new PBEKeySpec(secretKeyAES.toCharArray(), saltAES.getBytes(), 65536, 256);
	            SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
	            SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
	            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes("UTF-8")));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    
	    public String getAESDecrypt(String data) {
	        byte[] iv = new byte[16];
	        try {
	            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
	            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	            KeySpec keySpec = new PBEKeySpec(secretKeyAES.toCharArray(), saltAES.getBytes(), 65536, 256);
	            SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
	            SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
	            return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	
}
