package network.api;

import java.io.Serializable;

public class Message implements Serializable {

    private int token;
    private MessageType type;
    private Object data;
    private Object data2;

    public Message(MessageType type, Object data, Object data2) {
        this.type = type;
        this.data = data;
        this.data2 = data2;
    }

    public Message(MessageType type, Object data) {
        this(type, data, false);
    }

    public Message(MessageType type) {
        this(type, null, false);
    }
    
    public void setToken(int authToken) {
        this.token = authToken;
    }

    public int getToken() {
        return token;
    }

    public MessageType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public Object getData2() {
        return data2;
    }

    @Override
    public String toString() {
        return "Message{" +
                "token=" + token +
                ", type=" + type +
                ", data=" + ((data != null) ? data.toString() : "null")+
                '}';
    }
}
