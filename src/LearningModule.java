import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Simple JavaFX based learning module displaying a sequence of images.
 * Users can navigate with previous/next buttons and start the quiz when
 * reaching the last page.
 */
public class LearningModule {
    private String[] pages;
    private int currentPage;
    private int totalPages;

    private ImageView imageView;
    private Button nextButton;
    private Button prevButton;
    private Button startQuizButton;
    private ProgressBar progressBar;
    private Runnable onFinish;
    private BorderPane mainPane;

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
        mainPane = new BorderPane();
        mainPane.setPrefSize(MOBILE_WIDTH, MOBILE_HEIGHT);
        mainPane.setPadding(new Insets(10));
        mainPane.setStyle("-fx-background-color: beige;");

        Label title = new Label("Learning Module");
        title.setFont(javafx.scene.text.Font.font("Times New Roman", 20));
        BorderPane.setAlignment(title, Pos.CENTER);
        mainPane.setTop(title);

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(450);
        imageView.setFitWidth(MOBILE_WIDTH - 40);
        mainPane.setCenter(imageView);

        progressBar = new ProgressBar();
        progressBar.setPrefWidth(MOBILE_WIDTH - 40);

        prevButton = new Button("<");
        nextButton = new Button(">");
        startQuizButton = new Button("Start Quiz");
        startQuizButton.setVisible(false);

        prevButton.setOnAction(e -> showPreviousPage());
        nextButton.setOnAction(e -> showNextPage());
        startQuizButton.setOnAction(e -> {
            if (onFinish != null) {
                onFinish.run();
            }
        });

        HBox nav = new HBox(10, prevButton, nextButton, startQuizButton);
        nav.setAlignment(Pos.CENTER);

        VBox bottom = new VBox(5, progressBar, nav);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        mainPane.setBottom(bottom);

        displayPage();
    }

    private void displayPage() {
        if (pages.length == 0) {
            if (onFinish != null) onFinish.run();
            return;
        }

        Image img = new Image(new File(pages[currentPage]).toURI().toString());
        imageView.setImage(img);
        updateProgress();

        prevButton.setDisable(currentPage == 0);
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
            progressBar.setProgress(0);
        } else {
            progressBar.setProgress((double) currentPage / (totalPages - 1));
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

    public BorderPane getPane() {
        return mainPane;
    }
}
