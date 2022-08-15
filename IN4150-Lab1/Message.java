import java.io.Serializable;
import java.sql.Time;

public class Message implements Serializable {

    //private long timestamp;
    private int id;
    private TimeStamp ts;
    private String content;
    private int ack;

    public Message(int id, TimeStamp ts,String content) {
        this.id = id;
        this.ts = ts;
        this.content = content;
        ack = 0;
    }

    public int getMessageId(){
        return id;
    }

    public TimeStamp getTimeStamp(){
        return ts;
    }

    public void ackCountAddOne(){
        ack ++;
    }

    public int getAckCount(){
        return ack;
    }

    public void updateTimeStamp(int clock) {
        this.ts.setClock(clock);
    }

    
}


