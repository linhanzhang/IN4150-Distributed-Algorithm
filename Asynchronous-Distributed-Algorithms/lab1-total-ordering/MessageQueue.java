import java.io.Serializable;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

public class MessageQueue implements Serializable {
    public PriorityBlockingQueue<Message> message_queue;

    public MessageQueue(){
        message_queue = new PriorityBlockingQueue<>(50, new MessageComparator());
    }

    public Message peek(){
		return message_queue.peek();
	}

    public Message poll(){
        return message_queue.poll();
    }

    public void add(Message msg){
        message_queue.add(msg);
    }

    public boolean isEmpty(){
        return message_queue.isEmpty();
    }

    public Message getById(int msgId){
        Iterator iterator = message_queue.iterator();
        while (iterator.hasNext()){
            Message m = (Message) iterator.next();
            if(m.getMessageId() == msgId){
                return m;
            }
        }
        return null;
    }

    public void setById(int msgId){
       Message m = getById(msgId);
       if(m != null){
           m.ackCountAddOne();
       }
       else{

       }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        Iterator iterator = this.message_queue.iterator();
        while(iterator.hasNext()){
            Message m = (Message) iterator.next();
            sb.append(" [message "+m.getMessageId()+" time:"+m.getTimeStamp().getClock()+"] ");
        }
    
        return sb.toString();
    }

    public class MessageComparator implements Comparator<Message>{
        public int compare(Message m1, Message m2) {
            if(m1.getTimeStamp().smallerTo(m2.getTimeStamp())){
                return -1;
            }
            else{
                return 1;
            }
        };
    }

    

}

