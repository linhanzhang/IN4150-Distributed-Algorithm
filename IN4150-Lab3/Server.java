import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server {
	
	public Registry registry;
	public int candidateAmount;
	public int ordinaryAmount;
	private int Port = 1099;
	public ComponentRMI[] componentRMIs;
    public ComponentRMI[] ordinary;
	
	public int killed;
	public int final_killed;
	public int ignored;
	public int final_ignored;
	public int acknowledged;
	public int final_ack;
	public int captured;
	public int final_captured;
	public int kill_denied;
	public int final_kill_denied;
	public int sent;
	public int final_sent;
	public boolean elected;
	public int delay;
	
	public void main(int cand, int ord, int delay) throws AlreadyBoundException, NotBoundException, IOException{
		registry = LocateRegistry.createRegistry(Port);
		candidateAmount = cand;
		ordinaryAmount = ord;
		
		
		ArrayList<ComponentRMI> components = new ArrayList<ComponentRMI>();
        for(int i=0; i<(ordinaryAmount); i++){
			Ordinary new_component = new Ordinary(i, this);
			new_component.delay=delay;
			components.add(new_component);
			registry.bind("OC"+(i+1), new_component );
		}	
		for(int i=0; i<candidateAmount; i++){
            int candidate_id=i+ordinaryAmount;
			Candidate new_component = new Candidate(candidate_id, this);
			new_component.delay=delay;
			components.add(new_component);
			registry.bind("CC"+(candidate_id+1), new_component );
            // Ordinary new_component1 = new Ordinary(candidate_id,this);
            // components.add(new_component1);
			// registry.bind("OC"+(candidate_id+1), new_component1);
		}
		setRegistry();
		
	}
	
	public void setRegistry() throws RemoteException, NotBoundException{
		componentRMIs = new ComponentRMI[registry.list().length];
        ordinary =new ComponentRMI[ordinaryAmount];
		for(int i=0; i<registry.list().length; i++){
			componentRMIs[i] = (ComponentRMI) registry.lookup(registry.list()[i]);
		}
        for(int i=0; i<ordinaryAmount; i++){
			ordinary[i] = (ComponentRMI) registry.lookup("OC"+(i+1));
		}
        
		for(int i=0; i<componentRMIs.length; i++){
			ComponentRMI RMI_ID_SELECTED = componentRMIs[i];
			new Thread ( () -> {					
				try {
					RMI_ID_SELECTED.setUntraversed(componentRMIs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
	}
	
	public void Elected() throws RemoteException, InterruptedException{
		if(!elected){
			elected = true;
			final_ack = acknowledged;
			final_ignored = ignored;
			final_kill_denied = kill_denied;
			final_captured = captured;
			final_killed = killed;
			final_sent = sent;
			Thread.sleep(3000);

			System.out.println("Ack: " + final_ack);
			System.out.println("Captures: " + final_captured);
			System.out.println("Successful kills: " + final_killed);
			System.out.println("Total kill messages: "+ (final_killed+final_kill_denied));

			// deregister registry
			if (this.registry != null) {
				UnicastRemoteObject.unexportObject(this.registry, true);
			} else{
	
			}
		}
		
	}
}
