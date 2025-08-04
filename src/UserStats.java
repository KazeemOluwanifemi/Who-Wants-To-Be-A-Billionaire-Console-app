import java.util.Scanner;

public class UserStats {
    static class User{
        Scanner reader = new Scanner(System.in);
        String name;
        int noOfCrtAnswers;
        double amtEarned;

//        Use an arrayList to be able to iterate over the different items when used in a loop
        String getName(){
            if(name == null || name.isEmpty()){
                System.out.println("What is your name: ");
                name = reader.nextLine();
            }
            return name;
        }
    }
}
