// package Lab2;
import java.io.Serializable;

import org.xml.sax.helpers.AttributesImpl;

public class Message implements Serializable{

    private int index;
    private MessageType type;
    private int sender;
    private int receiver;
    private int amount;



    // constructor for normal message
    public Message(int sender, MessageType type, int index, int amount, int receiver)  {
        this.sender = sender;
        this.type = type;
        this.index = index;
        this.amount= amount;
        this.receiver=receiver;
    }

    // Constructor for Marker, which has no index
    public Message(int sender, MessageType type)  {
        this.sender = sender;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public MessageType getType() {
        return type;
    }

    public int getSender() {
        return sender;
    }

    public int getReceiver(){
        return receiver;
    }

    public int getAmount(){
        return amount;
    }


    public String toString(){
        return Integer.toString(this.amount);
    }

}
