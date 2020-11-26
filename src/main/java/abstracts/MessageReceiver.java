package abstracts;

import network.api.Message;

public interface MessageReceiver {
    void receive(Message message);
}
