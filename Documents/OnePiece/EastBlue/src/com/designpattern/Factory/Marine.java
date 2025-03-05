package com.designpattern.Factory;

public class Marine implements OnePieceCharacter{

    private final String name;

    public Marine(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }


    @Override
    public void displayRoles() {
        System.out.println("Marines to stop pirates to find One piece!");
    }
}
