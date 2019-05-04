package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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

    protected Object[] args;

    //Generic write and read methods
    //This methods are executed AFTER temp file is created!
    abstract void writeFile(String fileURL);
    abstract AlbumCatalog readFile(String fileURL, String description, String folderPath, String fileName, int option);

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

    public StorageProvider(Context context, AlbumCatalog catalog, Operation operation, Object[] args) {
        this(context, catalog);
        this.operation = operation;
        this.args = args;
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
                catalog = readFile((String) args[0], (String) args[1], (String) args[2], (String) args[3], (Integer) args[4]);
                

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



}
