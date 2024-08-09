package org.example.a2spotyapi;

public class Genre {

    String id; // Unique identifier for the genre
    String name; // Name of the genre

    // Constructor to initialize a Genre object
    Genre(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Override toString to display the genre name in UI components
    @Override
    public String toString() {
        return name;
    }
}