package pt.ulisboa.tecnico.meic.cmu.p2photo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import pt.ulisboa.tecnico.meic.cmu.p2photo.R;

/**
 * A List Photo Adapter for List Photo Thumbnails
 */
public class ListPhotoAdapter extends BaseAdapter {

    private final Context context;

    /** The directory of pictures **/
    private File directory;

    /** Size of thumbnail pictures on albums directory **/
    private static final int THUMBNAIL_SIZE = 180;

    public ListPhotoAdapter(Context context, String pathToDirectory) {
        this.context = context;
        this.directory = new File(pathToDirectory);
        //if album is empty then create folder just to display empty content
        if (!this.directory.exists())
            this.directory.mkdir();
    }

    @Override
    public int getCount() {
        //Log.i("ListPhotoAdapter", new Boolean(directory == null).toString());
        return directory.listFiles().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        File[] files = directory.listFiles();

        Bitmap myBitmap = BitmapFactory.decodeFile(files[position].getAbsolutePath());

        //vitor: just for debug
        ImageView myImage = new ImageView(context);
        myImage.setImageBitmap(myBitmap);
        Log.i("ListPhotoAdapter", "Path2Pic: " + files[position].getAbsolutePath());
        myImage.setTag(files[position].getAbsolutePath());
        //define the thumbnail size
        myImage.setLayoutParams(new LinearLayout.LayoutParams(THUMBNAIL_SIZE,THUMBNAIL_SIZE));
        return myImage;
    }
}
