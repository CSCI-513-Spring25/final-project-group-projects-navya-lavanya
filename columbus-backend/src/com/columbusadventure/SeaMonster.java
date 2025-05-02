package com.columbusadventure;

import java.util.*;

// Abstract base class for sea monsters in the game
// This class defines shared logic for movement and positioning
public abstract class SeaMonster {
    protected int x, y; // Current position of the sea monster

    // Sets the sea monster's position on the grid
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Returns the current x-coordinate of the sea monster
    public int getX() {
        return x;
    }

    // Returns the current y-coordinate of the sea monster
    public int getY() {
        return y;
    }

    // Each type of sea monster implements its own movement behavior
    public abstract void move(GameState state);

    // Returns the type of sea monster as a string (e.g., "OCTOPUS", "SHARK")
    public abstract String getType();

    // Helper method for moving the sea monster one step toward a target position
    // (tx, ty)
    // Uses Breadth-First Search (BFS) to calculate the shortest path
    protected void moveToward(int tx, int ty, OceanGrid grid, ColumbusShip columbus) {
        int size = grid.getSize();

        // Keeps track of visited tiles during BFS
        boolean[][] visited = new boolean[size][size];

        // Stores the path taken to reach each tile
        int[][][] prev = new int[size][size][2];

        // Queue for BFS traversal starting from the current position
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] { x, y });
        visited[x][y] = true;

        // Movement directions: up, down, left, right
        int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        boolean found = false;

        // Perform BFS to find the shortest path to the target
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();

            // Stop if we've reached the target tile
            if (curr[0] == tx && curr[1] == ty) {
                found = true;
                break;
            }

            // Check all neighboring tiles
            for (int[] d : dirs) {
                int nx = curr[0] + d[0];
                int ny = curr[1] + d[1];

                // Only consider valid and unvisited tiles
                if (grid.isValid(nx, ny) && !visited[nx][ny]) {
                    String tile = grid.getTileType(nx, ny, columbus);

                    // Sea monsters can move through ocean or Columbus's location
                    if (tile.equals("OCEAN") || tile.equals("COLUMBUS")) {
                        visited[nx][ny] = true;
                        prev[nx][ny] = new int[] { curr[0], curr[1] };
                        queue.offer(new int[] { nx, ny });
                    }
                }
            }
        }

        // If there's no path, don't move
        if (!found)
            return;

        // Backtrack from the target tile to determine the next step
        int cx = tx, cy = ty;
        while (prev[cx][cy][0] != x || prev[cx][cy][1] != y) {
            int[] p = prev[cx][cy];
            cx = p[0];
            cy = p[1];
        }

        // Move to the next step along the shortest path
        x = cx;
        y = cy;
    }
}
