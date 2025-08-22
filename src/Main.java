import questionHandling.Question;
import userstats.User;
import userstats.UserFile;


import java.util.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        the main method only serves as the game initializer
        boolean running = true;
        Scanner reader = new Scanner(System.in);
        while (running) {
//            User Statistics Inner class instantiated
            User user = new User();
            Question questions = new Question();
            List<Question> questionList = questions.getQuestionDet("trivia_quiz.csv");

//            display menu to start the game or exit
            displayMainMenu();

//            starts/exits the game based on user's input
            int playOrExitGame = 0;
//            Start or end the game
            if (reader.hasNextInt()) {
                playOrExitGame = reader.nextInt();
                reader.nextLine();

//                return a method's functionality based on user's input
                if (playOrExitGame == 1) {
                    System.out.println("Alright then, let's play!");
                    System.out.println();
                    user.initializeUserName();
                    displayRules(user);
                    loopThroughQuestions(questionList, user);
                } else if(playOrExitGame == 2) {
                    System.out.println("Exiting game now.");
                    running = false;
                }
            } else {
                System.out.println("Invalid input, try again!");
                reader.nextLine();
            }
        }
        reader.close();
    }

//    This method displays the main menu to prompt the user to play or exit the game
    public static void displayMainMenu() {
        System.out.println("Welcome to Who Wants to Be a Millionaire!");
        System.out.println("1. Play Game");
        System.out.println("2. Exit Game");
        System.out.println("Please enter your choice: ");
        System.out.print("-> ");
    }

//    This method displays the rules of the game to the user
    public static void displayRules(User user){
        System.out.println();
        System.out.println("Welcome " + user.getName() + " to Who Wants to Be a Millionaire!");
        System.out.println("========================================================");
        System.out.println("Here are the rules of the game:");
        System.out.println("1. You will be asked a series of questions.");
        System.out.println("2. Each question has four options, and you must choose the correct one.");
        System.out.println("3. You can use key [L] to choose from your available lifelines if you get stuck.");
        System.out.println("4. The game ends when you answer all questions or choose to quit.");
        System.out.println("5. Good luck!");
        System.out.println("========================================================");
        System.out.println();
    }

//    this method displays current question
    public static void displayQuestion(Question question) {
        System.out.println("Question: " + question.getQuestion());
        System.out.println("A: " + question.getOptionA());
        System.out.println("B: " + question.getOptionB());
        System.out.println("C: " + question.getOptionC());
        System.out.println("D: " + question.getOptionD());
        System.out.print("Please enter your answer (A, B, C, or D): ");
    }

//    this method loops through questions
    public static void loopThroughQuestions(List<Question> questionsList, User user) {
        UserFile userFile = new UserFile();
        Collections.shuffle(questionsList);
        List<Question> shuffledQuestions = questionsList;

        Scanner reader = new Scanner(System.in);
        int correctAnswers = 0;

        for (int index = 0; index < questionsList.size(); index++) {
            displayQuestion(questionsList.get(index));
            String userAnswer = reader.nextLine().trim().toUpperCase();
            String optionValue = checkOption(questionsList.get(index), userAnswer);

            if (userAnswer.equals(questionsList.get(index).getCorrectOption()) || optionValue.equals(questionsList.get(index).getCorrectOption())) {
                correctAnswers++;
                user.setCrtAnswers(correctAnswers);
                System.out.println("Correct!");
                user.setAmtEarned(questionsList.get(index).cashPrize(index));
                System.out.println("You have earned: #" + user.getAmtEarned());
                System.out.println();
                setSafetyNet(user, index, questionsList);

            } else if(userAnswer.equalsIgnoreCase("L")){
                String lifeLine = "L";
                boolean running = true;

                while (running) {
                    displayLifelineMenu();
                    String lifelineChoice = reader.nextLine().trim();
                    switch (lifelineChoice) {
                        case "":
                            System.out.println("Input cannot be empty. Please try again.");
                        case "1":
                            lifeLine = "F";
                            displayLifelineIllegibilityMessage(lifeLine);
                            System.out.println("50/50 lifeline activated! Two incorrect options have been removed.");
                            System.out.println();
                            lifeline5050(index, shuffledQuestions, reader, user, userFile);
                            running = false;
                            break;
                        case "2":
                            lifeLine = "S";
                            displayLifelineIllegibilityMessage(lifeLine);
                            System.out.println("Swap question lifeline activated! The question has been swapped.");
                            System.out.println();
                            swapQuestion(questionsList, index, reader, user, userFile);
                            running = false;
                            break;
                        case "3":
                            lifeLine = "A";
                            displayLifelineIllegibilityMessage(lifeLine);
                            System.out.println("This feature is still under construction. Please try again later.");
                            running = false;
                            break;
                    }
                }
            } else if (user.getCrtAnswers() == questionsList.size()) {
                System.out.println("Congratulations! You have answered all questions correctly!");
                System.out.println("You have earned a total of: #" + user.getAmtEarned());
                endGameMessage(user, userFile);
            }
            else {
                System.out.println("Wrong! The correct answer was: " + questionsList.get(index).getCorrectOption());
                endGameMessage(user, userFile);
                break;
            }
        }
        userFile.createAndWriteToUserFile(user);
        System.out.println();
        reader.close();
    }

//    this method returns the option value based on the user's input
    public static String checkOption(Question question, String userAnswer) {
        return switch (userAnswer) {
            case "A" -> question.getOptionA();
            case "B" -> question.getOptionB();
            case "C" -> question.getOptionC();
            case "D" -> question.getOptionD();
            default -> "Invalid entry";
        };
    }

//    this method ends the game
    public static void endGameMessage(User user, UserFile userFile) {
        System.out.println();
        System.out.println("Game Over!");
        System.out.println("Thank you for playing, " + user.getName() + "!");
        System.out.println("These are your stats for this game session:");
        System.out.println("========================================================");
        userFile.displayUserStats(user);
        System.out.println("========================================================");
        System.out.println();
    }

//    this method sets the safety net
    public static void setSafetyNet(User user, int index, List<Question> questionsList) {
        int safetyNet = 0;
        if(user.getCrtAnswers() >=5 || user.getCrtAnswers() >= 10) {
            if (user.getCrtAnswers() >= 10) {
                System.out.println("========================================================");
                user.setAmtEarned(questionsList.get(index).cashPrize(4));
                System.out.println("Congratulations! You have reached the first safety net.");
                System.out.println("You have earned a safety net of: #" + user.getAmtEarned());
                System.out.println("========================================================");
                safetyNet = 1;
            } else if (user.getCrtAnswers() >= 5) {
                System.out.println("========================================================");
                user.setAmtEarned(questionsList.get(index).cashPrize(9));
                System.out.println("Congratulations! You have reached the second safety net.");
                System.out.println("You have earned a safety net of: #" + user.getAmtEarned());
                System.out.println("========================================================");
                safetyNet = 2;
            }
        } else {
            System.out.println("You have not reached any safety net yet.");
        }
    }

//    this function displays lifeline menu
    public static void displayLifelineMenu() {
        System.out.println("Your available lifelines: ");
        System.out.println("1. 50/50");
        System.out.println("2. Swap question");
        System.out.println("3. Ask the Audience");

        System.out.print("Please enter your choice: ");
    }

//    Swap method
    public static void swapQuestion(List<Question> questionsList, int index, Scanner reader, User user, UserFile userFile) {
        displayQuestion(questionsList.get(index + 2));
        String userAnswerSwapped = reader.nextLine().trim().toUpperCase();
        String optionValueSwapped = checkOption(questionsList.get(index + 2), userAnswerSwapped);

        if(userAnswerSwapped.equals(questionsList.get(index + 2).getCorrectOption()) || optionValueSwapped.equals(questionsList.get(index + 2).getCorrectOption())) {
            user.setCrtAnswers(user.getCrtAnswers() + 1);
            System.out.println("Correct!");
            user.setAmtEarned(questionsList.get(index + 2).cashPrize(index + 2));
            System.out.println("You have earned: #" + user.getAmtEarned());
            System.out.println();
        } else {
            System.out.println("Wrong! The correct answer was: " + questionsList.get(index + 2).getCorrectOption());
            endGameMessage(user, userFile);
        }
        System.out.println();
        System.out.println("You have used the Swap question lifeline.");
        System.out.println("You can no longer use this lifeline again.");
        System.out.println();
    }

//    50/50 lifeline method
private static void lifeline5050(int index, List<Question> shuffledList, Scanner reader, User user, UserFile userFile){
    System.out.println(shuffledList.get(index).getQuestion());
    System.out.println("A. " + shuffledList.get(index).getCorrectOption());
    System.out.println("B. " + shuffledList.get(index).getTrickOption());

    System.out.print("Please enter your answer (A or B): ");
    String userAnswer = reader.nextLine().trim().toUpperCase();
    if (userAnswer.equals(shuffledList.get(index).getCorrectOption()) || userAnswer.equals(shuffledList.get(index).getTrickOption())) {
        user.setAmtEarned(shuffledList.get(index).cashPrize(index));
        user.setCrtAnswers(user.getCrtAnswers() + 1);
        System.out.println("Correct!");
        System.out.println("You have earned: #" + user.getAmtEarned());
    } else {
        System.out.println("Wrong! The correct answer was: " + shuffledList.get(index).getCorrectOption());
        endGameMessage(user, userFile);
    }
    System.out.println();
    System.out.println("You have used the 50/50 lifeline.");
    System.out.println("You can no longer use this lifeline again.");
    System.out.println();
}

//    display lifeline illegibility message
    public static void displayLifelineIllegibilityMessage(String status) {
        if(status.equalsIgnoreCase("F")){
            System.out.println("You have already used the 50/50 lifeline.");
        } else if(status.equalsIgnoreCase("S")){
            System.out.println("You have already used the Swap question lifeline.");
        } else if(status.equalsIgnoreCase("A")){
            System.out.println("You have already used the Ask the Audience lifeline.");
        }
    }

}