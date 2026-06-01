package com.pgc.model;

public class Hero {
    private int entityId;
    private String name;
    private float x;
    private float y;
    private int health;

    public Hero(int entityId) {
        this.entityId = entityId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
