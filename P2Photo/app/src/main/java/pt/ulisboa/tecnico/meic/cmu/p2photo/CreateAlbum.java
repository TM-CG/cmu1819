package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import pt.ulisboa.tecnico.meic.cmu.p2photo.api.AlbumCatalog;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.CloudStorage;
import pt.ulisboa.tecnico.meic.cmu.p2photo.api.StorageProvider;

public class CreateAlbum extends AppCompatActivity {

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

        //TODO: hardcoded to be changed
        AlbumCatalog catalog = new AlbumCatalog(1, "Album do zé");

        new Thread(new CloudStorage(CreateAlbum.this, catalog, StorageProvider.Operation.WRITE), "WritingThread").start();

        CloudStorage cs = new CloudStorage(CreateAlbum.this, 1, StorageProvider.Operation.READ);
        new Thread(cs, "ReadingThread").start();

        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }


}
