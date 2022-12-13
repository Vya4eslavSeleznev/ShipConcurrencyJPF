package tasks;

import other.Defines;
import other.Tunnel;
import ships.Ship;
import ships.types.ShipTypes;
import ships.types.Size;

import java.util.Random;

public class ShipGenerator implements Runnable {
    private final Tunnel tunnel;
    private final int shipCount;
    private final int max = 2;
    private final int min = 0;

    static ShipGenerator shipGeneratorInstance = null;

    public ShipGenerator(Tunnel tunnel, int shipCount) {
        this.tunnel = tunnel;
        this.shipCount = shipCount;
        if (shipGeneratorInstance == null)
            shipGeneratorInstance = this;
    }

    public static ShipGenerator getinstance()
    {
        return shipGeneratorInstance;
    }

    private int count = 0;

    public int getCount()
    {
        return count;
    }

    public int getShipCount() {
        return shipCount;
    }

    @Override
    public void run() {
        while (count < shipCount) {
            Thread.currentThread().setName("Generator ship");
            count++;
            tunnel.add(Ship.newShip(getRandomSize(), getRandomType()));
            try {
                Thread.sleep(5 * Defines.TIME_UNIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tunnel.shipFinished();
        if (Defines.PRINT_TEXT)
            System.out.println("ship Generator finish work");
    }

    private int getRandomType() {
        return ShipTypes.TYPES[new Random().nextInt(max - min + 1) + min];
    }

    private int getRandomSize() {
        return Size.SIZES[new Random().nextInt(max - min + 1) + min];
    }
}
