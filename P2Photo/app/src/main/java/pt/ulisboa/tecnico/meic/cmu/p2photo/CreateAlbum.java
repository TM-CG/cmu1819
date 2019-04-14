package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.ListFolderResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.P2PhotoException;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.ServerConnector;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;

public class CreateAlbum extends AppCompatActivity {
    EditText album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
    }

    public void cancel(View view){
        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    public void create(View view){
        album = (EditText) findViewById(R.id.nameInput);

        //create a new folder
        new CreateFolderTask().execute(album.getText().toString(),getApplicationContext());

        //creates album on the server
        new CreateAlbumOnServer().execute();

        /*CloudStorage cs = new CloudStorage(CreateAlbum.this, 1, StorageProvider.Operation.READ);
        new Thread(cs, "ReadingThread").start();*/
    }

    class CreateAlbumOnServer extends AsyncTask {

        private ServerConnector sv = MainActivity.sv;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            Integer albumId = null;

            if (o != null) {
                albumId = (Integer) o;
            } else {
                Log.i("CreateAlbumOnServer", "albumId is null");
                return;
            }

            String albumTitle = album.getText().toString();

            //create album catalog for new album
            AlbumCatalog catalog = new AlbumCatalog(albumId, albumTitle);

            new Thread(new CloudStorage(CreateAlbum.this, catalog, StorageProvider.Operation.WRITE), "WritingThread").start();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                return sv.createAlbum();
            } catch (P2PhotoException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}


class CreateFolderTask extends AsyncTask<Object,Object,Object[]> {

    @Override
    protected Object[] doInBackground(Object[] objects) {
        String albumName = (String) objects[0];
        Object[] result = new Object[2];
        result[0] = objects[1];
        try {
            CreateFolderResult res = DropboxClientFactory.getClient().files().createFolderV2("/" + albumName);
            result[1] = "OK";
            return result;
        } catch (DbxException e1) {
            result[1] = "NOK";
            return result;
        }
    }

    @Override
    protected void onPostExecute(Object[] result) {
        String res = (String) result[1];
        if(res == "OK"){
            Toast.makeText((Context)result[0], "Album created in your dropbox",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText((Context)result[0], "Album NOT created in your dropbox",
                    Toast.LENGTH_LONG).show();
        }
    }


}
