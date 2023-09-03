package ngit.maker.recorder.workflow;

import ngit.maker.recorder.keyboards.EModKeys;
import ngit.maker.recorder.keyboards.ENativeKeys;
import ngit.maker.recorder.keyboards.GlobalKeyListener;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsPane extends JFrame {
    private final WorkflowSaver saver;
    private final GlobalKeyListener listener;
    private final Logger logger;
    private WorkflowSaver.ReadSupplier minimizeHotkey;
    private WorkflowSaver.ReadSupplier minimizeAvailable;
    private WorkflowSaver.ReadSupplier undecoratedHotkey;
    private WorkflowSaver.ReadSupplier undecoratedAvailable;
    private WorkflowSaver.ReadSupplier exitHotkey;
    private WorkflowSaver.ReadSupplier exitAvailable;
    private WorkflowSaver.ReadSupplier saveHotkey;
    private WorkflowSaver.ReadSupplier saveAvailable;
    public SettingsPane(WorkflowSaver saver, GlobalKeyListener listener, Logger logger){
        this.saver = saver;
        this.listener = listener;
        this.logger = logger;
        basicSetup(this);

        utilInit(this);

        setVisible(true);
    }
    private void basicSetup(JFrame frame){
        frame.setSize(700,300);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(false);
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.setLayout(new GridLayout(8,1,5,5));
    }

    private void utilInit(JFrame frame){
        frame.add(getHotkeysHeadSet());
        /*Read from properties.*/
        minimizeHotkey = saver.readMinimizeHotkey();
        minimizeAvailable = saver.readMinimizeAvailable();

        undecoratedHotkey = saver.readUndecoratedHotkey();
        undecoratedAvailable = saver.readUndecoratedAvailable();

        exitHotkey = saver.readExitHotkey();
        exitAvailable = saver.readExitAvailable();

        saveHotkey = saver.readSaveHotkey();
        saveAvailable = saver.readSaveAvailable();

        /*If anything went wrong, tip for user and read from default.*/
        if (!(minimizeHotkey.isValid() &&
                undecoratedHotkey.isValid() &&
                exitHotkey.isValid() &&
                saveHotkey.isValid())
                &&
                !(minimizeAvailable.isValid() &&
                undecoratedAvailable.isValid() &&
                exitAvailable.isValid() &&
                saveAvailable.isValid())
        ){
            JOptionPane.showConfirmDialog(frame, "Fatal on key reading! now using default. See more at logs.");
        }


        boolean minimizeAvailableB = Boolean.parseBoolean(minimizeAvailable.getObj());
        boolean undecoratedAvailableB = Boolean.parseBoolean(undecoratedAvailable.getObj());
        boolean exitAvailableB = Boolean.parseBoolean(exitAvailable.getObj());
        boolean saveAvailableB = Boolean.parseBoolean(saveAvailable.getObj());

        KeySetter setter1 = getHotkeySetter(
                "最小化/还原热键: ",
                saver.translateKeys(minimizeHotkey.getObj(), GlobalKeyListener.KEY_TAGS.MINIMIZE_KEY_MARK),
                minimizeAvailableB
        );
        KeySetter setter2 = getHotkeySetter(
                "取消标题栏/还原热键: ",
                saver.translateKeys(undecoratedHotkey.getObj(), GlobalKeyListener.KEY_TAGS.UNDECORATED_KEY_MARK),
                undecoratedAvailableB
        );
        KeySetter setter3 = getHotkeySetter(
                "全部关闭热键: ",
                saver.translateKeys(exitHotkey.getObj(), GlobalKeyListener.KEY_TAGS.EXIT_ALL_KEY_MARK),
                exitAvailableB
        );
        KeySetter setter4 = getHotkeySetter(
                "全部保存热键: ",
                saver.translateKeys(saveHotkey.getObj(), GlobalKeyListener.KEY_TAGS.SAVE_KEY_MARK),
                saveAvailableB
        );

        JPanel btnS =new JPanel(new GridLayout(1,2,5,5));
        btnS.add(saveAndExitBtnSetup(setter1, setter2, setter3, setter4, frame));
        btnS.add(cancelAndExitBtnSetup(frame));
        JPanel hold2 = new JPanel(new BorderLayout());
        hold2.add(new JLabel(), BorderLayout.CENTER);
        hold2.add(btnS,BorderLayout.EAST);

        frame.add(setter1);frame.add(setter2);frame.add(setter3);frame.add(setter4);
        frame.add(new JPanel());frame.add(new JPanel());
        frame.add(hold2);
    }

    private JPanel getHotkeysHeadSet(){
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("热键设置:   ");
        label.setForeground(Color.darkGray);

        panel.add(label, BorderLayout.WEST);
        panel.add(new JLabel(), BorderLayout.CENTER);
        return panel;
    }

    private KeySetter getHotkeySetter(String description, GlobalKeyListener.IKeyPack keyPack, boolean available){
        return new KeySetter(description, EModKeys.valueOf(keyPack.getMod1()), EModKeys.valueOf(keyPack.getMod2()),
                EModKeys.valueOf(keyPack.getMod3()),ENativeKeys.valueOf(keyPack.getKey()),
                available);
    }

    private JButton saveAndExitBtnSetup(KeySetter setter1, KeySetter setter2, KeySetter setter3, KeySetter setter4, JFrame frame){
        JButton button = new JButton("确定");
        button.setSize(70,30);
        button.addActionListener(e -> {
            WorkflowSaver.ReadSupplier rMinimizeHotkey = saver.mixKeys(getModSelect(setter1.mod1), getModSelect(setter1.mod2), getModSelect(setter1.mod3), getKeySelect(setter1.key));
            WorkflowSaver.ReadSupplier rMinimizeAvailable = WorkflowSaver.ReadSupplier.returnValidOne(String.valueOf(setter1.isAvailable.isSelected()));

            WorkflowSaver.ReadSupplier rUndecoratedHotkey = saver.mixKeys(getModSelect(setter2.mod1), getModSelect(setter2.mod2), getModSelect(setter2.mod3), getKeySelect(setter2.key));
            WorkflowSaver.ReadSupplier rUndecoratedAvailable = WorkflowSaver.ReadSupplier.returnValidOne(String.valueOf(setter2.isAvailable.isSelected()));

            WorkflowSaver.ReadSupplier rExitHotkey = saver.mixKeys(getModSelect(setter3.mod1), getModSelect(setter3.mod2), getModSelect(setter3.mod3), getKeySelect(setter3.key));
            WorkflowSaver.ReadSupplier rExitAvailable = WorkflowSaver.ReadSupplier.returnValidOne(String.valueOf(setter3.isAvailable.isSelected()));

            WorkflowSaver.ReadSupplier rSaveHotkey = saver.mixKeys(getModSelect(setter4.mod1), getModSelect(setter4.mod2), getModSelect(setter4.mod3), getKeySelect(setter4.key));
            WorkflowSaver.ReadSupplier rSaveAvailable = WorkflowSaver.ReadSupplier.returnValidOne(String.valueOf(setter4.isAvailable.isSelected()));


            if (!(rMinimizeHotkey.equals(minimizeHotkey) &&
                    rMinimizeAvailable.equals(minimizeAvailable) &&

                    rUndecoratedHotkey.equals(undecoratedHotkey) &&
                    rUndecoratedAvailable.equals(undecoratedAvailable) &&

                    rExitHotkey.equals(exitHotkey) &&
                    rExitAvailable.equals(exitAvailable) &&

                    rSaveHotkey.equals(saveHotkey) &&
                    rSaveAvailable.equals(saveAvailable))
            ){
                if (!(
                    saver.setMinimizeHotkey(rMinimizeHotkey.getObj()) &&
                    saver.setMinimizeAvailable(setter1.isAvailable.isSelected()) &&

                    saver.setUndecoratedHotkey(rUndecoratedHotkey.getObj()) &&
                    saver.setUndecoratedAvailable(setter2.isAvailable.isSelected()) &&

                    saver.setExitHotkey(rExitHotkey.getObj()) &&
                    saver.setExitAvailable(setter3.isAvailable.isSelected()) &&

                    saver.setSaveHotkey(rSaveHotkey.getObj()) &&
                    saver.setSaveAvailable(setter4.isAvailable.isSelected())
                )){
                    logger.log(Level.SEVERE, "Can't save keys to properties.", new Exception("Unknown"));
                }

                listener.resetKeys(
                        setter1.isAvailable.isSelected()?saver.translateKeys(rMinimizeHotkey.getObj(), GlobalKeyListener.KEY_TAGS.MINIMIZE_KEY_MARK):null,
                        setter2.isAvailable.isSelected()?saver.translateKeys(rUndecoratedHotkey.getObj(), GlobalKeyListener.KEY_TAGS.UNDECORATED_KEY_MARK):null,
                        setter3.isAvailable.isSelected()?saver.translateKeys(rExitHotkey.getObj(), GlobalKeyListener.KEY_TAGS.EXIT_ALL_KEY_MARK):null,
                        setter4.isAvailable.isSelected()?saver.translateKeys(rSaveHotkey.getObj(), GlobalKeyListener.KEY_TAGS.SAVE_KEY_MARK):null);
            }
            frame.dispose();
        });

        return button;
    }

    private JButton cancelAndExitBtnSetup(JFrame frame){
        JButton button = new JButton("取消");
        button.setSize(70,30);
        button.addActionListener(e -> frame.dispose());
        return button;
    }

    public EModKeys getModSelect(JComboBox<EModKeys> cb) {
        return cb.getItemAt(cb.getSelectedIndex());
    }
    public ENativeKeys getKeySelect(JComboBox<ENativeKeys> cb) {
        return cb.getItemAt(cb.getSelectedIndex());
    }
}

class KeySetter extends JPanel{
    public final JComboBox<EModKeys> mod1;
    public final JComboBox<EModKeys> mod2;
    public final JComboBox<EModKeys> mod3;
    public final JComboBox<ENativeKeys> key;
    public final JCheckBox isAvailable;

    public KeySetter(String description, EModKeys reKey1, EModKeys reKey2, EModKeys reKey3, ENativeKeys atKey, boolean available){
        setLayout(new GridLayout(1,6,10,10));
        mod1 = getModKey(reKey1);
        mod2 = getModKey(reKey2);
        mod3 = getModKey(reKey3);
        key = getNativeKey(atKey);
        isAvailable = new JCheckBox("使用");
        isAvailable.setSelected(available);
        add(new JLabel(description));add(mod1);add(mod2);add(mod3);add(key);add(isAvailable);
    }

    private JComboBox<EModKeys> getModKey(EModKeys reKey){
        JComboBox<EModKeys> keysBox = new JComboBox<>();
        for (EModKeys key : EModKeys.values()){
            keysBox.addItem(key);
        }
        keysBox.setSelectedItem(reKey);
        return keysBox;
    }
    private JComboBox<ENativeKeys> getNativeKey(ENativeKeys reKey){
        JComboBox<ENativeKeys> keysBox = new JComboBox<>();
        for (ENativeKeys key : ENativeKeys.values()){
            keysBox.addItem(key);
        }
        keysBox.setSelectedItem(reKey);
        return keysBox;
    }

}