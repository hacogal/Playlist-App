package org.example.a2spotyapi;

public class Playlist {
    String id; // Unique identifier for the playlist
    String name; // Name of the playlist

    // Constructor to initialize a Playlist object
    Playlist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Override toString to display the playlist name in UI components
    @Override
    public String toString() {
        return name;
    }
}