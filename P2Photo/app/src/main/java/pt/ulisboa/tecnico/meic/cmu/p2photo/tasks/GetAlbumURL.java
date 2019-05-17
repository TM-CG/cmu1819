package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.dropbox.core.DbxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;

public class GetAlbumURL extends AsyncTask<Object, Object, Object[]> {
    private static final String TAG = GetAlbumURL.class.getName();
    private ServerConnector sv = Main.sv;

        @Override
        protected Object[] doInBackground(Object[] objects) {
            Integer albumID = (Integer) objects[1];
            Object[] result = new Object[2];
            result[0] = objects[0];

            TextView tv = (TextView) objects[0];
            try {
                List<String> urlList = sv.listUserAlbumSlices(albumID);
                if (urlList.size() > 0) {
                    String ownerURL = urlList.get(0);

                    Log.i("GetAlbumURL", "Link received from server: " + ownerURL);

                    try {
                        File path;
                        path = Environment.getExternalStoragePublicDirectory(
                                Main.CACHE_FOLDER + "/" + Main.username + "/tmp");

                        //Create tmp folder if not exists
                        path.mkdir();

                        File file = new File(path, albumID + "_catalog.txt");
                        String fileName = file.getName();

                        // Download the file.
                        /*try (OutputStream outputStream = new FileOutputStream(file)) {
                            DropboxClientFactory.getClient().sharing().getSharedLinkFile(ownerURL).download(outputStream);
                        }*/

                        Log.d(TAG, "I'm about to download file at: " + ownerURL + "&raw=1");
                        URL fileUrl = new URL(ownerURL + "&raw=1");

                        ReadableByteChannel readableByteChannel = Channels.newChannel(fileUrl.openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        FileChannel fileChannel = fileOutputStream.getChannel();

                        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                        String encryptedAlbumId = Main.sv.displayAlbumKey(albumID);
                        Log.d(TAG, "EncryptedAlbumId " + encryptedAlbumId);
                        String privateKey = Main.antiMirone.getPrivateKey();
                        Log.d(TAG, "PrivateKey " + privateKey);
                        String decryptedAlbumKey = Main.antiMirone.decryptAlbumKey(encryptedAlbumId, privateKey);
                        Log.d(TAG, "DecryptedAlbumKey " + decryptedAlbumKey);

                        SecretKeySpec albumKeySpec = Main.antiMirone.readKey2Bytes(decryptedAlbumKey);
                        Main.antiMirone.writeKey2File(Main.DATA_FOLDER + "/" + Main.username + "/" + albumID + "_key.txt", albumKeySpec.getEncoded());

                        //String albumKey = Main.antiMirone.readKeyFromFile(Main.DATA_FOLDER + "/" + Main.username + "/" + albumID + "_key.txt");
                        Main.antiMirone.decryptAlbumCatalog(file.getAbsolutePath(), albumKeySpec, Main.DATA_FOLDER + "/" + Main.username, fileName);

                        File decrypted = new File(Main.DATA_FOLDER + "/" + Main.username + "/" + albumID + "_catalog.txt");
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(decrypted));

                        String albumTitle = bufferedReader.readLine().split(" ")[1];
                        Log.i("GetAlbumURL", "Album title: " + albumTitle);
                        tv.setText(albumTitle);

                        result[1] = file;
                        return result;
                    } catch (IOException e) {
                        Log.i("GetAlbumURL", "Got an exception : " + e.getMessage());

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
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            } catch (P2PhotoException e) {
                e.printStackTrace();
            }
            return null;

        }
}
