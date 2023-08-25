package ngit.maker.recorder;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {
    public boolean shiftPressed;
    public boolean ctrlPressed;
    public boolean f7Pressed;
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) 

        if (e.getKeyChar() == NativeKeyEvent.VC_F7) f7Pressed = true;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {

    }

}
