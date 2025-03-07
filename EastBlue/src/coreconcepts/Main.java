package coreconcepts;

public class Main {
    public static void main(String[] args) {

        Student student=() -> System.out.println("This is a lamba expression");
        System.out.println(student);
    }
}
