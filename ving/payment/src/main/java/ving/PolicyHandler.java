package ving;

import ving.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){
    }
    //Bean 간 연결
    @Autowired
    PayRepository payrepo;
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryCanceled_Cancelled(@Payload DeliveryCanceled deliveryCanceled){

        if(deliveryCanceled.isMe()){
            System.out.println("##### listener  : " + deliveryCanceled.toJson());

            Optional<Pay> optpay = payrepo.findById(deliveryCanceled.getOrderId());
            Pay pay = optpay.get();

            pay.setStatus("order cancelled...");
            payrepo.save(pay);

            Cancelled cancelled = new Cancelled();
            cancelled.setAmout(pay.getAmount());
            cancelled.setOrderid(pay.getId());
            cancelled.setStatus(pay.getStatus());
            cancelled.publish();
        }
    }
}
