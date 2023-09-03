package ngit.maker.recorder.workflow;

import ngit.maker.recorder.keyboards.GlobalKeyListener;
import ngit.maker.recorder.resource.IGoodsCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**Main frame, as an easy manager.<br>
 * include info for all text pane: {@link WorkflowRecorderPane}<br>
 * include saver for all properties and save methods: {@link WorkflowSaver}*/
public class WorkflowManager extends JFrame {
    /*Belows are the statics.*/
    /**A final timer, hold the {@link WorkflowSaver} task and so on.*/
    private static final Timer TIMER = new Timer();

    /*Belows are the dynamics.*/
    /**We need a logger to log for whole sys.*/
    public final Logger logger;
    /**We need a listener to give action from user.*/
    public GlobalKeyListener listener;
    /**We need a saver system to save user's records and properties settings, too.*/
    private final WorkflowSaver saver;
    /**This is a recorder list, contains all the text pane that still lives.*/
    private final List<WorkflowRecorderPane> recorders = new ArrayList<>();

    /**This means the current time the frame start, sometimes it could be the last read place's create-time*/
    private String saveTime;

    /*-These headed the basic state of all text pane.*/
    public boolean allMinimize;
    public boolean allUndecorated;


    /**Construction method.*/
    public WorkflowManager(String startTime, Logger logger){
        /*Init some constants here.*/
        this.logger = logger;
        this.saver = new WorkflowSaver(logger);
        this.saveTime = startTime;

        /*Using method setups here.*/
        basicSetup(this);

        /*Add element to main frame.*/
        add(btnPanelSetup(this), BorderLayout.CENTER);
        add(stgPanelSetup(), BorderLayout.SOUTH);
        setVisible(true);
    }

    /**Setup some basic properties, register closing listener and add save task.*/
    public void basicSetup(JFrame frame){
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);//Then frame will be right at the middle of teh screen.
        frame.setIconImage(IGoodsCollection.getIcon(logger).getImage());

        /*Listening on window close to save all works.*/
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!recorders.isEmpty()){
                    try {
                        saver.saveTasks(recorders, saveTime);
                        logger.finest("Your work have been saved successfully.");
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "Could not read store place: ", ex);
                    }
                }
                super.windowClosing(e);
            }
        });

        /*This timer runs every second, we protect users work.*/
        TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    saver.saveTasks(recorders, saveTime);
                    logger.finest("Your work have been saved successfully.");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Could not read store place: ", e);
                }
            }
        }, 0, 1000*60);

        /*FUND ME!!!!!!*/
        JFrame fundFrame = new JFrame("PLEASE!!!  THANKS!!!");
        fundFrame.setLayout(new BorderLayout());
        fundFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ImageIcon image = IGoodsCollection.getFund(logger);
        JLabel fundHold = new JLabel(image);
        fundHold.setOpaque(true);
        fundHold.setBackground(Color.WHITE);
        fundFrame.setSize(image.getIconWidth()+50, image.getIconHeight()+fundFrame.getInsets().top+100);
        fundFrame.setLocationRelativeTo(null);

        fundFrame.add(fundHold, BorderLayout.CENTER);
        fundFrame.setAlwaysOnTop(true);
        fundFrame.setVisible(true);
    }


    /**Setup buttons and add to panel.*/
    public JPanel btnPanelSetup(JFrame frame){
        /*A JPanel holding all the buttons here*/
        JPanel panel = new JPanel(new FlowLayout());

        /*Add elements.*/
        panel.add(createPaneBtnSetup());
        JButton undecoratedBtn = undecoratedBtnSetup();
        panel.add(undecoratedBtn);
        JButton minimizeBtn = allMinimizeBtnSetup();
        panel.add(minimizeBtn);
        panel.add(allCloseBtnSetup());
        panel.add(allSaveBtnSetup());

        globalListenerSetup(frame, minimizeBtn, undecoratedBtn);

        return panel;
    }

    /**Setup elements and add to panel.*/
    private JPanel stgPanelSetup(){
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(textBoardSetup(), BorderLayout.CENTER);
        panel.add(settingsBtnSetup(), BorderLayout.EAST);

        return panel;
    }

    /*Belows are setups for button.*/

    /**This button can create a new pane when clicked.*/
    private JButton createPaneBtnSetup(){
        JButton createTextPane = new JButton("新建");
        createTextPane.setSize(70, 30);

        /*Happens on button click.*/
        createTextPane.addActionListener(e -> {
            /*Get a new pane and add a new frame to list.*/
            WorkflowRecorderPane pane  = new WorkflowRecorderPane(recorders);
            recorders.add(pane);
            pane.flushInterface(recorders);

            /*Build the time String for it's logger.*/
            StringBuilder builder = new StringBuilder();
            int count = 0;

            /*Layout all the panes in this recorder list.*/
            for (WorkflowRecorderPane r : recorders){
                builder.append("[").append(count).append("]").append(r.getCreatedTime()).append("; ");
            }
            logger.log(Level.CONFIG,builder.toString());
        });

        return createTextPane;
    }

    /**This button runs {@link WorkflowManager#getUndecoratedRuns(JButton)} -> {@link Runnable#run()} when clicked.*/
    private JButton undecoratedBtnSetup() {
        JButton undecoratedChange = new JButton("全部取消标题栏");
        undecoratedChange.setSize(120, 30);

        /*Happens on button click.*/
        undecoratedChange.addActionListener(e -> getUndecoratedRuns(undecoratedChange).run());

        return undecoratedChange;
    }

    /**This method can set all the panes from headed to undecorated, or switch back.*/
    private Runnable getUndecoratedRuns(JButton button){
        return () -> {
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
                    //Don't forget to bring it back.
                    r.setVisible(true);

                    /*We also need some changes after, if it's not undecorated.*/
                    if (!allUndecorated){
                        r.setSize(r.getWidth(), r.getHeight()+r.getInsets().top);
                        r.setLocation(r.getX(), r.getY()-r.getInsets().top);

                        r.repaint();
                        //It's for some unforeseeable problems about frame displays
                        //like head number don't changed and else.
                    }
                }
            }

            /*After all, we still need to change the text on the button, too.*/
            if (allUndecorated) button.setText("全部显示标题栏");
            else button.setText("全部取消标题栏");
        };
    }

    /**This button runs {@link WorkflowManager#getMinimizeRuns(JButton)} -> {@link Runnable#run()} when clicked.*/
    private JButton allMinimizeBtnSetup(){
        JButton minimizeAll = new JButton("全部最小化");
        minimizeAll.setSize(100, 30);

        /*Happens on button click.*/
        minimizeAll.addActionListener(e -> getMinimizeRuns(minimizeAll).run());

        return minimizeAll;
    }

    /**This method can set all the panes from normalize to minimize, or switch back.*/
    private Runnable getMinimizeRuns(JButton button){
        return () -> {
            //First change the minimize boolean(switch)
            allMinimize = !allMinimize;

            //Then translate this boolean to variable for chooser.
            int transState = allMinimize ? Frame.ICONIFIED : Frame.NORMAL;

            /*Run check for every pane and change unexpected frames.*/
            for (WorkflowRecorderPane pane : recorders){
                if (pane.getState() != transState) pane.setState(transState);
            }

            /*After all, we still need to change the text on the button, too.*/
            if (allMinimize) button.setText("全部还原");
            else button.setText("全部最小化");
        };
    }

    /**This button runs {@link WorkflowManager#getSaveRuns(boolean)}true -> {@link Runnable#run()} when clicked.*/
    private JButton allCloseBtnSetup(){
        JButton closeAll = new JButton("全部关闭");
        closeAll.setSize(70, 30);

        /*Happens on button click.*/
        closeAll.addActionListener(e -> getSaveRuns(true).run());

        return closeAll;
    }

    /**This button runs {@link WorkflowManager#getSaveRuns(boolean)}false -> {@link Runnable#run()} when clicked.*/
    private JButton allSaveBtnSetup(){
        JButton saveAll = new JButton("全部保存");
        saveAll.setSize(70, 30);

        /*Happens on button click.*/
        saveAll.addActionListener(e -> getSaveRuns(false).run());

        return saveAll;
    }

    /**This method can save all the panes, and when it's true, close them all.*/
    private Runnable getSaveRuns(boolean shouldClose){
        return () -> {
            if (recorders != null) {
                /*Try to save.*/
                try {
                    saver.saveTasks(recorders, saveTime);
                    logger.finest("Your work have been saved successfully.");
                    if (shouldClose){//Then close all.
                        for (WorkflowRecorderPane pane : recorders) {
                            pane.dispose();
                        }
                        recorders.clear();
                    }
                } catch (IOException ex) {
                    /*Don't close frames on error.*/
                    logger.log(Level.SEVERE, "Could not read store place: ", ex);
                }
            }
        };
    }

    private JLabel textBoardSetup(){
        JLabel label = new JLabel();
        TIMER.schedule(new TimerTask() {
            int count = 0;
            @Override
            public void run() {
                label.setText(IGoodsCollection.GOOD_WORDS.get(count));
                if (count != IGoodsCollection.GOOD_WORDS.size()-1){
                    count++;
                } else count = 0;
            }
        }, 0, 1000*7);

        return label;
    }

    private JButton settingsBtnSetup(){
        JButton settings = new JButton(IGoodsCollection.getIcon(logger));

        settings.addActionListener(e -> new SettingsPane(saver, listener, logger));
        return settings;
    }

    /**This is all for register global hotkeys.*/
    private void globalListenerSetup(JFrame frame, JButton minimizeBtn, JButton undecoratedBtn){
        /*Read from properties.*/
        WorkflowSaver.ReadSupplier minimizeHotkey = saver.readMinimizeHotkey();
        WorkflowSaver.ReadSupplier minimizeAvailable = saver.readMinimizeAvailable();

        WorkflowSaver.ReadSupplier undecoratedHotkey = saver.readUndecoratedHotkey();
        WorkflowSaver.ReadSupplier undecoratedAvailable = saver.readUndecoratedAvailable();

        WorkflowSaver.ReadSupplier exitHotkey = saver.readExitHotkey();
        WorkflowSaver.ReadSupplier exitAvailable = saver.readExitAvailable();

        WorkflowSaver.ReadSupplier saveHotkey = saver.readSaveHotkey();
        WorkflowSaver.ReadSupplier saveAvailable = saver.readSaveAvailable();

        /*If anything went wrong, tip for user and read from default.*/
        if (!(minimizeHotkey.isValid()&&undecoratedHotkey.isValid()&&exitHotkey.isValid()&&saveHotkey.isValid()) &&
            !(minimizeAvailable.isValid()&&undecoratedAvailable.isValid()&&exitAvailable.isValid()&&saveAvailable.isValid())){
            JOptionPane.showConfirmDialog(frame, "Fatal on key reading! now using default. See more at logs.");
        }


        boolean minimizeAvailableB = Boolean.parseBoolean(minimizeAvailable.getObj());
        boolean undecoratedAvailableB = Boolean.parseBoolean(undecoratedAvailable.getObj());
        boolean exitAvailableB = Boolean.parseBoolean(exitAvailable.getObj());
        boolean saveAvailableB = Boolean.parseBoolean(saveAvailable.getObj());

        /*Init keyPacks and translate to keys.*/
        GlobalKeyListener.IKeyPack[] packs = new GlobalKeyListener.IKeyPack[] {
                minimizeAvailableB?saver.translateKeys(minimizeHotkey.getObj(), GlobalKeyListener.KEY_TAGS.MINIMIZE_KEY_MARK):null,
                undecoratedAvailableB?saver.translateKeys(undecoratedHotkey.getObj(), GlobalKeyListener.KEY_TAGS.UNDECORATED_KEY_MARK):null,
                exitAvailableB?saver.translateKeys(exitHotkey.getObj(), GlobalKeyListener.KEY_TAGS.EXIT_ALL_KEY_MARK):null,
                saveAvailableB?saver.translateKeys(saveHotkey.getObj(), GlobalKeyListener.KEY_TAGS.SAVE_KEY_MARK):null
        };

        /*Register keys.*/
        listener = new GlobalKeyListener(packs[0], packs[1], packs[2], packs[3]);
        listener.setKeyListeners(getMinimizeRuns(minimizeBtn), getUndecoratedRuns(undecoratedBtn),
                getSaveRuns(true), getSaveRuns(false));
    }
}