package versions;

import questionHandling.Question;
import userstats.User;
import userstats.UserFile;
import userstats.RandomTokenGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class GameGUI extends JFrame {

    // Core state (reuses your classes)
    private final User user = new User();
    private final UserFile userFile = new UserFile();
    private List<Question> questions;

    // Game progress
    private int currentIndex = 0;      // index into questions list
    private boolean used5050 = false;  // lifeline locks to one use (like your console version intent)
    private boolean usedSwap = false;

    // UI
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    // Game panel controls
    private JLabel titleLabel;
    private JLabel earningsLabel;
    private JTextArea questionArea;
    private JRadioButton optA, optB, optC, optD;
    private ButtonGroup optionsGroup;
    private JButton submitBtn, lifelineBtn, exitBtn;

    // Lifeline dialog buttons
    private JButton fiftyBtn, swapBtn, askBtn;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gui = new GameGUI();
            gui.setVisible(true);
        });
    }

    public GameGUI() {
        super("Who Wants to Be a Millionaire");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);

        buildMenuCard();
        buildGameCard();

        cards.show(root, "menu");
    }

    /* =========================
       UI: Menu Screen
       ========================= */
    private void buildMenuCard() {
        JPanel menu = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0;

        JLabel welcome = new JLabel("Who Wants to Be a Millionaire");
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 22f));
        gbc.gridy = 0;
        menu.add(welcome, gbc);

        JButton play = new JButton("Play");
        play.addActionListener(e -> startGameFlow());
        gbc.gridy = 1;
        menu.add(play, gbc);

        JButton quit = new JButton("Exit");
        quit.addActionListener(e -> System.exit(0));
        gbc.gridy = 2;
        menu.add(quit, gbc);

        root.add(menu, "menu");
    }

    /* =========================
       UI: Game Screen
       ========================= */
    private void buildGameCard() {
        JPanel game = new JPanel(new BorderLayout(10, 10));
        game.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel top = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Question", SwingConstants.LEFT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        top.add(titleLabel, BorderLayout.WEST);

        earningsLabel = new JLabel("Earnings: #0", SwingConstants.RIGHT);
        top.add(earningsLabel, BorderLayout.EAST);

        game.add(top, BorderLayout.NORTH);

        // Question text
        questionArea = new JTextArea(3, 20);
        questionArea.setWrapStyleWord(true);
        questionArea.setLineWrap(true);
        questionArea.setEditable(false);
        questionArea.setFont(questionArea.getFont().deriveFont(16f));
        game.add(new JScrollPane(questionArea), BorderLayout.CENTER);

        // Options + controls
        JPanel bottom = new JPanel(new BorderLayout(10, 10));

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optA = new JRadioButton();
        optB = new JRadioButton();
        optC = new JRadioButton();
        optD = new JRadioButton();
        optionsGroup = new ButtonGroup();
        optionsGroup.add(optA);
        optionsGroup.add(optB);
        optionsGroup.add(optC);
        optionsGroup.add(optD);
        optionsPanel.add(optA);
        optionsPanel.add(optB);
        optionsPanel.add(optC);
        optionsPanel.add(optD);

        bottom.add(optionsPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lifelineBtn = new JButton("Lifelines");
        lifelineBtn.addActionListener(e -> openLifelinesDialog());

        submitBtn = new JButton("Submit Answer");
        submitBtn.addActionListener(e -> onSubmitAnswer());

        exitBtn = new JButton("End Game");
        exitBtn.addActionListener(e -> endGameAndReturnToMenu(false));

        actions.add(lifelineBtn);
        actions.add(submitBtn);
        actions.add(exitBtn);

        bottom.add(actions, BorderLayout.SOUTH);

        game.add(bottom, BorderLayout.SOUTH);

        root.add(game, "game");
    }

    /* =========================
       Flow: Start Game
       ========================= */
    private void startGameFlow() {
        // Get player name (GUI version sets name here; avoids console Scanner in User.initializeUserName())
        String name = null;
        while (name == null || name.isBlank()) {
            name = JOptionPane.showInputDialog(this, "What is your name?");
            if (name == null) return; // cancel -> back to menu
        }
        user.setName(name.trim());

        // Load questions using your existing loader in Question
        Question qLoader = new Question();
        questions = qLoader.getQuestionDet("trivia_quiz.csv");
        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Could not load questions file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Collections.shuffle(questions);

        // Reset state
        currentIndex = 0;
        used5050 = false;
        usedSwap = false;
        user.setCrtAnswers(0);
        user.setAmtEarned(0);

        // Show rules (reuses content of terminal rules)
        showRules();

        // Start game
        loadCurrentQuestion();
        cards.show(root, "game");
    }

    private void showRules() {
        String msg =
                "Welcome " + user.getName() + " to Who Wants to Be a Millionaire!\n" +
                        "--------------------------------------------------------\n" +
                        "1. You will be asked a series of questions.\n" +
                        "2. Each question has four options; choose the correct one.\n" +
                        "3. Click 'Lifelines' for 50/50 or Swap Question.\n" +
                        "4. The game ends when you miss a question or finish the set.\n" +
                        "5. Good luck!";
        JOptionPane.showMessageDialog(this, msg, "Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    /* =========================
       Display: Current Question
       ========================= */
    private void loadCurrentQuestion() {
        if (user.getCrtAnswers() == 15) {
            // Finished all questions
            JOptionPane.showMessageDialog(this,
                    "Congratulations! You have answered all questions!\nTotal: #" + (long)user.getAmtEarned(),
                    "Completed", JOptionPane.INFORMATION_MESSAGE);
            endGameAndReturnToMenu(true);
            return;
        }

        Question q = questions.get(currentIndex);
        titleLabel.setText("Question " + (currentIndex + 1));
        questionArea.setText(q.getQuestion());

        // Restore options (ensure all visible/enabled in case 50/50 used previously)
        optA.setVisible(true); optA.setEnabled(true);
        optB.setVisible(true); optB.setEnabled(true);
        optC.setVisible(true); optC.setEnabled(true);
        optD.setVisible(true); optD.setEnabled(true);

        optA.setText("A: " + q.getOptionA());
        optB.setText("B: " + q.getOptionB());
        optC.setText("C: " + q.getOptionC());
        optD.setText("D: " + q.getOptionD());
        optionsGroup.clearSelection();

        updateEarningsLabel();
    }

    private void updateEarningsLabel() {
        earningsLabel.setText("Earnings: #" + (long)user.getAmtEarned());
    }

    /* =========================
       Answer Handling
       ========================= */
    private void onSubmitAnswer() {
        Question q = questions.get(currentIndex);

        String selectedValue = null;
        if (optA.isSelected()) selectedValue = q.getOptionA();
        if (optB.isSelected()) selectedValue = q.getOptionB();
        if (optC.isSelected()) selectedValue = q.getOptionC();
        if (optD.isSelected()) selectedValue = q.getOptionD();

        if (selectedValue == null) {
            JOptionPane.showMessageDialog(this, "Please select an option.");
            return;
        }

        if (selectedValue.equals(q.getCorrectOption())) {
            // Correct!
            int newCorrect = user.getCrtAnswers() + 1;
            user.setCrtAnswers(newCorrect);
            user.setAmtEarned(q.cashPrize(currentIndex));
            JOptionPane.showMessageDialog(this,
                    "Correct!\nYou have earned: #" + q.cashPrize(currentIndex),
                    "Correct", JOptionPane.INFORMATION_MESSAGE);

            // Safety net behavior (mirrors your terminal logic)
            applySafetyNetIfReached(currentIndex);

            currentIndex++;
            loadCurrentQuestion();
        } else {
            // Wrong -> game over
            JOptionPane.showMessageDialog(this,
                    "Wrong! The correct answer was: " + q.getCorrectOption(),
                    "Incorrect", JOptionPane.ERROR_MESSAGE);
            endGameAndReturnToMenu(false);
        }
    }

    /* =========================
       Lifelines
       ========================= */
    private void openLifelinesDialog() {
        JDialog dlg = new JDialog(this, "Lifelines", true);
        dlg.setLayout(new GridLayout(3, 1, 8, 8));
        dlg.setSize(300, 180);
        dlg.setLocationRelativeTo(this);

        fiftyBtn = new JButton("50/50");
        swapBtn = new JButton("Swap Question");
        askBtn = new JButton("Ask the Audience");

        fiftyBtn.addActionListener(e -> {
            if (used5050) {
                JOptionPane.showMessageDialog(dlg, "You have already used the 50/50 lifeline.");
            } else {
                apply5050();
                used5050 = true;
                JOptionPane.showMessageDialog(dlg, "50/50 lifeline activated!");
            }
        });

        swapBtn.addActionListener(e -> {
            if (usedSwap) {
                JOptionPane.showMessageDialog(dlg, "You have already used the Swap question lifeline.");
            } else {
                applySwap();
                usedSwap = true;
                JOptionPane.showMessageDialog(dlg, "Swap question lifeline activated!");
            }
        });

        askBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(dlg, "Ask the Audience is under construction.")
        );

        dlg.add(fiftyBtn);
        dlg.add(swapBtn);
        dlg.add(askBtn);
        dlg.setVisible(true);
    }

    // 50/50: show only CorrectOption + TrickOption (matches your console intent)
    private void apply5050() {
        Question q = questions.get(currentIndex);

        // Map which buttons match correct / trick
        // Correct
        boolean aCorrect = q.getOptionA().equals(q.getCorrectOption());
        boolean bCorrect = q.getOptionB().equals(q.getCorrectOption());
        boolean cCorrect = q.getOptionC().equals(q.getCorrectOption());
        boolean dCorrect = q.getOptionD().equals(q.getCorrectOption());

        // Trick
        boolean aTrick = q.getOptionA().equals(q.getTrickOption());
        boolean bTrick = q.getOptionB().equals(q.getTrickOption());
        boolean cTrick = q.getOptionC().equals(q.getTrickOption());
        boolean dTrick = q.getOptionD().equals(q.getTrickOption());

        // Hide everything that is not correct or trick
        optA.setVisible(aCorrect || aTrick);
        optB.setVisible(bCorrect || bTrick);
        optC.setVisible(cCorrect || cTrick);
        optD.setVisible(dCorrect || dTrick);

        // Also disable hidden ones to be safe
        optA.setEnabled(optA.isVisible());
        optB.setEnabled(optB.isVisible());
        optC.setEnabled(optC.isVisible());
        optD.setEnabled(optD.isVisible());

        optionsGroup.clearSelection();
    }

    // Swap: replace the current question with another (console used index+2)
    private void applySwap() {
        // Try to swap with index+2 like your terminal version; if not available, fallback to index+1
        int target = (currentIndex + 2 < questions.size()) ? currentIndex + 2
                : (currentIndex + 1 < questions.size() ? currentIndex + 1 : -1);
        if (target == -1) {
            JOptionPane.showMessageDialog(this, "No more questions to swap with!");
            return;
        }

        // Present the swapped question immediately
        Question swapped = questions.get(target);
        titleLabel.setText("Swapped Question (" + (target + 1) + ")");
        questionArea.setText(swapped.getQuestion());

        optA.setVisible(true); optA.setEnabled(true);
        optB.setVisible(true); optB.setEnabled(true);
        optC.setVisible(true); optC.setEnabled(true);
        optD.setVisible(true); optD.setEnabled(true);

        optA.setText("A: " + swapped.getOptionA());
        optB.setText("B: " + swapped.getOptionB());
        optC.setText("C: " + swapped.getOptionC());
        optD.setText("D: " + swapped.getOptionD());
        optionsGroup.clearSelection();

        // Temporarily repurpose Submit to answer the swapped question
        submitBtn.removeActionListener(submitBtn.getActionListeners()[0]);
        submitBtn.addActionListener(e -> onSubmitSwappedAnswer(target));
    }

    private void onSubmitSwappedAnswer(int targetIndex) {
        Question q = questions.get(targetIndex);

        String selectedValue = null;
        if (optA.isSelected()) selectedValue = q.getOptionA();
        if (optB.isSelected()) selectedValue = q.getOptionB();
        if (optC.isSelected()) selectedValue = q.getOptionC();
        if (optD.isSelected()) selectedValue = q.getOptionD();

        if (selectedValue == null) {
            JOptionPane.showMessageDialog(this, "Please select an option.");
            return;
        }

        if (selectedValue.equals(q.getCorrectOption())) {
            user.setCrtAnswers(user.getCrtAnswers() + 1);
            user.setAmtEarned(q.cashPrize(targetIndex));
            JOptionPane.showMessageDialog(this,
                    "Correct!\nYou have earned: #" + q.cashPrize(targetIndex),
                    "Correct", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Wrong! The correct answer was: " + q.getCorrectOption(),
                    "Incorrect", JOptionPane.ERROR_MESSAGE);
            endGameAndReturnToMenu(false);
            return;
        }

        // After swapped, continue after the swapped question
        currentIndex = targetIndex + 1;
        // Restore Submit behavior to normal flow
        submitBtn.removeActionListener(submitBtn.getActionListeners()[0]);
        submitBtn.addActionListener(e -> onSubmitAnswer());

        loadCurrentQuestion();
    }

    /* =========================
       Safety Net (mirrors your method)
       ========================= */
    private void applySafetyNetIfReached(int indexJustAnswered) {
        // This mirrors your console logic exactly:
        // if >=10 correct → set amount to cashPrize(4) and print "first safety net"
        // else if >=5 correct → set amount to cashPrize(9) and print "second safety net"
        // (Yes, the labels in your console code are inverted vs amounts; we keep behavior identical.)
        if (user.getCrtAnswers() == 5 || user.getCrtAnswers() == 10) {
            if (user.getCrtAnswers() == 10) {
                user.setAmtEarned(questions.get(indexJustAnswered).cashPrize(9));
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You have reached the second safety net.\n" +
                                "Safety Net Amount: #" + questions.get(indexJustAnswered).cashPrize(9));
            } else if (user.getCrtAnswers() == 5) {
                user.setAmtEarned(questions.get(indexJustAnswered).cashPrize(4));
                JOptionPane.showMessageDialog(this,
                        "Congratulations! You have reached the first net.\n" +
                                "Safety Net Amount: #" + questions.get(indexJustAnswered).cashPrize(4));
            }
        }
        updateEarningsLabel();
    }

    /* =========================
       End Game & Save
       ========================= */
    private void endGameAndReturnToMenu(boolean finishedAll) {
        // Generate token like your displayUserStats() does
        user.setUserToken(RandomTokenGenerator.generator(10));

        // Show end summary (GUI)
        String summary =
                "Game Over!\n" +
                        "Player: " + user.getName() + "\n" +
                        "Correct Answers: " + user.getCrtAnswers() + "\n" +
                        "Total Earnings: #" + (long)user.getAmtEarned() + "\n" +
                        "Checkout Token: " + user.getUserToken();
        JOptionPane.showMessageDialog(this, summary, "Session Summary", JOptionPane.INFORMATION_MESSAGE);

        // Persist to file using your class
        userFile.createAndWriteToUserFile(user);

        // Back to menu
        cards.show(root, "menu");
    }

}
