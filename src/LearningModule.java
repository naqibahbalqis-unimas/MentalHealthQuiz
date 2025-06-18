import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class LearningModule {
    private String[] pages;
    private int currentPage;
    private int totalPages;
    private ScaledImagePanel imagePanel;
    private BasicArrowButton nextButton;
    private BasicArrowButton prevButton;
    private JButton startQuizButton;
    private JProgressBar progressBar;
    private Runnable onFinish;
    private JPanel mainPanel;

    // Mobile dimensions
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
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Learning Module", JLabel.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Image panel with proper scaling - this is where your 1080x1920 images will display perfectly
        imagePanel = new ScaledImagePanel(null, true);
        imagePanel.setPreferredSize(new Dimension(MOBILE_WIDTH - 40, 450));
        imagePanel.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(imagePanel, BorderLayout.CENTER);

        // Progress bar
        progressBar = new JProgressBar(0, Math.max(totalPages - 1, 1));
        progressBar.setValue(currentPage);
        progressBar.setStringPainted(true);
        updateProgressText();

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout());
        prevButton = new BasicArrowButton(BasicArrowButton.WEST);
        nextButton = new BasicArrowButton(BasicArrowButton.EAST);
        startQuizButton = new JButton("Start Quiz");
        startQuizButton.setVisible(false);
        startQuizButton.setFont(new Font("Times New Roman", Font.BOLD, 14));

        prevButton.addActionListener(e -> showPreviousPage());
        nextButton.addActionListener(e -> showNextPage());
        startQuizButton.addActionListener(e -> {
            if (onFinish != null) {
                onFinish.run();
            }
        });

        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(startQuizButton);

        // Control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(progressBar, BorderLayout.NORTH);
        controlPanel.add(navPanel, BorderLayout.CENTER);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Display first page
        displayPage();
    }

    private void displayPage() {
        if (pages.length == 0) {
            // No images found, go directly to quiz
            if (onFinish != null) {
                onFinish.run();
            }
            return;
        }

        // Load and display the image with perfect scaling
        ImageIcon originalIcon = new ImageIcon(pages[currentPage]);
        imagePanel.setImageIcon(originalIcon);

        updateProgressText();
        prevButton.setEnabled(currentPage > 0);
        
        if (currentPage == totalPages - 1) {
            nextButton.setVisible(false);
            startQuizButton.setVisible(true);
        } else {
            nextButton.setVisible(true);
            startQuizButton.setVisible(false);
        }
    }

    private void updateProgressText() {
        progressBar.setValue(currentPage);
        progressBar.setString("Page " + (currentPage + 1) + " of " + totalPages);
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

    // Reset to first page
    public void reset() {
        currentPage = 0;
        displayPage();
    }

    // Get the main panel to add to parent container
    public JPanel getPanel() {
        return mainPanel;
    }

    // Get current page info
    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getProgressPercentage() {
        return totalPages == 0 ? 0 : currentPage * 100 / (totalPages - 1);
    }

    // Enhanced image panel that maintains aspect ratio and fixes compression
    public static class ScaledImagePanel extends JPanel {
        private ImageIcon imageIcon;
        private boolean maintainAspectRatio;

        public ScaledImagePanel(ImageIcon imageIcon, boolean maintainAspectRatio) {
            this.imageIcon = imageIcon;
            this.maintainAspectRatio = maintainAspectRatio;
            setBackground(Color.WHITE);
        }

        public void setImageIcon(ImageIcon imageIcon) {
            this.imageIcon = imageIcon;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imageIcon != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                
                // High-quality rendering hints to prevent compression artifacts
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imageWidth = imageIcon.getIconWidth();
                int imageHeight = imageIcon.getIconHeight();

                if (maintainAspectRatio && imageWidth > 0 && imageHeight > 0) {
                    // Calculate scale to fit image within panel while maintaining aspect ratio
                    double scaleX = (double) panelWidth / imageWidth;
                    double scaleY = (double) panelHeight / imageHeight;
                    double scale = Math.min(scaleX, scaleY); // Use smaller scale to maintain aspect ratio

                    int scaledWidth = (int) (imageWidth * scale);
                    int scaledHeight = (int) (imageHeight * scale);

                    // Center the image in the panel
                    int x = (panelWidth - scaledWidth) / 2;
                    int y = (panelHeight - scaledHeight) / 2;

                    // Draw with high quality scaling
                    g2d.drawImage(imageIcon.getImage(), x, y, scaledWidth, scaledHeight, this);
                } else if (imageWidth > 0 && imageHeight > 0) {
                    // Stretch to fill panel (not recommended for your 1080x1920 images)
                    g2d.drawImage(imageIcon.getImage(), 0, 0, panelWidth, panelHeight, this);
                }
                g2d.dispose();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return super.getPreferredSize();
        }
    }

    // Utility method for external use - scale any ImageIcon properly
    public static ImageIcon scaleImageIcon(ImageIcon originalIcon, int maxWidth, int maxHeight) {
        if (originalIcon == null) return null;

        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // Calculate scale maintaining aspect ratio
        double scaleX = (double) maxWidth / originalWidth;
        double scaleY = (double) maxHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        // Create scaled image with high quality
        Image scaledImage = originalIcon.getImage().getScaledInstance(
            scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }
}