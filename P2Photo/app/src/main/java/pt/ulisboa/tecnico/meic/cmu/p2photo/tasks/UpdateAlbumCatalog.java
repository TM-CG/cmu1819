package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;

import static pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage.CATALOG_SUFFIX;

public class UpdateAlbumCatalog extends AsyncTask {
    public static final String TAG = "UpdateAlbumCatalog";

    @Override
    protected Object doInBackground(Object[] objects) {
        File catalogFile = (File) objects[0];

        Integer albumId = (Integer) objects[1];
        Log.i(TAG, "UpdateAlbumCatalog: AlbumID: " + albumId);
        String photoURL = (String) objects[2];
        Log.i(TAG, "UpdateAlbumCatalog: photoURL: " + photoURL);
        String fileName = String.format(CATALOG_SUFFIX, albumId);


        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        if (catalogFile != null) {
            //append new photoURL
            try {

                String keyFileName = albumId + "_key.txt";
                Log.d(TAG, "KeyFileName: " + keyFileName);

                String albumKey = Main.antiMirone.readKeyFromFile(Main.DATA_FOLDER + "/" + Main.username + "/" + keyFileName);
                SecretKeySpec albumKeySpec = Main.antiMirone.readKey2Bytes(albumKey);
                Main.antiMirone.decryptAlbumCatalog(catalogFile.getAbsolutePath(), albumKeySpec, Main.DATA_FOLDER + "/" + Main.username, fileName);


                File rootFolder = new File(Main.DATA_FOLDER, Main.username);
                File decryptedFile = new File(rootFolder, fileName);


                fileWriter = new FileWriter(decryptedFile.getAbsoluteFile(), true);
                bufferedWriter = new BufferedWriter(fileWriter);
                Log.i(TAG, "UpdateAlbumCatalog: Writing URL to catalog: " + photoURL);
                bufferedWriter.write(photoURL + "\n");
                bufferedWriter.close();

                String readKey = Main.antiMirone.readKeyFromFile(Main.DATA_FOLDER + "/" + Main.username + "/" + albumId + "_key.txt");
                SecretKeySpec spec = Main.antiMirone.readKey2Bytes(readKey);
                String encryptedFileURL = Main.antiMirone.encryptAlbumCatalog(decryptedFile.getAbsolutePath(), spec, fileName);

                return encryptedFileURL;


            } catch (IOException e) {
                Log.i(TAG, "UpdateAlbumCatalog: IOException");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } finally {

                try {

                    if (bufferedWriter != null)
                        bufferedWriter.close();

                    if (fileWriter != null)
                        fileWriter.close();

                } catch (IOException ex) {

                    Log.i(TAG, "UpdateAlbumCatalog: IOException when closing");

                }
            }
        }
        else {
            Log.i(TAG, "UpdateAlbumCatalog: catalogFile is null!");
        }

        if (catalogFile == null)
            return null;
        return catalogFile.getAbsolutePath();
    }
}