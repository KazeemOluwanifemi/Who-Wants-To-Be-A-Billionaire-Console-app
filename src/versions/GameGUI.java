package versions;

import questionHandling.Question;
import userstats.User;
import userstats.UserFile;
import userstats.UserRanking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

public class GameGUI extends JFrame {

    // Core state (names mirror terminal logic)
    private User user;
    private UserFile userFile;
    private UserRanking userRanks;

    private List<Question> questionList;
    private int currentIndex = 0;
    private int correctAnswers = 0;

    private int lifeLineUsed5050 = 0;
    private int lifeLineUsedSwap = 0;
    private int lifeLineUsedAsk = 0;

    // UI
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    // Game screen widgets
    private JLabel questionLabel;
    private JButton optionA, optionB, optionC, optionD;
    private JButton lifelineBtn, nextBtn;
    private JLabel infoLabel, headerLabel;

    // Ranking screen widgets to refresh
    private JPanel rankingPanel;
    private JLabel highestLabel;
    private DefaultTableModel rankingTableModel;
    private JTable rankingTable;

    // Keep current Question handy
    private Question currentQuestion;

    public GameGUI() {
        setTitle("Who Wants to Be a Millionaire - Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setLocationRelativeTo(null);

        userFile = new UserFile();
        userRanks = new UserRanking();

        addMainMenu();
        addRulesPanel();
        addGamePanel();
        addRankingPanel(); // ranking card added

        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "menu");
    }

    /** ----------- Screens ----------- */
    private void addMainMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Welcome to Who Wants to Be a Millionaire!", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JButton playBtn = new JButton("1. Play Game");
        JButton rankingBtn = new JButton("2. View Rankings"); // now opens ranking screen
        JButton exitBtn = new JButton("3. Exit Game");

        playBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rankingBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        playBtn.addActionListener(e -> {
            // Create a new user and mimic initializeUserName() behavior
            user = new User();
            String name = JOptionPane.showInputDialog(this, "What is your name:\n-> ");
            if (name == null || name.trim().isEmpty()) {
                // keep default if empty
                user.setName("Player");
            } else {
                user.setName(name.trim());
            }
            // Load and shuffle questions exactly like terminal
            Question loader = new Question();
            questionList = loader.getQuestionDet("trivia_quiz.csv");
            Collections.shuffle(questionList);

            // Reset run state
            currentIndex = 0;
            correctAnswers = 0;
            lifeLineUsed5050 = 0;
            lifeLineUsedSwap = 0;
            lifeLineUsedAsk = 0;
            user.setCrtAnswers(0);
            user.setAmtEarned(0);

            // Show rules (terminal prints; we show them then move to game)
            cardLayout.show(mainPanel, "rules");
        });

        rankingBtn.addActionListener(e -> {
            // Recompute and refresh ranking panel each time it's requested
            // also call highestScore() to keep console behavior (unchanged)
            try {
                userRanks.highestScore(); // still prints to console as your terminal did
            } catch (Exception ex) {
                // preserve console printing behavior but don't crash GUI
                System.out.println("Could not compute highestScore: " + ex.getMessage());
            }
            refreshRankingData();
            cardLayout.show(mainPanel, "ranking");
        });

        exitBtn.addActionListener(e -> System.exit(0));

        menu.add(title);
        menu.add(Box.createVerticalStrut(24));
        menu.add(playBtn);
        menu.add(Box.createVerticalStrut(12));
        menu.add(rankingBtn);
        menu.add(Box.createVerticalStrut(12));
        menu.add(exitBtn);

        mainPanel.add(menu, "menu");
    }

    private void addRulesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea rulesText = new JTextArea();
        rulesText.setEditable(false);
        rulesText.setFont(rulesText.getFont().deriveFont(14f));
        rulesText.setText(
                "\nWelcome " + (user != null ? user.getName() : "") + " to Who Wants to Be a Millionaire!\n" +
                        "========================================================\n" +
                        "Here are the rules of the game:\n" +
                        "1. You will be asked a series of questions.\n" +
                        "2. Each question has four options, and you must choose the correct one.\n" +
                        "3. You can use key [L] to choose from your available lifelines if you get stuck.\n" +
                        "4. The game ends when you answer all questions or choose to quit.\n" +
                        "5. Good luck!\n" +
                        "========================================================\n"
        );

        JButton startBtn = new JButton("Start Game");
        startBtn.addActionListener(e -> {
            displayQuestion();
            cardLayout.show(mainPanel, "game");
        });

        panel.add(new JScrollPane(rulesText), BorderLayout.CENTER);
        panel.add(startBtn, BorderLayout.SOUTH);
        mainPanel.add(panel, "rules");
    }

    private void addGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        headerLabel = new JLabel("Earned: #0 | Correct: 0", SwingConstants.CENTER);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));

        questionLabel = new JLabel("Question appears here", SwingConstants.CENTER);
        questionLabel.setFont(questionLabel.getFont().deriveFont(Font.BOLD, 18f));

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionA = new JButton("A: ");
        optionB = new JButton("B: ");
        optionC = new JButton("C: ");
        optionD = new JButton("D: ");

        Action answerListener = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton src = (JButton) e.getSource();
                handleStandardAnswer(src.getText());
            }
        };
        optionA.addActionListener(answerListener);
        optionB.addActionListener(answerListener);
        optionC.addActionListener(answerListener);
        optionD.addActionListener(answerListener);

        optionsPanel.add(optionA);
        optionsPanel.add(optionB);
        optionsPanel.add(optionC);
        optionsPanel.add(optionD);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        infoLabel = new JLabel("Make a choice or use a lifeline", SwingConstants.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        lifelineBtn = new JButton("Use Lifeline");
        lifelineBtn.addActionListener(e -> openLifelineDialog());
        nextBtn = new JButton("Next");
        nextBtn.setEnabled(false);
        nextBtn.addActionListener(e -> gotoNextOrEnd());

        btnPanel.add(lifelineBtn);
        btnPanel.add(nextBtn);

        bottomPanel.add(infoLabel, BorderLayout.CENTER);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(questionLabel, BorderLayout.CENTER);
        panel.add(optionsPanel, BorderLayout.WEST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "game");
    }

    /** ----------- Ranking screen (JTable) ----------- */
    private void addRankingPanel() {
        rankingPanel = new JPanel(new BorderLayout(10, 10));
        rankingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("🏆 Player Rankings", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        rankingPanel.add(title, BorderLayout.NORTH);

        // Highest scorer label (will be refreshed)
        highestLabel = new JLabel("", SwingConstants.CENTER);
        highestLabel.setFont(highestLabel.getFont().deriveFont(16f));
        rankingPanel.add(highestLabel, BorderLayout.PAGE_START);

        // Table model & table (empty now; refreshed when requested)
        String[] columns = {"Player", "Score"};
        rankingTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        rankingTable = new JTable(rankingTableModel);
        rankingTable.setRowHeight(24);
        rankingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        rankingTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Sorting by Score (column index 1) in descending order
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(rankingTableModel);
        rankingTable.setRowSorter(sorter);
        sorter.setComparator(1, (o1, o2) -> ((Integer) o2).compareTo((Integer) o1));
        sorter.toggleSortOrder(1);

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        rankingPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        buttonPanel.add(backButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        rankingPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(rankingPanel, "ranking");
    }

    /** Refresh ranking data from disk & update label */
    private void refreshRankingData() {
        // read all rankings (preserves your existing readFromFile logic)
        List<UserRanking> allRankings = userRanks.readFromFile(userRanks.mainDB);

        // update highest label -- call highestScore() as terminal did (it prints too)
        try {
            userRanks.highestScore();
        } catch (Exception ex) {
            // ignore — keep GUI alive
        }

        highestLabel.setText("✨ Top Player: " + userRanks.getTopPlayer() + " — Score: " + userRanks.getHighestScore());

        // rebuild table model
        rankingTableModel.setRowCount(0); // clear
        for (UserRanking r : allRankings) {
            // readFromFile returns int for score; store as Integer so comparator works
            rankingTableModel.addRow(new Object[]{r.getUserName(), r.getUserScore()});
        }
    }

    /** ----------- Game flow ----------- */
    private void displayQuestion() {
        currentQuestion = questionList.get(currentIndex);
        questionLabel.setText("Q" + (currentIndex + 1) + ": " + currentQuestion.getQuestion());
        optionA.setText("A: " + currentQuestion.getOptionA());
        optionB.setText("B: " + currentQuestion.getOptionB());
        optionC.setText("C: " + currentQuestion.getOptionC());
        optionD.setText("D: " + currentQuestion.getOptionD());
        headerLabel.setText("Earned: #" + user.getAmtEarned() + " | Correct: " + user.getCrtAnswers());
        infoLabel.setText("Please enter your answer (A, B, C, or D)");
        nextBtn.setEnabled(false);
        setAnswerButtonsEnabled(true);

        // mirror terminal printouts
        System.out.println("Question: " + currentQuestion.getQuestion());
        System.out.println("A: " + currentQuestion.getOptionA());
        System.out.println("B: " + currentQuestion.getOptionB());
        System.out.println("C: " + currentQuestion.getOptionC());
        System.out.println("D: " + currentQuestion.getOptionD());
        System.out.print("Please enter your choice: ");
    }

    private void setAnswerButtonsEnabled(boolean enabled) {
        optionA.setEnabled(enabled);
        optionB.setEnabled(enabled);
        optionC.setEnabled(enabled);
        optionD.setEnabled(enabled);
    }

    private void handleStandardAnswer(String buttonText) {
        // Extract "A", "B", "C", or "D"
        String choice = buttonText.substring(0, 1);
        String optionValue = GameTerminal.checkOption(currentQuestion, choice);

        System.out.println(optionValue); // mirror terminal behaviour

        // If the currentIndex is somehow out of range, clamp it to last valid one
        int safeIndex = Math.max(0, Math.min(currentIndex, questionList.size() - 1));

        if (choice.equalsIgnoreCase(currentQuestion.getCorrectOptionValue())
                || optionValue.equalsIgnoreCase(currentQuestion.getCorrectOptionValue())) {

            // correct
            correctAnswers++;
            user.setCrtAnswers(correctAnswers);

            // award prize using safeIndex so cashPrize never gets an invalid index
            user.setAmtEarned(currentQuestion.cashPrize(safeIndex));

            infoLabel.setText("Correct! You have earned: #" + user.getAmtEarned());

            // safety net logic (mirrors terminal)
            setSafetyNet(user, safeIndex, questionList);

            headerLabel.setText("Earned: #" + user.getAmtEarned() + " | Correct: " + user.getCrtAnswers());
            nextBtn.setEnabled(true);
            setAnswerButtonsEnabled(false);

            // If this was the last question, handle win immediately instead of waiting for Next
            if (user.getCrtAnswers() == questionList.size()) {
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You have answered all questions correctly!\n" +
                                "You have earned a total of: #" + user.getAmtEarned());
                // Save and return to menu
                endGameAndReturnToMenu();
            }
        } else {
            // wrong answer -> apply end game (safety net has already been set previously if reached)
            infoLabel.setText("Wrong! The correct answer was: " + currentQuestion.getCorrectOptionValue());

            // Apply safety net fallback here to replicate terminal logic for losing:
            // If the user had previously reached the 10-question safety net (crtAnswers >= 10)
            // or 5-question safety net (crtAnswers >= 5), ensure the amount reflects that.
            // In your terminal version this was handled via setSafetyNet during correct answers,
            // but we ensure it here too just in case.
            if (user.getCrtAnswers() >= 10) {
                user.setAmtEarned(questionList.get(Math.min(9, questionList.size()-1)).cashPrize(9));
            } else if (user.getCrtAnswers() >= 5) {
                user.setAmtEarned(questionList.get(Math.min(4, questionList.size()-1)).cashPrize(4));
            } else {
                // if they never hit a safety net, set to 0 to mimic terminal loss
                user.setAmtEarned(0);
            }

            endGameAndReturnToMenu();
        }
    }


    private void gotoNextOrEnd() {
        // If currentIndex is already at or beyond last question, treat as finished.
        if (questionList == null || questionList.isEmpty()) {
            // nothing to do
            cardLayout.show(mainPanel, "menu");
            return;
        }

        if (currentIndex >= questionList.size() - 1) {
            // Last question already answered -- win case handled here to be safe
            JOptionPane.showMessageDialog(this,
                    "🎉 Congratulations " + user.getName() + "! 🎉\n" +
                            "You answered all questions correctly!\n" +
                            "Your total prize is ₦" + user.getAmtEarned(),
                    "Game Over", JOptionPane.INFORMATION_MESSAGE);

            // Save user stats
            userFile.createAndWriteToUserFile(user);
            userRanks.createWriteToMainFile(user);

            // Also call terminal end-screen to keep behavior identical
            GameTerminal.endGameMessage(user, userFile);

            cardLayout.show(mainPanel, "menu");
        } else {
            // Move to next question
            currentIndex++;
            // As an extra guard, if increment somehow exceeded bounds, clamp it
            if (currentIndex > questionList.size() - 1) {
                currentIndex = questionList.size() - 1;
            }
            displayQuestion();
        }
    }


    /** ----------- Lifelines ----------- */
    private void openLifelineDialog() {
        // We’ll show choices 1/2/3 like terminal, enforce counters with the same messages
        Object[] options = {"1. 50/50", "2. Swap question", "3. Ask the Audience", "Cancel"};
        int sel = JOptionPane.showOptionDialog(this,
                "Your available lifelines:",
                "Lifelines",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (sel == 0) {               // 50/50
            lifeLineUsed5050++;
            GameTerminal.displayLifelineValidityMessage("F", lifeLineUsed5050);
            if (lifeLineUsed5050 > 1) return;
            JOptionPane.showMessageDialog(this, "50/50 lifeline activated! Two incorrect options have been removed.");
            lifeline5050();
        } else if (sel == 1) {        // Swap question
            lifeLineUsedSwap++;
            GameTerminal.displayLifelineValidityMessage("S", lifeLineUsedSwap);
            if (lifeLineUsedSwap > 1) return;
            JOptionPane.showMessageDialog(this, "Swap question lifeline activated! The question has been swapped.");
            swapQuestionGUI();
        } else if (sel == 2) {        // Ask the Audience
            lifeLineUsedAsk++;
            GameTerminal.displayLifelineValidityMessage("A", lifeLineUsedAsk);
            if (lifeLineUsedAsk > 1) return;
            JOptionPane.showMessageDialog(this, "This feature is still under construction. Please try again later.");
        }
    }

    /**
     * Replicates terminal lifeline5050 logic (including its original equality checks).
     * Shows a modal with only A/B choices:
     *  A -> correctOptionValue
     *  B -> trickOption
     */
    private void lifeline5050() {
        Question q = currentQuestion;

        // Build two-choice dialog (A / B) mirroring terminal printout
        String msg = q.getQuestion() + "\n\n" +
                "A. " + q.getCorrectOptionValue() + "\n" +
                "B. " + q.getTrickOption() + "\n\n" +
                "Please enter your answer (A or B):";
        String answer = askForLetter("A", "B", msg);
        if (answer == null) return; // cancelled

        // Mirror terminal check:
        String optionValue = GameTerminal.checkOption(q, answer);
        boolean isCorrect = answer.equalsIgnoreCase(q.getCorrectOptionValue())
                || optionValue.equalsIgnoreCase(q.getCorrectOptionValue());

        int safeIndex = Math.max(0, Math.min(currentIndex, questionList.size() - 1));

        if (isCorrect) {
            user.setAmtEarned(q.cashPrize(safeIndex));
            user.setCrtAnswers(user.getCrtAnswers() + 1);
            correctAnswers = user.getCrtAnswers();
            JOptionPane.showMessageDialog(this,
                    "Correct!\nYou have earned: #" + user.getAmtEarned());
            headerLabel.setText("Earned: #" + user.getAmtEarned() + " | Correct: " + user.getCrtAnswers());
            infoLabel.setText("You have used the 50/50 lifeline. You can no longer use this lifeline again.");
            nextBtn.setEnabled(true);
            setAnswerButtonsEnabled(false);

            // If that correct makes them finish all questions, handle win
            if (user.getCrtAnswers() == questionList.size()) {
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You have answered all questions correctly!\n" +
                                "You have earned a total of: #" + user.getAmtEarned());
                endGameAndReturnToMenu();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Wrong! The correct answer was: " + q.getCorrectOptionValue());
            infoLabel.setText("You have used the 50/50 lifeline. You can no longer use this lifeline again.");
            // ensure safety net fallback like in handleStandardAnswer
            if (user.getCrtAnswers() >= 10) {
                user.setAmtEarned(questionList.get(Math.min(9, questionList.size()-1)).cashPrize(9));
            } else if (user.getCrtAnswers() >= 5) {
                user.setAmtEarned(questionList.get(Math.min(4, questionList.size()-1)).cashPrize(4));
            } else {
                user.setAmtEarned(0);
            }
            endGameAndReturnToMenu();
        }
    }


    /**
     * Replicates terminal swapQuestion logic with (index + 2).
     * Shows that swapped question in a modal-like flow; updates same variables.
     */
    private void swapQuestionGUI() {
        int swappedIndex = currentIndex + 2;
        if (swappedIndex >= questionList.size()) {
            // If out-of-bounds, mimic a “no-op swap” with a notice.
            JOptionPane.showMessageDialog(this,
                    "No available question to swap to (index + 2 out of range).");
            return;
        }

        Question swappedQ = questionList.get(swappedIndex);

        // Present swapped Q and collect letter A-D
        String msg = "Swap Question:\n\n" +
                "Question: " + swappedQ.getQuestion() + "\n" +
                "A: " + swappedQ.getOptionA() + "\n" +
                "B: " + swappedQ.getOptionB() + "\n" +
                "C: " + swappedQ.getOptionC() + "\n" +
                "D: " + swappedQ.getOptionD() + "\n\n" +
                "Please enter your answer (A, B, C, or D):";
        String letter = askForLetter("A", "B", "C", "D", msg);
        if (letter == null) return;

        String optionValue = GameTerminal.checkOption(swappedQ, letter);
        boolean correct = letter.equalsIgnoreCase(swappedQ.getCorrectOptionValue())
                || optionValue.equalsIgnoreCase(swappedQ.getCorrectOptionValue());

        if (correct) {
            user.setCrtAnswers(user.getCrtAnswers() + 1);
            correctAnswers = user.getCrtAnswers();
            user.setAmtEarned(swappedQ.cashPrize(swappedIndex));
            JOptionPane.showMessageDialog(this,
                    "Correct!\nYou have earned: #" + user.getAmtEarned());
            headerLabel.setText("Earned: #" + user.getAmtEarned() + " | Correct: " + user.getCrtAnswers());
            nextBtn.setEnabled(true);
            setAnswerButtonsEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Wrong! The correct answer was: " + swappedQ.getCorrectOptionValue());
            endGameAndReturnToMenu();
            return;
        }

        JOptionPane.showMessageDialog(this,
                "You have used the Swap question lifeline.\nYou can no longer use this lifeline again.");
        // Terminal doesn’t skip ahead after swap; we keep currentIndex for normal flow and allow Next.
    }

    /** ----------- Helpers ----------- */

    private String askForLetter(String a, String b, String message) {
        Object[] opts = {a, b, "Cancel"};
        int res = JOptionPane.showOptionDialog(this, message, "Answer",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opts, opts[0]);
        if (res == 0) return a;
        if (res == 1) return b;
        return null;
    }

    private String askForLetter(String a, String b, String c, String d, String message) {
        Object[] opts = {a, b, c, d, "Cancel"};
        int res = JOptionPane.showOptionDialog(this, message, "Answer",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opts, opts[0]);
        if (res == 0) return a;
        if (res == 1) return b;
        if (res == 2) return c;
        if (res == 3) return d;
        return null;
    }

    private void setSafetyNet(User user, int index, List<Question> questionsList) {
        if (index == 4) { // 5th question (safety net #50,000)
            user.setAmtEarned(questionsList.get(4).cashPrize(4));
            JOptionPane.showMessageDialog(this,
                    "🎉 Safety Net Reached!\nYou are now guaranteed: #" + user.getAmtEarned());
        } else if (index == 9) { // 10th question (safety net #1,000,000)
            user.setAmtEarned(questionsList.get(9).cashPrize(9));
            JOptionPane.showMessageDialog(this,
                    "🎉 Big Safety Net Reached!\nYou are now guaranteed: #" + user.getAmtEarned());
        }
    }

    private void endGameAndReturnToMenu() {
        // Mirror terminal endGameMessage + file writes
        userFile.createAndWriteToUserFile(user);
        userRanks.createWriteToMainFile(user);
        GameTerminal.endGameMessage(user, userFile);

        JOptionPane.showMessageDialog(this,
                "Game Over!\n" +
                        "Thank you for playing, " + user.getName() + "!\n" +
                        "Correct Answers: " + user.getCrtAnswers() + "\n" +
                        "Amount Earned: #" + user.getAmtEarned(),
                "Game Over", JOptionPane.INFORMATION_MESSAGE);

        cardLayout.show(mainPanel, "menu");
    }

    /** ----------- Entry Point ----------- */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI().setVisible(true));
    }
}
