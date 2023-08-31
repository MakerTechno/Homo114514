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

/**Main frame, as an easy manager.
 * include info for all text pane: {@link WorkflowRecorderPane}*/
public class WorkflowManager extends JFrame {
    /*Belows are the statics.*/
    /**A final timer, hold the {@link WorkflowSaver} task and so on.*/
    public static final Timer TIMER = new Timer();

    /*Belows are the dynamics.*/
    /**We need a logger to log for whole sys.*/
    public final Logger logger;
    /**We need a saver system to save user's records, too.*/
    private final WorkflowSaver saver;

    private String saveTime;

    /*-These headed the basic state of all text pane.*/
    public boolean allMinimize;
    public boolean allUndecorated;

    /**This is a recorder list, contains all the text pane that still lives.*/
    private final List<WorkflowRecorderPane> recorders = new ArrayList<>();


    /**Construction method.*/
    public WorkflowManager(String startTime, Logger logger){
        /*Init some constants here.*/
        this.logger = logger;
        saver = new WorkflowSaver(logger);
        saveTime = startTime;

        /*Using method setups here.*/
        basicSetup(this);

        /*Add element to main frame.*/
        add(utilSetup(this));
        setVisible(true);
    }

    /**Setup some basic properties.*/
    public void basicSetup(JFrame frame){
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);//Then frame will be right at the middle of teh screen.

        /*Listening on window close to save all works.*/
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!recorders.isEmpty()){
                    saver.saveTasks(recorders, saveTime);
                    logger.finest("Your work have been saved successfully.");
                }
                super.windowClosing(e);
            }
        });

        /*This timer runs every second, we protect users work.*/
        TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                saver.saveTasks(recorders, saveTime);
                logger.finest("Your work have been saved successfully.");
            }
        }, 0, 1000*60);
    }


    public JPanel utilSetup(JFrame frame){
        /*A JPanel holding all the buttons here*/
        JPanel panel = new JPanel(new FlowLayout());
        panel.setLocation(0, 0);
        panel.setSize(frame.getSize());

        /*Add elements.*/
        panel.add(createPaneBtnSetup());
        panel.add(undecoratedBtnSetup());
        panel.add(allMinimizeBtnSetup());
        panel.add(allCloseBtnSetup());

        return panel;
    }

    private JButton createPaneBtnSetup(){
        /*1st button.*/
        JButton createTextPane = new JButton("新建");
        createTextPane.setSize(70, 30);

        /*Happens on button click.*/
        createTextPane.addActionListener(e -> {
            /*Generate a new pane and add a new frame to list.*/
            recorders.add(new WorkflowRecorderPane(recorders));

            StringBuilder builder = new StringBuilder();
            int count = 0;

            for (WorkflowRecorderPane r : recorders){
                builder.append("[").append(count).append("]").append(r.getCreatedTime()).append("; ");
            }
            /*Log out all the pane exist at this time.*/
            logger.log(Level.CONFIG,builder.toString());
        });

        return createTextPane;
    }

    private JButton undecoratedBtnSetup() {
        /*2nd button.*/
        JButton undecoratedChange = new JButton("全部取消标题栏");
        undecoratedChange.setSize(120, 30);

        /*Happens on button click.*/
        undecoratedChange.addActionListener(e -> {
            //First change the undecorated boolean(switch)
            allUndecorated = !allUndecorated;

            /*Run check for every pane and change unexpected frames.*/
            for (WorkflowRecorderPane r : recorders){
                if (r.isUndecorated() != allUndecorated){
                    /*We need to change some location problem before title change start, if it's undecorated.*/
                    if (allUndecorated){
                        r.setSize(r.getWidth(),r.getHeight()-r.getInsets().top);
                        r.setLocation(r.getX(), r.getY()+r.getInsets().top);
                    }

                    /*Must release the frame, then undecorated settings will be accepted.*/
                    r.dispose();
                    r.setUndecorated(allUndecorated);
                    //Then repaint.
                    r.setVisible(true);

                    /*We also need some changes after, if it's not undecorated.*/
                    if (!allUndecorated){
                        r.setSize(r.getWidth(), r.getHeight()+r.getInsets().top);
                        r.setLocation(r.getX(), r.getY()-r.getInsets().top);

                        r.repaint();//It's for some unforeseeable problems about frame displays.
                    }
                }
            }

            /*After all, we still need to change the text on the button, too.*/
            if (allUndecorated) undecoratedChange.setText("全部显示标题栏");
            else undecoratedChange.setText("全部取消标题栏");
        });

        return undecoratedChange;
    }

    private JButton allMinimizeBtnSetup(){
        JButton minimizeAll = new JButton("全部最小化");
        minimizeAll.setSize(100, 30);

        minimizeAll.addActionListener(e -> {
            //First change the minimize boolean(switch)
            allMinimize = !allMinimize;

            //Then translate this boolean to variable for chooser.
            int transState = allMinimize ? Frame.ICONIFIED : Frame.NORMAL;

            /*Run check for every pane and change unexpected frames.*/
            for (WorkflowRecorderPane pane : recorders){
                if (pane.getState() != transState) pane.setState(transState);
            }

            /*After all, we still need to change the text on the button, too.*/
            if (allMinimize) minimizeAll.setText("全部还原");
            else minimizeAll.setText("全部最小化");
        });

        return minimizeAll;
    }

    private JButton allCloseBtnSetup(){
        JButton closeAll = new JButton("全部关闭");
        closeAll.setSize(70, 30);

        closeAll.addActionListener(e -> {
            if (recorders != null) {
                saver.saveTasks(recorders, saveTime);
                for (WorkflowRecorderPane pane : recorders) {
                    pane.dispose();
                }
                recorders.clear();
            }
        });

        return closeAll;
    }
}

class WorkflowRecorderPane extends JFrame {
    private String createdTime;
    private JTextArea area;
    private int myPlace;
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
                            flushInterface(recorders);
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
        SimpleDateFormat sdf2 = new SimpleDateFormat("[yyyy-MM-dd_HH-mm-ss-SSS]");
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

    public void flushInterface(List<WorkflowRecorderPane> recorders){
        int count = 0;
        if (!recorders.isEmpty()) {
            for (WorkflowRecorderPane r : recorders) {
                if (Objects.equals(r.createdTime, createdTime)) {
                    myPlace = ++count;
                    break;
                }
                count++;
            }
        }
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g ;
        g2.setColor(new Color(92, 92, 92, 132));
        g2.setFont(new Font("Inconsolata", Font.BOLD, 20));
        g2.drawString(String.valueOf(myPlace), 10, 30);
    }
}

class WorkflowSaver{
    public static final String MAKER_APPLICATION_PORT = System.getenv("LOCALAPPDATA")+"\\MakerApps";
    public static final String THIS_SETTINGS_TAG = "WorkflowManager";

    private final String STORE_PLACE_KEY = "WFM-Default store place:";
    private final String STORE_PLACE_DEFAULT = MAKER_APPLICATION_PORT + "\\WorkflowMgr\\saves";
    private final String STORE_PASSWORD_KEY = "WFM-You know it:";
    private final String STORE_PASSWORD_DEFAULT = "1145141919810";
    private final String STORE_MINIMIZE_KEYBOARD_KEY = "Minimize hotkey: ";
    private final String STORE_MINIMIZE_KEYBOARD_DEFAULT = "Ctrl_Shift_F7";
    private final String STORE_EXIT_KEYBOARD_KEY = "Exit hotkey: ";
    private final String STORE_EXIT_KEYBOARD_DEFAULT = "Ctrl_Shift_F8";
    private final String STORE_UNDECORATED_KEYBOARD_KEY = "Undecorated hotkey: ";
    private final String STORE_UNDECORATED_KEYBOARD_DEFAULT = "Ctrl_Shift_F9";
    private final String STORE_SAVE_KEYBOARD_KEY = "Save hotkey: ";
    private final String STORE_SAVE_KEYBOARD_DEFAULT = "Ctrl_S";

    private final Logger logger;
    private final Properties properties;

    private enum PropertiesExistKnockBack{
        TRULY_EXIST,
        CANT_READ,
        CANT_WRITE,
        NOTHING_IN,
        NOT_CORRECT,
        NOT_EXIST
    }

    public interface ReadSupplier{
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
        static ReadSupplier returnInvalidOne(final String string){
            return new ReadSupplier() {
                @Override
                public String getObj() {return string;}
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
        if (!file.exists()) {
            if (file.isDirectory() && !file.exists()) return file.mkdirs();
            else if (!file.getParentFile().exists()) return file.getParentFile().mkdirs() && file.createNewFile();
            else return file.createNewFile();
        }
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
            logger.log(Level.INFO, "Properties file not correct, try to refresh.");
            if(refreshProperties()) {
                logger.log(Level.INFO, "Successfully refresh properties file.");
            } else {
                logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                return ReadSupplier.returnInvalidOne(defaultKey);
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
                    return ReadSupplier.returnInvalidOne(defaultKey);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Can't create new user profile: ", e);
                return ReadSupplier.returnInvalidOne(defaultKey);
            }
        } else if (knockBack.equals(PropertiesExistKnockBack.CANT_READ) || knockBack.equals(PropertiesExistKnockBack.CANT_WRITE)){
            return ReadSupplier.returnInvalidOne(defaultKey);
        } else if (knockBack.equals(PropertiesExistKnockBack.NOTHING_IN)){
            if(refreshProperties()) {
                logger.log(Level.INFO, "Successfully refresh properties file.");
            } else {
                logger.log(Level.SEVERE, "Couldn't refresh your properties.");
                return ReadSupplier.returnInvalidOne(defaultKey);
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
    public boolean setMinimizeHotkey(final String newKeyArray){
        properties.setProperty(STORE_MINIMIZE_KEYBOARD_KEY, newKeyArray);
        return commitChange();
    }
    public ReadSupplier readMinimizeHotkey(){
        return readProperty(STORE_MINIMIZE_KEYBOARD_KEY, STORE_MINIMIZE_KEYBOARD_DEFAULT);
    }
    public boolean setExitHotkey(final String newKeyArray){
        properties.setProperty(STORE_EXIT_KEYBOARD_KEY, newKeyArray);
        return commitChange();
    }
    public ReadSupplier readExitHotkey(){
        return readProperty(STORE_EXIT_KEYBOARD_KEY, STORE_EXIT_KEYBOARD_DEFAULT);
    }
    public boolean setUndecoratedHotkey(final String newKeyArray){
        properties.setProperty(STORE_UNDECORATED_KEYBOARD_KEY, newKeyArray);
        return commitChange();
    }
    public ReadSupplier readUndecoratedHotkey(){
        return readProperty(STORE_UNDECORATED_KEYBOARD_KEY, STORE_UNDECORATED_KEYBOARD_DEFAULT);
    }
    public boolean setSaveHotkey(final String newKeyArray){
        properties.setProperty(STORE_SAVE_KEYBOARD_KEY, newKeyArray);
        return commitChange();
    }
    public ReadSupplier readSaveHotkey(){
        return readProperty(STORE_SAVE_KEYBOARD_KEY, STORE_SAVE_KEYBOARD_DEFAULT);
    }

    public boolean refreshProperties(){
        properties.setProperty(STORE_PLACE_KEY, STORE_PLACE_DEFAULT);
        properties.setProperty(STORE_PASSWORD_KEY, STORE_PASSWORD_DEFAULT);
        properties.setProperty(STORE_MINIMIZE_KEYBOARD_KEY, STORE_MINIMIZE_KEYBOARD_DEFAULT);
        properties.setProperty(STORE_EXIT_KEYBOARD_KEY, STORE_EXIT_KEYBOARD_DEFAULT);
        properties.setProperty(STORE_UNDECORATED_KEYBOARD_KEY, STORE_UNDECORATED_KEYBOARD_DEFAULT);
        properties.setProperty(STORE_SAVE_KEYBOARD_KEY, STORE_SAVE_KEYBOARD_DEFAULT);
        return commitChange();
    }
    public void saveTasks(List<WorkflowRecorderPane> recorders, String startTime) {
        File path = new File(readStorePlace().getObj() + File.separator + startTime);
        File iteratorFile;

        for (WorkflowRecorderPane recorder : recorders) {

            iteratorFile = new File(path.getAbsolutePath() + File.separator + recorder.getCreatedTime() + ".rot");
            try {
                if (!iteratorFile.exists()) {
                    if (!createDirAndFile(iteratorFile))
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