package pt.ulisboa.tecnico.meic.cmu.p2photo.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class for treating AlbumCatalog as objects
 */
public class AlbumCatalog {

    private int albumId;

    private String albumTitle;

    /** List of URLS that points to the pictures **/
    private List<String> paths2Pics;

    public AlbumCatalog(int albumId, String albumTitle) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.paths2Pics = new ArrayList<>();
    }

    public AlbumCatalog(int albumId, String albumTitle, List<String> paths2Pics) {
        this.albumId = albumId;
        this.albumTitle = albumTitle;
        this.paths2Pics = paths2Pics;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public List<String> getPaths2Pics() {
        return paths2Pics;
    }

    public void setPaths2Pics(List<String> paths2Pics) {
        this.paths2Pics = paths2Pics;
    }

    public void addURL(String url) {
        this.paths2Pics.add(url);
    }

    @Override
    public String toString() {
        String representation = albumId + " " + albumTitle + "\n\n";

        for (String url : paths2Pics) {
            representation += url + "\n";
        }

        return representation;
    }

    /**
     * Given a representation parse it to an AlbumCatalog
     * @param representation
     * @return
     */
    public static AlbumCatalog parseToAlbumCatalog(String representation) {
        int albumId = -1;
        String albumTitle = null;
        ArrayList<String> urls = new ArrayList<>();

        Scanner scanner = new Scanner(representation);
        int lineNo = 1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            switch (lineNo) {
                case 1: //first line
                    int firstSpace = line.indexOf(' ');
                    albumId = Integer.parseInt(line.substring(0, firstSpace));
                    albumTitle = line.substring(firstSpace + 1);
                    break;

                case 2: break; //just ignore second line

                default:
                    urls.add(line);
            }
            lineNo++;

        }
        scanner.close();

        Log.i("AlbumCatalog", "ID: " + albumId);
        Log.i("AlbumCatalog", "TITLE: " + ((albumTitle == null) ? "null" : albumTitle));

        if ((albumId != -1) && (albumTitle != null))
            return new AlbumCatalog(albumId, albumTitle, urls);
        else return null;
    }
}
