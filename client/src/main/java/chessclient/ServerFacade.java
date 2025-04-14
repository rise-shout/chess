package chessclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;

import model.GameData;
import server.GameListResult;
import server.UserController;
import service.*;

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
    public RegisterResult register(RegisterRequest request) throws Exception {
        // URL for the register endpoint
        String endpoint = serverUrl + "/user";
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Set up the connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Convert
            String jsonRequest = gson.toJson(request);

            // Write
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
                    // Success: Parse and return the result
                    return gson.fromJson(responseBuilder.toString(), RegisterResult.class);
                } else {
                    // Failure: Throw an exception with the error message
                    throw new Exception("Registration failed: " + responseBuilder);
                }
            }
        } finally {
            connection.disconnect();
        }
    }

    public LoginResult login(LoginRequest request) throws Exception {
        // URL for the login endpoint
        String endpoint = serverUrl + "/session";
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Set up the connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Convert
            String jsonRequest = gson.toJson(request);

            // Write
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
                    // Success: Parse and return the result
                    return gson.fromJson(responseBuilder.toString(), LoginResult.class);
                } else {
                    // Failure: Throw an exception with the error message
                    throw new Exception("Login failed: " + responseBuilder);
                }
            }
        } finally {
            connection.disconnect();
        }
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
}


