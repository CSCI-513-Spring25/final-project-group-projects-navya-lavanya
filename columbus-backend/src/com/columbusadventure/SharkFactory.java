package com.columbusadventure;

// Factory class responsible for creating Shark-type sea monsters
public class SharkFactory extends SeaMonsterFactory {

    // Creates and returns a new Shark instance
    @Override
    public SeaMonster createSeaMonster() {
        return new Shark();
    }
}
