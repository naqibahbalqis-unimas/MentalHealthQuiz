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

    private ScaledImagePanel imagePanel;
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
        System.out.println("Looking for images in: " + dir.getAbsolutePath());
        
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null || files.length == 0) {
            System.out.println("No PNG files found in: " + dir.getAbsolutePath());
            pages = new String[0];
        } else {
            Arrays.sort(files, Comparator.comparing(File::getName));
            pages = Arrays.stream(files).map(File::getPath).toArray(String[]::new);
            System.out.println("Found " + pages.length + " images:");
            for (String page : pages) {
                System.out.println("  - " + page);
            }
        }
        totalPages = pages.length;
        currentPage = 0;
    }

    private void createComponents() {
        mainPane = new JPanel(new BorderLayout(10, 10));
        mainPane.setPreferredSize(new Dimension(MOBILE_WIDTH, MOBILE_HEIGHT));
        mainPane.setBackground(new Color(245, 245, 220));
        mainPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Learning Module", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 20));
        title.setForeground(new Color(60, 60, 60));
        mainPane.add(title, BorderLayout.NORTH);

        // Use ScaledImagePanel instead of JLabel for proper image scaling
        imagePanel = new ScaledImagePanel(null, true);
        imagePanel.setPreferredSize(new Dimension(MOBILE_WIDTH - 40, 450));
        imagePanel.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPane.add(imagePanel, BorderLayout.CENTER);

        // Progress bar with text
        progressBar = new JProgressBar(0, Math.max(totalPages - 1, 1));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.PLAIN, 12));

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        startQuizButton = new JButton("Start Quiz");
        startQuizButton.setVisible(false);
        
        // Style buttons
        prevButton.setFont(new Font("Arial", Font.BOLD, 12));
        nextButton.setFont(new Font("Arial", Font.BOLD, 12));
        startQuizButton.setFont(new Font("Arial", Font.BOLD, 14));
        startQuizButton.setBackground(new Color(76, 175, 80));
        startQuizButton.setForeground(Color.WHITE);

        prevButton.addActionListener(e -> showPreviousPage());
        nextButton.addActionListener(e -> showNextPage());
        startQuizButton.addActionListener(e -> {
            if (onFinish != null) {
                onFinish.run();
            }
        });

        JPanel nav = new JPanel(new FlowLayout());
        nav.add(prevButton);
        nav.add(nextButton);
        nav.add(startQuizButton);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.add(progressBar, BorderLayout.NORTH);
        bottom.add(nav, BorderLayout.CENTER);
        mainPane.add(bottom, BorderLayout.SOUTH);

        displayPage();
    }

    private void displayPage() {
        if (pages.length == 0) {
            // Show message when no images found
            imagePanel.setMessage("No images found in assets/information/\nPlease add PNG files to continue.");
            progressBar.setString("No images found");
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            startQuizButton.setVisible(true);
            return;
        }

        try {
            // Load image using simple path - this should work for your 1080x1920 images
            ImageIcon icon = new ImageIcon(pages[currentPage]);
            
            if (icon.getIconWidth() == -1) {
                // Image failed to load
                System.out.println("Failed to load image: " + pages[currentPage]);
                imagePanel.setMessage("Image not found:\n" + new File(pages[currentPage]).getName());
            } else {
                // Image loaded successfully - ScaledImagePanel will handle scaling
                System.out.println("Loaded image: " + pages[currentPage] + 
                    " (Size: " + icon.getIconWidth() + "x" + icon.getIconHeight() + ")");
                imagePanel.setImageIcon(icon);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imagePanel.setMessage("Error loading image:\n" + e.getMessage());
        }

        updateProgress();
        prevButton.setEnabled(currentPage > 0);
        
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
            progressBar.setString("No images");
        } else {
            int value = currentPage;
            progressBar.setValue(value);
            progressBar.setString("Page " + (currentPage + 1) + " of " + totalPages);
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

    // Enhanced image panel that properly scales images and handles errors
    public static class ScaledImagePanel extends JPanel {
        private ImageIcon imageIcon;
        private boolean maintainAspectRatio;
        private String message;

        public ScaledImagePanel(ImageIcon imageIcon, boolean maintainAspectRatio) {
            this.imageIcon = imageIcon;
            this.maintainAspectRatio = maintainAspectRatio;
            setBackground(Color.WHITE);
        }

        public void setImageIcon(ImageIcon imageIcon) {
            this.imageIcon = imageIcon;
            this.message = null;
            repaint();
        }

        public void setMessage(String message) {
            this.message = message;
            this.imageIcon = null;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (message != null) {
                // Display error message
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                FontMetrics fm = g2d.getFontMetrics();
                
                String[] lines = message.split("\n");
                int totalHeight = lines.length * fm.getHeight();
                int startY = (getHeight() - totalHeight) / 2 + fm.getAscent();
                
                for (int i = 0; i < lines.length; i++) {
                    int textWidth = fm.stringWidth(lines[i]);
                    int x = (getWidth() - textWidth) / 2;
                    int y = startY + i * fm.getHeight();
                    g2d.drawString(lines[i], x, y);
                }
            } else if (imageIcon != null) {
                // Display scaled image
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imageWidth = imageIcon.getIconWidth();
                int imageHeight = imageIcon.getIconHeight();

                if (maintainAspectRatio && imageWidth > 0 && imageHeight > 0) {
                    // Calculate scale to fit image within panel while maintaining aspect ratio
                    double scaleX = (double) panelWidth / imageWidth;
                    double scaleY = (double) panelHeight / imageHeight;
                    double scale = Math.min(scaleX, scaleY);

                    int scaledWidth = (int) (imageWidth * scale);
                    int scaledHeight = (int) (imageHeight * scale);

                    // Center the image
                    int x = (panelWidth - scaledWidth) / 2;
                    int y = (panelHeight - scaledHeight) / 2;

                    g2d.drawImage(imageIcon.getImage(), x, y, scaledWidth, scaledHeight, this);
                } else if (imageWidth > 0 && imageHeight > 0) {
                    // Stretch to fill panel
                    g2d.drawImage(imageIcon.getImage(), 0, 0, panelWidth, panelHeight, this);
                }
            }
            g2d.dispose();
        }
    }
}