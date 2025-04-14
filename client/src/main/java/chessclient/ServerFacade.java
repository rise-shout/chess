package chessclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import com.google.gson.Gson;

import com.google.gson.JsonObject;
import model.*;


import java.util.*;

public class ServerFacade {
    public final String serverUrl;
    private final Gson gson;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.gson = new Gson();
    }

    // Implement the register method similar to login
    public AuthData register(UserData userToAdd) throws Exception {
        // URL for the register endpoint
        String path = "/user";
        return this.makeRequest("POST", path, userToAdd, AuthData.class, null);

    }

    public AuthData login(UserData userToLogin) {
        // URL for the login endpoint
        try {
            String path = "/session";
            return this.makeRequest("POST", path, userToLogin, AuthData.class, null);
        } catch (Exception e){
            return null;
        }
    }

    public List<GameData> listGames(String authToken) {
        String path = "/game";

        record listGamesResponse(List<GameData> games) {}
        try {
            listGamesResponse response = this.makeRequest("GET", path, null, listGamesResponse.class, authToken);//FIXME
            return response.games;
        } catch (Exception e) {
            return null;
        }
    }

    // Method to create a game
    public int createGame(GameData gameToCreate, String username, String authToken) throws Exception {
        String path = "/game";
        //System.out.println("Auth Token in create game: " + authToken);
        GameData request = this.makeRequest("POST", path, gameToCreate, GameData.class, authToken);
        return request.gameID();


    }

    public void joinGame(String userAuthToken, int gameNumber, String color) throws Exception{
            String path = "/game";
            //System.out.println("Auth Token in create game: " + userAuthToken);
            this.makeRequestTwo("PUT", path, gameNumber, color, GameData.class, userAuthToken);

    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken); // Add the token to the header
                //System.out.println("Auth Token in make request: " + authToken);
            }

            //System.out.println();

            writeBody(request, http);

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception("500 error");
        }
    }

    private <T> void makeRequestTwo(String method, String path, Object request1, Object request2, Class<T> responseClass, String authToken) throws Exception {

            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
                //System.out.println("Auth Token in make request two: " + authToken);
            }

            // Combine request1 and request2 into a single JSON object
            writeBody(request1, request2, http);

            http.connect();
            throwIfNotSuccessful(http);
            readBody(http, responseClass);

    }

    private static void writeBody(Object request1, Object request2, HttpURLConnection http) throws IOException {
        if (request1 != null && request2 != null) {
            http.addRequestProperty("Content-Type", "application/json");

            // Combine the two objects into one JSON
            Gson gson = new Gson();
            // Create a JsonObject and add the parts
            JsonObject combinedJson = new JsonObject();
            combinedJson.add("gameID", new Gson().toJsonTree(request1));
            combinedJson.add("playerColor", new Gson().toJsonTree(request2));

            String combinedJsonString = combinedJson.toString();
            //System.out.println(combinedJsonString);

            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(combinedJsonString.getBytes());
            }
        }
    }



    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {


            http.addRequestProperty("Content-Type", "application/json");

            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException,Exception {
        int status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw new Exception("Not successful");
                }
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}


