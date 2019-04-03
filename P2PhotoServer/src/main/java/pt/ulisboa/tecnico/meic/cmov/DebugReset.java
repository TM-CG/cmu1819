package pt.ulisboa.tecnico.meic.cmov;

import java.util.List;

/**
 * Debug operation to reset the server state
 */
public class DebugReset extends Instruction {

    DebugReset(List<String> args, Server server) {
        super("DBG-RST", args, server);
    }

    @Override
    public String execute() {

        server.reset();

        return "OK";
    }
}
