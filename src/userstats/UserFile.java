package userstats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserFile {
    private String dateTime;

    public void createUserFile(String userName, int crtAnswers, double amtEarned){
//        generate cashout token per session
        String token = RandomTokenGenerator.generator(10);

//        store the date and time of playing to make it easier to access
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formattedDateTime = dateTime.format(formatDateTime);

        //        creates the file
        int fileStatus = 1;
        try{
            File userFile = new File(userName + ".txt");
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
                FileWriter fileWriter = new FileWriter(userName + ".txt");
                BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter);

                bufferedFileWriter.write("Date and time of playing: " + formattedDateTime + "\n");
                bufferedFileWriter.write("Username: " + userName + "\n");
                bufferedFileWriter.write("Number of correctly answered questions: " + crtAnswers + "\n");
                bufferedFileWriter.write("Amount earned by " + userName + " is: " + amtEarned + "\n");
                bufferedFileWriter.write("Checkout token for this game session is: " + token + "\n");

                bufferedFileWriter.close();
            } catch(IOException e){
                System.out.println("An error occurred while writing to file");
                e.printStackTrace();
            }
        } else if(fileStatus == 0){
            try{
                FileWriter fileWriter = new FileWriter(userName + ".txt");
                BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter);

                bufferedFileWriter.write("Date and time of playing: " + formattedDateTime + "\n");
                bufferedFileWriter.append("Username: ").append(userName).append("\n");
                bufferedFileWriter.append("Number of correctly answered questions: ").append(String.valueOf(crtAnswers)).append("\n");
                bufferedFileWriter.append("Amount earned by ").append(userName).append(" is: ").append(String.valueOf(amtEarned)).append("\n");
                bufferedFileWriter.write("Checkout token for this game session is: " + token + "\n");

                bufferedFileWriter.close();

                System.out.println("Use this token to withdraw your earning for this game session: " + token);
            } catch(IOException e){
                System.out.println("An error occurred while writing to file");
                e.printStackTrace();
            }
        }

    }

}
