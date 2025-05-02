package com.columbusadventure;

import java.util.Observable;

// This class represents Columbus's ship in the game.
// It keeps track of the ship's current location and notifies any observers when the ship moves.
public class ColumbusShip extends Observable {
    private int x; // The ship's current X-coordinate (horizontal position)
    private int y; // The ship's current Y-coordinate (vertical position)

    // Constructor that places Columbus's ship at the given starting coordinates
    public ColumbusShip(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Moves the ship to a new position and notifies all observers (e.g., pirate
    // ships)
    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        setChanged(); // Marks the object as changed so observers are notified
        notifyObservers(new int[] { x, y }); // Notifies observers with the updated position
    }

    // Returns the current X-coordinate of the ship
    public int getX() {
        return x;
    }

    // Returns the current Y-coordinate of the ship
    public int getY() {
        return y;
    }

    // Sets the X-coordinate without notifying observers (used for silent updates)
    public void setX(int x) {
        this.x = x;
    }

    // Sets the Y-coordinate without notifying observers (used for silent updates)
    public void setY(int y) {
        this.y = y;
    }

    // Sets both X and Y coordinates directly without notifying observers
    // Useful for silent repositioning like teleportation or respawning
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
