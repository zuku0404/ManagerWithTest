package com.example.enigma.task_1;

import lombok.Getter;

@Getter
public abstract class Tree {
    private final String trunk;
    private int branches;
    private final LeafType leafType;
    private double height;
    private int age;
    private boolean abilityToPhotosynthesize;

    public Tree(String trunk, int branches, LeafType leafType, double height, int age, boolean abilityToPhotosynthesize) {
        this.trunk = trunk;
        this.branches = branches;
        this.leafType = leafType;
        this.height = height;
        this.age = age;
        this.abilityToPhotosynthesize = abilityToPhotosynthesize;
    }

    public void addBranch() {
        branches += 1;
    }

    public void setOlder() {
        age += 1;
    }

    public void changeAbilityToPhotosynthesize() {
        abilityToPhotosynthesize = !abilityToPhotosynthesize;
    }

    public void grow (double growthAmount){
        height += growthAmount;
    }
    public abstract void produceOxygen();
    public abstract String describe();

}
