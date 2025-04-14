public class ServerFacade {
    private String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String register(String username, String password) {
        // Send a POST request to /register API.
        return "EMPTY";
    }

    public String login(String username, String password) {
        // Send a POST request to /login API.
        return "EMPTY";
    }

    public String quit() {
        //quits the program
        return "EMPTY";
    }
}

