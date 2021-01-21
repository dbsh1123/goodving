package ving;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PointRepository extends PagingAndSortingRepository<Point, Long>{

    void deleteByPayId(Long payId);
    Optional<Point> findByPayId(Long payId);
}