package gmfd;

import gmfd.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MileageViewViewHandler {


    @Autowired
    private MileageViewRepository mileageViewRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaid_then_CREATE_1 (@Payload Paid paid) {
        try {
            if (paid.isMe()) {
                // view 객체 생성
                MileageView mileageView  = new MileageView();
                // view 객체에 이벤트의 Value 를 set 함
                mileageView.setOrderId(paid.getOrderid());
                mileageView.setPayId(paid.getId());
                mileageView.setCnt((int)Math.round(paid.getAmout()));
                // view 레파지 토리에 save
                mileageViewRepository.save(mileageView);
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
                MileageView mileageView = mileageViewRepository.findByPayId(cancelled.getId()).get();

                mileageViewRepository.delete(mileageView);


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}