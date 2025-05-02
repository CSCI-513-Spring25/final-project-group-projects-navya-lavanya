package com.columbusadventure;

// Octopus is a type of sea monster that actively chases Columbus's ship
public class Octopus extends SeaMonster {

    // Moves the octopus toward Columbus's current position on the grid
    @Override
    public void move(GameState state) {
        ColumbusShip columbus = state.getColumbus();
        moveToward(columbus.getX(), columbus.getY(), state.getGrid(), columbus);
    }

    // Returns the type of this sea monster (used for rendering or logic checks)
    @Override
    public String getType() {
        return "OCTOPUS";
    }
}
