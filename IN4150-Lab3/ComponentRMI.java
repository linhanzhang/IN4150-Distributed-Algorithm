import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ComponentRMI extends Remote {
	
	/**
	 * Set untraversed links for Candidate processes
	 * @param c
	 * @throws RemoteException
	 */
	public void setUntraversed(ComponentRMI[] c) throws RemoteException;
		
	public void receive(int lvl, int id, ComponentRMI c) throws RemoteException, InterruptedException;
	
	public void send(int lvl, int id, ComponentRMI c) throws RemoteException, InterruptedException;
}