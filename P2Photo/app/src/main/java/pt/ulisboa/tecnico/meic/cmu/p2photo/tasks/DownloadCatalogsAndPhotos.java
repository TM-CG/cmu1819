package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.v2.DbxClientV2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.DropboxActivity;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.ListPhoto;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.adapters.ListPhotoAdapter;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;

public class DownloadCatalogsAndPhotos extends AsyncTask<Object, String, String> {

    private static final String TAG = DownloadCatalogsAndPhotos.class.getName();

    private DropboxActivity activity;
    private ListPhotoAdapter adapter;

    public DownloadCatalogsAndPhotos(DropboxActivity activity, ListPhotoAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    protected String doInBackground(Object... params) {
        int albumId = (Integer) params[0];
        String albumTitle = (String) params[1];
        String tmpFolderPath = albumId + " "
                + albumTitle;

        int i = 1;

        List<String> catalogsURL = null;
        try {
            Log.i(TAG, "Started fetching all catalogs");
            catalogsURL = Main.sv.listUserAlbumSlices(albumId);


        List<String> picsURLs = new ArrayList<>();
        //Download album catalogs from server's link
            Log.i(TAG, "Started downloading all catalogs from link");
            if (catalogsURL != null) {
                for (String url : catalogsURL) {
                    picsURLs.addAll(downloadFile(url, "Loading catalogs", "", "tmp" + i++ + "_catalog.txt", 1, albumId));
                }
                Log.i(TAG, "Finished downloading and parsing catalogs! I've " + picsURLs.size() + " picture(s)!");

                if (picsURLs != null) {
                    //Download all pics from all album catalogs that were previously downloaded
                    for (String pictureURL : picsURLs) {
                        downloadFile(pictureURL, "Loading pictures", tmpFolderPath, null, 0, albumId);
                    }
                }
                Log.i(TAG, "Finished downloading pictures!");
            }
        } catch (P2PhotoException e) {
            e.printStackTrace();
        }

        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

        return null;
    }

    private interface Callback {
        void onDownloadComplete(File result);
        void onError(Exception e);
    }


    private List<String> downloadFile(String url, String description, String folderPath, String fileName, int option, int albumId) {

        if (fileName == null) //get name from url
        {
            if (url.contains("?"))
                fileName = url.substring(url.lastIndexOf('/'), url.indexOf('?'));
            else fileName = url.substring(url.lastIndexOf('/'));
        }

        Log.i(TAG, "FileName: " + fileName);

        DownloadFileFromLink dft = new DownloadFileFromLink(this.activity, DropboxClientFactory.getClient(), new DownloadFileFromLink.Callback() {
            @Override
            public void onDownloadComplete(File result) {


            }

            @Override
            public void onError(Exception e) {

            }
        });

        File result = null;
        try {
            result = downloadFileFromLink(this.activity, DropboxClientFactory.getClient(), new Callback() {
                @Override
                public void onDownloadComplete(File result) {

                }

                @Override
                public void onError(Exception e) {

                }
            }, url, folderPath, fileName);

            //DOWNLOAD CATALOGS

            if (option == 1) {


                try {
                    String fileNameCatalog = result.getName();
                    String keyFileName = albumId + "_key.txt";
                    Log.d(TAG, "KeyFileName: " + keyFileName);

                    String albumKey = Main.antiMirone.readKeyFromFile(Main.DATA_FOLDER + "/" + Main.username + "/" + keyFileName);
                    SecretKeySpec albumKeySpec = Main.antiMirone.readKey2Bytes(albumKey);
                    Main.antiMirone.decryptAlbumCatalog(result.getAbsolutePath(), albumKeySpec, Main.DATA_FOLDER + "/" + Main.username, fileName);


                    File rootFolder = new File(Main.DATA_FOLDER, Main.username);
                    File decryptedFile = new File(rootFolder, fileNameCatalog);
                    BufferedReader br = new BufferedReader(new FileReader(decryptedFile));

                    String line, catalog = "";

                    while ((line = br.readLine()) != null) {
                        catalog += line + "\n";
                    }

                    Log.i(TAG, "Catalog content: " + catalog);

                    AlbumCatalog albumCatalog = AlbumCatalog.parseToAlbumCatalog(catalog);
                    return albumCatalog.getPaths2Pics();

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
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private File downloadFileFromLink(final Context context, final DbxClientV2 dbxClient, final Callback callback, String...params) {
        String url = params[0];
        String folderPath = params[1];
        String fileName = params[2];
        Exception mException;

        try {
            File path;
            if (folderPath == "") {
                path = new File (Main.CACHE_FOLDER + "/" + Main.username);
            } else {
                path = new File(Main.CACHE_FOLDER + "/" + Main.username + "/" + folderPath);
            }
            File file = new File(path, fileName);

            // Make sure the Downloads directory exists.
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    mException = new RuntimeException("Unable to create directory: " + path);
                    return null;
                }
            } else if (!path.isDirectory()) {
                mException = new IllegalStateException("Download path is not a directory: " + path);
                return null;
            }

            // Download the file.

            Log.d(TAG, "I'm about to download file at: " + url + "&raw=1");
            URL fileUrl = new URL(url + "&raw=1");

            ReadableByteChannel readableByteChannel = Channels.newChannel(fileUrl.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            FileChannel fileChannel = fileOutputStream.getChannel();

            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            return file;
        } catch (IOException e) {
            mException = e;
        }

        return null;
    }
}
