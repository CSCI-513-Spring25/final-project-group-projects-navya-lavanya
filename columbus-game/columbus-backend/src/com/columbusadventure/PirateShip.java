package com.columbusadventure;

import java.util.*;

// This class represents a pirate ship that watches Columbus's ship.
// It uses the Observer pattern to update its position whenever Columbus moves.
public class PirateShip implements Observer {
    private int x; // Current x-coordinate of the pirate ship
    private int y; // Current y-coordinate of the pirate ship

    // Sets the pirate ship's position on the grid
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Returns the current x-coordinate of the pirate ship
    public int getX() {
        return x;
    }

    // Returns the current y-coordinate of the pirate ship
    public int getY() {
        return y;
    }

    // This method is called automatically when Columbus moves
    // It updates the pirate’s position based on the new location of Columbus
    @Override
    public void update(Observable o, Object arg) {
        OceanGrid grid = GameState.getInstance().getGrid();
        ColumbusShip columbus = GameState.getInstance().getColumbus();
        updateSmart(o, arg, grid, columbus);
    }

    // Uses Breadth-First Search (BFS) to find the shortest path toward Columbus
    // The pirate can move through ocean tiles or directly onto Columbus’s location
    public void updateSmart(Observable o, Object arg, OceanGrid grid, ColumbusShip columbus) {
        int[] target = (int[]) arg;
        int targetX = target[0];
        int targetY = target[1];

        int size = grid.getSize(); // Size of the ocean grid
        boolean[][] visited = new boolean[size][size]; // Tracks which tiles were visited
        int[][][] prev = new int[size][size][2]; // Stores previous positions for backtracking

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] { x, y }); // Start BFS from current pirate position
        visited[x][y] = true;
        boolean found = false;

        // Possible movement directions (up, down, left, right)
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        // Begin BFS to find shortest path to Columbus
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();

            // If Columbus's position is reached, stop searching
            if (curr[0] == targetX && curr[1] == targetY) {
                found = true;
                break;
            }

            // Explore all neighboring tiles
            for (int[] dir : directions) {
                int nx = curr[0] + dir[0];
                int ny = curr[1] + dir[1];

                // Only consider valid and unvisited tiles
                if (grid.isValid(nx, ny) && !visited[nx][ny]) {
                    String tile = grid.getTileType(nx, ny, columbus);

                    // Pirate can only travel on ocean or to Columbus's tile
                    if (tile.equals("OCEAN") || tile.equals("COLUMBUS")) {
                        visited[nx][ny] = true;
                        prev[nx][ny] = new int[] { curr[0], curr[1] };
                        queue.offer(new int[] { nx, ny });
                    }
                }
            }
        }

        // If no path to Columbus was found, don't move
        if (!found)
            return;

        // Backtrack from Columbus’s position to determine the next move
        int cx = targetX;
        int cy = targetY;
        while (prev[cx][cy][0] != x || prev[cx][cy][1] != y) {
            int[] p = prev[cx][cy];
            cx = p[0];
            cy = p[1];
        }

        // Update the pirate ship’s position to move one step closer
        x = cx;
        y = cy;
    }
}
