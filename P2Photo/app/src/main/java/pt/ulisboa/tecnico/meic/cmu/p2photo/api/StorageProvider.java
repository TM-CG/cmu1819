package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.DropboxActivity;
import pt.ulisboa.tecnico.meic.cmu.p2photo.activities.Main;

/**
 * Abstract class for describing generic StorageProvider
 */
public abstract class StorageProvider extends DropboxActivity implements Runnable{
    private static final String TAG = StorageProvider.class.getName();
    static final String CATALOG_TMP_FILE = "_catalog.txt";
    static final String LINE_SEP = System.getProperty("line.separator");

    public enum Operation {READ, WRITE}

    private Context context;

    /** Just for find the correct file **/
    private int albumId;

    private AlbumCatalog catalog;

    private Operation operation;

    //Generic write and read methods
    //This methods are executed AFTER temp file is created!
    abstract void writeFile(String fileURL);
    abstract String readFile(String fileURL);

    private StorageProvider(Context context, AlbumCatalog catalog) {
        this.context = context;
        this.catalog = catalog;
    }

    private StorageProvider(Context context, int albumId) {
        this.context = context;
        this.albumId = albumId;
    }

    public StorageProvider(Context context, AlbumCatalog catalog, Operation operation) {
        this(context, catalog);
        this.operation = operation;
    }

    public StorageProvider(Context context, int albumId, Operation operation) {
        this(context, albumId);
        this.operation = operation;
    }

    public Context getContext() {
        return context;
    }

    public AlbumCatalog getCatalog() {
        return catalog;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public void run() {

        Looper.prepare();

        switch (operation) {
            case READ:
                Scanner scanner = null;
                StringBuilder sb = new StringBuilder();

                //Creates a temporary file
                FileInputStream fis = null;
                try {
                    fis = context.openFileInput(albumId + CATALOG_TMP_FILE);
                    // scanner does mean one more object, but it's easier to work with
                    scanner = new Scanner(fis);
                    while (scanner.hasNextLine()) {
                        sb.append(scanner.nextLine() + LINE_SEP);
                    }

                    //parse to Object
                    catalog = AlbumCatalog.parseToAlbumCatalog(sb.toString());
                } catch (IOException e) {
                    Log.i("StorageProvider", "Failed to read temp file");
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException e) {
                        Log.i("StorageProvider", "Close error");
                    }
                }

                break;

            case WRITE:
                String rep = catalog.getAlbumId() + " " + catalog.getAlbumTitle() + "\n\n";

                // === 1. Creates a temporary file ===
                FileOutputStream fos = null;
                try {
                    fos = context.openFileOutput(catalog.getAlbumId() + CATALOG_TMP_FILE, Context.MODE_PRIVATE);
                    fos.write(rep.getBytes());
                } catch (IOException e) {
                    Log.i("StorageProvider", "Failed to write temp file");
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.i("StorageProvider", "Close error");
                    }
                }

                // === 2. Perform action after writing ===
                String filePath = context.getFileStreamPath(catalog.getAlbumId() + CATALOG_TMP_FILE).getAbsolutePath();
                Log.i("StorageProvider", filePath);
                writeFile(filePath);
                break;
        }

    }

    public class AddAlbumSliceCatalogURL extends AsyncTask {

        private ServerConnector sv = Main.sv;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i(TAG, "Starting doInBackground");
            try {
                int albumId = (int) objects[0];
                String sliceURL = (String) objects[1];

                sv.acceptIncomingRequest(albumId, sliceURL);
                Log.i(TAG, "Accepted my own request!");

            } catch (P2PhotoException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
