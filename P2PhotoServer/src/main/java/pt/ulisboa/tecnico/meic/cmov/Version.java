package pt.ulisboa.tecnico.meic.cmov;

/**
 * Displays the current version of the API running on the server
 */
public class Version extends Instruction {

    Version() {
        super("VER");
    }

    @Override
    public String execute() {
        return OK_PLUS + API_VERSION;
    }
}
