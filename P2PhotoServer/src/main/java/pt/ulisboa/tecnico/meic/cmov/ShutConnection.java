package pt.ulisboa.tecnico.meic.cmov;

public class ShutConnection extends Instruction {

    ShutConnection() {
        super("SHUT");
    }

    @Override
    public String execute() {
        return "SHUT OK";
    }
}
