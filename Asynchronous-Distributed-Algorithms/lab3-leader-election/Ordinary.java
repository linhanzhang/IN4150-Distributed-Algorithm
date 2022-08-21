import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Ordinary extends UnicastRemoteObject implements ComponentRMI{

    private int index;

    /**
     * number of captured nodes
     */
    private int level;
    
    /**
     * id of current owner
     */
    private int owner_id;

    /**
     * link to current owner
     */
    private ComponentRMI father;

     /** 
      * link to potential future owner
      */
    private ComponentRMI potential_father;

    public Server server;

    public int delay;

    public Ordinary(int index, Server s) throws RemoteException{
        this.index=index;
        this.level=-1;
        this.owner_id=-1;
        this.father=null;
        this.potential_father= null;
        this.server=s;
        System.out.println("Ordinary process "+this.index+" has been initialized.");
    }

    @Override
    public void receive(int lvl, int id, ComponentRMI c) throws RemoteException, InterruptedException {
        if ((lvl<this.level) || (lvl == this.level && id<this.owner_id)){ 
            // ignore capture or OK
            server.kill_denied++;
        } else if ((lvl>this.level) || (lvl==this.level && id>this.owner_id)){ // new better candidate
            this.potential_father=c;
            this.level=lvl;
            this.owner_id=id;
            if (this.father==null){
                father = potential_father;
                server.captured++;
                System.out.println("Process "+this.index+" has been captured by process "+this.owner_id);
            }
            send(lvl, id, father);
        } else if (lvl==this.level && id == this.owner_id){ // ok from previous father
            server.captured++;
            this.father=potential_father;
            send(lvl,id,father); 
            System.out.println("Process "+this.index+" has been captured by process "+this.owner_id);
        }
    }

    @Override
    public void send(int level, int id, ComponentRMI c) throws RemoteException, InterruptedException {
        if (this.delay==1){
            Thread.sleep(randomDelay());
        }
        c.receive(level, id, this);
}

	@Override
	public void setUntraversed(ComponentRMI[] c) throws RemoteException {
		// unused
	}
    
    /**
     * Gives value for random delay [0.5,3]s
     * @return random value
     */
    public static int randomDelay() {
        return (new Random().nextInt(2500)+500);
    }

}
