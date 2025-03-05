package loguetown;

public class GomuGomuNoMi implements DevilFruit{

    int a=10;

    @Override
    public String setType(String s) {
        return s;
    }

    @Override
    public void weakNess(String w) {
        System.out.println("The weakness is "+w);
    }

    @Override
    public void showDetails() {
        System.out.println();
    }
}
