package org.example.a2spotyapi;

/* Haizan Cordoba Gallardo- @hacogal*/

// Import statements for necessary libraries and classes
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.IntStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlaylistController {
    // FXML-annotated fields for UI components
    @FXML private ComboBox<Genre> genreComboBox;  // Dropdown for selecting a music genre
    @FXML private ComboBox<Playlist> playlistComboBox;  // Dropdown for selecting a playlist
    @FXML private ListView<Track> trackListView;  // List view to display tracks in a playlist
    @FXML private VBox trackDetailBox;  // A VBox layout to show track details
    @FXML private VBox mainView;  // The main view of the application
    @FXML private VBox detailView;  // The detail view that shows more information about a selected track


    // Constants for Spotify API authentication and endpoints
    private static final String CLIENT_ID = "fe706e4300854ae9b1b3c6936d988c80";  // Spotify API client ID
    private static final String CLIENT_SECRET = "a3505ed588e74206b1cb915ba651c59b";  // Spotify API client secret
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";  // URL to obtain OAuth token
    private static final String API_BASE_URL = "https://api.spotify.com/v1";  // Base URL for Spotify API requests

    // Fields for API communication
    private String accessToken;  // The OAuth access token used for authentication
    private final HttpClient httpClient = HttpClient.newHttpClient();  // HttpClient instance for making HTTP requests
    private final Gson gson = new Gson();  // Gson instance for parsing JSON responses

    @FXML
    public void initialize() {
        // This method is called automatically after the FXML file has been loaded.
        // It's used to initialize the controller, setup listeners, and prepare data, configuring UI components, and attaching event listeners.

        // Get access token for API requests to the Spotify API.
        // This token is required to authenticate and make API calls.
        getAccessToken();
        // Load the list of available music genres from the Spotify API and populate the genreComboBox.
        loadGenres();

        // Set up event handler for the genreComboBox.
        // When the user selects a different genre, the playlists associated with that genre will be loaded.
        genreComboBox.setOnAction(event -> loadPlaylists());

        // Set up a listener for the trackListView's selection model.
        // When the user selects a different track from the list, the detailed information for that track will be displayed.
        trackListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // If a new track is selected (newSelection is not null), load and display its details.
            if (newSelection != null) {
                loadTrackDetails(newSelection);
            }
        });
    }

    // Method to obtain access token from Spotify API
    private void getAccessToken() {
        // Create the authorization string by combining the CLIENT_ID and CLIENT_SECRET
        // and then encoding it in Base64 format. This is required for basic authentication.
        String authString = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        // Build an HTTP POST request to the Spotify token endpoint
        // The request includes necessary headers and the body specifies that we're using the client_credentials grant type
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))  // The URL where the token request is sent
                .header("Authorization", "Basic " + encodedAuth)  // Basic authentication header with encoded credentials
                .header("Content-Type", "application/x-www-form-urlencoded")  // Content type of the request body
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))  // The grant type for obtaining the token
                .build();

        try {
            // Send the HTTP request and receive the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response to extract the access token
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            accessToken = jsonResponse.get("access_token").getAsString();  // Store the access token for future API requests
        } catch (IOException | InterruptedException e) {
            // Handle potential exceptions that may occur during the HTTP request or response processing
            e.printStackTrace();
        }
    }

    // Method to load genres from Spotify API
    private void loadGenres() {
        // Construct the URL for the Spotify API request to retrieve music genres (categories).
        // The URL includes parameters for locale (en_CA) and a limit of 50 genres.
        String url = API_BASE_URL + "/browse/categories?locale=en_CA&limit=50";

        // Make the API request to the constructed URL and retrieve the response as a JsonObject.
        JsonObject response = makeApiRequest(url);

        // Check if the response is not null and contains a "categories" object.
        if (response != null && response.has("categories")) {
            // Extract the "items" array from the "categories" object.
            JsonArray items = response.getAsJsonObject("categories").getAsJsonArray("items");

            // Iterate over each item in the "items" array.
            IntStream.range(0, items.size()).mapToObj(i -> items.get(i).getAsJsonObject()).forEachOrdered(item ->
                    // Create a new Genre object using the "id" and "name" fields from each item,
                    // and add it to the genreComboBox's list of items.
                    genreComboBox.getItems().add(new Genre(item.get("id").getAsString(), item.get("name").getAsString()))
            );
        }
    }

    // Method to load playlists for a selected genre
    private void loadPlaylists() {
        // Get the selected genre from the genreComboBox.
        Genre selectedGenre = genreComboBox.getSelectionModel().getSelectedItem();

        // If no genre is selected, return immediately.
        if (selectedGenre == null) return;

        // Construct the URL for the Spotify API request to retrieve playlists for the selected genre.
        // The URL includes the genre ID and a limit of 50 playlists.
        String url = API_BASE_URL + "/browse/categories/" + selectedGenre.id + "/playlists?limit=50";

        // Make the API request to the constructed URL and retrieve the response as a JsonObject.
        JsonObject response = makeApiRequest(url);

        // Clear any existing items from the playlistComboBox to prepare for new data.
        playlistComboBox.getItems().clear();

        // Check if the response is not null and contains a "playlists" object.
        if (response != null && response.has("playlists")) {
            // Extract the "items" array from the "playlists" object.
            JsonArray items = response.getAsJsonObject("playlists").getAsJsonArray("items");

            // Iterate over each item in the "items" array.
            IntStream.range(0, items.size()).mapToObj(i -> items.get(i).getAsJsonObject()).forEachOrdered(item ->
                    // Create a new Playlist object using the "id" and "name" fields from each item,
                    // and add it to the playlistComboBox's list of items.
                    playlistComboBox.getItems().add(new Playlist(item.get("id").getAsString(), item.get("name").getAsString()))
            );
        }
    }


    // Event handler for the submit button click event
    @FXML
    private void onSubmitButtonClick() {
        // Get the selected playlist from the playlistComboBox.
        Playlist selectedPlaylist = playlistComboBox.getSelectionModel().getSelectedItem();

        // If no playlist is selected, exit the method early as there is nothing to process.
        if (selectedPlaylist == null) return;

        // Construct the URL for the Spotify API request to retrieve tracks from the selected playlist.
        // The URL includes the playlist ID to specify which playlist's tracks should be fetched.
        String url = API_BASE_URL + "/playlists/" + selectedPlaylist.id + "/tracks";

        // Make the API request to the constructed URL and retrieve the response as a JsonObject.
        JsonObject response = makeApiRequest(url);

        // Clear any existing items from the trackListView to prepare for new data.
        trackListView.getItems().clear();

        // Check if the response is not null and contains an "items" array.
        if (response != null && response.has("items")) {
            // Extract the "items" array, which contains track information.
            JsonArray items = response.getAsJsonArray("items");

            // Iterate over each item in the "items" array, extracting the "track" object.
            IntStream.range(0, items.size())
                    .mapToObj(i -> items.get(i).getAsJsonObject().getAsJsonObject("track"))
                    .map(trackObject -> new Track(
                            trackObject.get("id").getAsString(),  // Extract the track ID
                            trackObject.get("name").getAsString(),  // Extract the track name
                            trackObject.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString()  // Extract the first artist's name
                    ))
                    .forEachOrdered(track -> trackListView.getItems().add(track));  // Add each track to the trackListView
        }
    }


    // Event handler for the back button click event
    @FXML
    private void onBackButtonClick() {
        // Hide the detailed view panel.
        detailView.setVisible(false);

        // Show the main view panel, returning the user to the main interface.
        mainView.setVisible(true);
    }


    // Method to load and display track details
    private void loadTrackDetails(Track track) {
        // Construct the URL for the Spotify API request to retrieve details about the specified track.
        // The URL includes the track ID to specify which track's details should be fetched.
        String url = API_BASE_URL + "/tracks/" + track.id;

        // Make the API request to the constructed URL and retrieve the response as a JsonObject.
        JsonObject response = makeApiRequest(url);

        // Check if the response is not null, indicating that the API request was successful.
        if (response != null) {
            // Extract the track name from the JSON response and create a label to display it.
            String trackName = response.get("name").getAsString();
            Label titleLabel = new Label("Title: " + trackName);

            // Extract the artist's name from the first artist in the JSON response's "artists" array.
            // Create a label to display the artist's name.
            String artistName = response.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString();
            Label artistLabel = new Label("Artist: " + artistName);

            // Extract the album information from the JSON response's "album" object.
            // Get the album's name and create a label to display it.
            JsonObject album = response.getAsJsonObject("album");
            String albumName = album.get("name").getAsString();
            Label albumLabel = new Label("Album: " + albumName);

            // Extract the release date from the album information and create a label to display it.
            String releaseDate = album.get("release_date").getAsString();
            Label releaseDateLabel = new Label("Release Date: " + releaseDate);

            // Extract the track's duration (in milliseconds) from the JSON response.
            // Convert the duration to minutes and seconds, and create a label to display it.
            int durationMs = response.get("duration_ms").getAsInt();
            int minutes = durationMs / 60000;
            int seconds = (durationMs % 60000) / 1000;
            Label durationLabel = new Label(String.format("Duration: %d:%02d", minutes, seconds));

            // Extract the album image URL from the first image in the album's "images" array.
            // Create an ImageView to display the album's image, setting its height and width.
            String imageUrl = album.getAsJsonArray("images").get(0).getAsJsonObject().get("url").getAsString();
            ImageView imageView = new ImageView(new Image(imageUrl));
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);

            // Clear any previous track details from the trackDetailBox (a container for track details).
            // Add the newly created labels and image view to the trackDetailBox for display.
            trackDetailBox.getChildren().clear();
            trackDetailBox.getChildren().addAll(
                    imageView,         // Display the album image
                    titleLabel,        // Display the track title
                    artistLabel,       // Display the artist name
                    albumLabel,        // Display the album name
                    releaseDateLabel,  // Display the release date
                    durationLabel      // Display the track duration
            );

            // Hide the main view and show the detail view, so the user can see the track's detailed information.
            mainView.setVisible(false);
            detailView.setVisible(true);
        }
    }


    // Helper method to make API requests
    private JsonObject makeApiRequest(String url) {
        // Create an HttpRequest object with the specified URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // Set the URI for the request
                .header("Authorization", "Bearer " + accessToken) // Add Authorization header with the Bearer token
                .GET() // Specify the HTTP method as GET
                .build(); // Build the HttpRequest object

        try {
            // Send the request and get the response as a String
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Convert the response body from JSON string to JsonObject using Gson
            return gson.fromJson(response.body(), JsonObject.class);
        } catch (IOException | InterruptedException e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
        // Return null if an exception occurs
        return null;
    }

}