package ships.types;

public class ShipTypes {
    public static final int MEAL = 0x0100;
    public static final int DRESS = 0x0200;
    public static final int BANANA = 0x0300;

    public static final int[] TYPES= {MEAL, DRESS, BANANA};

    public static String getTypeName(int type) {
        switch (type) {
            case 0x00000100:
                return "MEAL";
            case 0x00000200:
                return "DRESS";
            case 0x00000300:
                return "BANANA";
            default:
                return "UNKNOWN";
        }
    }

    public static int getShipType(int ship) {
        return ship & 0x0300;
    }

    public static String getShipTypeName(int ship) {
        return getTypeName(ship & 0x0300);
    }
}
