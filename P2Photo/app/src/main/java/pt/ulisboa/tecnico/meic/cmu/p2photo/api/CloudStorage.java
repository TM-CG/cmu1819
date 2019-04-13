package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.content.Context;

/**
 * Support for storing catalog files in the cloud
 */
public class CloudStorage extends StorageProvider {

    public CloudStorage(Context context, AlbumCatalog catalog, Operation operation) {
        super(context, catalog, operation);
    }

    @Override
    boolean writeFile(String fileName) {
        return false;
    }

    @Override
    String readFile(String fileName) {
        return null;
    }
}
