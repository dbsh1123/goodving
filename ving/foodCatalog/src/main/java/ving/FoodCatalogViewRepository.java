package ving;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodCatalogViewRepository extends CrudRepository<FoodCatalogView, Long> {

    List<FoodCatalogView> findByfoodcatalogid(Long id);
}