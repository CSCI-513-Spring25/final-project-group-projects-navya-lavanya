package com.columbusadventure;

import java.util.*;

// Interface for components that can exist on tiles (like whirlpools or hearts)
// Allows defining behavior when Columbus interacts with the tile
public interface TileComponent {

    // Called when Columbus moves onto the tile at position (x, y)
    void onColumbusEnter(GameState state, int x, int y);

    // Returns true if this component is located at the given position on the grid
    boolean matchesPosition(int x, int y);
}
