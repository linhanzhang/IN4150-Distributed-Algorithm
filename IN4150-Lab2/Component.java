// package Lab2;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

//import org.apache.log4j.Logger;

public class Component extends UnicastRemoteObject implements ComponentRMI, Runnable {

    // The state of incoming channels, each channel has a message queue
    private LinkedList[] channels;

    // Total number of markers that has been received yet
    private int numMarkers;

    // Total number of components
    private int numComponents;

    // The id of this component
    private int index;

    // The local state of this component
    private int state;

    // The boolean localStateRecorded indicates whether the local state of this component has been recorded yet
    // which means the component has been participated in the global state recording
    private boolean localStateRecorded;

    // Boolean that indicate the end of recording
    private boolean recordingDone;

    // Map that maintain the index of last message sent to this component
    private Map<Integer,Integer> receiveMap = new HashMap<>();

    // Map that maintain the index of last message sent from this component
    private Map<Integer,Integer> sendMap = new HashMap<>();

    private Registry registry;
    
    private int port;


    private static final Logger LOGGER = Logger.getLogger(DA_CL_Main.class.getName());

    public Component(int port, int index, int numComponents) throws RemoteException {

        channels = new LinkedList[numComponents];
        this.index = index;
        for(int i = 0; i<numComponents; i++) {
            channels[i] = new LinkedList<>();
        }

        // initialize state
        this.state=100;

        this.localStateRecorded = false;
        this.numComponents = numComponents;
        this.numMarkers = 0;
        recordingDone = false;

        for(int i=0; i<numComponents; i++){
            if(i == index)
                continue;
            receiveMap.put(i,-1);
            sendMap.put(i,-1);
        }


        // bind the process
        try {
            registry = LocateRegistry.getRegistry("localhost", port);
            //LOGGER.info("Binding process " + index + " to port " + port);
            registry.bind("process-" + index, this);
        } catch (RemoteException e) {
            LOGGER.severe("Remote exception when binding process " + index);
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            System.out.println(index + "already bound to registry on port " + port);
            // LOGGER.severe(index + "already bound to registry on port " + port);
            e.printStackTrace();
        }

    }

    @Override
    public void receive(Message m) throws RemoteException{
        int sender = m.getSender();
        switch (m.getType()){
            case MESSAGE:
                if(localStateRecorded == true){
                    channels[sender].add(m);
                } else{
                    state = state+m.getAmount();
                }
            case MARKER:
                numMarkers ++;
                if(!localStateRecorded){
                    channels[sender] = new LinkedList<Message>();
                    record_and_send();
                    // localStateRecorded = true;
                    // broadcast marker
                    // Message marker = new Message(this.index, MessageType.MARKER);
                    // try{
                    //     send(marker,1000);
                    // } catch (RemoteException e){
                    //     e.printStackTrace();
                    // }
                }
                if(numMarkers == numComponents-1) {
                    System.out.println("C"+index+" received all markers, terminating.");
                    recordingDone = true;
                    // //reset
                    // numMarkers = 0;
                    // localStateRecorded = false;
                }
        }

    }

    // sending marker rules
    public void record_and_send() throws RemoteException{
        localStateRecorded=true;
        Message marker = new Message(this.index, MessageType.MARKER);
        send(marker, 1000);
    }

    @Override
    public void send(Message message, int delay) throws RemoteException{
        // thread to handle delays
        Runnable r = new MyRunnable(message,delay);
        new Thread(r).start();
    }

   @Override
     public void sendMessage(int receiverID, Message message) throws RemoteException{

        Registry registry = LocateRegistry.getRegistry(port);
        if(message ==null){
            return;
        }
        try{
            ComponentRMI receiver = (ComponentRMI) registry.lookup("process-" + receiverID);
            receiver.receive(message);
        } catch (NotBoundException e) {
            LOGGER.severe("Unable to locate process " + receiverID);
            e.printStackTrace();
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            LOGGER.severe("Remote exception when sending message " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void recordLocalState() throws RemoteException{
        
        localStateRecorded = true;
    }

    /**
     * Start global state recording when it is requested to
     */
    public void recordGlobalState(int delay) {

        // broadcast a marker after delay
        try{
            send(new Message(index,MessageType.MARKER), delay);
        } catch (RemoteException e){
            e.printStackTrace();
        }

    }

    /**
     * Start global state recording when it is requested to
     */
    public void printGlobalState() {  // pull result
        // set flag
        if (recordingDone == false) {
            try {
                throw new Exception("Please wait, component "+index+" not ready because only "+numMarkers+" markers have been returned");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // print out results
            System.out.println("Printing global state:");
            for (int i=0; i<numComponents; i++){
                
            }
            // reset states
            numMarkers = 0;
            recordingDone = false;
            LinkedList<Message>[] toReturn = channels;
            channels = new LinkedList[numComponents];
            for (int i = 0; i < numComponents; i++) {
                // Reset the state
                channels[i] = new LinkedList<>();
            }

            // to print the

        }
    }

    public void printState(){
        System.out.println("P"+index+": "+state);
        for (int i=0; i<numComponents;i++){
            if (i==index){
                continue;
            }else{
                System.out.println("Channel P"+i+" -> P"+index+": "+channels[i].toString());
            }
        }
    }

    /**
     * update the map
     * @param destOrSender
     * @param messageIndex
     * @param type (0 for message received, 1 for message sent)
     */
    public void updateMap(int destOrSender, int messageIndex, int type){

    }


    /**
     * print out the map in a nice format
     */
    public void printMap(){

    }

    @Override
    public void run() {

    }


    /**
     * Runnable for sending message
     */
    class MyRunnable implements Runnable {
        Message message;
        int delay;

        /**
         * Deals with delay
         */
        public MyRunnable(Message message,int delay) {
            this.message=message;
            this.delay=delay;
        }

        public void run() {
            
            if (message.getType()==MessageType.MARKER){
                // message is a marker
                localStateRecorded=true;
                // Broadcast marker to all components (outgoing channel)
                LOGGER.info("C" + index + " broadcasting "+message.getType());
                for(int i=0; i<numComponents; i++){
                    if(i ==index){
                        continue; //skip itself
                    }
                    try {
                        sendMessage(i,message);
                    }
                    catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } else if (message.getType()==MessageType.MESSAGE){
                if (!localStateRecorded){
                    state=state - message.getAmount();
                }
                // message is a normal message, directed to a particular receiver
                try{
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    LOGGER.severe("Interrupted thread during delay simulation.");
                    e.printStackTrace();
                }
                int receiverID=message.getReceiver();
                try{
                    sendMessage(receiverID, message);
                    LOGGER.info("C"+index+" sent "+message.getAmount()+" to C"+receiverID);
                } catch (RemoteException e){
                    e.printStackTrace();
                }
            } else{
                LOGGER.severe("Message type not found.");
            }

            
        }
    }
}


/*
    questions:
    1. what is called this algorithm is started by a designated process? does it mean only this process need
    to record the global state? others does not have to output their channel state?
    2. what is the purpose for maintainting 2(n-1) array?

    3. why need to record local state?
    4. why stop parpicipate when all markers received? why not stop channel recording one by one?
    5. pre-recording before post-recording
 */