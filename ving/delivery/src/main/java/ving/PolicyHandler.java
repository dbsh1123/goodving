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
    DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_Ship(@Payload Paid paid){

        if(paid.isMe()){
            System.out.println("Delivery의 PolicyHandler부분이다!!!!!!!!  : " + paid.toJson());
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_UpdateStatus(@Payload Paid paid){

        if(paid.isMe()){

            System.out.println("##### listener Ship : " + paid.toJson());
            Delivery delivery = new Delivery();

            delivery.setOrderId(paid.getOrderId());
            delivery.setStatus(paid.getStatus());

            deliveryRepository.save(delivery);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void onOrderCancelled(@Payload OrderCancelled orderCancelled){

        if(orderCancelled.isMe()){

            System.out.println("##### listener Cancelled : " + orderCancelled.toJson());
            Optional<Delivery> optionalDelivery = deliveryRepository.findById(orderCancelled.getId());

            Delivery delivery = optionalDelivery.get();
            delivery.setStatus("Order Canceled-delivery");

            deliveryRepository.save(delivery);
        }
    }
}
