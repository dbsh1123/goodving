package ving;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MyPointRepository extends CrudRepository<MyPoint, Long> {

    Optional<MyPoint> findByPayId(Long payId);
}