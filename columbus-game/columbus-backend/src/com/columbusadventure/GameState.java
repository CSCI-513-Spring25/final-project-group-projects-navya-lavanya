// Defines the package structure for this class
package com.columbusadventure;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;

import java.util.Iterator;

// GameState manages the entire world state, including grid, pirates, and gameplay logic
public class GameState {
    // Singleton instance for the game state
    // Singleton instance of the game state to ensure consistency
    private static final GameState instance = new GameState();

    // The main ocean grid for the game
    // The main ocean grid where all tiles and entities are placed
    private final OceanGrid grid;
    // The player's ship, controlled by the user
    // The player's ship (Columbus), controlled by user actions
    private final ColumbusShip columbus;
    // List of pirate ships currently in the game
    // List of all pirate ships active in the game
    private final List<PirateShip> pirates;
    // List of sea monsters currently in the game
    // List of sea monsters like sharks and octopuses
    private final List<SeaMonster> seaMonsters = new ArrayList<>();
    // X coordinate of the treasure
    // Coordinates of the treasure hidden on the grid
    private int treasureX;
    // Y coordinate of the treasure
    // Coordinates of the treasure hidden on the grid
    private int treasureY;
    // Collection of whirlpool tiles on the grid
    // Group of all whirlpool objects that teleport ships
    private final TileGroup whirlpools = new TileGroup();
    // The AI-controlled opponent ship
    // AI-controlled opponent that competes to reach the treasure
    private final OpponentShip opponent = new OpponentShip(0, 1);
    // List of heart positions (as int arrays) for extra lives
    // Positions of hearts on the map, granting extra lives
    private final List<int[]> hearts = new ArrayList<>();

    // Accessor for the singleton instance of GameState
    public static GameState getInstance() {
        return instance;
    }

    // Starts a fresh game with default level, score, and lives
    public void newGame() {
        level = 1;
        lives = 3;
        score = 0;
        highScore = 0;
        hearts.clear();
        whirlpools.clear();
        pirates.clear();
        seaMonsters.clear();
        grid.resize(20); // Reset grid size to 20x20
        Random rand = new Random();
        int spawnX, spawnY;
        // Find a random ocean tile to spawn Columbus
        do {
            spawnX = rand.nextInt(20);
            spawnY = rand.nextInt(20);
        } while (!grid.getTileType(spawnX, spawnY, columbus).equals("OCEAN"));
        columbus.setPosition(spawnX, spawnY);

        // Reset the rest of the game state for a new game
        reset(true);
    }

    // Constructor initializes the game world and entities
    private GameState() {
        grid = new OceanGrid();
        columbus = new ColumbusShip(0, 0);
        pirates = new ArrayList<>();
        // Initial reset for the starting state
        reset(false);
    }

    public List<SeaMonster> getSeaMonsters() {
        return seaMonsters;
    }

    public void reset(boolean newGame) {
        if (newGame) {
            level = 1;
            lives = 3;
            score = 0;
            highScore = 0;
            grid.resize(20); // Reset grid size to 20x20
        }
        // Clear all dynamic elements for a fresh state
        hearts.clear();
        whirlpools.clear(); // Clear previous whirlpools to prevent doubling
        pirates.clear();
        seaMonsters.clear();
        Random rand = new Random();
        int gridSize = grid.getSize();
        int spawnX, spawnY;
        // Place Columbus on a random ocean tile
        do {
            spawnX = rand.nextInt(gridSize);
            spawnY = rand.nextInt(gridSize);
        } while (!grid.getTileType(spawnX, spawnY, columbus).equals("OCEAN"));
        columbus.setPosition(spawnX, spawnY);
        int pirateCount = 3;
        // Keep track of pirate positions to avoid adjacency
        List<int[]> placedPiratePositions = new ArrayList<>();

        // Spawn pirates, ensuring they are not adjacent to each other or Columbus
        while (pirates.size() < pirateCount) {
            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);

            String tileType = grid.getTileType(x, y, columbus);
            boolean isValidTile = tileType.equals("OCEAN");

            boolean isInvalidPosition = false;
            for (int[] pos : placedPiratePositions) {
                int dx = Math.abs(pos[0] - x);
                int dy = Math.abs(pos[1] - y);
                boolean isSameCell = (dx == 0 && dy == 0);
                boolean isAdjacent = (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
                if (isSameCell || isAdjacent) {
                    isInvalidPosition = true;
                    break;
                }
            }

            if (isValidTile && !isInvalidPosition) {
                PirateShip pirate = new PirateShip();
                pirate.setPosition(x, y);
                // Pirates observe Columbus's moves
                columbus.addObserver(pirate);
                pirates.add(pirate);
                placedPiratePositions.add(new int[] { x, y });
            }
        }

        // Spawn one octopus and one shark at random ocean tiles
        SeaMonster octopus = new OctopusFactory().createSeaMonster();
        SeaMonster shark = new SharkFactory().createSeaMonster();

        int ox, oy;
        do {
            ox = rand.nextInt(gridSize);
            oy = rand.nextInt(gridSize);
        } while (!grid.getTileType(ox, oy, columbus).equals("OCEAN"));
        octopus.setPosition(ox, oy);

        int sx, sy;
        do {
            sx = rand.nextInt(gridSize);
            sy = rand.nextInt(gridSize);
        } while (!grid.getTileType(sx, sy, columbus).equals("OCEAN"));
        shark.setPosition(sx, sy);

        seaMonsters.add(octopus);
        seaMonsters.add(shark);

        // Place the treasure at a random non-island tile
        do {
            treasureX = rand.nextInt(gridSize);
            treasureY = rand.nextInt(gridSize);
        } while (grid.getTileType(treasureX, treasureY, columbus).equals("ISLAND"));

        // Place four whirlpools at random ocean tiles
        for (int i = 0; i < 4; i++) {
            int wx, wy;
            do {
                wx = rand.nextInt(grid.getSize());
                wy = rand.nextInt(grid.getSize());
            } while (!grid.getTileType(wx, wy, columbus).equals("OCEAN"));
            whirlpools.add(new Whirlpool(wx, wy));
        }

        // Place the opponent ship at a random ocean tile
        do {
            ox = rand.nextInt(gridSize);
            oy = rand.nextInt(gridSize);
        } while (!grid.getTileType(ox, oy, columbus).equals("OCEAN"));
        opponent.setPosition(ox, oy);
    }

    // The current level of the game
    private int level;
    // The number of lives Columbus has remaining
    private int lives;
    // The player's current score
    private int score;
    // The highest score achieved so far
    private int highScore;

    public void nextLevel() {
        level++;
        lives = 3;
        score += 20;
        if (score > highScore) {
            highScore = score;
        }

        hearts.clear();
        whirlpools.clear();
        pirates.clear();
        seaMonsters.clear();
        int newGridSize = grid.getSize() + 3;
        Random rand = new Random();
        int spawnX, spawnY;
        // Find a random position for Columbus to start the new level
        do {
            spawnX = rand.nextInt(newGridSize);
            spawnY = rand.nextInt(newGridSize);
        } while (!grid.getTileType(spawnX, spawnY, columbus).equals("OCEAN"));
        columbus.setPosition(spawnX, spawnY);

        // Resize the grid for the new level
        grid.resize(newGridSize);
        int pirateCount = 3 + (level - 1) * 2;
        int islandCount = 5 + (level - 1) * 5;
        int monsterCount = 2 + (level - 1) * 2;
        int whirlpoolCount = 3 + (level - 1) * 3;

        // Track pirate positions to avoid placing them adjacent to each other
        List<int[]> placedPiratePositions = new ArrayList<>();

        // Place pirates on the new grid, avoiding adjacency
        while (pirates.size() < pirateCount) {
            int x = rand.nextInt(newGridSize);
            int y = rand.nextInt(newGridSize);
            if (grid.getTileType(x, y, columbus).equals("OCEAN")) {
                boolean isNearOther = false;
                for (int[] pos : placedPiratePositions) {
                    int dx = Math.abs(pos[0] - x);
                    int dy = Math.abs(pos[1] - y);
                    if ((dx == 0 && dy == 0) || (dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                        isNearOther = true;
                        break;
                    }
                }
                if (!isNearOther) {
                    PirateShip pirate = new PirateShip();
                    pirate.setPosition(x, y);
                    columbus.addObserver(pirate);
                    pirates.add(pirate);
                    placedPiratePositions.add(new int[] { x, y });
                }
            }
        }

        // Place islands randomly on ocean tiles
        for (int i = 0; i < islandCount; i++) {
            int x, y;
            do {
                x = rand.nextInt(newGridSize);
                y = rand.nextInt(newGridSize);
            } while (!grid.getTileType(x, y, columbus).equals("OCEAN"));
            grid.setTile(x, y, "ISLAND");
        }

        // Place sea monsters, alternating between octopus and shark
        for (int i = 0; i < monsterCount; i++) {
            SeaMonster sm = (i % 2 == 0) ? new OctopusFactory().createSeaMonster()
                    : new SharkFactory().createSeaMonster();
            int x, y;
            do {
                x = rand.nextInt(newGridSize);
                y = rand.nextInt(newGridSize);
            } while (!grid.getTileType(x, y, columbus).equals("OCEAN"));
            sm.setPosition(x, y);
            seaMonsters.add(sm);
        }

        // Place whirlpools at random ocean tiles
        for (int i = 0; i < whirlpoolCount; i++) {
            int x, y;
            do {
                x = rand.nextInt(newGridSize);
                y = rand.nextInt(newGridSize);
            } while (!grid.getTileType(x, y, columbus).equals("OCEAN"));
            whirlpools.add(new Whirlpool(x, y));
        }

        // Place the opponent ship at a random ocean tile
        int ox, oy;
        do {
            ox = rand.nextInt(newGridSize);
            oy = rand.nextInt(newGridSize);
        } while (!grid.getTileType(ox, oy, columbus).equals("OCEAN"));
        opponent.setPosition(ox, oy);

        // Place treasure at a random ocean tile, but not too close to the opponent
        do {
            treasureX = rand.nextInt(newGridSize);
            treasureY = rand.nextInt(newGridSize);
        } while (!grid.getTileType(treasureX, treasureY, columbus).equals("OCEAN")
                || (Math.abs(treasureX - ox) + Math.abs(treasureY - oy) < (newGridSize / 2)));
    }

    public void moveColumbus(String direction) {
        int x = columbus.getX();
        int y = columbus.getY();
        // Adjust coordinates based on direction
        switch (direction.toLowerCase()) {
            case "up" -> x--;
            case "down" -> x++;
            case "left" -> y--;
            case "right" -> y++;
        }
        // Check if the move is within the grid and valid
        if (grid.isValid(x, y)) {
            columbus.move(x, y);
            // Trigger whirlpool logic if Columbus enters a whirlpool
            whirlpools.onColumbusEnter(this, x, y);
            // Move the opponent ship toward the treasure
            opponent.moveTowardTreasure(grid, treasureX, treasureY, columbus);
        }
    }

    public ColumbusShip getColumbus() {
        return columbus;
    }

    public OceanGrid getGrid() {
        return grid;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"grid\":[");
        int size = grid.getSize();
        for (int i = 0; i < size; i++) {
            sb.append("[");
            for (int j = 0; j < size; j++) {
                String tile = grid.getTileType(i, j, columbus);
                // Check if a shark and pirate occupy the same cell, which produces a heart
                boolean isSharkOnPirate = false;
                for (SeaMonster sm : seaMonsters) {
                    if (sm.getType().equals("SHARK")) {
                        for (PirateShip p : pirates) {
                            if (sm.getX() == p.getX() && sm.getY() == p.getY() && sm.getX() == i && sm.getY() == j) {
                                // Store heart position instead of just setting tile
                                isSharkOnPirate = true;
                                break;
                            }
                        }
                        if (isSharkOnPirate)
                            break;
                    }
                }
                if (!isSharkOnPirate) {
                    // Render pirates and sea monsters on the grid
                    for (PirateShip p : pirates) {
                        if (p.getX() == i && p.getY() == j) {
                            tile = "PIRATE";
                        }
                    }
                    for (SeaMonster sm : seaMonsters) {
                        if (sm.getX() == i && sm.getY() == j) {
                            tile = sm.getType();
                        }
                    }
                }
                // Render whirlpools
                if (whirlpools.matchesPosition(i, j)) {
                    tile = "WHIRLPOOL";
                }
                // Render treasure
                if (i == treasureX && j == treasureY) {
                    tile = "TREASURE";
                }
                // Render opponent
                if (opponent.getX() == i && opponent.getY() == j) {
                    tile = "OPPONENT";
                }
                // If this position is in hearts and not collected, show "HEALTH"
                boolean isHeartHere = false;
                for (int[] heart : hearts) {
                    if (heart[0] == i && heart[1] == j) {
                        isHeartHere = true;
                        break;
                    }
                }
                if (isHeartHere) {
                    tile = "HEALTH";
                }
                sb.append("\"").append(tile).append("\"");
                if (j < size - 1)
                    sb.append(",");
            }
            sb.append("]");
            if (i < size - 1)
                sb.append(",");
        }
        sb.append("],\"log\":[");
        List<String> logs = new ArrayList<>();

        // Log messages for pirate proximity or attack
        for (PirateShip pirate : pirates) {
            int dx = Math.abs(pirate.getX() - columbus.getX());
            int dy = Math.abs(pirate.getY() - columbus.getY());

            if (dx == 0 && dy == 0) {
                logs.add("\"💀 Game Over: Columbus was attacked by a pirate!\"");
            } else if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                logs.add("\"⚠️ Pirate is nearby!\"");
            }
        }

        // Log messages for sea monster proximity or attack
        for (SeaMonster sm : seaMonsters) {
            int dx = Math.abs(sm.getX() - columbus.getX());
            int dy = Math.abs(sm.getY() - columbus.getY());

            if (dx == 0 && dy == 0) {
                logs.add("\"💀 Game Over: Sea Monster attacked Columbus!\"");
            } else if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                logs.add("\"⚠️ Sea monster is nearby!\"");
            }
        }

        // Handle collisions between sharks and pirates, producing hearts
        Iterator<PirateShip> pirateIterator = pirates.iterator();
        while (pirateIterator.hasNext()) {
            PirateShip pirate = pirateIterator.next();
            Iterator<SeaMonster> monsterIterator = seaMonsters.iterator();
            boolean collisionOccurred = false;
            while (monsterIterator.hasNext()) {
                SeaMonster sm = monsterIterator.next();
                if (sm.getType().equals("SHARK")) {
                    if (sm.getX() == pirate.getX() && sm.getY() == pirate.getY()) {
                        logs.add("\"💀 Warning: Shark hunted a pirate!\"");
                        // Add a heart at the collision position
                        hearts.add(new int[] { pirate.getX(), pirate.getY() });
                        pirateIterator.remove(); // Remove pirate
                        monsterIterator.remove(); // Remove shark
                        collisionOccurred = true;
                        break;
                    }
                }
            }
            if (collisionOccurred) {
                // Only one pirate can be removed per collision, so break out of pirate loop
                break;
            }
        }

        // Log if Columbus finds the treasure
        if (columbus.getX() == treasureX && columbus.getY() == treasureY) {
            logs.add("\"💎 Game Over: Columbus found the treasure!\"");
        }

        // Log if the opponent finds the treasure
        if (opponent.getX() == treasureX && opponent.getY() == treasureY) {
            logs.add("\"💀 Game Over: The opponent reached the treasure first!\"");
        }

        // Log if the opponent is at or near Columbus
        int dx = Math.abs(opponent.getX() - columbus.getX());
        int dy = Math.abs(opponent.getY() - columbus.getY());

        if (dx == 0 && dy == 0) {
            logs.add("\"💀 Game Over: Opponent attacked Columbus!\"");
        } else if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
            logs.add("\"⚠️ Warning: Opponent is trying to attack you!\"");
        }

        // Check if Columbus found a heart and gained a life
        // Remove collected hearts to avoid rendering again
        for (int i = hearts.size() - 1; i >= 0; i--) {
            int[] heart = hearts.get(i);
            if (heart[0] == columbus.getX() && heart[1] == columbus.getY()) {
                logs.add("\"❤️ Columbus found a heart and gained a life!\"");
                hearts.remove(i);
            }
        }

        // Output all log messages as a JSON array
        for (int i = 0; i < logs.size(); i++) {
            sb.append(logs.get(i));
            if (i < logs.size() - 1)
                sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    public int getLevel() {
        return this.level;
    }

    public int getLives() {
        return this.lives;
    }

    public int getScore() {
        return this.score;
    }

    public List<PirateShip> getPirates() {
        return this.pirates;
    }
}
