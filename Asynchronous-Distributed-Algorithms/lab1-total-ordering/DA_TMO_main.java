// The main class DA-name-main creates all the processes of the distributed algorithm that
// will run on a single host. This can also be done using a script.

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

public class DA_TMO_main {

   // private static Logger LOGGER;
    private static Remote registry;

    //private  int delay = 2000;
    private static final int PORT = 1098;
    private static final Logger LOGGER = Logger.getLogger(DA_TMO_main.class.getName());

    public static void main(String[] args) throws NoSuchObjectException {

        System.out.println("Enter command (exit/test[1-4]):");
        Scanner in = new Scanner(System.in);
        console: while(true){
            String line = in.nextLine();
            switch(line){
                case "exit":
                    break console;
                case "test1":
                    Test1();
                    System.out.println("Enter command (exit/test[1-4]):");
                    break;
                case "test2":
                    Test2();
                    System.out.println("Enter command (exit/test[1-4]):");
                    break;
                case "test3":
                    Test3();
                    System.out.println("Enter command (exit/test[1-4]):");
                    break;
                case "test4":
                    Test4();
                    System.out.println("Enter command (exit/test[1-4]):");
                    break;
                default:
                    System.out.println("Wrong command, please enter (exit/test[1-4]):");
                    break;
            }
        }
        in.close();
        System.exit(0); 

    }

    /**
     * Test case 1: 3 processes, no delay
     * @throws NoSuchObjectException
     */
    public static void Test1() throws NoSuchObjectException{
        // Init the RMI registry and create processes.
        try
        {
            registry= LocateRegistry.createRegistry(PORT);
            System.out.println("Registry created");
        }
        catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        System.out.println("Test case 1: 3 processes, no delay.");
        int numProcesses = 3;
        int delay=2000;
        DA_TMO[] processes = new DA_TMO[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            try {
                LOGGER.info("Starting thread for process " +i);
                processes[i] = new DA_TMO(numProcesses,i,PORT);
                //processes[i].vectorClock = new VectorClock(numProcesses);
            } catch (RemoteException e) {
                LOGGER.severe("Remote exception creating RMI instance.");
                e.printStackTrace();
            }
        }
        // Send some messages.
        try {
            processes[0].start();
            processes[1].start();
            processes[2].start();
            processes[0].to_broadcast(1,"Message1",0);
            processes[1].to_broadcast(2,"Message2",0);
            processes[2].to_broadcast(3,"Message3",0);
        } catch (RemoteException e) {
            LOGGER.severe("Remote exception sending messages.");
            e.printStackTrace();
        }

        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*5);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // deregister the registry
        if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
        }
        System.out.println("Test 1 done. Check process(0-2) logs.");
    }

    /**
     * Test case 2: 3 processes, P0 no delay, P1 and P2 random delay.
     * @throws NoSuchObjectException
     */
    public static void Test2() throws NoSuchObjectException{
        // Init the RMI registry and create processes.
        try
        {
            registry= LocateRegistry.createRegistry(PORT);
            System.out.println("Registry created");
        }
        catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        System.out.println("3 processes, P1 no delay, P2 and P3 random delay.");
        int numProcesses = 3;
        DA_TMO[] processes = new DA_TMO[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            try {
                LOGGER.info("Starting thread for process " +i);
                processes[i] = new DA_TMO(numProcesses,i,PORT);
                //processes[i].vectorClock = new VectorClock(numProcesses);
            } catch (RemoteException e) {
                LOGGER.severe("Remote exception creating RMI instance.");
                e.printStackTrace();
            }
        }
        // Send some messages.
        try {
            processes[0].start();
            processes[1].start();
            processes[2].start();
            processes[0].to_broadcast(1,"Message1",0);
            processes[1].to_broadcast(2,"Message2",randomDelay());
            processes[2].to_broadcast(3,"Message3",randomDelay());
        } catch (RemoteException e) {
            LOGGER.severe("Remote exception sending messages.");
            e.printStackTrace();
        }

        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep((3000)*5);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // deregister the registry
        if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
        }
        System.out.println("Test 2 done. Check process(0-2) logs.");
    }

    /**
     * Test case 3: 5 processes, all with random delays.
     * @throws NoSuchObjectException
     */
    public static void Test3() throws NoSuchObjectException{
        // Init the RMI registry and create processes.
        try
        {
            registry= LocateRegistry.createRegistry(PORT);
            System.out.println("Registry created");
        }
        catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        System.out.println("Test case 3: 5 processes, all with random delays.");
        int numProcesses = 5;
        DA_TMO[] processes = new DA_TMO[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            try {
                LOGGER.info("Starting thread for process " +i);
                processes[i] = new DA_TMO(numProcesses,i,PORT);
                //processes[i].vectorClock = new VectorClock(numProcesses);
            } catch (RemoteException e) {
                LOGGER.severe("Remote exception creating RMI instance.");
                e.printStackTrace();
            }
        }
        // Send some messages.
        try {
            for (int i=0; i<numProcesses; i++){
                processes[i].start();
            }
            for (int i=0; i<numProcesses; i++){
                processes[i].to_broadcast(i+1, "Message"+i, randomDelay());
            }
        } catch (RemoteException e) {
            LOGGER.severe("Remote exception sending messages.");
            e.printStackTrace();
        }

        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep((3000)*7);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // deregister the registry
        if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
        }
        System.out.println("Test 3 done. Check process(0-4) logs.");
    }

    /**
     * Test case 4: 3 processes, each sends 2 messages (with random delays).
     * @throws NoSuchObjectException
     */
    public static void Test4() throws NoSuchObjectException{
        // Init the RMI registry and create processes.
        try
        {
            registry= LocateRegistry.createRegistry(PORT);
            System.out.println("Registry created");
        }
        catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        System.out.println("Test case 4: 3 processes, each sends 2 messages (with random delays)");
        int numProcesses = 3;
        DA_TMO[] processes = new DA_TMO[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            try {
                LOGGER.info("Starting thread for process " +i);
                processes[i] = new DA_TMO(numProcesses,i,PORT);
                //processes[i].vectorClock = new VectorClock(numProcesses);
            } catch (RemoteException e) {
                LOGGER.severe("Remote exception creating RMI instance.");
                e.printStackTrace();
            }
        }
        // Send some messages.
        try {
            for (int i=0; i<numProcesses; i++){
                processes[i].start();
            }
            for (int i=0; i<numProcesses; i++){
                processes[i].to_broadcast(i+1, "Message"+i, randomDelay());
                processes[i].to_broadcast(i+4, "Message"+(i+3), randomDelay());
            }
        } catch (RemoteException e) {
            LOGGER.severe("Remote exception sending messages.");
            e.printStackTrace();
        }

        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep((6000)*7);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // deregister the registry
        if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
        }
        System.out.println("Test 4 done. Check process(0-2) logs.");
    }

    /**
     * Gives value for random delay [0.5,3]s
     * @return random value
     */
    public static int randomDelay() {
        return (new Random().nextInt(2500)+500);
    }
}
