package userstats;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class UserRanking {
    private String userName;
    private int userScore;
    private int highestScore;
    private  String topPlayer;

    File parDir = new File("src/userFilesDatabase");
    public File mainDB = new File(parDir, "mainDB.csv");

    public void createWriteToMainFile(User user) {
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

    public List<UserRanking> readFromFile(File filename){
        List<UserRanking> userStats = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            for(String line; (line = br.readLine()) != null;){
                String[] pieces = line.split(",");

                if(pieces.length < 2){
                    System.out.println("There's a faulty line in your main DB file");
                } else {
                    UserRanking stats = new UserRanking();
                    stats.setUserName(pieces[0].trim());
                    stats.setUserScore(Integer.valueOf(pieces[1].trim()));

                    userStats.add(stats);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while loading main DB file");
        }
        return userStats;
    }

//    a method to get highest score

    public void highestScore(){
        List<UserRanking> userStats= readFromFile(mainDB);
//        UserRanking stats = new UserRanking();
        int highest = userStats.getFirst().getUserScore();
        String topPlayer = userStats.getFirst().getUserName();

        for(UserRanking values: userStats){
//            System.out.println(values.getUserName());
//            System.out.println(values.getUserScore());
//
            if (values.getUserScore() > highest){
                highest = values.getUserScore();
                topPlayer = values.getUserName();
            }
        }

        setHighestScore(highest);
        setTopPlayer(topPlayer);
        System.out.println("The top player is " + getTopPlayer() + " with a score of " + getHighestScore() + " *sparkles !!");
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

    public void setTopPlayer(String topPlayer) {
        this.topPlayer = topPlayer;
    }

    public String getTopPlayer() {
        return topPlayer;
    }
}
