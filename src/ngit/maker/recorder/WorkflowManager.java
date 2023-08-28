package ngit.maker.recorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowManager extends JFrame {

    boolean allMinimize;
    boolean allUndecorated;
    private final List<WorkflowRecorderPane> recorders = new ArrayList<>();
    public WorkflowManager(String startTime){
        defaultInit();

        GlobalKeyListener listener = new GlobalKeyListener();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            private boolean isAllHide;
            private boolean isHideHead;
            @Override
            public void run() {
                if (listener.shiftPressed && listener.ctrlPressed){
                    if (false){

                    } //else if (global2.f8Pressed){
                        for (WorkflowRecorderPane r : recorders){
                            r.dispose();
                            r.setUndecorated(isHideHead);
                            r.setVisible(true);
                       // }
                        isHideHead = !isHideHead;
                    }
                }
            }
        }, 0, 10);

        JPanel panel = new JPanel(new FlowLayout());
        panel.setSize(getSize());
        panel.setLocation(0, 0);
        addButton(panel);

        add(panel);
        setVisible(true);
    }
    public void defaultInit(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                super.windowClosing(e);
            }
        });
    }


    public void addButton(JPanel panel){
        JButton button = new JButton("新建");
        button.setSize(70, 30);
        button.setLocation((panel.getWidth()-button.getWidth())/2, (panel.getHeight()-button.getHeight())/2);
        button.addActionListener(e -> {
            WorkflowRecorderPane recorder = new WorkflowRecorderPane(recorders);
            recorders.add(recorder);
            for (WorkflowRecorderPane r : recorders){
                System.out.println(r.getCreatedTime());
            }
            System.out.println("\n");
        });

        JButton button1 = getjButton();

        panel.add(button);
        panel.add(button1);
    }

    private JButton getjButton() {
        JButton button1 = new JButton("测试1");
        button1.setSize(70, 30);
        button1.addActionListener(e -> {
            for (WorkflowRecorderPane r : recorders){
                if (r.isUndecorated() != allUndecorated){
                    if (allUndecorated){
                        r.setSize(r.getWidth(),r.getHeight()-r.getInsets().top);
                        r.setLocation(r.getX(), r.getY()+r.getInsets().top);
                    }
                    r.dispose();
                    r.setUndecorated(allUndecorated);
                    r.setVisible(true);
                    if (!allUndecorated){
                        r.setSize(r.getWidth(), r.getHeight()+r.getInsets().top);
                        r.setLocation(r.getX(), r.getY()-r.getInsets().top);
                        r.repaint();
                    }
                }
            }
            allUndecorated = !allUndecorated;
        });
        return button1;
    }
}

class WorkflowRecorderPane extends JFrame {
    private String createdTime;
    private JTextArea area;
    public WorkflowRecorderPane(List<WorkflowRecorderPane> recorders){
        defaultInit(recorders);
        timeInit();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(this.getSize());
        panel.setLocation(0,0);
        addTextArea(panel);

        add(panel, BorderLayout.CENTER);

        setTitle(createdTime);
        setVisible(true);
    }
    public WorkflowRecorderPane(List<WorkflowRecorderPane> recorders, String createdTime){
        this.createdTime = createdTime;
        defaultInit(recorders);
        timeInit();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(this.getSize());
        panel.setLocation(0,0);
        addTextArea(panel);

        add(panel, BorderLayout.CENTER);

        setTitle(createdTime);
        setVisible(true);
    }
    public void defaultInit(List<WorkflowRecorderPane> recorders){
        setSize(200, 100);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int count = 0;
                if (!recorders.isEmpty()) {
                    for (WorkflowRecorderPane r : recorders) {
                        if (Objects.equals(r.createdTime, createdTime)) {
                            recorders.remove(count);
                            break;
                        }
                        count++;
                    }

                    super.windowClosing(e);
                }
            }
        });
    }

    public void timeInit(){
        SimpleDateFormat sdf2 = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss:SSS]");
        createdTime = sdf2.format(new Date());
    }



    public void addTextArea(JPanel panel){
        area = new JTextArea();
        area.setSize(panel.getSize());
        area.setLocation(0, 0);
        panel.add(area, BorderLayout.CENTER);
    }

    @Override
    public String toString() {
        return area.getText();
    }

    public String  getCreatedTime() {
        return createdTime;
    }
}

class WorkflowSaver{
    public static final String MAKER_APPLICATION_PORT = System.getenv("LOCALAPPDATA")+"\\MakerApps";
    public static final String THIS_SETTINGS_TAG = "WorkflowManager";

    private final String STORE_PLACE_KEY = "WFM-Default store place:";
    private final String STORE_PLACE_DEFAULT = MAKER_APPLICATION_PORT + "\\WorkflowMgr\\saves";
    private final String STORE_PASSWORD_KEY = "WFM-You know it:";
    private final String STORE_PASSWORD_DEFAULT = "1145141919810";

    public final Logger logger;
    public final Properties properties;

    public enum PropertiesExistKnockBack{
        TRULY_EXIST,
        CANT_READ,
        CANT_WRITE,
        NOTHING_IN,
        NOT_CORRECT,
        NOT_EXIST;
    }

    interface ReadSupplier{
        String getObj();
        boolean isValid();
        static ReadSupplier returnValidOne(final String string){
            return new ReadSupplier() {
                @Override
                public String getObj() {return string;}

                @Override
                public boolean isValid() {return true;}
            };
        }
        static ReadSupplier returnInvalidOne(){
            return new ReadSupplier() {
                @Override
                public String getObj() {return null;}
                @Override
                public boolean isValid() {return false;}
            };
        }
    }

    public WorkflowSaver(Logger logger){
        this.logger = logger;
        this.properties = new Properties();
    }

    public boolean createPropertiesFile() throws IOException {
        File file = new File(MAKER_APPLICATION_PORT + "\\settings.properties");
        return createDirAndFile(file);
    }

    public boolean createDirAndFile(File file) throws IOException {
        if (!file.exists()) return file.createNewFile();
        else return false;
    }

    private PropertiesExistKnockBack readPropertiesCheck(Logger logger){
        try {
            File file = new File(MAKER_APPLICATION_PORT + "\\settings.properties");
            if (!file.exists()) throw new NullPointerException("I have to throw it, to reach the solution.");
            if (!file.canRead()) throw new IOException("I have to throw it, to reach the solution.");

            try (InputStream inputStream = new FileInputStream(file)){
                properties.load(inputStream);
                return properties.propertyNames() == null ?
                        PropertiesExistKnockBack.NOTHING_IN : PropertiesExistKnockBack.TRULY_EXIST;
            }

        } catch (NullPointerException e){
            return PropertiesExistKnockBack.NOT_EXIST;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception at reading user profile: ", e);
            return PropertiesExistKnockBack.CANT_READ;
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, "User profile is not correct: ", e);
            return PropertiesExistKnockBack.NOT_CORRECT;
        }
    }


    public ReadSupplier readProperty(final String key, final String defaultKey){
        PropertiesExistKnockBack knockBack = readPropertiesCheck(logger);

        if (knockBack.equals(PropertiesExistKnockBack.NOT_CORRECT)){
            if(refreshProperties()) {
                logger.log(Level.INFO, "Successfully refresh properties file.");
            } else {
                logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                return ReadSupplier.returnInvalidOne();
            }
        } else if (knockBack.equals(PropertiesExistKnockBack.NOT_EXIST)){
            try {
                if (createPropertiesFile()) {
                    logger.log(Level.INFO, "Successfully create a settings.properties file.");
                } else throw new RuntimeException("I cant create properties file! Is that you? (TAT)");
                if(refreshProperties()) {
                    logger.log(Level.INFO, "Successfully refresh properties file.");
                } else {
                    logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                    return ReadSupplier.returnInvalidOne();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Can't create new user profile: ", e);
                return ReadSupplier.returnInvalidOne();
            }
        } else if (knockBack.equals(PropertiesExistKnockBack.CANT_READ) || knockBack.equals(PropertiesExistKnockBack.CANT_WRITE)){
            return ReadSupplier.returnInvalidOne();
        } else if (knockBack.equals(PropertiesExistKnockBack.NOTHING_IN)){
            if(refreshProperties()) {
                logger.log(Level.INFO, "Successfully refresh properties file.");
            } else {
                logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                return ReadSupplier.returnInvalidOne();
            }
        }
        return ReadSupplier.returnValidOne(properties.getProperty(key, defaultKey));
    }

    private boolean commitChange(){
        try {
            File file = new File(MAKER_APPLICATION_PORT + "\\settings.properties");
            if (!file.exists()) throw new NullPointerException("(???) Where's my properties?!");
            if (!file.canWrite()) throw new IOException("I can't write in things! Let me in!!! (TAT)");

            try (OutputStream outputStream = new FileOutputStream(file)){
                properties.store(outputStream, THIS_SETTINGS_TAG);
                return true;
            }

        } catch (NullPointerException e){
            try {
                if (createPropertiesFile()) {
                    logger.log(Level.INFO, "Successfully create a settings.properties file.");
                } else throw new RuntimeException("I cant create properties file! Is that you? (TAT)");
                if(refreshProperties()) {
                    logger.log(Level.INFO, "Successfully refresh properties file.");
                } else {
                    logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                    return false;
                }
                return true;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Can't create new user profile: ", ex);
            }
            return false;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception at writing user profile: ", e);
            return false;
        }
    }


    public boolean setStorePlace(final String newPlace){
        properties.setProperty(STORE_PLACE_KEY, newPlace);
        return commitChange();
    }
    public ReadSupplier readStorePlace(){
        return readProperty(STORE_PLACE_KEY, STORE_PLACE_DEFAULT);
    }
    public boolean setStorePlaintextPassword(final String newPassword){
        properties.setProperty(STORE_PASSWORD_KEY, newPassword);
        return commitChange();
    }
    public ReadSupplier readStorePlaintextPassword(){
        return readProperty(STORE_PASSWORD_KEY, STORE_PASSWORD_DEFAULT);
    }

    public boolean refreshProperties(){
        properties.setProperty(STORE_PLACE_KEY, STORE_PLACE_DEFAULT);
        properties.setProperty(STORE_PASSWORD_KEY, STORE_PASSWORD_DEFAULT);
        return commitChange();
    }
    public void saveTasks(List<WorkflowRecorderPane> recorders, String startTime) {
        File path = new File(readStorePlace().getObj() + File.separator + startTime);
        File iteratorFile;

        for (WorkflowRecorderPane recorder : recorders) {

            iteratorFile = new File(path.getAbsolutePath() + File.separator + recorder.getCreatedTime() + ".rot");
            try {
                if (!iteratorFile.exists()) {
                    if (!iteratorFile.createNewFile())
                        throw new IOException("I can't create file record for your new task, sorry(TwT)");
                }

                try (FileWriter fileWriter = new FileWriter(iteratorFile)) {
                    fileWriter.write("");
                    fileWriter.write(recorder.toString());
                }

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Can't save work record \"" + recorder.getCreatedTime() + "\" task:", e);
            }
        }
    }
    public List<WorkflowRecorderPane> readTasks(List<WorkflowRecorderPane> recorders, String startTime) {
        File path = new File(readStorePlace().getObj() + File.separator + startTime);

        for (File recorderFile : Objects.requireNonNull(path.listFiles())) {
            WorkflowRecorderPane pane = new WorkflowRecorderPane(recorders, recorderFile.getName());
            try {
                if (!recorderFile.canRead()) throw new IOException("I can't read file record from your saves, sorry(TwT)");
                Files.readAllBytes(recorderFile.toPath());
                recorders.add(pane);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Can't read work record \"" + recorderFile.getName() + "\" task:", e);
            } catch (NullPointerException e){
                logger.log(Level.SEVERE, "Can't find file as collecting:", e);
            }
        }
        return recorders;
    }
}