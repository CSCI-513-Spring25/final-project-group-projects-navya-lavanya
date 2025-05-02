package com.columbusadventure;

import java.util.Random;

// Represents a Whirlpool tile on the grid that teleports Columbus when he steps on it
class Whirlpool implements TileComponent {
    private final int x, y; // The fixed position of the whirlpool on the grid

    // Creates a new Whirlpool at the specified grid location
    public Whirlpool(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // This method is triggered when Columbus enters the tile
    // If Columbus lands on this whirlpool, he gets teleported to a random ocean
    // tile
    @Override
    public void onColumbusEnter(GameState state, int cx, int cy) {
        if (this.x == cx && this.y == cy) {
            OceanGrid grid = state.getGrid();
            ColumbusShip columbus = state.getColumbus();
            Random rand = new Random();
            int tx, ty;

            // Randomly choose a new tile until a valid ocean tile is found
            do {
                tx = rand.nextInt(grid.getSize());
                ty = rand.nextInt(grid.getSize());
            } while (!grid.getTileType(tx, ty, columbus).equals("OCEAN"));

            // Move Columbus to the randomly selected ocean tile
            columbus.move(tx, ty);
        }
    }

    // Returns true if this whirlpool is located at the given (x, y) position
    @Override
    public boolean matchesPosition(int cx, int cy) {
        return this.x == cx && this.y == cy;
    }

    // Getter for the x-coordinate of the whirlpool
    public int getX() {
        return x;
    }

    // Getter for the y-coordinate of the whirlpool
    public int getY() {
        return y;
    }
}
