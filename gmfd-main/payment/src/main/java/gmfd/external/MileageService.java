
package gmfd.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="mileage", url="http://localhost:8087")//"http://mileage:8080")
public interface MileageService {

    @RequestMapping(method= RequestMethod.GET, path="/mileages")
    public void mileage(@RequestBody Mileage mileage);

}