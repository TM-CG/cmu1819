package pt.ulisboa.tecnico.meic.cmov;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Instruction {

    static final String API_VERSION = "0.7";

    /** Debug verbose messages **/
    static final String VERBOSE_NOK1 = "User %s does not exist!";
    static final String VERBOSE_NOK2 = "User %s failed login!";
    static final String VERBOSE_NOK3 = "Username %s already exists!";
    static final String VERBOSE_NOK4 = "Invalid sessionID!";
    static final String VERBOSE_NOK5 = "Invalid albumID!";
    static final String VERBOSE_NOK6 = "User %s does not exists or it is the owner when trying to add to album.";
    static final String VERBOSE_NOK7 = "URL %s is invalid!";

    //Server responses must follow API!
    static final String ERR     = "ERR"    ;
    static final String NOK_1   = "NOK 1"  ;
    static final String NOK_2   = "NOK 2"  ;
    static final String NOK_3   = "NOK 3"  ;
    static final String NOK_4   = "NOK 4"  ;
    static final String NOK_5   = "NOK 5"  ;
    static final String NOK_6   = "NOK 6"  ;
    static final String NOK_7   = "NOK 7"  ;
    static final String OK_PLUS = "OK "    ;
    static final String OK      = "OK"     ;
    static final String SHUT_OK = "SHUT OK";

    /** For debug messages with time**/
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    /** The instruction name **/
    protected String name;

    /** List of arguments **/
    protected List<String> args;

    protected Server server;

    Instruction() {
        this.name = "";
        this.args = new ArrayList<>();
        this.server = null;
    }

    Instruction(String name) {
        this.name = name;
        this.args = new ArrayList<>();
        this.server = null;
    }


    Instruction(String name, List<String> args) {
        this.name = name;
        this.args = args;
        this.server = null;
    }

    Instruction(String name, List<String> args, Server server) {
        this.name = name;
        this.args = args;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    /** Logic of instruction **/
    public abstract String execute();

    /**
     * Displays a debug message if server enabled the verbose debug option
     * @param message to be displayed
     */
    void displayDebug(String message) {
        if (this.server.isVerboseDebugEnabled())
            System.out.printf("**\t[%s]\t" + name + ":\t" + message + "\n", dateFormat.format(new Date()));
    }

    void displayDebug(String message, Object... arguments) {
        if (this.server.isVerboseDebugEnabled()) {
            System.out.printf(String.format("**\t[%s]\t" + name + ":\t", dateFormat.format(new Date())) + message + "\n", arguments);
        }
    }

}
