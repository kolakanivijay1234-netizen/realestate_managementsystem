import ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for the Real Estate Agent Mandate Management System.
 * Sets the system Look and Feel and initializes the MainFrame on the Event Dispatch Thread (EDT).
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system Look and Feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error initializing MainFrame: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
