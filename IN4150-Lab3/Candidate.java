import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Candidate extends UnicastRemoteObject implements ComponentRMI{

    /**
     * original id
     */
    int index;

    /**
     * number of captured nodes
     */
    private int level;

    /**
     * boolean indicating having been killed (initialized false)
     */
    boolean killed;

    /**
     * set of  unused links (initial: all links)
     */
    public ArrayList<ComponentRMI> untraversed;

    public boolean elected;

    public Server server;

    public int delay;



    public Candidate(int index, Server s) throws RemoteException{
        this.index=index;
        this.level=0;
        this.killed=false;
        this.server=s;
        this.untraversed=null;
        this.elected=false;
    }

    @Override
    public void setUntraversed(ComponentRMI[] c) throws RemoteException {
        this.untraversed = new ArrayList<ComponentRMI>(Arrays.asList(c));
		this.untraversed.remove(this);
		System.out.println("Candidate process " + this.index + " has been initialized.");
		if(!this.killed) {
			try {
                sendNext();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
    }

    @Override
    public void receive(int lvl, int id, ComponentRMI c) throws RemoteException, InterruptedException {
        System.out.println("Process " + this.index+ " Level " + this.level + " has received message: (Level:" + lvl + ", ID:" + id+")");
        if ((id==this.index) && !this.killed){ // own id: ack
            System.out.println("Process "+this.index+" has received an ACK");
            server.acknowledged++;
            this.level++;
            untraversed.remove(c);
            sendNext();
        } else if ((lvl<this.level || (lvl==this.level && id<=this.index))){ //ignore
            System.out.println("Process "+this.index+" has ignored a kill message from process "+id);
        } else { // bigger, successful kill attempt
            if (!this.killed){
                this.killed=true;
                System.out.println("Process "+this.index+" is killed by process "+id);
                server.killed++;
            } else{
                System.out.println("Process "+this.index+" was already killed");
            }
            
            send(lvl, id, c);
        }
        
    }

    @Override
    public void send(int level, int id, ComponentRMI c) throws RemoteException, InterruptedException {
        if (this.delay==1){
            Thread.sleep(randomDelay());
        }
        c.receive(level, id, this );
    }

    public void sendNext() throws RemoteException, InterruptedException{
        if (!untraversed.isEmpty()){
            // select and send to next link
            ComponentRMI next = untraversed.get(new Random().nextInt(untraversed.size()));
            send(this.level, this.index, next);
            server.sent++;
        } else{
            if (!this.killed){
                this.elected = true;
                System.out.println("Process "+ this.index + " with level " +this.level + " is elected.");
                server.Elected();
            }
        }
    }




    /**
     * Gives value for random delay [0.5,2]s
     * @return random value
     */
    public static int randomDelay() {
        return (new Random().nextInt(1500)+500);
    }

    
}
