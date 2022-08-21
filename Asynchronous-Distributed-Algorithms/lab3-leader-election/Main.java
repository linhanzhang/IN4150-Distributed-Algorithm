// package Lab2;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main  {
    // private static Logger LOGGER;
    private static Remote registry;
    public static int delay = 1000;

    //private  int delay = 2000;
    private static final int PORT = 1099;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws AlreadyBoundException, NotBoundException, IOException {

//


        System.out.println("Enter command (exit/test[1-5]):");
        Scanner in = new Scanner(System.in);
        console: while(true){
            String line = in.nextLine();
            switch(line){
                case "exit":
                    break console;
                case "test1":
                    Test1();
                    System.out.println("Enter command (exit/test[1-5]):");
                    break;
                case "test2":
                    Test2();
                    System.out.println("Enter command (exit/test[1-5]):");
                    break;
                case "test3":
                    Test3();
                    System.out.println("Enter command (exit/test[1-5]):");
                    break;
                case "test4":
                    Test4();
                    System.out.println("Enter command (exit/test[1-5]):");
                    break;
                case "test5":
                    Test5();
                    System.out.println("Enter command (exit/test[1-5]):");
                    break;
                default:
                    System.out.println("Wrong command, please enter (exit/test[1-5]):");
                    break;
            }
        }
        in.close();


        System.exit(0);



    }

    /**
     * Test case 1: 3 candidates, 3 ordinary, no delay
     * @throws NoSuchObjectException
     */
    public static void Test1() throws AlreadyBoundException, NotBoundException, IOException, NoSuchObjectException{
        Server server1 = new Server();
        // server1.candidateAmount=3;
        // server1.ordinaryAmount=3;
        server1.main(3,3,0);
        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*5);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // // deregister the registry
        // if (server1.registry != null) {
        //     UnicastRemoteObject.unexportObject(server1.registry, true);
        // } else{

        // }
    }


    /**
     * Test case 2: 3 candidates, 3 ordinaries, random delay
     * @throws IOException
     * @throws NotBoundException
     * @throws AlreadyBoundException
     */
    public static void Test2() throws AlreadyBoundException, NotBoundException, IOException{
        Server server2 = new Server();
        server2.main(3,3,1);
        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*5);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

        // // deregister the registry
        // if (registry != null) {
        //     UnicastRemoteObject.unexportObject(registry, true);
        // } else{

        // }
    }

    /**
     * Test case 3: 1 candidate, 100 ordinary, random delay
     * @throws IOException
     * @throws NotBoundException
     * @throws AlreadyBoundException
     */
    public static void Test3() throws AlreadyBoundException, NotBoundException, IOException{
        Server server3 = new Server();
        server3.main(1,100,1);
        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*5);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }

    }

    /**
     * Test case 4: 100 candidate, 100 ordinary, random delay
     * @throws IOException
     * @throws NotBoundException
     * @throws AlreadyBoundException
     */
    public static void Test4() throws AlreadyBoundException, NotBoundException, IOException{
        Server server4 = new Server();
        server4.main(100,100,1);
        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*5);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }
    }

    /**
     * Test case 5: as many as possible
     * @throws IOException
     * @throws NotBoundException
     * @throws AlreadyBoundException
     */
    public static void Test5() throws AlreadyBoundException, NotBoundException, IOException{
        Server server5 = new Server();
        server5.main(1000,0,1);
        // Sleep until all delays are finished to quit program.
        try {
            Thread.sleep(delay*5);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupt exception.");
            e.printStackTrace();
        }
    }


}
