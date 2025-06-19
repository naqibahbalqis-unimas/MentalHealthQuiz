import javax.swing.*;
import java.awt.*;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Swing based learning module displaying a sequence of images.
 * Users can navigate with previous/next buttons and start the quiz when
 * reaching the last page.
 */
public class LearningModule {
    private String[] pages;
    private int currentPage;
    private int totalPages;

    private JLabel imageLabel;
    private JButton nextButton;
    private JButton prevButton;
    private JButton startQuizButton;
    private JProgressBar progressBar;
    private Runnable onFinish;
    private JPanel mainPane;

    private static final int MOBILE_WIDTH = 394;
    private static final int MOBILE_HEIGHT = 700;

    public LearningModule(Runnable onFinish) {
        this.onFinish = onFinish;
        loadPages();
        createComponents();
    }

    private void loadPages() {
        File dir = new File("assets" + File.separator + "information");
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null) {
            pages = new String[0];
        } else {
            Arrays.sort(files, Comparator.comparing(File::getName));
            pages = Arrays.stream(files).map(File::getPath).toArray(String[]::new);
        }
        totalPages = pages.length;
        currentPage = 0;
    }

    private void createComponents() {
        mainPane = new JPanel(new BorderLayout());
        mainPane.setPreferredSize(new Dimension(MOBILE_WIDTH, MOBILE_HEIGHT));
        mainPane.setBackground(new Color(245, 245, 220));
        mainPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Learning Module", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 20));
        mainPane.add(title, BorderLayout.NORTH);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(MOBILE_WIDTH - 40, 450));
        mainPane.add(imageLabel, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, 100);

        prevButton = new JButton("<");
        nextButton = new JButton(">");
        startQuizButton = new JButton("Start Quiz");
        startQuizButton.setVisible(false);

        prevButton.addActionListener(e -> showPreviousPage());
        nextButton.addActionListener(e -> showNextPage());
        startQuizButton.addActionListener(e -> {
            if (onFinish != null) {
                onFinish.run();
            }
        });

        JPanel nav = new JPanel();
        nav.add(prevButton);
        nav.add(nextButton);
        nav.add(startQuizButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(progressBar, BorderLayout.CENTER);
        bottom.add(nav, BorderLayout.SOUTH);
        mainPane.add(bottom, BorderLayout.SOUTH);

        displayPage();
    }

    private void displayPage() {
        if (pages.length == 0) {
            if (onFinish != null) onFinish.run();
            return;
        }

        ImageIcon icon = new ImageIcon(new File(pages[currentPage]).toURI().toString());
        imageLabel.setIcon(icon);
        updateProgress();
        prevButton.setEnabled(currentPage != 0);
        if (currentPage == totalPages - 1) {
            nextButton.setVisible(false);
            startQuizButton.setVisible(true);
        } else {
            nextButton.setVisible(true);
            startQuizButton.setVisible(false);
        }
    }

    private void updateProgress() {
        if (totalPages <= 1) {
            progressBar.setValue(0);
        } else {
            int value = (int) (100.0 * currentPage / (totalPages - 1));
            progressBar.setValue(value);
        }
    }

    private void showNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayPage();
        }
    }

    private void showPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayPage();
        }
    }

    public void reset() {
        currentPage = 0;
        displayPage();
    }

    public JPanel getPane() {
        return mainPane;
    }
}
