package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.content.Context;

import java.util.concurrent.ExecutionException;

import pt.ulisboa.tecnico.meic.cmu.p2photo.tasks.AddAlbumSliceCatalogURL;

/**
 * Support for reading and writing files locally on the device in order to use Wi-Fi Direct file
 * transfer
 */
public class LocalStorage extends StorageProvider {

    /** Define the URL of the catalog to be sent to the server **/
    private static final String LOCAL_URL = "lan://local";

    public LocalStorage(Context context, AlbumCatalog catalog, Operation operation) {
        super(context, catalog, operation);
    }

    public LocalStorage(Context context, int albumId, Operation operation) {
        super(context, albumId, operation);
    }

    public LocalStorage(Context context, AlbumCatalog catalog, Operation operation, Object[] args) {
        super(context, catalog, operation, args);
    }

    @Override
    void writeFile(String fileURL) {
        //just to accept my own invitation in order to be considered as the owner of my album
        try {
            new AddAlbumSliceCatalogURL().execute(getCatalog().getAlbumId(), LOCAL_URL).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    AlbumCatalog readFile(String fileURL, String description, String folderPath, String fileName, int option) {
        return null;
    }

    @Override
    protected void loadData() {

    }
}
