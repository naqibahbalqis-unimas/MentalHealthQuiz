import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class LearningModule implements ContentProvider {
    private String[] pages;
    private int currentPage;
    private int totalPages;

    private JFrame frame;
    private JLabel imageLabel;
    private BasicArrowButton nextButton;
    private BasicArrowButton prevButton;
    private JButton startQuizButton;
    private JProgressBar progressBar;

    private Runnable onFinish;

    public LearningModule(Runnable onFinish) {
        this.onFinish = onFinish;
        loadPages();
        createGUI();
        displayPage();
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

    private void createGUI() {
        frame = new JFrame("Learning Module");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(540, 600);
        frame.setLayout(new BorderLayout());

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(imageLabel, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, Math.max(totalPages - 1, 0));
        progressBar.setValue(currentPage);

        prevButton = new BasicArrowButton(BasicArrowButton.WEST);
        nextButton = new BasicArrowButton(BasicArrowButton.EAST);
        startQuizButton = new JButton("Start Quiz");
        startQuizButton.setVisible(false);

        JPanel controlPanel = new JPanel();
        controlPanel.add(prevButton);
        controlPanel.add(nextButton);
        controlPanel.add(startQuizButton);
        controlPanel.add(progressBar);

        frame.add(controlPanel, BorderLayout.SOUTH);

        prevButton.addActionListener(e -> showPreviousPage());
        nextButton.addActionListener(e -> showNextPage());
        startQuizButton.addActionListener(e -> {
            frame.dispose();
            if (onFinish != null) {
                onFinish.run();
            }
        });

        frame.setVisible(true);
    }

    private void displayPage() {
        if (pages.length == 0) {
            return;
        }
        ImageIcon icon = new ImageIcon(pages[currentPage]);
        Image img = icon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
        progressBar.setValue(currentPage);
        prevButton.setEnabled(currentPage > 0);
        if (currentPage == totalPages - 1) {
            nextButton.setVisible(false);
            startQuizButton.setVisible(true);
        } else {
            nextButton.setVisible(true);
            startQuizButton.setVisible(false);
        }
    }

    private void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayPage();
        }
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayPage();
        }
    }

    @Override
    public String getContent() {
        return pages.length > 0 ? pages[currentPage] : "";
    }

    @Override
    public void showNextPage() {
        nextPage();
    }

    @Override
    public void showPreviousPage() {
        previousPage();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getProgressBar() {
        return totalPages == 0 ? 0 : currentPage * 100 / (totalPages - 1);
    }
}

