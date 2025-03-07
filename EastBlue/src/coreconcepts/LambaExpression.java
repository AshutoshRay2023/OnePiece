package coreconcepts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
//Method References allow us to refer to method without invoking it, making our code cleaner and more readable.
// They can be used in place of a lambda expression when lambda expression only calls an existing method
public class LambaExpression {

    public void print (String s){
        System.out.println(s);
    }

    public static void main(String[] args) {
            LambaExpression lambaExpression=new LambaExpression();
        List<String> students=Arrays.asList("Alice","Bob","Charlies");
        List<StudentConstructorReference> ashi= students.stream().map(StudentConstructorReference::new).collect(Collectors.toList());
        List<String> ashutosh =students.stream().map(x -> x).collect(Collectors.toList());
            students.forEach(lambaExpression::print);
//        Runnable runnable=new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Start");
//            }
//        };
        Runnable runnable= () -> System.out.println("Start");
    Thread t1=new Thread(() -> {
        for(int i=0;i<1000;i++){
            System.out.println("Start");}
    });

    Thread t2=new Thread(() -> {
        for(int i=0;i<1000;i++){
            System.out.println(i);
        }
    });
    // Lamba Expression- Anonymous Function
//    t1.start();
//    t2.start();

        //Filter On the basis of Name
        List<String> list= Arrays.asList("A","AB","AC","B");
        System.out.println(list);
        List<String> ans=list.stream().filter(
                    a->a.startsWith("A"))
                .toList();
        System.out.println(ans);

        List<Integer> nums=Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        List<Integer> even=nums.stream()
                .filter(a -> a%2==0)
                .toList();
        System.out.println();
//        nums.stream().forEach(s-> System.out.println(s));
        nums.forEach(System.out::println);
    }
}
