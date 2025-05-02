package com.columbusadventure;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OceanGridTest {
    private OceanGrid grid;

    @BeforeEach
    public void setUp() {
        grid = new OceanGrid();
    }

    @Test
    public void testIsValid() {
        // Test valid coordinates
        assertTrue(grid.isValid(0, 0));
        assertTrue(grid.isValid(19, 19));
        
        // Test invalid coordinates
        assertFalse(grid.isValid(-1, 0));
        assertFalse(grid.isValid(0, -1));
        assertFalse(grid.isValid(20, 0));
        assertFalse(grid.isValid(0, 20));
    }

    @Test
    public void testIslandOperations() {
        // Initially should not be an island at (5,5)
        assertFalse(grid.isIsland(5, 5));
        
        // Set as island and verify
        grid.setIsland(5, 5);
        assertTrue(grid.isIsland(5, 5));
        
        // Try setting invalid coordinates
        grid.setIsland(-1, -1);
        assertFalse(grid.isIsland(-1, -1));
    }
}