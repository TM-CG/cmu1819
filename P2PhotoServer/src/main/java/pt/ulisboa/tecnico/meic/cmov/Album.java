package pt.ulisboa.tecnico.meic.cmov;

import javafx.util.Pair;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Album implements Serializable {

    public static final String NOT_AVAILABLE_URL = "NA";

    private int ID;

    private List<Pair<String, String>> indexes;

    private HashMap<String, String> albumKeys;

    public Album(int ID, User owner) {
        synchronized(this) {
            this.ID = ID;
            this.indexes = new ArrayList<>();
            this.indexes.add(new Pair(owner.getUsername(), null));
            this.albumKeys = new HashMap<>();
        }
    }

    public Album(int ID, User owner, String ownerURL) {
        synchronized(this) {
            this.ID = ID;
            this.indexes = new ArrayList<>();
            this.indexes.add(new Pair(owner.getUsername(), ownerURL));
            this.albumKeys = new HashMap<>();
        }
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
     * @return the URL of the album index, null if its pending or "NA" if user doesnt participate in this album
     */
    public String getIndexOfUser(String username) {
        for (Pair<String, String> p: this.indexes) {
            if (p.getKey().equals(username))
                return p.getValue();
        }

        return "NA";
    }

    /**
     * Displays a list of all users' album slice urls.
     * @return the list of all user's album slice urls
     */
    public List<String> getAlbumSlicesURLs() {
        List<String> urls = new ArrayList<>();
        for (Pair<String, String> albumSlices: indexes) {
            if (albumSlices.getValue() != null)
                urls.add(albumSlices.getValue());
        }
        return urls;
    }

    /**
     * Given a username of a participant of this album replace the current URL
     * @param username
     */
    public void setIndexOfParticipant(String username, String directoryCloudURL) {
        int position = removeIndexOfParticipant(username);

        synchronized (indexes) {
            if (position != -1)
                indexes.add(position, new Pair<>(username, directoryCloudURL));
            else indexes.add(new Pair<>(username, directoryCloudURL));
            }
    }

    public int removeIndexOfParticipant(String username) {
        int position = -1, i;
        synchronized (indexes) {
            i = 0;
            Iterator<Pair<String, String>> iterator = indexes.iterator();
            while(iterator.hasNext()) {
                if (iterator.next().getKey().equals(username)) {
                    iterator.remove();
                    position = i;
                    break;
                }
                i++;
            }
        }
        return position;
    }

    /**
     * Return all members that were added by the owner but not accept the invitation yet
     * @return the number of members that were added by the owner but not accept the invitation yet
     */
    public int getNumberOfPendingParticipants() {
        int counter = 0;
        for (Pair<String, String> pair: indexes) {
            if (pair.getValue() == null)
                counter++;
        }
        return counter;
    }

    /**
     * Returns the number of users that contributes to this album even the ones in pending state
     * @return the number of users belonging to this album
     */
    public int getTotalNumberOfParticipants() {
        return this.indexes.size();
    }

    /**
     * Returns the real number of users participating in the album excluding the pending ones
     * @return the number of users belonging to this album
     */
    public int getNumberOfParticipants() {
        return this.indexes.size() - getNumberOfPendingParticipants();
    }

    /**
     * Adds an album key encrypted with username public key
     * @param username to encrypt the album's key
     * @param key the album's key encrypted with username public key
     */
    public void addAlbumKey(String username, String key) {
        this.albumKeys.put(username, key);
    }

    /**
     * Returns the albumKey encrypted with username public key
     * @param username the username of the requester
     * @return key album key encrypted with requester public key
     */
    public String getAlbumKeyOfUser(String username) {
        return this.albumKeys.get(username);
    }
}
