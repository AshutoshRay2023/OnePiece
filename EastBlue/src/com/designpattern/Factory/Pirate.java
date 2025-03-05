package com.designpattern.Factory;

public class Pirate implements OnePieceCharacter{
    private final String name;

    public Pirate(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
    @Override
    public void displayRoles() {
        System.out.println("Pirates to Find One Piece!");
    }
}
