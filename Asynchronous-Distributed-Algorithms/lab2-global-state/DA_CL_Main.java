// package Lab2;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DA_CL_Main  {
    // private static Logger LOGGER;
    private static Remote registry;

    //private  int delay = 2000;
    private static final int PORT = 1099;
    private static final Logger LOGGER = Logger.getLogger(DA_CL_Main.class.getName());

    public static void main(String[] args) throws NoSuchObjectException, RemoteException {

//


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
     * Test case 1: 3 processes
     * 
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
        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        int numProcesses = 3;
        int delay = 2000;
        // DA_TMO[] processes = new DA_TMO[numProcesses](numProcesses, int index, int port);
        Component[] components = new Component[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            try {
                LOGGER.info("Starting thread for process " +i);
                components[i] = new Component(PORT,i,numProcesses);
                //processes[i].vectorClock = new VectorClock(numProcesses);
            } catch (RemoteException e) {
                LOGGER.severe("Remote exception creating RMI instance.");
                e.printStackTrace();
            }
        }
        // Send some messages.
        // send a message: sender | messagetype | index (marker has no index)

        // two components, each only send one
        // components[0].send(new Message(0,MessageType.MESSAGE,0,50,1),0);
        // components[1].send(new Message(1,MessageType.MESSAGE,1,10,0),1000);
        // components[1].send(new Message(1,MessageType.MESSAGE,2,10,0),1000);
        // components[2].send(new Message(2,MessageType.MESSAGE,3,10,0),3000);
        // components[2].send(new Message(2,MessageType.MESSAGE,4,10,0),2000);
        // components[3].send(new Message(3,MessageType.MESSAGE,5,20,2),1000);

        try {
            components[0].send(new Message(0, MessageType.MESSAGE, 0, 50, 1), 3000);
            components[1].send(new Message(1, MessageType.MESSAGE, 0, 10, 0), 2500);
            components[2].send(new Message(2, MessageType.MESSAGE, 0, 10, 0), 2500);
            components[1].send(new Message(1, MessageType.MESSAGE, 0, 20, 2), 2500);

            // component 0 start the algorithm
            components[0].recordGlobalState(1000);
        }catch (RemoteException e){
            LOGGER.info("fail to send message");
        }

        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*10);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // print out the result
        // components[0].printGlobalState();
        printGlobalState(components);

        // deregister the registry
        if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
        }
    }


    /**
     * Test case 2: 3 processes, record global state after all the message sending
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
        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        int numProcesses = 3;
        int delay = 2000;
        // DA_TMO[] processes = new DA_TMO[numProcesses](numProcesses, int index, int port);
        Component[] components = new Component[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            try {
                LOGGER.info("Starting thread for process " +i);
                components[i] = new Component(PORT,i,numProcesses);
                //processes[i].vectorClock = new VectorClock(numProcesses);
            } catch (RemoteException e) {
                LOGGER.severe("Remote exception creating RMI instance.");
                e.printStackTrace();
            }
        }

        try {
            // sender,
            components[0].send(new Message(0,MessageType.MESSAGE,0,50,1),0);
            components[0].send(new Message(0,MessageType.MESSAGE,1,40,2),0);
            components[1].send(new Message(1,MessageType.MESSAGE,1,10,0),randomDelay());
            components[1].send(new Message(1,MessageType.MESSAGE,2,15,2),randomDelay());
            components[1].send(new Message(1,MessageType.MESSAGE,3,10,0),randomDelay());
            components[1].send(new Message(1,MessageType.MESSAGE,4,10,2),randomDelay());
            components[2].send(new Message(2,MessageType.MESSAGE,1,10,0),randomDelay());
            components[2].send(new Message(2,MessageType.MESSAGE,2,10,0),randomDelay());

            // component 0 start the algorithm
            components[0].recordGlobalState(1000);
        }catch (RemoteException e){
            LOGGER.info("fail to send message");
        }

        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*10);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // print out the result
        // components[0].printGlobalState();
        printGlobalState(components);

        // deregister the registry
        if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
        }
    }

    /**
     * Test 3: 5 processes
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
        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        int numProcesses = 5;
        int delay = 2000;
        // DA_TMO[] processes = new DA_TMO[numProcesses](numProcesses, int index, int port);
        Component[] components = new Component[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            try {
                LOGGER.info("Starting thread for process " +i);
                components[i] = new Component(PORT,i,numProcesses);
                //processes[i].vectorClock = new VectorClock(numProcesses);
            } catch (RemoteException e) {
                LOGGER.severe("Remote exception creating RMI instance.");
                e.printStackTrace();
            }
        }

        try {
            components[0].send(new Message(0, MessageType.MESSAGE, 0, 10, 1), randomDelay());
            components[1].send(new Message(1, MessageType.MESSAGE, 0, 20, 2), randomDelay());
            components[2].send(new Message(2, MessageType.MESSAGE, 0, 30, 3), randomDelay());
            components[3].send(new Message(3, MessageType.MESSAGE, 0, 10, 4), randomDelay());
            components[4].send(new Message(4, MessageType.MESSAGE, 0, 20, 0), randomDelay());
            components[1].send(new Message(1, MessageType.MESSAGE, 0, 10, 3), randomDelay());
            components[2].send(new Message(2, MessageType.MESSAGE, 0, 10, 1), randomDelay());

            // component 0 start the algorithm
            components[0].recordGlobalState(1000);
        }catch (RemoteException e){
            LOGGER.info("fail to send message");
        }

        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*10);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // print out the result
        // components[0].printGlobalState();
        printGlobalState(components);

        // deregister the registry
        if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
        }
    }

    public static void Test4() throws NoSuchObjectException{


        // deregister the registry
        if (registry != null) {
            UnicastRemoteObject.unexportObject(registry, true);
        }
    }



    public static void printGlobalState(Component[] components){
        System.out.println("\nGlobal state:");
        for(int i=0; i<components.length;i++){
            components[i].printState();
        }
    }

    /**
     * Gives value for random delay [0.5,3]s
     * @return random value
     */
    public static int randomDelay() {
        return (new Random().nextInt(2500)+500);
    }




}
