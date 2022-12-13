package other;

import exception.TunnelMaxSizePropertyException;
import ships.types.ShipTypes;
import ships.types.Size;

public class Tunnel {

    private final int[] store;
    private static final int maxShipsInTunnel = 5;
    private static final int minShipsInTunnel = 0;

    private int shipsCounter = 0;

    private boolean shipFinished = false;


    public Tunnel() {
        store = new int[maxShipsInTunnel];
    }

    public synchronized boolean add(int ship) {

        try {
            if (shipsCounter < maxShipsInTunnel) {
                notifyAll();
                for (int i = 0; i < maxShipsInTunnel; i++) {
                    if (store[i] == 0) {
                        store[i] = ship;
                        break;
                    }
                }
                if (Defines.PRINT_TEXT) {
                    String info = String.format("%s + The ship arrived in the tunnel: %s %s %s", shipsCounter + 1,
                            ShipTypes.getShipType(ship), Size.getShipSize(ship), Thread.currentThread().getName());
                    System.out.println(info);
                }
                shipsCounter++;

                if (shipsCounter > maxShipsInTunnel)
                    throw new TunnelMaxSizePropertyException("Tunnel max size property error");

            } else {
                if (Defines.PRINT_TEXT) {
                    System.out.println(shipsCounter + "> There is no place for a ship in the tunnel: "
                            + Thread.currentThread().getName());
                }
                wait();
                return false;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public synchronized void shipFinished()
    {
        shipFinished = true;
        notifyAll();
    }

    public synchronized int get(int shipType) {

        try {
            if (shipsCounter > minShipsInTunnel) {
                notifyAll();
                for (int i = 0; i < maxShipsInTunnel; i++) {
                    if (ShipTypes.getShipType(store[i]) == shipType) {
                        shipsCounter--;
                        if (Defines.PRINT_TEXT) {
                            System.out.println(shipsCounter + "- The ship is taken from the tunnel: " + Thread.currentThread().getName());
                        }
                        int ship = store[i];
                        store[i] = 0;
                        return ship;
                    }
                }
            }
            if (Defines.PRINT_TEXT) {
                System.out.println("0 < There are no ships in the tunnel");
            }
            if (!noMoreShips())
                wait();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean noMoreShips()
    {
        return shipFinished;
    }
}
