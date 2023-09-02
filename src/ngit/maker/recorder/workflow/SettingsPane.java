package ngit.maker.recorder.workflow;

import ngit.maker.recorder.keyboards.EModKeys;
import ngit.maker.recorder.keyboards.ENativeKeys;
import ngit.maker.recorder.keyboards.GlobalKeyListener;

import javax.swing.*;
import java.awt.*;

public class SettingsPane extends JFrame {
    public SettingsPane(WorkflowSaver saver){
        basicSetup(this);

        utilInit(this, saver);

        setVisible(true);
    }
    private void basicSetup(JFrame frame){
        frame.setSize(400,170);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(false);
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.setLayout(new GridLayout(8,1,5,5));
    }

    private void utilInit(JFrame frame, WorkflowSaver saver){
        frame.add(getHotkeysHeadSet());
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
        frame.add(setter1);frame.add(setter2);frame.add(setter3);frame.add(setter4);
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