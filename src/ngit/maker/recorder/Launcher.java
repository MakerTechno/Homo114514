package ngit.maker.recorder;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            SimpleDateFormat sdf2 = new SimpleDateFormat("Save_yyyy-MM-dd-HH:mm:ss-SSS");
            new WorkflowManager(sdf2.format(new Date()));
        });
    }
}
