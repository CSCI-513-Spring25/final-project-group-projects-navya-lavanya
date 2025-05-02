package com.columbusadventure;

import java.util.*;

// A composite class that groups multiple TileComponents together
// This allows managing multiple interactive tiles as a single unit
class TileGroup implements TileComponent {
    private final List<TileComponent> children = new ArrayList<>();

    // Adds a new TileComponent (like a Whirlpool or Heart) to the group
    public void add(TileComponent component) {
        children.add(component);
    }

    // When Columbus enters a tile, notify all child components
    @Override
    public void onColumbusEnter(GameState state, int x, int y) {
        for (TileComponent c : children) {
            c.onColumbusEnter(state, x, y);
        }
    }

    // Checks if any child component exists at the given (x, y) position
    @Override
    public boolean matchesPosition(int x, int y) {
        return children.stream().anyMatch(c -> c.matchesPosition(x, y));
    }

    // Returns the list of all child components
    public List<TileComponent> getChildren() {
        return children;
    }

    // Removes all tile components from the group
    public void clear() {
        children.clear();
    }
}
