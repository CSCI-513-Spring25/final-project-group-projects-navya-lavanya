package com.columbusadventure;

import java.util.*;

// Represents an AI-controlled opponent ship in the game.
// It moves intelligently across the ocean grid to try to reach the treasure.
public class OpponentShip {
    private int x, y; // Current position of the opponent ship

    // Constructor to initialize the opponent ship at a specific starting position
    public OpponentShip(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Sets the ship’s position to the given coordinates
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Returns the current x-coordinate of the ship
    public int getX() {
        return x;
    }

    // Returns the current y-coordinate of the ship
    public int getY() {
        return y;
    }

    // Moves the opponent ship one step closer to the treasure using BFS pathfinding
    // It only moves through valid "OCEAN" or "TREASURE" tiles
    public void moveTowardTreasure(OceanGrid grid, int treasureX, int treasureY, ColumbusShip columbus) {
        int size = grid.getSize();

        // Keep track of visited tiles to avoid cycles during BFS
        boolean[][] visited = new boolean[size][size];

        // Store previous tiles for reconstructing the shortest path
        int[][][] prev = new int[size][size][2];

        // Queue used for BFS traversal
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] { x, y });
        visited[x][y] = true;

        // Possible directions to explore: up, down, left, right
        int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        boolean found = false;

        // Perform BFS to find the shortest path to the treasure
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();

            // If the treasure is found, exit the loop
            if (curr[0] == treasureX && curr[1] == treasureY) {
                found = true;
                break;
            }

            // Explore adjacent tiles
            for (int[] d : dirs) {
                int nx = curr[0] + d[0];
                int ny = curr[1] + d[1];

                // Check if tile is within bounds and not visited
                if (nx >= 0 && ny >= 0 && nx < size && ny < size && !visited[nx][ny]) {
                    String tile = grid.getTileType(nx, ny, columbus);

                    // Only move through ocean tiles or treasure tile
                    if (tile.equals("OCEAN") || tile.equals("TREASURE")) {
                        visited[nx][ny] = true;
                        prev[nx][ny] = new int[] { curr[0], curr[1] };
                        queue.offer(new int[] { nx, ny });
                    }
                }
            }
        }

        // If no valid path to treasure was found, don't move
        if (!found)
            return;

        // Backtrack from the treasure to determine the next step to take
        int cx = treasureX, cy = treasureY;
        while (prev[cx][cy][0] != x || prev[cx][cy][1] != y) {
            int[] p = prev[cx][cy];
            cx = p[0];
            cy = p[1];
        }

        // Move the opponent ship one step closer along the shortest path
        this.x = cx;
        this.y = cy;
    }
}
