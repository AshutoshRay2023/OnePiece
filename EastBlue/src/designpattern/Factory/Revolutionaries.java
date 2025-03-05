package designpattern.Factory;

public class Revolutionaries implements OnePieceCharacter{
    private final String name;

    public Revolutionaries(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public void displayRoles() {
        System.out.println("Revolt against the World Government!");
    }
}
