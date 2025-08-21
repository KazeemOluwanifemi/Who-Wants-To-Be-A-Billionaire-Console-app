package userstats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserFile {
    private String dateTime;

    public void createAndWriteToUserFile(User user){
//        store the date and time of playing to make it easier to access
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formattedDateTime = dateTime.format(formatDateTime);

        //        creates the file
        int fileStatus = 1;
        try{
            File userFile = new File(user.getName() + ".txt");
            if(userFile.createNewFile()){
                fileStatus = 1;
            } else{
                System.out.println("Your data file already exists and has been updated");
                fileStatus = 0;
            }
        } catch (IOException e){
            System.out.println("An error occurred");
            e.printStackTrace();
        }


//        write user stats to file or update the file based on if the file exists before or not
        if(fileStatus == 1){
            try{
                FileWriter fileWriter = new FileWriter(user.getName() + ".txt");
                BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter);

                bufferedFileWriter.write("Date and time of playing: " + formattedDateTime + "\n");
                bufferedFileWriter.write("Username: " + user.getName() + "\n");
                bufferedFileWriter.write("Number of correctly answered questions: " + user.getCrtAnswers() + "\n");
                bufferedFileWriter.write("Amount earned by " + user.getName() + " is: #" + user.getAmtEarned() + "\n");
                bufferedFileWriter.write("--------------------------------------------------------------");
                bufferedFileWriter.write("Checkout token for this game session is: " + user.getUserToken() + "\n");

                bufferedFileWriter.close();
            } catch(IOException e){
                System.out.println("An error occurred while writing to file");
                e.printStackTrace();
            }
        } else if(fileStatus == 0){
            try{
                FileWriter fileWriter = new FileWriter(user.getName() + ".txt");
                BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter);

                bufferedFileWriter.write("Date and time of playing: " + formattedDateTime + "\n");
                bufferedFileWriter.append("Username: ").append(user.getName()).append("\n");
                bufferedFileWriter.append("Number of correctly answered questions: ").append(String.valueOf(user.getCrtAnswers())).append("\n");
                bufferedFileWriter.append("Amount earned by ").append(user.getName()).append(" is: #").append(String.valueOf(user.getAmtEarned())).append("\n");
                bufferedFileWriter.write("Checkout token for this game session is: " + user.getUserToken() + "\n");

                bufferedFileWriter.close();
            } catch(IOException e){
                System.out.println("An error occurred while writing to file");
                e.printStackTrace();
            }
        }

    }

    public void displayUserStats(User user) {
        user.setUserToken(RandomTokenGenerator.generator(10));
        System.out.println("User Name: " + user.getName());
        System.out.println("Correct Answers: " + user.getCrtAnswers());
        System.out.println("Amount Earned: #" + user.getAmtEarned());
        System.out.println("Checkout Token: " + user.getUserToken());
    }

//    this method sets the safety net


}
