package other;

import exception.ThreadAwakeWithoutTaskException;
import exception.TunnelMaxSizePropertyException;
import ships.types.ShipTypes;
import ships.types.Size;

public class Tunnel {

    private final int[] store;

    // 0 object lock for tunnel 1..size - 1 for pierLoaders
    private final Object[] syncObjects;
    private static final int maxShipsInTunnel = 5;
    private static final int minShipsInTunnel = 0;

    private int shipsCounter = 0;

    private boolean shipFinished = false;


    public Tunnel() {
        store = new int[maxShipsInTunnel];
        syncObjects = new Object[ShipTypes.TYPES.length + 1];
        for (int i = 0; i < syncObjects.length; i++)
        {
            syncObjects[i] = new Object();
        }
    }

    public boolean add(int ship) {

        try {
            if (shipsCounter < maxShipsInTunnel) {
                for (int i = 0; i < maxShipsInTunnel; i++) {
                    if (store[i] == 0) {
                        store[i] = ship;
                        break;
                    }
                }
                if (Defines.PRINT_TEXT) {
                    String info = String.format("%s + The ship arrived in the tunnel: %s %s %s", shipsCounter + 1,
                            ShipTypes.getShipTypeName(ship), Size.getShipRealSize(ship), Thread.currentThread().getName());
                    System.out.println(info);
                }
                shipsCounter++;

                for (int i = 1; i < syncObjects.length; i ++) {
                    synchronized (syncObjects[i]) {
                        syncObjects[i].notify();
                    }
                }
                if (shipsCounter > maxShipsInTunnel)
                    throw new TunnelMaxSizePropertyException("Tunnel max size property error");

            } else {
                if (Defines.PRINT_TEXT) {
                    System.out.println(shipsCounter + "> There is no place for a ship in the tunnel: "
                            + Thread.currentThread().getName());
                }
                synchronized (syncObjects[0]) {
                    syncObjects[0].wait();
                }
                if (shipsCounter == maxShipsInTunnel)
                    throw new ThreadAwakeWithoutTaskException("Thread " + Thread.currentThread().getName() + " awake " +
                            "when tunnel is full, property thread inactive without task does not " );
                return false;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void shipFinished()
    {
        shipFinished = true;
        for (int i = 1; i < syncObjects.length; i ++) {
            synchronized (syncObjects[i]) {
                syncObjects[i].notify();
            }
        }
    }

    public int get(int shipType) {

        try {
            if (shipsCounter > minShipsInTunnel) {
                for (int i = 0; i < maxShipsInTunnel; i++) {
                    if (ShipTypes.getShipType(store[i]) == shipType) {
                        synchronized (syncObjects[0]) {
                            shipsCounter--;
                            syncObjects[0].notify();
                        }
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
            if (!noMoreShips()) {
                for (int i = 1; i < syncObjects.length; i++) {
                    if (ShipTypes.TYPES[i - 1] == shipType)
                        synchronized (syncObjects[i]) {
                            syncObjects[i].wait();
                        }
                }
                if (shipsCounter == minShipsInTunnel & !noMoreShips())
                    throw new ThreadAwakeWithoutTaskException("Thread " + Thread.currentThread().getName() + " awake " +
                            "when tunnel is full, property thread inactive without task does not " );
            }

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
