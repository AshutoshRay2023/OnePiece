package designpattern.Factory;

public class OnePieceGame {
    public static void main(String[] args) {
        OnePieceCharacter luffy=CharacterFactory.createCharacter("pirate","Monkey D. Luffy");
        OnePieceCharacter akainu=CharacterFactory.createCharacter("marine","Sakazuki");
        OnePieceCharacter dragon=CharacterFactory.createCharacter("revolutionaries","Monkey D. Dragon");
        luffy.displayRoles();
        akainu.displayRoles();
        dragon.displayRoles();

    }
}
