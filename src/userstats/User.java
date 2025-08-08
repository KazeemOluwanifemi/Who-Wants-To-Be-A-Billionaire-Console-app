package userstats;

import java.util.Scanner;

public class User{
    private String name;
    private int crtAnswers;
    private int wrongAnswers;
    private double amtEarned;

    public String initializeUserName(){
        Scanner reader = new Scanner(System.in);
        if (this.name == null || this.name.isEmpty()) {
            System.out.println("What is your name: ");
            System.out.print("->");
            this.name = reader.nextLine().trim();
        }
        return this.name;
    }

    //        Use an arrayList to be able to iterate over the different items when used in a loop
//    public String getName() {
//
//    }

//        declare getters and setters for each data field
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getCrtAnswers() {
        return crtAnswers;
    }

    public void setCrtAnswers(int crtAnswers) {
        this.crtAnswers = crtAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public double getAmtEarned() {
        return amtEarned;
    }

    public void setAmtEarned(double amtEarned) {
        this.amtEarned = amtEarned;
    }
}
