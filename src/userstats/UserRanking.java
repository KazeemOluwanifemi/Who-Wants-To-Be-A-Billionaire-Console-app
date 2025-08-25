package userstats;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class UserRanking {
    private String userName;
    private int userScore;
    private int highestScore;

    public void createWriteToMainFile(User user) {

        File parDir = new File("src/userFilesDatabase");
        File mainDB = new File(parDir, "mainDB.csv");
        setUserName(user.getName());
        setUserScore(user.getCrtAnswers());

        try {
            String line = getUserName() + "," + getUserScore();
            FileWriter fileWriter = new FileWriter(mainDB, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Error occurred while writing to main user database file");
        }
    }

    public HashMap<String,Integer> readFromFile(String filename){
        HashMap<String,Integer> userStats = new HashMap<>();

        try(Scanner reader = new Scanner(Paths.get(filename))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                if(pieces.length < 2){
                    System.out.println("There's a faulty line in your main DB file");
                } else {
                    setUserName(pieces[0].trim());
                    setUserScore(Integer.valueOf(pieces[1].trim()));

                    userStats.put(getUserName(), getUserScore());
                }
            }
        } catch (IOException e) {
            System.out.println("Error while loading main DB file");
        }
        return userStats;
    }

//    a method to get highest score

    public int highestScore(){
        HashMap<String, Integer> userStats= readFromFile("mainDB.csv");

//        find and set the highest value
        int highest = userStats.values().iterator().next();
        for(int value: userStats.values()){
            if(value > highest){
                highest = value;
            }
        }
        setHighestScore(highest);
        return getHighestScore();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public int getHighestScore() {
        return highestScore;
    }
}
