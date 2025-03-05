package com.designpattern.Factory;

public class CharacterFactory {
    public static OnePieceCharacter createCharacter(String type, String name){
        switch(type.toLowerCase()){
            case "pirate":
                return new Pirate(name);
            case "marine":
                return new Marine(name);
            case "revolutionaries":
                return new Revolutionaries(name);
            default:
                throw new IllegalArgumentException("Unknown :" + type);
        }
    }
}
