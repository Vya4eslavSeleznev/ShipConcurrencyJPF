package ships;

public class Ship {

    public static int newShip(int shipType, int shipSize)
    {
        return shipType | shipSize;
    }

    public static int getShipCount(int ship)
    {
        return ship & 0xFF;
    }

    public static boolean checkShipSize(int ship)
    {
        return (ship & 0xFF) < (ship & 0xFF0000) / 0x10000;
    }
}
