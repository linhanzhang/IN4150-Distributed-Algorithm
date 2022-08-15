// package Lab2;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ComponentRMI extends Remote{
    /**
     * Upon receiving a message (types: message or marker)
     */
    public void receive(Message m) throws RemoteException;

    /**
     * Handles sending all types of messages - marker or normal
     * If marker, will be broadcasted
     * If normal, will be directed according to message.receiver
     */
    public void send(Message message, int delay) throws RemoteException;

    /**
     * Send message to other nodes
     * @param receiverID target node
     * @param message
     * @throws RemoteException
     */
   public void sendMessage(int receiverID, Message message) throws RemoteException;

    /**
     * Record local state
     */
    public void recordLocalState() throws RemoteException;

    //public void record();
}
