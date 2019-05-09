package pt.ulisboa.tecnico.meic.cmu.p2photo.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import pt.ulisboa.tecnico.meic.cmu.p2photo.UriHelpers;

/**
 * An AsyncTask to copy local files
 */
public class LocalFileCopy extends AsyncTask<String, Void, Void> {
    private static final String TAG = LocalFileCopy.class.getName();


    private Context context;

    public LocalFileCopy(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String sourceURI = params[0];
        String destURI = params[1];

        File source = UriHelpers.getFileForUri(context, Uri.parse(sourceURI));

        File destFolder = new File(destURI);
        destFolder.mkdir();

        File dest = new File(destURI + "/" + source.getName());

        Log.d(TAG, "Source Path: " + source.getAbsolutePath());
        Log.d(TAG, "Dest Path: " + dest.getAbsolutePath());

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
