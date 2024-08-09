package org.example.a2spotyapi;

/* Haizan Cordoba Gallardo- @hacogal*/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class PlaylistApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file for the application's UI
        FXMLLoader fxmlLoader = new FXMLLoader(PlaylistApplication.class.getResource("PlaylistApp.fxml"));
        // Create a new scene with the loaded FXML content
        Scene scene = new Scene(fxmlLoader.load());
        // Set the application title
        stage.setTitle("Playlist App by Spotify API");

        // Load and set the application icon
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/a2spotyapi/spotify_icon.png")));
        stage.getIcons().add(icon);

        // Set the scene and display the stage
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}