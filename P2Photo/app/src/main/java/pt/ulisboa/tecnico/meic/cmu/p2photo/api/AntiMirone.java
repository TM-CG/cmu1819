package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;

/**
 * Class for describing AntiMirone Protection
 * Encryption of catalog files on Cloud mode
 */
public class AntiMirone {
    private final static String ALGORITHM = "RSA";

    private final static int KEY_BITS = 2048;

    private String privateKey;

    private String publicKey;

    public AntiMirone(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public AntiMirone() {

    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
        kpg.initialize(KEY_BITS);
        KeyPair kp = kpg.genKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();

        this.privateKey = Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);
        this.publicKey = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
    }

    public String encryptAlbumKey(String data, String publicKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, stringToPublicKey(publicKey));
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
    }

    public String decryptAlbumKey(String data, String privateKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, stringToPrivateKey(privateKey));
        byte[] decryptedBytes = cipher.doFinal(Base64.decode(data, Base64.NO_WRAP));
        return new String(decryptedBytes);
    }

    private PublicKey stringToPublicKey(String publicKeyString)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException {

        byte[] keyBytes = Base64.decode(publicKeyString, Base64.NO_WRAP);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    private PrivateKey stringToPrivateKey(String privateKeyString)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException {

        byte [] pkcs8EncodedBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        return kf.generatePrivate(keySpec);
    }

    public void writePrivateKey2File(String filePath) throws IOException {
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(this.privateKey);

        bufferedWriter.close();
    }

    public void writePublicKey2File(String filePath) throws IOException {
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(this.publicKey);

        bufferedWriter.close();
    }

    public void writeKey2File(String filePath, String key) throws IOException {
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(key);

        bufferedWriter.close();
    }

    public void writeKey2File(String filePath, byte[] keyArray) throws IOException {
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(Base64.encodeToString(keyArray, Base64.NO_WRAP));

        bufferedWriter.close();
    }

    public String readKeyFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String st, content = "";
        while ((st = bufferedReader.readLine()) != null) {
            content += st;
        }

        bufferedReader.close();
        return content;
    }

    public SecretKeySpec generateAlbumKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecretKey key = keyGenerator.generateKey();

        byte[] keyBytes = key.getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public SecretKeySpec readKey2Bytes(String key) throws NoSuchAlgorithmException {
        byte[] byteKey = Base64.decode(key.getBytes(), Base64.NO_WRAP);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, "AES");
        return keySpec;
    }

    public String encryptAlbumCatalog(String catalogFilePath, SecretKeySpec albumKey, String fileName)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, albumKey);
        byte[] iv = cipher.getIV();

        File catalog = new File(catalogFilePath);
        FileReader fileReader = new FileReader(catalog);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String st, content = "";
        while ((st = bufferedReader.readLine()) != null) {
            content += st;
        }

        byte[] ciphered = cipher.doFinal(content.getBytes());
        String base64Ciphered = Base64.encodeToString(ciphered, Base64.NO_WRAP);
        String base64iv = Base64.encodeToString(iv, Base64.NO_WRAP);

        File folder = new File(Main.CACHE_FOLDER + "/tmp");
        folder.mkdir();
        File file = new File(folder, fileName);
        file.createNewFile();



        /*FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(iv);
        outputStream.write(base64Ciphered.getBytes());
        outputStream.close();*/
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(base64iv + "\n");
        bufferedWriter.write(base64Ciphered);
        bufferedWriter.close();
        return Main.CACHE_FOLDER + "/tmp/" + fileName;
    }

    public void decryptAlbumCatalog(String catalogFilePath, SecretKeySpec albumKey, String pathToFile, String fileName)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException {



        File catalog = new File(catalogFilePath);
        FileReader fileReader = new FileReader(catalog);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        /*while ((st = bufferedReader.readLine()) != null) {
            content += st;
        }*/

        String iv = bufferedReader.readLine();
        String content = bufferedReader.readLine();

        byte[] ivBytes = Base64.decode(iv, Base64.NO_WRAP);
        byte[] contentBytes = Base64.decode(content, Base64.NO_WRAP);

        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");


            final GCMParameterSpec spec = new GCMParameterSpec(128, ivBytes, 0, 12);
            cipher.init(Cipher.DECRYPT_MODE, albumKey, spec);


            byte[] deciphered = cipher.doFinal(contentBytes);
            String fileContent = new String(deciphered);

            File folder = new File(pathToFile);
            folder.mkdir();
            File file = new File(folder, fileName);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(fileContent);
            bufferedWriter.close();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

}
