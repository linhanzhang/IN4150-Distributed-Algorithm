import java.io.Serializable;

public class TimeStamp implements Serializable {
    int clock;
    int pid;

    public TimeStamp(int clock,int pid){
        this.clock = clock;
        this.pid = pid ;
    }

    public int getClock(){
        return clock;
    }

    public int getIndex(){
        return pid;
    }

    public void setClock(int clock) {
        this.clock=clock;
    }

    public boolean smallerTo(TimeStamp timeStamp){
        if (timeStamp == null)
            return true;

        if (this.clock< timeStamp.clock)
            return true;
        else if (this.clock == timeStamp.clock && this.pid< timeStamp.pid){
                return true;
        }
        return false;
    }
}
