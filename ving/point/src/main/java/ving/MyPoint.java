package ving;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="MyPoint_table")
public class MyPoint {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long orderId;
        private Long payId;
        private String status;
        private Integer rate;
        private Integer amount;

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        public Long getPayId() {
            return payId;
        }
        public void setPayId(Long payId) {
            this.payId = payId;
        }

        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getRate() {
            return rate;
        }
        public void setRate(Integer rate) {
            this.rate = rate;
        }

        public Integer getAmount() {
            return amount;
        }
        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
}
