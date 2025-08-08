package questionHandling;

import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class Question {
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private int cashPrize;

    //    method to implement loading the file contents functionality
    public List<Question> getQuestionDet(String filename) {
//        list containing different question objects
        List<Question> questions = new ArrayList<>();

        try (Scanner reader = new Scanner(Paths.get(filename))) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                if (pieces.length < 7) {
                    System.out.println("There is a faulty line in the question file");
                } else {
                    Question theQuestion = new Question();
                    theQuestion.setQuestion(pieces[0].trim());
                    theQuestion.setOptionA(pieces[1].trim());
                    theQuestion.setOptionB(pieces[2].trim());
                    theQuestion.setOptionC(pieces[3].trim());
                    theQuestion.setOptionD(pieces[4].trim());
                    theQuestion.setCorrectOption(pieces[5].trim());
                    theQuestion.setCashPrize(Integer.valueOf(pieces[6].trim()));
                    questions.add(theQuestion);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while handling file: " + e);
        }
        return questions;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    public int getCashPrize() {
        return cashPrize;
    }

    public void setCashPrize(Integer prize){
        this.cashPrize = prize;
    }
}
