package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AntiMirone;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

public class AntiMironeInitKeys extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {
        try {
            String path2PrivKey = Main.DATA_FOLDER + "/" + Main.username + "/privatekey.txt";
            String path2PubKey = Main.DATA_FOLDER + "/" + Main.username + "/publickey.txt";
            File folder = new File(Main.DATA_FOLDER + "/" + Main.username);
            folder.mkdir();
            File privateKey = new File(folder, "privatekey.txt");

            Main.antiMirone = new AntiMirone();

            if (!privateKey.exists()) {
                privateKey.createNewFile();
                Main.antiMirone.generateKeyPair();
                Main.antiMirone.writePrivateKey2File(path2PrivKey);
                Main.antiMirone.writePublicKey2File(path2PubKey);
            } else {
                //Private key already exists let's read it
                String privK = Main.antiMirone.readKeyFromFile(path2PrivKey);
                Main.antiMirone.setPrivateKey(privK);
                String pubK = Main.antiMirone.readKeyFromFile(path2PubKey);
                Main.antiMirone.setPublicKey(pubK);
            }

            String publicKey = Main.antiMirone.readKeyFromFile(path2PubKey);
            Main.sv.addUserPublicKey(publicKey);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (P2PhotoException e) {
            e.printStackTrace();
        }
        return null;
    }
}
