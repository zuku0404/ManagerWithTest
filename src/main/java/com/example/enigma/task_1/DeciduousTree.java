package com.example.enigma.task_1;

public class DeciduousTree extends Tree {
    DeciduousTree(String trunk, int branches, LeafType leafType, int height, int age, boolean abilityToPhotosynteze) {
        super(trunk, branches, leafType, height, age, abilityToPhotosynteze);
    }

    public void grow() {
        super.grow(5.0);
        System.out.println("grow like Deciduous Tree - fruits");
    }

    @Override
    public void produceOxygen() {
        if (isAbilityToPhotosynthesize()) {
            System.out.println("is producing a lot of oxygen");
        } else {
            System.out.println("I can not produce oxygen");
        }
    }

    @Override
    public String describe() {
        return "I am DeciduousTree with a " + getTrunk() + " trunk, " +
                getBranches() + " branches, leaf type: " + getLeafType() +
                ", height: " + getHeight() + "m, age: " + getAge() + " years." +
                " Ability to photosynthesize: " + isAbilityToPhotosynthesize();
    }
}
