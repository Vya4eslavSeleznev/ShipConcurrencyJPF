import exception.ShipCountException;
import other.Tunnel;
import ships.types.ShipTypes;
import tasks.PierLoader;
import tasks.ShipGenerator;

public class Main {

    private static final int SHIP_COUNT = 3;

    public static void main(String[] args) throws ShipCountException {
        //int availableCPUs = Runtime.getRuntime().availableProcessors();
        //System.out.println("Available number of cores: " + availableCPUs);

        Tunnel tunnel = new Tunnel();

        ShipGenerator shipGenerator = new ShipGenerator(tunnel, SHIP_COUNT);

        if (shipGenerator.getShipCount() > SHIP_COUNT)
            throw new ShipCountException("The number of ships is more than possible");

        PierLoader pierLoader1 = new PierLoader(tunnel, ShipTypes.DRESS);
        PierLoader pierLoader2 = new PierLoader(tunnel, ShipTypes.BANANA);
        PierLoader pierLoader3 = new PierLoader(tunnel, ShipTypes.MEAL);
        /*ExecutorService service;
        if (availableCPUs < 4) {
            service = Executors.newFixedThreadPool(4);
        } else {
            service = Executors.newFixedThreadPool(availableCPUs);
        }*/
        Thread thread1 = new Thread(shipGenerator);
        Thread thread2 = new Thread(pierLoader1);
        Thread thread3 = new Thread(pierLoader2);
        Thread thread4 = new Thread(pierLoader3);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        /*service.execute(shipGenerator);
        service.execute(pierLoader1);
        service.execute(pierLoader2);
        service.execute(pierLoader3);
        */

        //service.shutdown();
    }
}
