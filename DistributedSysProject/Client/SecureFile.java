import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.io.IOException;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.util.Arrays;
import java.util.Base64;

public class SecureFile{

    private String algo;
    private String path;
    static SecretKeySpec secretKey;

    public SecureFile(String algo,String path) {
        this.algo = algo; //setting algo
        this.path = path;//setting file path
    }

    public static void setKey(String myKey){
        MessageDigest sha = null;
        try {
            byte[] key = myKey.getBytes("UTF-8");
            System.out.println(key.length);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            System.out.println(key.length);
            System.out.println(new String(key,"UTF-8"));
            secretKey = new SecretKeySpec(key, "DES");

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void encrypt() throws Exception{
        //creating and initialising cipher and cipher streams
        Cipher encrypt =  Cipher.getInstance(algo);
        encrypt.init(Cipher.ENCRYPT_MODE, secretKey);
        //opening streams
        FileOutputStream fos =new FileOutputStream(path+".enc");
        try(FileInputStream fis =new FileInputStream(path)){
            try(CipherOutputStream cout=new CipherOutputStream(fos, encrypt)){
                copy(fis,cout);
            }
        }
    }

    public void decrypt() throws Exception{
        //creating and initialising cipher and cipher streams
        Cipher decrypt =  Cipher.getInstance(algo);
        decrypt.init(Cipher.DECRYPT_MODE, secretKey);
        //opening streams
        FileInputStream fis = new FileInputStream(path);
        try(CipherInputStream cin=new CipherInputStream(fis, decrypt)){
            try(FileOutputStream fos =new FileOutputStream(path.substring(0,path.lastIndexOf(".")))){
                copy(cin,fos);
            }
        }
    }

    private void copy(InputStream is,OutputStream os) throws Exception{
        byte buf[] = new byte[4096];  //4K buffer set
        int read = 0;
        while((read = is.read(buf)) != -1)  //reading
            os.write(buf,0,read);  //writing
    }

    /**
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String encryptFile (String username, String path) throws Exception {
        try{
            String filePath = "";
            // Checks if a password was entered in the password field for storing
            File fs = new File(username + "-PrivateKey.txt");
            if (fs.isFile()) {
                filePath = fs.getAbsolutePath();
            } else {
                FileDialog fd = new FileDialog(new JFrame());
                fd.setVisible(true);
                File[] f = fd.getFiles();
                if (f.length > 0) {
                    filePath = fd.getFiles()[0].getAbsolutePath();
                    System.out.println(fd.getFiles()[0].getAbsolutePath());
                }
            }

            BufferedReader reader = new BufferedReader( new FileReader(filePath));
            byte[] encoded = Base64.getDecoder().decode(reader.readLine());
            System.out.println(filePath + encoded.toString());
            secretKey = new SecretKeySpec(encoded, "AES");
            new SecureFile("AES/ECB/PKCS5Padding", path).encrypt();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return "File Encrypted";
    }
    /**
     *
     * @param path
     * @return
     * @throws Exception
     */

    public String decryptFile (String username, String path) throws Exception {
        try{
            String filePath = "";
            // Checks if a password was entered in the password field for storing
            File fs = new File(username + "-PrivateKey.txt");
            if (fs.isFile()) {
                filePath = fs.getAbsolutePath();
            } else {
                FileDialog fd = new FileDialog(new JFrame());
                fd.setVisible(true);
                File[] f = fd.getFiles();
                if (f.length > 0) {
                    filePath = fd.getFiles()[0].getAbsolutePath();
                    System.out.println(fd.getFiles()[0].getAbsolutePath());
                }
            }
            BufferedReader reader = new BufferedReader( new FileReader(filePath));
            byte[] encoded = reader.readLine().getBytes();
            System.out.println(filePath + encoded.toString());
            secretKey = new SecretKeySpec(encoded, "AES");
            new SecureFile("AES/ECB/PKCS5Padding", path).decrypt();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return "File Decrypted";
    }

    public static SecretKey loadKey(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        byte[] encoded = Base64.getDecoder().decode(reader.readLine());

        SecretKey aesKey = new SecretKeySpec(encoded, "DES");
        return aesKey;
    }
}
