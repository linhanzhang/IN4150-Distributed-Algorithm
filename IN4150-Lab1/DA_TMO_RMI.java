
import java.rmi.Remote;
import java.rmi.RemoteException;

//  Remote interface that defines the methods that can be called remotely 
public interface DA_TMO_RMI extends Remote{
    /**
     * Broadcast message to all processes
     * @param message message to send
     * @throws RemoteException
     */
    public void to_broadcast(int msg_id, String content, int delay) throws RemoteException;
    
    /**
     * Send an message to a single process
     */
    public void sendMessage(int receiverID, Message message) throws RemoteException;

    /**
     * Deliver a received message
     * @param message message 
     * @throws RemoteException
     */
    public void to_deliver() throws RemoteException;

    /**
     * Receives message from a (remote) process.
     * @param message transmitted message
     * @throws RemoteException
     */
    public void receive(Message message) throws RemoteException;
    

    /**
     * Receive acknowledgement from a (remote) process.
     * @param message
     * @param origin
     */
    public void receiveAck(TimeStamp ts, int msgID, int sender) throws RemoteException;

    /**
     * Broadcast an acknowledgement (CATCH_UP) when message is received
     */
    public void acknowledge(Message msg) throws RemoteException;

    public void addMessage(Message msg) throws RemoteException;
}