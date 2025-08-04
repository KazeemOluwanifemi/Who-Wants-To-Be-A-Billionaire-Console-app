import java.util.*;
//package UserStats;

public class Main{
    public static void main(String[] args) {
        boolean running = true;
        while(running){
            Scanner reader = new Scanner(System.in);
//            User Statistics Inner class instantiated
            UserStats.User user = new UserStats.User();
            System.out.println("==============================================");
            System.out.println("        WHO WANTS TO BE A MILLIONAIRE      ");
            System.out.println("==============================================");
            System.out.println("Pick an option");
            System.out.println("1. Play the game");
            System.out.println("2. Exit the game");

//            Validate user's input
            int playOrExitGame = 0;

            if(reader.hasNextInt()){
                playOrExitGame = reader.nextInt();
//                return a method's functionality based on user's input
                if(playOrExitGame == 1){
                    System.out.println("Alright then, let's play!");
                    String userName = user.getName();
                    startGame(userName);
                } else{
                    System.out.println("Exiting game now.");
                    running = false;
                }
            } else{
                System.out.println("Invalid input, try again!");
            }
            handleQuestions();
        }
    }

    public static void startGame(String userName){
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
            System.out.println("Alright, that's it! Its time to earn cash " + userName + " , are you ready?");
        }
    }

    public static void handleQuestions(){
        Scanner reader = new Scanner(System.in);

        QuestionsHandling questionFile = new QuestionsHandling();
//            Initialize arrayLists to store returned elements from the
//            methods in the fileHandling class
        ArrayList<String> questions = new ArrayList<>();
        questions = questionFile.getQuestion("trivia_quiz.csv");
        ArrayList<String> firstOptions = new ArrayList<>();
        firstOptions = questionFile.firstOption();
        ArrayList<String> secondOptions = new ArrayList<>();
        secondOptions = questionFile.secondOption();
        ArrayList<String> thirdOptions = new ArrayList<>();
        thirdOptions = questionFile.thirdOption();
        ArrayList<String> fourthOptions = new ArrayList<>();
        fourthOptions = questionFile.fourthOption();
        ArrayList<String> correctOptions = new ArrayList<>();
        correctOptions = questionFile.correctOption();

        String currentQuestion = "";
        String currentFirstOpt = "";
        String currentSecondOpt = "";
        String currentThirdOpt = "";
        String currentFourthOpt = "";
        String currentAnswer = "";
        String userAnswer = "";

        for(int i = 0; i < questions.size(); i++){
            currentQuestion = questions.get(i);
            currentFirstOpt = firstOptions.get(i);
            currentSecondOpt = secondOptions.get(i);
            currentThirdOpt = thirdOptions.get(i);
            currentFourthOpt = fourthOptions.get(i);
            currentAnswer =  correctOptions.get(i);

            System.out.println("Here's your first question: ");

            System.out.println(currentQuestion);
            System.out.println("A. " + currentFirstOpt);
            System.out.println("B. " + currentSecondOpt);
            System.out.println("C. " + currentThirdOpt);
            System.out.println("D. " + currentFourthOpt);

            System.out.println("Enter your answer[choose an option]: ");
            userAnswer = reader.nextLine();

            if(userAnswer.equals(currentAnswer)){
                System.out.println("Correct!");
                System.out.println("This question earned you #10,000");
            } else{
                System.out.println("Wrong Answer");
            }

        }

    }
}
