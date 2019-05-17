package pt.ulisboa.tecnico.meic.cmov;

import java.io.Serializable;

/**
 * A Class for describing User
 */
public class User implements Serializable {
    private String username;

    private String password;

    private String publicKey;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String publicKey) {
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
