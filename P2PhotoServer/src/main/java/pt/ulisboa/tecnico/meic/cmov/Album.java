package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Album {

    public static int CounterID = 1;

    private int ID;

    private String title;

    private List<Pair<String, String>> indexes;

    public Album(int ID, String title, User owner) {
        this.ID = ID;
        this.title = title;
        this.indexes = new ArrayList<>();
    }

    public Album(int ID, String title, User owner, String ownerURL) {
        this.ID = ID;
        this.title = title;
        this.indexes = new ArrayList<>();
        this.indexes.add(new Pair(owner.getUsername(), ownerURL));
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return this.indexes.get(0).getKey();
    }

    public String getOwnerURL() {return this.indexes.get(0).getValue();}

    public void setOwner(String owner) {
        this.indexes.set(0, new Pair<>(owner, getOwnerURL()));
    }

    public void setOwnerURL(String ownerURL) {
        this.indexes.set(0, new Pair<>(getOwner(), ownerURL));
    }

    public List<Pair<String, String>> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Pair<String, String>> indexes) {
        this.indexes = indexes;
    }

    /**
     * Adds an user to an album.
     * @param username to be added
     * @param userURL the URL of the index
     */
    public void addUserPermission(String username, String userURL) {
        this.indexes.add(new Pair<>(username, userURL));
    }

    /**
     * Returns the index URL of a given user of this album.
     * @param username of the user to search
     * @return the URL of the album index
     */
    public String getIndexOfUser(String username) {
        for (Pair<String, String> p: this.indexes) {
            if (p.getKey().equals(username))
                return p.getValue();
        }

        return null;
    }

    /**
     * Displays a list of all users' album slice urls.
     * @return the list of all user's album slice urls
     */
    public List<String> getAlbumSlicesURLs() {
        List<String> urls = new ArrayList<>();
        for (Pair<String, String> albumSlices: indexes) {
            urls.add(albumSlices.getValue());
        }
        return urls;
    }

    /**
     * Returns the number of users that contributes to this album
     * @return the number of users belonging to this album
     */
    public int getNumberOfParticipants() {
        return this.indexes.size();
    }
}
