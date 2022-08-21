import java.io.BufferedWriter;
import java.io.File;
//import org.apache.log4j.Logger;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

// The remote algorithm class DA-name implements the remote interface DA-name RMI, in
// which the actual work of a single process of the distributed algorithm is performed

public class DA_TMO extends UnicastRemoteObject implements DA_TMO_RMI, Runnable{
    /**
     * Index of a current process
     */
    private int index;
    public int test;
    private final Thread thread;
    /**
     * Port
     */
    private int port;

    /**
     * Number of processes participating in message exchange
     */
    private int numProcesses;

    /**
     * local clock
     */
    private List<Integer> clock;

    /**
     * pending (message queue): set of pairs <m, <d,j>>
     */
    //private final Map<Message, TimeStamp>  pending;

    /**
     * to_deliverable: sequence containing list of meassages
     * that have been received, totally ordered, and not yet to_delivered
     */
   // private final List<Message> to_deliverable;

    private static final Logger LOGGER = Logger.getLogger(DA_TMO_main.class.getName());

    private MessageQueue msg_q;

    String logFile;

    private Map<Integer,Integer> ackCountMap = new HashMap<>();
    
    /**
     * Default constructor following RMI conventions
     *
     * @param numProcesses number of participating processes
     * @param index        index of current process
     * @throws RemoteException if RMI mechanisms fail
     */
    public DA_TMO(int numProcesses, int index, int port) throws RemoteException {
        super();
        this.test = 0;
        this.msg_q = new MessageQueue();
        this.port = port;
        this.index = index;
        this.numProcesses = numProcesses;
        this.logFile = "process-"+index+".txt";
    
       // pending = new HashMap<>();
       // to_deliverable = new List<Message>();
        this.clock= new ArrayList<Integer>(numProcesses);
        for (int i=0; i <numProcesses;i++){
            clock.add(0);
        }

        // bind the process
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", port);
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

        //Create process log file
        try{
            File processlog = new File(logFile);
            if(processlog.delete()){
                processlog.createNewFile();
            }
        } catch (IOException e){
           e.printStackTrace();
        }

       // Runnable background = new BackGroundRunnable();
       // new Thread(background).start();
        thread = new Thread(this);

    }

    /**
     * Increment local clock upon event occurrence (send message).
     * @param clock - clock to update
     */
    private void incrementLocalClock(List<Integer> clock) {
        clock.set(index, clock.get(index) + 1);
    }

    /**
     * Update local clock when sender's clock>own clock
     * @param sd_date
     * @param index
     */
    private void updateLocalClock(int sd_date, List<Integer> clock){
        clock.set(index, sd_date+1);
    }

    /**
     * Update the clock of remote message/ACK sender
     * @param sd_date the latest time of message/ACK sender
     * @param index the index of message/ACK sender
     */
    private void updateRemoteClock(int sd_date,int index) {
        clock.set(index,sd_date);
    }

    /**
     * Delivery condition met if it's at the head of message queue
     * and has received ack from all processes
     * @param message
     * @return
     */
    private synchronized boolean deliveryCondition(Message message) {
        // LOGGER.info("num of "+message.getMessageId()+"ack count: " + message.getAckCount());
        //ackCountMap.get(message.getMessageId())
        return((ackCountMap.get(message.getMessageId())!= null) && (ackCountMap.get(message.getMessageId()) == numProcesses));
        // if ((ackCountMap.get(message.getMessageId())!= null) && (ackCountMap.get(message.getMessageId()) == numProcesses)){
        //     LOGGER.info(message.getMessageId()+"should be delivered");
        //     return true;
        // }
        // return false;
    }



    @Override
    public void run() {
        // try {
            
        // } catch (RemoteException e) {
        //     e.printStackTrace();
        // }

    }

    public void start() {
        thread.start();
    }
//
//
    public void test(Message message, int delay) throws RemoteException, InterruptedException {
        // TODO Auto-generated method stub
        LOGGER.info("Starting Runnable");
        System.out.println("in run method");
        // delay n ms
        try{
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted thread during delay simulation.");
            e.printStackTrace();
        }

        // for all processes except itself, receive message
        for(int i=0; i<numProcesses; i++){
            if(i == this.index){
                continue; //skip itself
            }
            sendMessage(i,message);
        }
    }

    @Override
    public void sendMessage(int receiverID, Message message) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(port);
        if(message ==null){
            return;
        }

        try
        {
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Sent message "+ message.getMessageId()+" to process "+receiverID + " time: "+message.getTimeStamp().getClock());
            bw.newLine();
            bw.close();
            DA_TMO_RMI receiver = (DA_TMO_RMI) registry.lookup("process-" + receiverID);
            receiver.receive(message);
            
        } catch (NotBoundException e) {
            LOGGER.severe("Unable to locate process " + receiverID);
            e.printStackTrace();
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            LOGGER.severe("Remote exception when sending message " + message);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Broadcast message to all processes
     * @param   msg_id id of the message
     * @param   content message content to send
     *
     */
    public void to_broadcast(int msg_id, String content, int delay) throws RemoteException {
        // TODO Auto-generated method stub

        // set message timestamp to clock, index
        TimeStamp msg_ts=new TimeStamp(this.clock.get(index),this.index);
        Message message = new Message(msg_id,msg_ts,content);


        Runnable r = new MyRunnable(message,delay);
        new Thread(r).start();
    }

    @Override
    public void to_deliver() throws RemoteException {            
        while(!msg_q.isEmpty() && deliveryCondition(msg_q.peek())){
            Message m = msg_q.peek();
            LOGGER.info("Message "+m.getMessageId()+" is delivered in process "+index);

            //Message newhead= msg_q.peek();

            try{
                FileWriter fw = new FileWriter(logFile, true);
                BufferedWriter bw = new BufferedWriter(fw);
                // bw.write("msg_head:"+m.getMessageId());

                bw.newLine();
                Message removed_m=pollMessage();

                if(removed_m == null){
                    bw.write("remove a null message\n");
                }
                else{
                    bw.write("not null\n");
                    bw.write("(!) Message "+removed_m.getMessageId()+" is delivered in process "+index+"\n");
                }
                // bw.write(String.valueOf(clock));
                // bw.write(String.valueOf(msg_q));

                bw.newLine();
                if (!msg_q.isEmpty()){
                    // bw.write("msg_q: "+String.valueOf(msg_q));
                    bw.write("msg_q head: message"+msg_q.peek().getMessageId());
                    bw.newLine();
                } else{
                    bw.write("msg_q empty after deliver");
                    bw.newLine();
                }
               // bw.write("msg_q: "+String.valueOf(msg_q));
                bw.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receive(Message message) throws RemoteException {
        // update the sender's and own clock, add message to message queue, then send ACK to all processes
        updateRemoteClock(message.getTimeStamp().getClock(),message.getTimeStamp().getIndex());
        updateLocalClock(message.getTimeStamp().getClock(), clock);
        addMessage(message);
        try{
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("Received message"+message.getMessageId()+" with "+ ackCountMap.get(message.getMessageId())+" acks.");
            bw.newLine();
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        acknowledge(message);
    }

	@Override
	public void acknowledge(Message msg) throws RemoteException {
		// broadcast acknowledgement to all processes
        Registry registry = LocateRegistry.getRegistry(port);

        for(int i=0;i<numProcesses;i++){
            try{
                DA_TMO_RMI stub = (DA_TMO_RMI) registry.lookup("process-" + i);
                TimeStamp ts = new TimeStamp(this.clock.get(index),index);
                stub.receiveAck(ts,msg.getMessageId(),index);

                try{
                    FileWriter fw = new FileWriter(logFile, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write("Sent ACK message "+msg.getMessageId()+" to process "+i+ " time: "+ts.getClock());
                    bw.newLine();
                    bw.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            } catch (NotBoundException e) {

                e.printStackTrace();
            }
        }
        // ACK: timestamp + msg id
		
	}

    @Override
    public void receiveAck(TimeStamp ts, int msgID, int sender) throws RemoteException {
        // update clock of ACK sender from timestamp
        updateRemoteClock(ts.getClock(),ts.getIndex());

        // increment message's ACK
       if (!ackCountMap.containsKey(msgID)){
           ackCountMap.put(msgID,1);
       } else{
           ackCountMap.put(msgID,ackCountMap.get(msgID)+1);
       }
       int ackcount= ackCountMap.get(msgID);
       
       try{
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            // ackCountMap.entrySet().forEach(entry -> {
            //     try {
            //         bw.write(entry.getKey() + " " + entry.getValue());
            //     } catch (IOException e) {
            //         e.printStackTrace();
            //     }
            // });

            bw.write("ACK for message "+msgID+ " received from process " + sender + ", total ACK: "+ ackcount);
            bw.newLine();
            if (msg_q.peek()!=null){
                // bw.write("msg_q: "+String.valueOf(msg_q));
                bw.write("msg_q head: message"+msg_q.peek().getMessageId());
                bw.newLine();
            } else{
                bw.write("msg_q empty");
                bw.newLine();
            }
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        to_deliver();
    }

    /**
     * Add message to message queue
     */
	public void addMessage(Message message) throws RemoteException{
		this.msg_q.add(message);
	}

    /**
     * Deletes head of message queue  
     * @return head of message queue
     */
    public Message pollMessage(){
        return this.msg_q.poll();
    }


    
    /**
     * Runnable for sending message
     */
    class MyRunnable implements Runnable {
        Message message;
        int delay;
        /**
         * Deals with broadcast delay
         */
        public MyRunnable(Message message,int delay) {
            this.message=message;
            this.delay=delay;
        }

        public void run() {
            try{
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                LOGGER.severe("Interrupted thread during delay simulation.");
                e.printStackTrace();
            }

            incrementLocalClock(clock);
            message.updateTimeStamp(clock.get(index));

            // add the message to its own message queue
            try {
                addMessage(message);
                acknowledge(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            for(int i=0; i<numProcesses; i++){
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
        }
    }
}

