import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class QuestionsHandling {
    Scanner reader = new Scanner(System.in);
//    one question class serving as a template for each object question
static class Question{
        String question;
        String optionA;
        String optionB;
        String optionC;
        String optionD;
        String correctOption;
    }

//    method to implement loading the file contents functionality
    ArrayList<Question> getQuestionDet(String filename) {
//        list containing different question objects
        ArrayList<Question> questions = new ArrayList<>();

        try(Scanner reader = new Scanner(Paths.get(filename))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                if(pieces.length < 6){
                    System.out.println("There is a faulty line in the question file");
                } else {
                    Question theQuestion = new Question();
                    theQuestion.question = pieces[0].trim();
                    theQuestion.optionA = pieces[1].trim();
                    theQuestion.optionB = pieces[2].trim();
                    theQuestion.optionC = pieces[3].trim();
                    theQuestion.optionD = pieces[4].trim();
                    theQuestion.correctOption = pieces[5].trim();
                    questions.add(theQuestion);
                }
            }
        } catch(IOException e){
            System.out.println("Error while handling file: " + e);
        }
        return questions;
    }
}
