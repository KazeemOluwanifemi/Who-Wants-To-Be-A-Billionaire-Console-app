import java.util.*;
//package UserStats;

public class Main {
    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            Scanner reader = new Scanner(System.in);
//            User Statistics Inner class instantiated
            UserStats.User user = new UserStats.User();
            System.out.println("==============================================");
            System.out.println("        WHO WANTS TO BE A MILLIONAIRE      ");
            System.out.println("==============================================");
            System.out.println("Pick an option");
            System.out.println("1. Play the game");
            System.out.println("2. Exit the game");

//            Validate user's input and start game
            int playOrExitGame = 0;
//            Start or end the game
            if (reader.hasNextInt()) {
                playOrExitGame = reader.nextInt();
//                return a method's functionality based on user's input
                if (playOrExitGame == 1) {
                    System.out.println("Alright then, let's play!");
                    String userName = user.getName();
                    startGame(userName);
                } else {
                    System.out.println("Exiting game now.");
                    running = false;
                }
            } else {
                System.out.println("Invalid input, try again!");
            }
            reader.close();

//            load game questions; counter counts the number of questions displayed
            int counter = 0;

            while(true){
                System.out.println("Are you ready?[Yes/No]");
//            Accept user input, validate it and call displayQuestion method based on the user's input
                String userPlay = reader.nextLine().trim();
                if (userPlay.isEmpty()) {
                    System.out.println("Input cannot be empty.");
                } else if (userPlay.equalsIgnoreCase("Yes")) {
                    displayQuestions(counter);
                    break;
                }
            }
        }
    }

//    Handles starting the game screen
    public static void startGame(String userName) {
        Scanner reader = new Scanner(System.in);
        if (userName.isEmpty()) {
            System.out.println("An error occurred while accepting your name, please try again");
        } else {
            System.out.println("Hello " + userName + " , welcome to Who Wants to Be a Millionaire!");
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
    }

//    Handles question display
    public static void displayQuestions(int counter){
//        access the userStats details

//        initialize number of questions counter


//        access the method in the questionHandling class
        QuestionsHandling theQuestion = new QuestionsHandling();
        ArrayList<QuestionsHandling.Question> questionList = theQuestion.getQuestionDet("trivia_quiz.csv");

        for (QuestionsHandling.Question question : questionList) {
            System.out.println(question.question);
            System.out.println("A. " + question.optionA);
            System.out.println("B. " + question.optionB);
            System.out.println("C. " + question.optionC);
            System.out.println("D. " + question.optionD);
//            System.out.println(questionList.get(i).correctOption);
        }

//        questionList = theQuestion.getQuestionDet("trivia_quiz.csv");
    }
}