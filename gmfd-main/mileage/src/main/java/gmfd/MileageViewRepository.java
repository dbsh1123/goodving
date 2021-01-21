package gmfd;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MileageViewRepository extends CrudRepository<MileageView, Long> {


        //void deleteByOrderId(Long orderId);
        //void deleteByPayId(Long payId);
        Optional<MileageView> findByPayId(Long payId);


}