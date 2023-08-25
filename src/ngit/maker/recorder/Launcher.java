package ngit.maker.recorder;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManager::new);
    }
}
