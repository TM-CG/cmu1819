package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.R;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

public class AddUsersToAlbum extends AsyncTask<Object,Void,Object[]> {
    @Override
    protected Object[] doInBackground(Object[] o) {
        Integer albumId = null;
        ArrayList<String> items2 = null;

        if (o != null) {
            albumId = (Integer) o[0];
            items2 = (ArrayList<String>) o[1];

            if (items2 != null) {
                for (String item : items2) {
                    try {
                        Log.d("inviteAlbum", item);
                        Main.getSv().updateAlbum(albumId, item);
                        Cache.getInstance().clientLog.add(Main.username + " added user " + item + "to an album with id " + albumId +
                                " at"  + new Timestamp(System.currentTimeMillis()));

                        if (Main.STORAGE_TYPE == Main.StorageType.CLOUD) {
                            //Encrypt the album key with the participant public key
                            String participantPublicKey = Main.sv.getUserPublicKey(item);

                            String albumKey = Main.antiMirone.readKeyFromFile(Main.DATA_FOLDER + "/" + Main.username + "/" + albumId + "_key.txt");

                            String encriptedAlbumKey = Main.antiMirone.encryptAlbumKey(albumKey, participantPublicKey);

                            Main.sv.addAlbumKey(albumId, item, encriptedAlbumKey);
                        }

                    } catch (P2PhotoException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Cache.getInstance().loadingSpinner(false);
        //TODO
        return null;
    }

    @Override
    protected void onPostExecute(Object[] result) {
        //TODO
    }
}
