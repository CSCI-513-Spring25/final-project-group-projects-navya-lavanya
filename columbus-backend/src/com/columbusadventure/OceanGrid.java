package com.columbusadventure;

import java.util.Random;

// OceanGrid represents the game's map where Columbus, pirates, islands, and other elements are placed.
// It handles grid sizing, island placement, tile checking, and resizing the map as the game progresses.
public class OceanGrid {
    private int size = 20; // Default size of the ocean grid (20x20)
    private boolean[][] islands; // 2D array to mark the positions of islands

    // Constructor that creates the grid and randomly places a specified number of
    // islands
    public OceanGrid(int islandCount) {
        islands = new boolean[size][size];
        Random rand = new Random();
        int placed = 0;

        // Keep placing random islands until the required number is reached
        while (placed < islandCount) {
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            if (!islands[x][y]) {
                islands[x][y] = true;
                placed++;
            }
        }
    }

    // Default constructor that places 10 random islands on the grid
    public OceanGrid() {
        this(10);
    }

    // Returns the current size (width/height) of the ocean grid
    public int getSize() {
        return size;
    }

    // Sets a tile at (x, y) to be an island, only if the coordinates are valid
    public void setIsland(int x, int y) {
        if (isValid(x, y)) {
            islands[x][y] = true;
        }
    }

    // Checks if the given (x, y) coordinates fall within the grid boundaries
    public boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && x < size && y < size;
    }

    // Returns true if the tile at (x, y) is marked as an island
    public boolean isIsland(int x, int y) {
        return isValid(x, y) && islands[x][y];
    }

    // Determines the type of tile at (x, y)
    // Possible values: "INVALID", "ISLAND", "COLUMBUS", or "OCEAN"
    public String getTileType(int x, int y, ColumbusShip columbus) {
        if (!isValid(x, y))
            return "INVALID";
        if (isIsland(x, y))
            return "ISLAND";
        if (columbus.getX() == x && columbus.getY() == y)
            return "COLUMBUS";
        return "OCEAN";
    }

    // Changes the grid size and attempts to retain any islands that fall within the
    // new bounds
    public void resize(int newSize) {
        boolean[][] newIslands = new boolean[newSize][newSize];

        // Copy the island layout to the new grid (up to the smaller of old/new size)
        for (int i = 0; i < Math.min(size, newSize); i++) {
            for (int j = 0; j < Math.min(size, newSize); j++) {
                newIslands[i][j] = islands[i][j];
            }
        }

        size = newSize;
        islands = newIslands;
    }

    // Manually sets a tile to a specific type if valid
    // Currently only supports marking a tile as an "ISLAND"
    public void setTile(int x, int y, String type) {
        if (isValid(x, y) && "ISLAND".equals(type)) {
            islands[x][y] = true;
        }
    }
}
