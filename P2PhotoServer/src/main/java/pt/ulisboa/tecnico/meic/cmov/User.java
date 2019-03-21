package pt.ulisboa.tecnico.meic.cmov;

/**
 * A Class for describing User
 */
public class User {
    private String username;

    private String password;

    private String cloudURL;

    public User(String username, String password, String cloudURL) {
        this.username = username;
        this.password = password;
        this.cloudURL = cloudURL;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.cloudURL = "";
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

    public String getCloudURL() {
        return cloudURL;
    }

    public void setCloudURL(String cloudURL) {
        this.cloudURL = cloudURL;
    }
}
