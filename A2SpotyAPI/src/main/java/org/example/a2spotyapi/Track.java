package org.example.a2spotyapi;

public class Track {
    String id; // Unique identifier for the track
    String name; // Name of the track
    String artist; // Name of the artist

    // Constructor to initialize a Track object
    Track(String id, String name, String artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    // Override toString to display track name and artist in UI components
    @Override
    public String toString() {
        return name + " - " + artist;
    }
}