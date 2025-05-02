package com.columbusadventure;

public class OctopusFactory extends SeaMonsterFactory {
    /**
     * Creates a new Octopus instance.
     * 
     * @return New Octopus object
     */
    @Override
    public SeaMonster createSeaMonster() {
        return new Octopus();
    }
}