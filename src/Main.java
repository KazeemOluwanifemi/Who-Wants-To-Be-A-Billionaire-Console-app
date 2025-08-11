import questionHandling.Question;
import userstats.User;


import java.util.*;
//package userstats.UserStats;

public class Main {
    public static void main(String[] args) {
        boolean running = true;
        Scanner reader = new Scanner(System.in);
        while (running) {
//            User Statistics Inner class instantiated
            User user = new User();
            displayMainMenu();
//            Validate user's input and start game
            int playOrExitGame = 0;
//            Start or end the game
            if (reader.hasNextInt()) {
                playOrExitGame = reader.nextInt();
                reader.nextLine();
//                return a method's functionality based on user's input
                if (playOrExitGame == 1) {
                    System.out.println("Alright then, let's play!");
                    String userName = user.initializeUserName();
                    startGame(userName,reader);
//                    running = false;
                } else {
                    System.out.println("Exiting game now.");
                    running = false;
                }
            } else {
                System.out.println("Invalid input, try again!");
            }
        }

        reader.close();
    }

    private static void displayMainMenu() {
        System.out.println("==============================================");
        System.out.println("        WHO WANTS TO BE A MILLIONAIRE      ");
        System.out.println("==============================================");
        System.out.println("Pick an option");
        System.out.println("1. Play the game");
        System.out.println("2. Exit the game");
        System.out.print("-> ");
    }

    //    Handles starting the game screen
    public static void startGame(String userName, Scanner reader) {
//        Scanner reader = new Scanner(System.in);
        if (userName.isEmpty()) {
            System.out.println("An error occurred while accepting your name, please try again");
        } else {
            displayRules(userName);
//            load game questions;
//            counter counts the number of questions displayed
            int questionCounter = 0;
            while(true){
                System.out.println("Are you ready?[Yes/No]");
                System.out.print("-> ");
//            Accept user input, validate it and call displayQuestion method based on the user's input
                String userPlay = reader.nextLine();
//                reader.nextLine();
                if (userPlay.isEmpty()) {
                    System.out.println("Input cannot be empty.");
                } else if (userPlay.equalsIgnoreCase("Yes")) {
                    displayQuestions(questionCounter, userName);
                    break;
                } else if (userPlay.equalsIgnoreCase("No")){
                    System.out.println("Okay, bye!");
                    break;
                }
            }

        }
    }

    private static void displayRules(String userName) {
        System.out.println("Hello " + userName + ", welcome to Who Wants to Be a Millionaire!");
        System.out.println("Please pay attention to the following rules: ");
        System.out.println("======================================");
        System.out.println("          RULES OF THE GAME           ");
        System.out.println("======================================");
        System.out.println("1. An answer confirmed as your final answer cannot be changed");
        System.out.println("2. You are required to correctly answer 15 questions in a row to win the 1 million Naira prize");
        System.out.println("3. Walk Away: If you are unsure about a question, you can choose to forfeit that question" +
                "and walk away with your previously earned amount ");
        System.out.println("4. Three correctly answered questions are required to activate 'Walk Away'");
        System.out.println("5. Safety Net: The amount attached to a 'safety net' question is guaranteed for you even you " +
                "if you miss the next set of questions but answer that question correctly");
        System.out.println("6. There is a designated safety net for every 5 questions answered correctly");
        System.out.println("============================================================================");
        System.out.println();
        System.out.println("Alright, that's it! Its time to earn cash");
    }

    //    Handles question display
    public static void displayQuestions(int counter, String userName){
        Scanner reader = new Scanner(System.in);
//        access the userStats details
        User user = new User();
//        initialize number of questions counter and user answer variable
        int counterCorrect = user.getCrtAnswers();
        int counterWrong = user.getWrongAnswers();
        int counterCorrectSwapped;
        String userAnswer = "";
//        String userSwap = "";
        String finalAnswer = "";
        String finalAnswerCheck = "";
        String userAnswerCheck = "";
        boolean exitAll = true;
        boolean running2 = true;
        double amtEarned = user.getAmtEarned();

//        access the method in the questionHandling class
        Question theQuestion = new Question();
        List<Question> questionList = theQuestion.getQuestionDet("trivia_quiz.csv");

//        System.out.println("CHECK: counter=" + counter + ", exitAll=" + exitAll);
        outerLoop:
        while(exitAll){
//            System.out.println("CHECK: counter=" + counter + ", exitAll=" + exitAll);
            for(int i = 0; i < questionList.size(); i++){
                running2 = true;
                currentQuestion(questionList, i);
                System.out.print("Enter your final answer[this answer cannot be changed]: ");
                userAnswer = reader.nextLine().toUpperCase();
                userAnswerCheck = checkOption(questionList, userAnswer, i);
                counter++;
//                process and validate the input using a function
                if(userAnswerCheck.equalsIgnoreCase(questionList.get(i).getCorrectOption())){
                    System.out.println("Correct, you have earned #" + questionList.get(i).getCashPrize());
                    amtEarned += questionList.get(i).getCashPrize();
                    counterCorrect++;
                } else {
//                    ask user to validate wrong answer
                    System.out.println("Are you sure of this answer?[Y/N]");
                    String confirmAnswer = " ";
                    System.out.println(running2);
                    while(running2){
                        System.out.println(running2);
                        System.out.print("-> ");
                        confirmAnswer = reader.nextLine().toUpperCase().trim();
                        switch (confirmAnswer) {
                            case "" -> System.out.println("Input cannot be empty.");
                            case "Y", "YES" -> {
                                System.out.println("You entered the wrong answer");
                                System.out.println("Total amount earned by " + userName + " is #" + amtEarned);
                                System.out.println("Returning to main menu...");
                                counterWrong++;
                                break outerLoop;
                            }
                            case "N", "NO" -> {
                                currentQuestion(questionList, i);
                                System.out.println("Enter your final answer[last time]: ");
                                finalAnswer = reader.nextLine().toUpperCase();
                                finalAnswerCheck = checkOption(questionList, finalAnswer, i);


                                if (finalAnswerCheck.equalsIgnoreCase(questionList.get(i).getCorrectOption())) {
                                    System.out.println("Correct, you have earned #" + questionList.get(i).getCashPrize());
                                    amtEarned += questionList.get(i).getCashPrize();
                                    counterCorrect++;
                                    running2 = false;
                                } else {
//                                     this is supposed to notify a user's validity status for the swap question functionality
                                    System.out.println("Wrong answer, again.");
                                    if(counter >= 3 && !finalAnswerCheck.equalsIgnoreCase(questionList.get(i).getCorrectOption())){
                                        int swap = displaySwapMenu();
                                        if(swap == 1){
                                            counterCorrectSwapped = swapQuestion(reader, questionList, i, counterCorrect);
                                            if (counterCorrectSwapped > counterCorrect){
                                                System.out.println("Yay, you got the answer!");
                                                System.out.println("You have earned #" + questionList.get(i + 2).getCashPrize());
                                                amtEarned += questionList.get(i + 2).getCashPrize();
                                                counterCorrect++;
                                                running2 = false;
                                                break;
                                            } else{
//                                                get back to the main menu
                                                break outerLoop;
                                            }
                                        } else if(swap == 0){
                                            System.out.println("Alright, thanks for playing!");
                                            System.out.println("Total amount earned by " + user.getName() + " is #" + amtEarned);
                                            System.out.println("Returning to main menu...");
                                            counterWrong++;
                                            break outerLoop;
                                        }
                                    }
                                    else {
                                        System.out.println("You are not eligible for the swap lifeline.");
                                        System.out.println("Total Amount Earned: #" + amtEarned);
                                        System.out.println("Returning to main menu...");
                                        running2 = false;
//                                        break outerLoop;
                                    }
                                }
                            }
                        }
                    }

                }

                if(counter == 15 || counterCorrect == 15){
                    System.out.println("You have reached the end of the game, congratulations!");
                    System.out.println("Total amount earned by " + user.getName() + " is #" + amtEarned);
                    exitAll = false;
                }
            }
        }
    }

    private static int swapQuestion(Scanner reader, List<Question> questionList, int i, int counterCorrect) {
        String userSwap;
        String userAnswer;
        String userAnswerCheck;
        boolean running3 = true;
        while(running3){
            System.out.println("Swap Question");
            System.out.println("Press 'S' to swap your question[key 'N' to exit the game]");
            userSwap = reader.nextLine().toUpperCase();
            switch (userSwap) {
                case "S" -> {
//                                swap the question
                    System.out.println(questionList.get(i + 2).getQuestion());
                    System.out.println("A. " + questionList.get(i + 2).getOptionA());
                    System.out.println("B. " + questionList.get(i + 2).getOptionB());
                    System.out.println("C. " + questionList.get(i + 2).getOptionC());
                    System.out.println("D. " + questionList.get(i + 2).getOptionD());

                    System.out.println("Enter your final answer[this answer cannot be changed]: ");
                    System.out.print("-> ");
                    userAnswer = reader.nextLine().toUpperCase();
                    userAnswerCheck = checkOption(questionList, userAnswer, i + 2);
                    if (userAnswerCheck.equalsIgnoreCase(questionList.get(i + 2).getCorrectOption())) {
                        counterCorrect++;
                        running3 = false;
                    } else{
                        System.out.println("You entered the wrong answer");
                        System.out.println("Returning to main menu...");
                        running3 = false;
                    }
//                This is the only time the loop needs another iteration
                }
                case "N" -> {
                    System.out.println("Thanks for playing, bye!");
                    running3 = false;
                }
            }
        }
        return counterCorrect;
    }

    private static void currentQuestion(List<Question> questionList, int i) {
        System.out.println(questionList.get(i).getQuestion());
        System.out.println("A. " + questionList.get(i).getOptionA());
        System.out.println("B. " + questionList.get(i).getOptionB());
        System.out.println("C. " + questionList.get(i).getOptionC());
        System.out.println("D. " + questionList.get(i).getOptionD());
    }

//    function to return correct answer based on option chosen as users
    private static String checkOption(List<Question> questionList, String userAnswer, int i){
        return switch (userAnswer) {
            case "A" -> questionList.get(i).getOptionA();
            case "B" -> questionList.get(i).getOptionB();
            case "C" -> questionList.get(i).getOptionC();
            case "D" -> questionList.get(i).getOptionD();
            default -> "Invalid entry";
        };
    }

    private static int displaySwapMenu(){
        Scanner reader = new Scanner(System.in);
        System.out.println("Would you like to swap your question?[Yes/No]");
        System.out.print("-> ");
        boolean running = true;
        int returnValue = 2;
        while(running){
            String userAnswer = reader.nextLine().trim();
            if(userAnswer.equalsIgnoreCase("Yes")){
                returnValue = 1;
                running = false;
            }
            else if(userAnswer.equalsIgnoreCase("No")){
                returnValue = 0;
                running = false;
            } else{
                System.out.println("Invalid input, try again.");;
            }
        }
        return returnValue;

    }
}