import chessclient.Repl;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting the ♕ 240 Chess Client...\n\n\n\n");

        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();

    }
}