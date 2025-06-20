import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class WorldLandmarksExplorerGUI extends JFrame {
    private JLabel questionLabel;
    private JLabel scoreLabel;
    private JLabel progressLabel;
    private JButton[] optionButtons = new JButton[4];
    private JPanel questionPanel;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<String> currentOptions;

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SECONDARY_COLOR = new Color(46, 204, 113);
    private final Color ACCENT_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);

    public WorldLandmarksExplorerGUI() {
        setTitle("🌍 World Landmarks Explorer 🌍");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create start screen
        createStartScreen();
        
        // Setup question panel
        setupQuestionPanel();
    }

    private void createStartScreen() {
        JPanel startPanel = new JPanel();
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setBackground(BACKGROUND_COLOR);
        startPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Title
        JLabel titleLabel = new JLabel("🌍 World Landmarks Explorer 🌍");
        try {
            Font emojiFont = new Font("Segoe UI Emoji", Font.BOLD, 28);
            titleLabel.setFont(emojiFont);
        } catch (Exception e) {
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        }
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Test your knowledge of world landmarks!");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Start button
        JButton startButton = createStyledButton("Start Quiz", SECONDARY_COLOR);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startQuiz());

        startPanel.add(Box.createVerticalGlue());
        startPanel.add(titleLabel);
        startPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        startPanel.add(subtitleLabel);
        startPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        startPanel.add(startButton);
        startPanel.add(Box.createVerticalGlue());

        add(startPanel, BorderLayout.CENTER);
    }

    private void setupQuestionPanel() {
        questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(BACKGROUND_COLOR);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Header panel with score and progress
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(SECONDARY_COLOR);
        
        progressLabel = new JLabel("Question 1 of 15");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        progressLabel.setForeground(PRIMARY_COLOR);
        
        headerPanel.add(scoreLabel, BorderLayout.WEST);
        headerPanel.add(progressLabel, BorderLayout.EAST);

        // Question label
        questionLabel = new JLabel("Question will go here", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionLabel.setForeground(TEXT_COLOR);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));

        // Options panel
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        optionsPanel.setBackground(BACKGROUND_COLOR);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createStyledButton("Option " + (i + 1), PRIMARY_COLOR);
            int finalI = i;
            optionButtons[i].addActionListener(e -> checkAnswer(finalI));
            optionsPanel.add(optionButtons[i]);
        }

        questionPanel.add(headerPanel, BorderLayout.NORTH);
        questionPanel.add(questionLabel, BorderLayout.CENTER);
        questionPanel.add(optionsPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Store original color as a property
        button.putClientProperty("originalColor", bgColor);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Only apply hover effect if button hasn't been answered (feedback colors)
                Color currentBg = button.getBackground();
                if (!currentBg.equals(SECONDARY_COLOR) && !currentBg.equals(DANGER_COLOR)) {
                    button.setBackground(bgColor.darker());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Only reset to original if button hasn't been answered (feedback colors)
                Color currentBg = button.getBackground();
                if (!currentBg.equals(SECONDARY_COLOR) && !currentBg.equals(DANGER_COLOR)) {
                    button.setBackground(bgColor);
                }
            }
        });
        
        return button;
    }

    private void startQuiz() {
        questions = loadQuestions();
        Collections.shuffle(questions);
        score = 0;
        currentQuestionIndex = 0;
        getContentPane().removeAll();
        add(questionPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        showNextQuestion();
    }

    private void showNextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            showFinalScore();
            return;
        }

        Question q = questions.get(currentQuestionIndex);
        questionLabel.setText("<html><div style='text-align: center;'>" + q.text + "</div></html>");
        
        // Update score and progress
        scoreLabel.setText("Score: " + score);
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());

        currentOptions = new ArrayList<>(q.wrongAnswers);
        currentOptions.add(q.correctAnswer);
        Collections.shuffle(currentOptions);

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText("<html><div style='text-align: center;'>" + currentOptions.get(i) + "</div></html>");
            optionButtons[i].setBackground(PRIMARY_COLOR);
        }
    }

    private void checkAnswer(int selectedIndex) {
        String selected = currentOptions.get(selectedIndex);
        Question q = questions.get(currentQuestionIndex);

        // Visual feedback
        for (int i = 0; i < 4; i++) {
            if (currentOptions.get(i).equals(q.correctAnswer)) {
                optionButtons[i].setBackground(SECONDARY_COLOR);
            } else if (i == selectedIndex) {
                optionButtons[i].setBackground(DANGER_COLOR);
            }
        }

        // Wait a moment to show the feedback
        Timer timer = new Timer(1500, e -> {
            if (selected.equals(q.correctAnswer)) {
                score++;
                currentQuestionIndex++;
                showNextQuestion();
            } else {
                showCustomDialog("Incorrect!", 
                    "The correct answer was: " + q.correctAnswer, 
                    DANGER_COLOR);
                showFinalScore();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showFinalScore() {
        String message = "Final Score: " + score + " out of " + questions.size();
        String title = score >= questions.size() / 2 ? "Great Job! 🎉" : "Good Try! 👍";
        
        // Create custom dialog
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Score message
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(TEXT_COLOR);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Play again question
        JLabel questionLabel = new JLabel("Would you like to play again?");
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        questionLabel.setForeground(TEXT_COLOR);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(messageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(questionLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton yesButton = createStyledButton("Yes", SECONDARY_COLOR);
        yesButton.addActionListener(e -> {
            dialog.dispose();
            getContentPane().removeAll();
            createStartScreen();
            revalidate();
            repaint();
        });

        JButton noButton = createStyledButton("No", DANGER_COLOR);
        noButton.addActionListener(e -> {
            dialog.dispose();
            System.exit(0);
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showCustomDialog(String title, String message, Color color) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JLabel label = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton okButton = createStyledButton("OK", color);
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(okButton);

        dialog.add(label, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        dialog.setVisible(true);
    }

    private List<Question> loadQuestions() {
        return new ArrayList<>(List.of(
            new Question("In which country is the Eiffel Tower located?", "France", List.of("Germany", "Italy", "Spain")),
            new Question("In which country is the Statue of Liberty located?", "USA", List.of("Canada", "Mexico", "Brazil")),
            new Question("In which country is the Taj Mahal located?", "India", List.of("Nepal", "Pakistan", "China")),
            new Question("In which country is Machu Picchu located?", "Peru", List.of("Colombia", "Chile", "Argentina")),
            new Question("In which country is the Great Wall located?", "China", List.of("Japan", "South Korea", "Vietnam")),
            new Question("What is the capital of Australia?", "Canberra", List.of("Sydney", "Melbourne", "Perth")),
            new Question("Which river runs through Egypt?", "Nile", List.of("Amazon", "Yangtze", "Mississippi")),
            new Question("Mount Everest is located in which mountain range?", "Himalayas", List.of("Andes", "Alps", "Rockies")),
            new Question("Which continent is the Sahara Desert located in?", "Africa", List.of("Asia", "Australia", "South America")),
            new Question("Which country has the most islands?", "Sweden", List.of("Indonesia", "Canada", "Philippines")),
            new Question("Which ocean is the largest?", "Pacific", List.of("Atlantic", "Indian", "Arctic")),
            new Question("Which country is both in Europe and Asia?", "Turkey", List.of("Spain", "Greece", "Germany")),
            new Question("What is the capital city of Canada?", "Ottawa", List.of("Toronto", "Vancouver", "Montreal")),
            new Question("What is the smallest country in the world?", "Vatican City", List.of("Monaco", "Malta", "San Marino")),
            new Question("Which country is famous for the city of Petra?", "Jordan", List.of("Morocco", "Egypt", "Lebanon"))
        ));
    }

    static class Question {
        String text;
        String correctAnswer;
        List<String> wrongAnswers;

        Question(String text, String correctAnswer, List<String> wrongAnswers) {
            this.text = text;
            this.correctAnswer = correctAnswer;
            this.wrongAnswers = wrongAnswers;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WorldLandmarksExplorerGUI().setVisible(true);
        });
    }
}