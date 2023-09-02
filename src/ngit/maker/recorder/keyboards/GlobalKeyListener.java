package ngit.maker.recorder.keyboards;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;


public class GlobalKeyListener {
    public enum KEY_TAGS{
        MINIMIZE_KEY_MARK,//最小化的热键标识
        UNDECORATED_KEY_MARK,//无标题栏的热键标识
        EXIT_ALL_KEY_MARK,//关闭的热键标识
        SAVE_KEY_MARK//保存的热键标识
    }
    public IKeyPack[] keyPacks;
    private Runnable[] runnables;
    private HotkeyListener hotkeyListener;

    public interface IKeyPack{
        int getKeyMark();
        String getMod1();
        String getMod2();
        String getMod3();
        String getKey();
        static IKeyPack newPack(KEY_TAGS keyMark, String mod1, String mod2, String mod3, String key){
            return new IKeyPack() {
                public int getKeyMark() {return keyMark.ordinal();}
                public String getMod1() {return mod1;}
                public String getMod2() {return mod2;}
                public String getMod3() {return mod3;}
                public String getKey() {return key;}
            };
        }
        default int translateMod(String s){
            return EModKeys.valueOf(s).getIntValue();
        }
        default int translateAllMods(){
            return translateMod(getMod1()) + translateMod(getMod2()) + translateMod(getMod3());
        }
        default int translateKey(String s){
            return ENativeKeys.valueOf(s).getIntValue();
        }
    }

    public GlobalKeyListener(IKeyPack keyPackMin, IKeyPack keyPackUnd, IKeyPack keyPackExit, IKeyPack keyPackSave){
        keyPacks = new IKeyPack[] {keyPackMin, keyPackUnd, keyPackExit, keyPackSave};
        resetKeys(keyPackMin, keyPackUnd, keyPackExit, keyPackSave);
    }

    public void setKeyListeners(Runnable rMin, Runnable rUndecorated, Runnable rExit, Runnable rSave){
        runnables = new Runnable[] {rMin, rExit, rUndecorated, rSave};
         hotkeyListener = i -> {
            switch (i){
                case 0 -> rMin.run();
                case 1 -> rUndecorated.run();
                case 2 -> rExit.run();
                case 3 -> rSave.run();
            }
        };
        JIntellitype.getInstance().addHotKeyListener(hotkeyListener);
    }

    private void resetKeysSecret(){
        for (IKeyPack pack : keyPacks) {
            if (pack != null) {
                JIntellitype.getInstance().registerHotKey(
                        pack.getKeyMark(), pack.translateAllMods(), pack.translateKey(pack.getKey())
                );
            }
        }
    }

    public void resetKeys(IKeyPack keyPackMin, IKeyPack keyPackUnd, IKeyPack keyPackExit, IKeyPack keyPackSave) {
        JIntellitype.getInstance().removeHotKeyListener(hotkeyListener);
        for (IKeyPack keyPack : keyPacks) {
            JIntellitype.getInstance().unregisterHotKey(keyPack.getKeyMark());
        }

        keyPacks = new IKeyPack[] {keyPackMin, keyPackUnd, keyPackExit, keyPackSave};
        this.resetKeysSecret();
        if (runnables != null) {
            setKeyListeners(runnables[0], runnables[1], runnables[2], runnables[3]);
        }
    }
}
