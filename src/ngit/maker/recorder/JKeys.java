package ngit.maker.recorder;

public enum JKeys {
    A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,
    K0,K1,K2,K3,K4,K5,K6,K7,K8,K9,
    F1,F2,F3,F4,F5,F6,F7,F8,F9,F10,F11,F12,
    ESC,TAB,CAPSLOCK,SPACE,
    BACK_SPACE,CARRIAGE_RETURN,BACK_SLASH,//as"/"
    INSERT,DEL,HOME,END,PAGE_UP,PAGE_DOWN,PRINT_SCREEN,SCR_LOCK,PAUSE,
    LEFT_ARROW_KEY,UP_ARROW_KEY,RIGHT_ARROW_KEY,DOWN_ARROW_KEY;

    public int getIntValue(){
        return switch (this){
            case A -> 65;case B -> 66;case C -> 67;case D -> 68;case E -> 69;
            case F -> 70;case G -> 71;case H -> 72;case I -> 73;case J -> 74;
            case K -> 75;case L -> 76;case M -> 77;case N -> 78;case O -> 79;
            case P -> 80;case Q -> 81;case R -> 82;case S -> 83;case T -> 84;
            case U -> 85;case V -> 86;case W -> 87;case X -> 88;case Y -> 89;
            case Z -> 90;

            case K0 -> 48;case K1 -> 49;case K2 -> 50;case K3 -> 51;case K4 -> 52;
            case K5 -> 53;case K6 -> 54;case K7 -> 55;case K8 -> 56;case K9 -> 57;

            case F1 -> 112;case F2 -> 113;case F3 -> 114;case F4 -> 115;
            case F5 -> 116;case F6 -> 117;case F7 -> 118;case F8 -> 119;
            case F9 -> 120;case F10 -> 121;case F11 -> 122;case F12 -> 123;

            case ESC -> 27;
            case TAB -> 9;
            case CAPSLOCK -> 20;
            case SPACE -> 32;

            case CARRIAGE_RETURN -> 13;
            case BACK_SLASH -> 220;
            case BACK_SPACE -> 8;

            case INSERT -> 45;
            case DEL -> 46;
            case HOME -> 36;
            case END -> 35;
            case PAGE_UP -> 33;
            case PAGE_DOWN -> 34;
            case PRINT_SCREEN -> 44;
            case SCR_LOCK -> 145;
            case PAUSE -> 19;

            case LEFT_ARROW_KEY -> 37;
            case UP_ARROW_KEY -> 38;
            case RIGHT_ARROW_KEY -> 39;
            case DOWN_ARROW_KEY -> 40;
        };
    }
}
