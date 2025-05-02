package com.columbusadventure;

import java.util.List;

// Shark is a sea monster that hunts the closest pirate ship on the grid
public class Shark extends SeaMonster {

    // Moves the shark one step closer to the nearest pirate ship
    // Uses Java reflection to access the private pirate list from GameState
    @Override
    public void move(GameState state) {
        List<PirateShip> pirates;

        try {
            // Use reflection to access the private 'pirates' field inside GameState
            java.lang.reflect.Field field = GameState.class.getDeclaredField("pirates");
            field.setAccessible(true);
            pirates = (List<PirateShip>) field.get(state);
        } catch (Exception e) {
            // If reflection fails for any reason, abort the move
            return;
        }

        // If no pirates are found, the shark stays in place
        if (pirates == null || pirates.isEmpty()) {
            return;
        }

        // Find the closest pirate using Manhattan distance
        int minDist = Integer.MAX_VALUE;
        int tx = x, ty = y; // Initialize target coordinates

        for (PirateShip p : pirates) {
            int dist = Math.abs(p.getX() - x) + Math.abs(p.getY() - y);
            if (dist < minDist) {
                minDist = dist;
                tx = p.getX();
                ty = p.getY();
            }
        }

        // Move the shark one step toward the closest pirate ship
        moveToward(tx, ty, state.getGrid(), state.getColumbus());
    }

    // Returns the type of this sea monster, used for rendering and logic
    @Override
    public String getType() {
        return "SHARK";
    }
}
