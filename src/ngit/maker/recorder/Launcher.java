package ngit.maker.recorder;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**Using a separated class run the program, it's a good habit.*/
public class Launcher {

    /**The MAIN method.*/
    public static void main(String[] args) {
        /*We need invokeLater to correctly run Swing utils.*/
        SwingUtilities.invokeLater(()->{
            Logger logger = Logger.getLogger(Launcher.class.getName());
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
            new WorkflowManager("Save_" + sdf2.format(new Date()), logger);
        });
    }
}
