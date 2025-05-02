package com.columbusadventure;

// GameMap provides helpful methods to work with the ocean grid.
// It includes functions to validate positions, detect islands, and check tile types.
public class GameMap {

    // Checks if the given coordinates are inside the boundaries of the game grid
    public static boolean isValid(int x, int y) {
        int size = GameState.getInstance().getGrid().getSize();
        return x >= 0 && y >= 0 && x < size && y < size;
    }

    // Returns true if the tile at (x, y) is an island
    public static boolean isIsland(int x, int y) {
        return isValid(x, y) && GameState.getInstance().getGrid().isIsland(x, y);
    }

    // Marks the tile at (x, y) as an island, but only if the coordinates are valid
    public static void setIsland(int x, int y) {
        if (isValid(x, y)) {
            GameState.getInstance().getGrid().setIsland(x, y);
        }
    }

    // Returns the type of tile at (x, y)
    // It could be "INVALID", "ISLAND", "COLUMBUS", or "OCEAN"
    public static String getTileType(int x, int y) {
        if (!isValid(x, y))
            return "INVALID"; // Tile is out of bounds
        if (isIsland(x, y))
            return "ISLAND"; // Tile is marked as an island
        ColumbusShip columbus = GameState.getInstance().getColumbus();
        if (columbus.getX() == x && columbus.getY() == y)
            return "COLUMBUS"; // Columbus is on this tile
        return "OCEAN"; // Otherwise, it's open ocean
    }
}
