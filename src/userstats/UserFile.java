package userstats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UserFile {
    private String name;
    private int noOfCrtAnswers;
    private int noOfWrongAnswers;
    private double userAmtEarned;

    public void createUserFile(String userName, int crtAnswers, int wrongAnswer, double amtEarned){
        name = userName;
        noOfCrtAnswers = crtAnswers;
        noOfWrongAnswers = wrongAnswer;
        userAmtEarned = amtEarned;

        int fileStatus;
        try{
            File userFile = new File(userName + ".txt");
            if(userFile.createNewFile()){
                fileStatus = 1;
            } else{
                System.out.println("File already exists");
                fileStatus = 0;
            }
        } catch (IOException e){
            System.out.println("An error occurred");
            e.printStackTrace();
        }

        try{
            FileWriter fileWriter = new FileWriter(userName + ".txt");
            fileWriter.write("Username: " + userName + "\n");
            fileWriter.write("Number of correctly answered questions: " + crtAnswers + "\n");
            fileWriter.write("Number of wrongly answered questions: " + wrongAnswer + "\n");
            fileWriter.write("Amount earned by " + userName + " is: " + amtEarned + "\n");
            fileWriter.close();
        } catch(IOException e){
            System.out.println("An error occurred while writing to file");
            e.printStackTrace();
        }
    }

}
