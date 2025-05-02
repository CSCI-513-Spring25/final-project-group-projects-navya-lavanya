package com.columbusadventure;

/**
 * Abstract factory class for creating SeaMonster instances.
 */
public abstract class SeaMonsterFactory {
    /**
     * Creates a new instance of a SeaMonster.
     * 
     * @return New SeaMonster object
     */
    public abstract SeaMonster createSeaMonster();
}
