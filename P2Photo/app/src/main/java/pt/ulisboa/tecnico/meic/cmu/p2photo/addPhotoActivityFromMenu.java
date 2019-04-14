package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class addPhotoActivityFromMenu extends DropboxActivity {

    private List<String> albums;
    private ArrayAdapter<String> spinnerArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo_from_menu);

        Spinner sel_album = (Spinner) findViewById(R.id.sel_album);

        albums = new ArrayList<String>();

        spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, albums);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view

        sel_album.setAdapter(spinnerArrayAdapter);
    }

    public void cancel(View view){
        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }

    public void create(View view){
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }

    public void selectPhoto(View view) {
        Intent intent = new Intent(this, selectPhotoActivity.class);
        startActivityForResult(intent, 14);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            /*Choose select photo option*/
            case 14:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Photo selected successfully",
                            Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Photo selection aborted",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void downloadFile(FileMetadata file) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Reading catalogs");
        dialog.show();

        new DownloadFileTask(this, DropboxClientFactory.getClient(), new DownloadFileTask.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                dialog.dismiss();
                if(result != null) {
                    try {

                        BufferedReader br = new BufferedReader(new FileReader(result));
                        String st;
                        while ((st = br.readLine()) != null) {
                            Log.d("readFile", st);
                            st = st.replace(System.getProperty("line.separator"), "");
                            if(! albums.contains(st)) {
                                albums.add(st);
                            }
                            break;
                        }
                        Log.d("albumsTeste", albums.toString());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();

                Log.i("download", "fail");

            }
        }).execute(file);

    }

    @Override
    protected void loadData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading");
        dialog.show();

        new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult result) {
                dialog.dismiss();
                if(result != null) {
                    try {
                        for(Metadata m : result.getEntries()){
                            if(m.getName().endsWith("_catalog.txt")) {
                                downloadFile((FileMetadata) m);
                            }
                        }

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();


            }
        }).execute("");
    }
}
