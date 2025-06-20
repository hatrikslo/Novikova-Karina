import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class WorldLandmarksExplorerGUI extends JFrame {
    private JLabel questionLabel;
    private JButton[] optionButtons = new JButton[4];
    private JPanel questionPanel;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private List<String> currentOptions;

    public WorldLandmarksExplorerGUI() {
        setTitle("🌍 World Landmarks Explorer 🌍");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Начальный экран
        JPanel startPanel = new JPanel();
        JButton startButton = new JButton("Start Quiz");
        startButton.addActionListener(e -> startQuiz());
        startPanel.add(startButton);
        add(startPanel, BorderLayout.CENTER);

        // Панель с вопросами (создаётся позже)
        setupQuestionPanel();
    }

    private void setupQuestionPanel() {
        questionPanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Question will go here", SwingConstants.CENTER);
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        questionPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton("Option " + (i + 1));
            int finalI = i;
            optionButtons[i].addActionListener(e -> checkAnswer(finalI));
            optionsPanel.add(optionButtons[i]);
        }

        questionPanel.add(optionsPanel, BorderLayout.CENTER);
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
        questionLabel.setText("Question " + (currentQuestionIndex + 1) + ": " + q.text);

        currentOptions = new ArrayList<>(q.wrongAnswers);
        currentOptions.add(q.correctAnswer);
        Collections.shuffle(currentOptions);

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(currentOptions.get(i));
        }
    }

    private void checkAnswer(int selectedIndex) {
        String selected = currentOptions.get(selectedIndex);
        Question q = questions.get(currentQuestionIndex);

        if (selected.equals(q.correctAnswer)) {
            score++;
            currentQuestionIndex++;
            showNextQuestion();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Wrong! The correct answer was: " + q.correctAnswer,
                    "Incorrect",
                    JOptionPane.ERROR_MESSAGE);
            showFinalScore();
        }
    }

    private void showFinalScore() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Final score: " + score + "\nDo you want to play again?",
                "Quiz Over",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            startQuiz();
        } else {
            System.exit(0);
        }
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