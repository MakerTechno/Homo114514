package ngit.maker.recorder;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;


public class GlobalKeyListener {
    public enum KEY_TAGS{
        MINIMIZE_KEY_MARK,//最小化的热键标识
        EXIT_ALL_KEY_MARK,//关闭的热键标识
        UNDECORATED_KEY_MARK,//无标题栏的热键标识
        SAVE_KEY_MARK//保存的热键标识
    }
    public IKeyPack[] keyPacks;
    private Runnable[] runnables;
    private HotkeyListener hotkeyListener;

    public interface IKeyPack{
        int getKeyMark();
        int getModInt();
        int getKeyInt();
        static IKeyPack newPack(KEY_TAGS keyMark, int mod, int key){
            return new IKeyPack() {
                public int getKeyMark() {return keyMark.ordinal();}
                public int getModInt() {return mod;}
                public int getKeyInt() {return key;}
            };
        }
    }

    public GlobalKeyListener(IKeyPack keyPackMin, IKeyPack keyPackExit, IKeyPack keyPackUnd, IKeyPack keyPackSave){
        resetKeys(keyPackMin, keyPackExit, keyPackUnd, keyPackSave);
    }

    public void setKeyListeners(Runnable rMin, Runnable rExit, Runnable rUndecorated, Runnable rSave){
        runnables = new Runnable[] {rMin, rExit, rUndecorated, rSave};
         hotkeyListener = i -> {
            switch (i){
                case 0 -> rMin.run();
                case 1 -> rExit.run();
                case 2 -> rUndecorated.run();
                case 3 -> rSave.run();
            }
        };
        JIntellitype.getInstance().addHotKeyListener(hotkeyListener);
    }

    private void resetKeysSecret(IKeyPack keyPackMin, IKeyPack keyPackExit, IKeyPack keyPackUnd, IKeyPack keyPackSave){
        for (IKeyPack pack : keyPacks) {
            JIntellitype.getInstance().registerHotKey(pack.getKeyMark(), pack.getModInt(), pack.getKeyInt());
        }
    }

    public void resetKeys(IKeyPack keyPackMin, IKeyPack keyPackExit, IKeyPack keyPackUnd, IKeyPack keyPackSave) {
        keyPacks = new IKeyPack[] {keyPackMin, keyPackExit, keyPackUnd, keyPackSave};
        JIntellitype.getInstance().removeHotKeyListener(hotkeyListener);
        for (IKeyPack keyPack : keyPacks) {
            JIntellitype.getInstance().unregisterHotKey(keyPack.getKeyMark());
        }
        this.resetKeysSecret(keyPackMin, keyPackExit, keyPackUnd, keyPackSave);
        if (runnables != null) {
            setKeyListeners(runnables[0], runnables[1], runnables[2], runnables[3]);
        }
    }
}
