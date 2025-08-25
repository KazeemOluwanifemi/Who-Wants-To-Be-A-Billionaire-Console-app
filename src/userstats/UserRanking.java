package userstats;

import java.io.*;

public class UserRanking {
    private String userName;
    private String userScore;

    public void createWriteToMainFile(User user) {

        File parDir = new File("src/userFilesDatabase");
        File mainDB = new File(parDir, "mainDb.csv");
        setUserName(user.getName());
        setUserScore(String.valueOf(user.getCrtAnswers()));

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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserScore() {
        return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }
}
