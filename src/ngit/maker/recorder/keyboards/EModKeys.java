package ngit.maker.recorder.keyboards;

public enum EModKeys {
    NULL,
    ALT,
    CONTROL,
    SHIFT,
    WIN;

    public int getIntValue() {
        return switch (this) {
            case NULL -> 0;
            case ALT -> 1;
            case CONTROL -> 2;
            case SHIFT -> 4;
            case WIN -> 8;
        };
    }

}
