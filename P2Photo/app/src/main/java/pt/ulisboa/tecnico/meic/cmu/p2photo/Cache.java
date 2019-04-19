package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Cache extends AppCompatActivity {

    //all about the albums
    public static List<Integer> ownedAlbumsIDs; /*just the owned albums*/
    public static List<Integer> ownedAndPartAlbumsIDs; /*owned + participating albums*/
    public static ArrayList<Integer> albumsIDs;
    public static List<String> ownedAlbums;
    public static List<String> ownedAlbumWithIDs;/*to help add photo*/
    public static List<String> ownedAndPartAlbums;
    public static ArrayList<String> albums; /*all albums fetched from dropbox*/
    public static ArrayAdapter<String> adapterTitle;
    public static ListView albumsList;
    //albums but using spinners
    public static ArrayAdapter<String> itemsAdapterSpinner;
    public static Spinner lvItemsSpinner;
    public static ArrayAdapter<String> spinnerArrayAdapter;
    public static Spinner sel_album;

    private static Cache single_instance = null;

    private Cache(){
        albums = new ArrayList<String>();
        ownedAlbums = new ArrayList<String>();
        ownedAndPartAlbums = new ArrayList<String>();
        albumsIDs = new ArrayList<Integer>();
        ownedAlbumsIDs = new ArrayList<Integer>();
        ownedAndPartAlbumsIDs = new ArrayList<Integer>();
        ownedAlbumWithIDs = new ArrayList<String>();

    }

    public static Cache getInstance() {
        if(single_instance == null) {
            single_instance = new Cache();
        }
        return single_instance;
    }

    //update the adapters
    public void notifyAdapters() {
        try {
            adapterTitle.notifyDataSetChanged();
        } catch(Exception  e){
            //TODO
        }
        try {
            itemsAdapterSpinner.notifyDataSetChanged();
        } catch(Exception e) {
            //TODO
        }
        try {
            spinnerArrayAdapter.notifyDataSetChanged();
        } catch(Exception e) {
            //TODO
        }
    }
    //To be called when context is changed
    public void cleanArrays() {
        albums = new ArrayList<String>();
        ownedAlbums = new ArrayList<String>();
        ownedAndPartAlbums = new ArrayList<String>();
        albumsIDs = new ArrayList<Integer>();
        ownedAlbumsIDs = new ArrayList<Integer>();
        ownedAndPartAlbumsIDs = new ArrayList<Integer>();
        ownedAlbumWithIDs = new ArrayList<>();
    }


}
