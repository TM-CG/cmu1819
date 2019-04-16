package pt.ulisboa.tecnico.meic.cmu.p2photo;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class Cache extends AppCompatActivity {

    //all about the albums
    public static ArrayList<Integer> albumsIDs;
    public static ArrayList<String> albums;
    public static ArrayAdapter<String> adapterTitle;
    public static ListView albumsList;
    //albums but using spinner
    public static ArrayAdapter<String> itemsAdapterSpinner;
    public static Spinner lvItemsSpinner;

    private static Cache single_instance = null;

    private Cache(){
        albums = new ArrayList<String>();
        albumsIDs = new ArrayList<Integer>();
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
    }

}
