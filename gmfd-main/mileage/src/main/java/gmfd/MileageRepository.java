package gmfd;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface MileageRepository extends PagingAndSortingRepository<Mileage, Long>{

    void deleteByPayId(Long payId);

    Optional<Mileage> findByPayId(Long payId);
}