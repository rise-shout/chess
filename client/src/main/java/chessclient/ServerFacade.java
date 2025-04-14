package chessclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import com.google.gson.Gson;

import main.exception.ResponseException;
import model.*;
import server.GameListResult;
import service.GameRequest;
import service.GameResponse;
import service.JoinGameRequest;


import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServerFacade {
    public final String serverUrl;
    private final Gson gson;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
        this.gson = new Gson();
    }

    // Implement the register method similar to login
    public UserData register(UserData userToAdd) throws Exception {
        // URL for the register endpoint
        var path = "/user";
        return this.makeRequest("POST", path, userToAdd, UserData.class);

    }

    public UserData login(UserData userToLogin) throws Exception {
        // URL for the login endpoint
        String path = "/session";
        return this.makeRequest("POST", path, userToLogin, UserData.class);
    }

    public List<GameData> listGames(String authToken) throws Exception {
        String endpoint = serverUrl + "/game";
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Set up the connection
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", authToken); // Add the auth token
            connection.setDoInput(true);

            int statusCode = connection.getResponseCode();

            InputStream responseStream = (statusCode == 200) ? connection.getInputStream() : connection.getErrorStream();

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                StringBuilder responseBuilder = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }

                if (statusCode == 200) {
                    // Deserialize the JSON response into a GameListResult
                    GameListResult gameListResult = gson.fromJson(responseBuilder.toString(), GameListResult.class);
                    return gameListResult.games(); // Extract the list of games
                } else {
                    throw new Exception("Failed to retrieve games: " + responseBuilder);
                }
            }
        } finally {
            connection.disconnect();
        }
    }

    // Method to create a game
    public int createGame(String authToken, String gameName) throws Exception {
        String endpoint = serverUrl + "/game";
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Set up the connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", authToken);  // Set the authorization token
            connection.setDoOutput(true);

            // Create the request body
            GameRequest gameRequest = new GameRequest(gameName);
            String jsonRequest = gson.toJson(gameRequest);

            // Write the JSON to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code and handle accordingly
            int statusCode = connection.getResponseCode();
            InputStream responseStream = (statusCode == 200) ? connection.getInputStream() : connection.getErrorStream();

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                StringBuilder responseBuilder = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }

                if (statusCode == 200) {
                    // Successfully created game, parse the response and return game ID
                    GameResponse gameResponse = gson.fromJson(responseBuilder.toString(), GameResponse.class);
                    return gameResponse.gameID();  // Return the created game ID
                } else {
                    throw new Exception("Failed to create game: " + responseBuilder);
                }
            }
        } finally {
            connection.disconnect();
        }
    }

    public void joinGame(String userAuthToken, int gameNumber, String color) throws Exception{
        String endpoint = serverUrl + "/game";
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();


        try {
            // Set up the connection
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", userAuthToken);  // Set the authorization token
            connection.setDoOutput(true);

            // Create the request body
            JoinGameRequest joinRequest = new JoinGameRequest(color, gameNumber);
            String jsonRequest = gson.toJson(joinRequest);

            // Write the JSON to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code and handle accordingly
            int statusCode = connection.getResponseCode();
            InputStream responseStream = (statusCode == 200) ? connection.getInputStream() : connection.getErrorStream();

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                StringBuilder responseBuilder = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }

                if (statusCode == 200) {
                    System.out.println("Joined game successfully.");
                } else {
                    throw new Exception("Failed to join game: " + responseBuilder);
                }
            }
        } finally {
            connection.disconnect();
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
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

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
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


