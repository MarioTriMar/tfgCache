package org.tfg.subscriber;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.tfg.controller.Manager;


public class Receiver implements MessageListener {
    Logger logger = LoggerFactory.getLogger(Receiver.class);
    String port = Manager.get().getPort();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if(!message.toString().split("/")[1].equals(port))
            logger.info("Consumed event {}", message);
            Manager.get().getCacheEntries().evict("customers");
    }
}
