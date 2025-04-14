import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;

import model.GameData;
import server.GameListResult;
import server.UserController;
import service.RegisterRequest;
import service.LoginRequest;
import service.LoginResult;
import service.RegisterResult;

import java.util.*;

public class ServerFacade {
    private final String serverUrl;
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

            // Convert the RegisterRequest object to JSON
            String jsonRequest = gson.toJson(request);

            // Write the JSON to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code and handle accordingly
            int statusCode = connection.getResponseCode();
            InputStream responseStream = (statusCode == 200) ? connection.getInputStream() : connection.getErrorStream();

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
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
                    throw new Exception("Registration failed: " + responseBuilder.toString());
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

            // Convert the LoginRequest object to JSON
            String jsonRequest = gson.toJson(request);

            // Write JSON to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code and handle accordingly
            int statusCode = connection.getResponseCode();
            InputStream responseStream = (statusCode == 200) ? connection.getInputStream() : connection.getErrorStream();

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
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
                    throw new Exception("Login failed: " + responseBuilder.toString());
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
            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
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
                    throw new Exception("Failed to retrieve games: " + responseBuilder.toString());
                }
            }
        } finally {
            connection.disconnect();
        }
    }

}


