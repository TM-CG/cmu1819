package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private int ID;

    private String title;

    private User owner;

    private List<Pair<String, String>> indexes;

    public Album(int ID, String title, User owner) {
        this.ID = ID;
        this.title = title;
        this.owner = owner;
        this.indexes = new ArrayList<>();
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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
}
