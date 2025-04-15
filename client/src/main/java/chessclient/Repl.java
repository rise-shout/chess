package chessclient;

import chessclient.ChessClient;
import websocket.NotificationHandler;
import websocket.messages.ServerMessage.Notification;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        if(serverUrl == null) {
            serverUrl = "http://localhost:8080";
        }
        client = new ChessClient(serverUrl, this);
    }

    public void run() throws Exception {
        String[] argsToPass = new String[0];
        ChessClient.main(argsToPass);
        /*
        System.out.println("\uD83D\uDC36 Welcome to the pet store. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();

         */


    }

    public void notify(Notification notification) {
        //System.out.println(RED + notification.message());
        System.out.println(notification.message());
        //printPrompt();
    }

    private void printPrompt() {
        //System.out.print("\n" + RESET + ">>> " + GREEN);
        System.out.print("\n>>> ");
    }

}

