package ships.types;

public class Size {
    public static final int SMALL = 10 * 0x10000;
    public static final int MIDDLE = 50 * 0x10000;
    public static final int LARGE = 100 * 0x10000;

    public static int[] SIZES = {SMALL, MIDDLE, LARGE};

    public static int getShipSize(int ship) {
        return ship & 0xFF0000;
    }

    public static int getShipCurrentSize(int ship) {
        return ship & 0xFF;
    }
}
