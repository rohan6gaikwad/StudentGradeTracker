package src;

/**
 * Student Grade Tracker Application
 * Entry point - launches the GUI application.
 *
 * @author Senior Java Developer
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // Launch on Event Dispatch Thread for Swing safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GradeTrackerGUI().setVisible(true);
        });
    }
}
