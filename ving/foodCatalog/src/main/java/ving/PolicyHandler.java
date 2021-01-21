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
    FoodCatalogRepository foodCatalogRepository;
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_(@Payload Ordered ordered){

        if(ordered.isMe()){

            Optional<FoodCatalog> optionalFoodCatalog = foodCatalogRepository.findById(ordered.getFoodcaltalogid());
            FoodCatalog foodCatalog = optionalFoodCatalog.get();
            foodCatalog.setStock(foodCatalog.getStock() - ordered.getQty());

            foodCatalogRepository.save(foodCatalog);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_(@Payload OrderCancelled orderCancelled){

        if(orderCancelled.isMe()){

            Optional<FoodCatalog> optionalFoodCatalog = foodCatalogRepository.findById(orderCancelled.getFoodcaltalogid());

            FoodCatalog foodCatalog = optionalFoodCatalog.get();
            foodCatalog.setStock(foodCatalog.getStock() + orderCancelled.getQty());
            foodCatalogRepository.save(foodCatalog);
        }
    }
}
