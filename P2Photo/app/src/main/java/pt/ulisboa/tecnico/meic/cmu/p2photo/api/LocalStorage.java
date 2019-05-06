package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.content.Context;

/**
 * Support for reading and writing files locally on the device in order to use Wi-Fi Direct file
 * transfer
 */
public class LocalStorage extends StorageProvider {

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
        //no need to do any thing else beside write the temporary file :)
    }

    @Override
    AlbumCatalog readFile(String fileURL, String description, String folderPath, String fileName, int option) {
        return null;
    }

    @Override
    protected void loadData() {

    }
}
