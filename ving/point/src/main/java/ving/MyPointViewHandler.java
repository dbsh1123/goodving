package ving;

import ving.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MyPointViewHandler {

    @Autowired
    private MyPointRepository myPointRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaid_then_CREATE_1 (@Payload Paid paid) {
        try {
            if (paid.isMe()) {

                // view 객체 생성
                MyPoint mypoint = new MyPoint();
                // view 객체에 이벤트의 Value 를 set 함
                mypoint.setOrderId(paid.getOrderId());
                mypoint.setPayId(paid.getId());
                // System.out.println(paid.getOrderId() + " ----- " + paid.getId() + " -----이거 오류지??????--------------------------     " + paid.getAmount());

                int temp = (int)Math.round(paid.getAmount());
                mypoint.setAmount(temp);

                // 5개이상 주문시 20% 그외 10%
                if(temp > 5)
                    mypoint.setRate(20);
                else
                    mypoint.setRate(10);

                mypoint.setStatus("적립 성공");
                myPointRepository.save(mypoint);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @StreamListener(KafkaProcessor.INPUT)
    public void whenCancelled_then_DELETE_1(@Payload Cancelled cancelled) {
        try {
            if (cancelled.isMe()) {
                // view 레파지 토리에 삭제 쿼리
                MyPoint mypoint = myPointRepository.findByPayId(cancelled.getId()).get();
                myPointRepository.delete(mypoint);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}