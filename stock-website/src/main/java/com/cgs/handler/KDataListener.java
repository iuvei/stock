package com.cgs.handler;

import com.cgs.entity.graphs.KEntity;
import com.cgs.quotes.message.QuotesMessage;
import com.cgs.websocket.QuotesHandler;
import com.cgs.websocket.entity.WebSocketSessionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketMessage;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Map;

public class KDataListener implements MessageListener {

    @Autowired
    private QuotesHandler quotesHandler;

    @Override
    public void onMessage(Message message) {
        Map<String, WebSocketSessionEntity> sessionMap = quotesHandler.getSessionMap();
        try {
            QuotesMessage.KResponse response = parseKData((TextMessage) message);
            WebSocketMessage webSocketMessage = new BinaryMessage(response.toByteArray());
            for (String key : sessionMap.keySet()) {
                sessionMap.get(key).send(webSocketMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QuotesMessage.KResponse parseKData(TextMessage message) throws JMSException {
        QuotesMessage.KResponse.Builder builder = QuotesMessage.KResponse.newBuilder();
        String content = message.getText();
        KEntity entity = new KEntity();
        entity = entity.parseFromMessage(content);
        QuotesMessage.KItem.Builder itemBuilder = QuotesMessage.KItem.newBuilder();
        itemBuilder.setClose(entity.getClose());
        itemBuilder.setHigh(entity.getHigh());
        itemBuilder.setLow(entity.getLow());
        itemBuilder.setOpen(entity.getOpen());
        itemBuilder.setTimestamp(entity.getTimestamp());

        builder.setPeriodValue(entity.getPeriod().ordinal());
        builder.setStockId(entity.getStockId());
        builder.addItems(itemBuilder.build());
        return builder.build();
    }
}
