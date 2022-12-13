package tasks;

import exception.LoadingGoodsException;
import exception.TypeMismatchException;
import other.Defines;
import other.Tunnel;
import ships.Ship;
import ships.types.ShipTypes;

public class PierLoader implements Runnable {
    private final Tunnel tunnel;
    private final int pierType;

    public PierLoader(Tunnel tunnel, int pierType) {
        this.tunnel = tunnel;
        this.pierType = pierType;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.currentThread().setName("Loader " + ShipTypes.getTypeName(pierType));

                //Time to load the goods
                Thread.sleep(Defines.TIME_UNIT);
                int ship = tunnel.get(pierType);

                if (ship == 0 && tunnel.noMoreShips()) {
                    if (Defines.PRINT_TEXT)
                        System.out.println("No more ships for " + Thread.currentThread().getName());
                    return;
                }
                if (ship != 0) {
                    if(ShipTypes.getShipType(ship) != pierType)
                        throw new TypeMismatchException("Pier type does not match ship type");

                    while(Ship.checkShipSize(ship)) {
                        Thread.sleep(Defines.TIME_UNIT);

                        if(Defines.TIME_UNIT > 1000)
                            throw new LoadingGoodsException("Time exceeded 1 sec");

                        ship += 10;
                        if(Defines.PRINT_TEXT)
                            System.out.println(Ship.getShipCount(ship) + " Loaded ship. " + Thread.currentThread().getName());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
