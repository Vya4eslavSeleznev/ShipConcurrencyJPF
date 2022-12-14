package other;

import exception.ThreadAwakeWithoutTaskException;
import exception.TunnelMaxSizePropertyException;
import ships.types.ShipTypes;
import ships.types.Size;
import tasks.ShipGenerator;

public class Tunnel {

    private final int[] store;

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

            if (shipsCounter < maxShipsInTunnel) {
                synchronized (syncObjects[0]) {
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
                    for (int i = 1; i < syncObjects.length; i++) {
                        if (ShipTypes.getShipType(ship) == ShipTypes.TYPES[i - 1]) {
                            synchronized (syncObjects[i]) {
                                syncObjects[i].notifyAll();
                            }
                            break;
                        }
                    }
                    if (shipsCounter > maxShipsInTunnel)
                        throw new TunnelMaxSizePropertyException("Tunnel max size property error");
                }
            } else {
                if (Defines.PRINT_TEXT) {
                    System.out.println(shipsCounter + "> There is no place for a ship in the tunnel: "
                            + Thread.currentThread().getName());
                }
                synchronized (syncObjects[0]) {
                    try {
                        syncObjects[0].wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (shipsCounter == maxShipsInTunnel)
                    throw new ThreadAwakeWithoutTaskException("Thread " + Thread.currentThread().getName() + " awake " +
                            "when tunnel is full, property thread inactive without task does not " );
                return false;
            }
        return true;
    }

    public void shipFinished()
    {
        shipFinished = true;
        for (int i = 1; i < syncObjects.length; i++) {
            synchronized (syncObjects[i]) {
                syncObjects[i].notifyAll();
            }
        }
    }

    public int get(int shipType) {
            if (shipsCounter > minShipsInTunnel) {
                for (int i = 0; i < maxShipsInTunnel; i++) {
                    if (ShipTypes.getShipType(store[i]) == shipType) {
                        int ship;
                        synchronized (syncObjects[0]) {
                            shipsCounter--;
                            syncObjects[0].notifyAll();
                            if (Defines.PRINT_TEXT) {
                                System.out.println(shipsCounter + "- The ship is taken from the tunnel: " + Thread.currentThread().getName());
                            }
                            ship = store[i];
                            store[i] = 0;
                        }
                        return ship;
                    }
                }
            } else {
                if (Defines.PRINT_TEXT) {
                    System.out.println("0 < There are no ships in the tunnel");
                }
                boolean sleeped = false;
                for (int i = 1; i < syncObjects.length; i++) {
                    if (ShipTypes.TYPES[i - 1] == shipType) {
                        synchronized (syncObjects[i]) {
                            if (!shipFinished) {
                                try {
                                    syncObjects[i].wait();
                                    sleeped = true;
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        break;
                    }
                }
                if (shipsCounter == minShipsInTunnel && !shipFinished) {
                    System.out.println(sleeped);
                    System.out.println(ShipGenerator.getinstance().getCount());
                    System.out.println(shipsCounter);
                    throw new ThreadAwakeWithoutTaskException("Thread " + Thread.currentThread().getName() + " awake " +
                            "when tunnel is empty, property thread is active");
                }
            }
        return 0;
    }

    public boolean noMoreShips()
    {
        return shipFinished;
    }
}
