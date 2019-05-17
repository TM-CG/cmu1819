package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import pt.ulisboa.tecnico.meic.cmu.p2photo.Cache;
import pt.ulisboa.tecnico.meic.cmu.p2photo.DropboxClientFactory;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.ListPhoto;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.AddAlbumSliceCatalogURL;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.AddUsersToAlbum;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.CreateFolder;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.DownloadFileFromLink;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.ShareLink;
import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.UploadFile;

/**
 * Support for storing catalog files in the cloud
 */
public class CloudStorage extends StorageProvider {

    /** Prefix of catalog files **/
    public static final String CATALOG_SUFFIX = "%d_catalog.txt";

    private static final String TAG = CloudStorage.class.getName();

    public CloudStorage(Context context, AlbumCatalog catalog, Operation operation) {
        super(context, catalog, operation);
    }

    public CloudStorage(Context context, int albumId, Operation operation) {
        super(context, albumId, operation);
    }

    public CloudStorage(Context context, AlbumCatalog catalog, Operation operation, Object[] args) {
        super(context, catalog, operation, args);
    }

    @Override
    void writeFile(final String fileURL) {

        final int albumId = getCatalog().getAlbumId();
        final String albumTitle = getCatalog().getAlbumTitle();
        //Writing file to a cloud provider is equivalent to upload it

        //vitor: i just remove the dialog because CreateAlbum closes so fast that dialog is running
        //only after this activity closes! :)

        /*final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Writing catalog file");
        dialog.show();*/

        //ENCRYPT FILE BEFORE SEND IT TO CLOUD
        //Generate Album Key
        String encryptedFileURL = null;
        try {
            String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            SecretKeySpec keyAlbum = Main.antiMirone.generateAlbumKey();
            encryptedFileURL = Main.antiMirone.encryptAlbumCatalog(fileURL, keyAlbum, fileName);

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
        }

        FileMetadata result = null;
        try {
            result = new UploadFile(getContext(), DropboxClientFactory.getClient(), new UploadFile.Callback() {

                @Override
                public void onUploadComplete(FileMetadata result) {
                    String message = result.getName() + " size " + result.getSize() + " modified " +
                            DateFormat.getDateTimeInstance().format(result.getClientModified());
                    /*Toast.makeText(getContext(), message, Toast.LENGTH_SHORT)
                            .show();*/
                    Log.i(TAG, message);
                    //After upload let's share the catalog in order to every with the link be able
                    //to read it

                }


                @Override
                public void onError(Exception e) {
                    //dialog.dismiss();

                    Log.i(TAG, "Failed to upload file.", e);
                    /*Toast.makeText(getContext(),
                            "An error has occurred",
                            Toast.LENGTH_SHORT)
                            .show();*/

                }

                //wait until the execution finishes
            }).execute(encryptedFileURL, "").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        SharedLinkMetadata linkMetadata = null;
        try {
            linkMetadata = new ShareLink(getContext(), DropboxClientFactory.getClient(), new ShareLink.Callback() {
                @Override
                public void onShareComplete(SharedLinkMetadata result) {
                    Log.i(TAG, "Successfully generated link to shared file: " + result.getUrl());

                    //Set that url to the server

                }

                @Override
                public void onError(Exception e) {
                    Log.i(TAG, "There was an error in generating the shared link!");
                }
                //need to wait this thread until asynctask finishes
            }).execute(result).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        try {
            new AddAlbumSliceCatalogURL().execute(albumId, linkMetadata.getUrl()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //create a new folder
        String albumName = albumId + " " + albumTitle;
        new CreateFolder().execute(albumName, getContext());
        String[] splited = albumName.split(" ");
        Cache.getInstance().albumsIDs.add(Integer.parseInt(splited[0]));
        Cache.getInstance().albums.add(splited[1]);
        Cache.getInstance().ownedAndPartAlbumsIDs.add(Integer.parseInt(splited[0]));
        Cache.getInstance().ownedAndPartAlbums.add(splited[1]);
        Cache.getInstance().ownedAlbumsIDs.add(Integer.parseInt(splited[0]));
        Cache.getInstance().ownedAlbums.add(splited[1]);
        Cache.getInstance().ownedAlbumWithIDs.add(splited[0] + " " + splited[1]);
        //add users to albums
        try {
            new AddUsersToAlbum().execute(args).get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    AlbumCatalog readFile(String fileURL, String description, String folderPath, String fileName, int option) {


        if (fileName == null) //get name from url
        {
            if (fileURL.contains("?"))
                fileName = fileURL.substring(fileURL.lastIndexOf('/'), fileURL.indexOf('?'));
            else fileName = fileURL.substring(fileURL.lastIndexOf('/'));
        }

        Log.i(TAG, "FileName: " + fileName);

        DownloadFileFromLink dft = new DownloadFileFromLink(getContext(), DropboxClientFactory.getClient(), new DownloadFileFromLink.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                //dialog.dismiss();

            }

            @Override
            public void onError(Exception e) {
                //dialog.dismiss();

                Log.i(TAG, "Failed to download file.", e);
                Toast.makeText(getContext(),
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        File result = null;
        try {
            result = dft.execute(fileURL, folderPath, fileName).get();

            //DOWNLOAD CATALOGS

            if (option == 1) {
                FileReader fr = new FileReader(result);
                BufferedReader br = new BufferedReader(fr);

                String line, catalog = "";

                while ((line = br.readLine()) != null) {
                    catalog += line + "\n";
                }
                Log.i(TAG, "Catalog content: " + catalog);

                AlbumCatalog albumCatalog = AlbumCatalog.parseToAlbumCatalog(catalog);
                return albumCatalog;
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void loadData() {

    }
}

