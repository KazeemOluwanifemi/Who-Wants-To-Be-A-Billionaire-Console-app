import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class QuestionsHandling {
//    Scanner reader = new Scanner(System.in);

    String question;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    String correctOption;

    ArrayList<String> getQuestion(String filename){
//        create a new list
        ArrayList<String> questions = new ArrayList<>();
//        validate presence of a string in the file to handle a possible exception
        try (Scanner reader = new Scanner(Paths.get(filename))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                question = pieces[0];
                questions.add(question);
            }
        } catch(IOException e){
            System.out.println("Error while handling file: " + e);
        }
        return questions;
    }

    ArrayList<String> firstOption(){
//        create a new list
        ArrayList<String> firstOptions = new ArrayList<>();
//        validate presence of text in file and handle possible exceptions using a try catch stuff
        try(Scanner reader = new Scanner(Paths.get("trivia_quiz.csv"))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                optionA = pieces[1];
                firstOptions.add(optionA);
            }
        } catch(IOException e){
            System.out.println("Error while handling file: " + e);
        }
        return firstOptions;
    }

    ArrayList<String> secondOption(){
        ArrayList<String> secondOptions = new ArrayList<>();
        try(Scanner reader = new Scanner(Paths.get("trivia_quiz.csv"))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                optionB = pieces[2];
                secondOptions.add(optionB);
            }
        } catch(IOException e){
            System.out.println("Error while handling file: " + e);
        }
        return secondOptions;
    }

    ArrayList<String> thirdOption(){
        ArrayList<String> thirdOptions = new ArrayList<>();

        try(Scanner reader = new Scanner(Paths.get("trivia_quiz.csv"))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                optionC = pieces[3];
                thirdOptions.add(optionC);
            }
        } catch(IOException e){
            System.out.println("Error while handling file: " + e);
        }
        return thirdOptions;
    }

    ArrayList<String> fourthOption() {
        ArrayList<String> fourthOptions = new ArrayList<>();

        try(Scanner reader = new Scanner(Paths.get("trivia_quiz.csv"))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                optionD = pieces[4];
                fourthOptions.add(optionD);
            }
        } catch(IOException e){
            System.out.println("Error while handling file: " + e);
        }
        return fourthOptions;
    }

    ArrayList<String> correctOption(){
        ArrayList<String> correctOptions = new ArrayList<>();

        try(Scanner reader = new Scanner(Paths.get("trivia_quiz.csv"))){
            while(reader.hasNextLine()){
                String line = reader.nextLine();

                String[] pieces = line.split(",");

                correctOption = pieces[5];
                correctOptions.add(correctOption);
            }
        } catch(IOException e){
            System.out.println("Error while handling file: " + e);
        }
        return correctOptions;
    }
}
