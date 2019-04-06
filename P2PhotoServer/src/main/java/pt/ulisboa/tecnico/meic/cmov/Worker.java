package pt.ulisboa.tecnico.meic.cmov;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Class for describing the Worker of the Server in order to respond to clients requests.
 */
public class Worker extends Thread {
    private PrintWriter out;
    private BufferedReader in;
    final Socket s;
    private Server server;

    public Worker(Server server) {
        this.s = server.getClientSocket();
        try {
            this.out = new PrintWriter(this.s.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
        } catch (IOException e) {
            System.err.println("** WORKER: Constructor Streams error");
	    return;
	}
        this.server = server;
    }

    @Override
    public void run() {
        String message, response;
        Instruction instruction;
        List<String> args;

        while(true) {
            try {
                message = in.readLine();

                //parse the instruction that came from the client
                args = parseArgs(message);

                //parse to instruction
                instruction = parseInstruction(args);
                
		        //execute instruction
                response = instruction.execute();

                out.println(response);
		        out.flush();

                //User request shut of channel
                if (response.equals("SHUT OK"))
                {
                    this.out.close();
                    this.in.close();
                    this.s.close();
                    return;
                }

            } catch (IOException e) {
                System.err.println("** WORKER: IOException when Worker is running!");
            	return;
            } catch (NullPointerException e) {
                System.err.println("** WORKER: Invalid instruction nice try :)");
                System.out.println();
                System.out.flush();
                try {
                    this.out.close();
                    this.in.close();
                    this.s.close();
                } catch(IOException f) {
                    System.err.println("** WORKER: IOException when terminating by invalid instruction!");
                }
                return;
            }
        }

    }

    /**
     * Given an instruction (String) parses it according to the several criteria.
     * @param instruction
     * @return A list of string with the arguments
     */
    private List<String> parseArgs(String instruction) {
        List<String> args = null;
        if (instruction.startsWith("ALB")) {
            //Split by space ignoring spaces inside quotes
            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(instruction);

            args = new ArrayList<>();
            while (m.find())
                args.add(m.group(1).replace("\"", ""));
        }
        else {
            args = Arrays.asList(instruction.split(" "));
        }
        return args;
    }

    /**
     * Given a list of arguments and a function execute it
     * @param args list of arguments
     * @return the output of the execution
     */
    private Instruction parseInstruction(List<String> args) {


        String instruction = args.get(0);

        switch (instruction) {
            case "LOGIN"  : return new LogIn           (args, server);
            case "SIGNUP" : return new SignUp          (args, server);
            case "LOGOUT" : return new LogOut          (args, server);
            case "ALB-CR8": return new CreateAlbum     (args, server);
            case "USR-FND": return new FindUser        (args, server);
            case "USR-IRQ": return new IncomingRequests(args, server);
            case "USR-URQ": return new UpdateRequests  (args, server);
            case "ALB-LST": return new ListAlbum       (args, server);
            case "ALB-AUP": return new UpdateAlbum     (args, server);
            case "ALB-UAS": return new ListAlbumSlices (args, server);
            case "SHUT"   : return new ShutConnection  ();
            case "VER"    : return new Version         ();

            //debug operation: to be removed in a real case scenario
            case "DBG-STA": return new DebugStatus     (args, server);
            case "DBG-RST": return new DebugReset      (args, server);
        }


        return null;
    }
}
