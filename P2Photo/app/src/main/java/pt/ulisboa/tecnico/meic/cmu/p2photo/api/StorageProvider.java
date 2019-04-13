package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Abstract class for describing generic StorageProvider
 */
public abstract class StorageProvider implements Runnable{

    static final String CATALOG_TMP_FILE = "catalog.txt";
    static final String LINE_SEP = System.getProperty("line.separator");

    public enum Operation {READ, WRITE}

    private Context context;
    private AlbumCatalog catalog;
    private Operation operation;

    //Generic write and read methods
    abstract boolean writeFile(String fileName);
    abstract String readFile(String fileName);

    private StorageProvider(Context context, AlbumCatalog catalog) {
        this.context = context;
        this.catalog = catalog;
    }

    public StorageProvider(Context context, AlbumCatalog catalog, Operation operation) {
        this(context, catalog);
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
        switch (operation) {
            case READ:
                Scanner scanner = null;
                StringBuilder sb = new StringBuilder();

                //Creates a temporary file
                FileInputStream fis = null;
                try {
                    fis = context.openFileInput(CATALOG_TMP_FILE);
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
                        fis.close();
                    } catch (IOException e) {
                        Log.i("StorageProvider", "Close error");
                    }
                }

                break;

            case WRITE:
                String rep = catalog.getAlbumId() + " " + catalog.getAlbumTitle() + "\n\n";

                //Creates a temporary file
                FileOutputStream fos = null;
                try {
                    fos = context.openFileOutput(CATALOG_TMP_FILE, Context.MODE_PRIVATE);
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

                break;
        }
    }

}
