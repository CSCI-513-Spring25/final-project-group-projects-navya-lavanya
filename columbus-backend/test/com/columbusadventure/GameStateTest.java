package com.columbusadventure;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameStateTest {
    private GameState gameState;

    @BeforeEach
    public void setUp() {
        gameState = GameState.getInstance();
        gameState.newGame(); // Reset to initial state
    }

    @Test
    public void testInitialGameSetup() {
        // Verify initial values
        assertEquals(1, gameState.getLevel());
        assertEquals(3, gameState.getLives());
        assertEquals(0, gameState.getScore());
        
        // Verify Columbus is placed on the grid
        ColumbusShip columbus = gameState.getColumbus();
        assertTrue(columbus.getX() >= 0 && columbus.getX() < 20);
        assertTrue(columbus.getY() >= 0 && columbus.getY() < 20);
        
        // Verify pirates are created
        assertFalse(gameState.getPirates().isEmpty());
    }

    @Test
    public void testNextLevel() {
        // Store initial values
        int initialLevel = gameState.getLevel();
        int initialSize = gameState.getGrid().getSize();
        
        // Move to next level
        gameState.nextLevel();
        
        // Verify level increased
        assertEquals(initialLevel + 1, gameState.getLevel());
        
        // Verify grid size increased
        assertEquals(initialSize + 3, gameState.getGrid().getSize());
        
        // Verify score increased
        assertEquals(20, gameState.getScore());
        
        // Verify new pirates were created
        assertFalse(gameState.getPirates().isEmpty());
    }
}
