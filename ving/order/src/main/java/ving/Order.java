package ving;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer qty;
    private String status;
    private Long foodcaltalogid;
    private Long customerid;

    @PostPersist
    public void onPostPersist() throws InterruptedException {

        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);

        ordered.publishAfterCommit();

        ving.external.Pay pay = new ving.external.Pay();

        pay.setOrderId(this.getId());
        pay.setStatus("Ordered Success!");

        System.out.println("이곳이냐??!!!! Order.java   ------------------------ " + this.getQty() + " -------- ");
        pay.setAmount(this.getQty());
        // mappings goes here
        OrderApplication.applicationContext.getBean(ving.external.PayService.class)
            .pay(pay);
    }

    @PostUpdate
    public void onPostUpdate(){

    }

    @PreRemove
    public void onPreRemove(){
        OrderCancelled orderCancelled = new OrderCancelled();
        BeanUtils.copyProperties(this, orderCancelled);

        orderCancelled.publishAfterCommit();

        ving.external.Cancellation cancellation = new ving.external.Cancellation();
        // mappings goes here
        cancellation.setOrderId(this.getId());
        cancellation.setStatus("Order Cancelled!");

        OrderApplication.applicationContext.getBean(ving.external.CancellationService.class)
            .cancelShip(cancellation);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getFoodcaltalogid() {
        return foodcaltalogid;
    }

    public void setFoodcaltalogid(Long foodcaltalogid) {
        this.foodcaltalogid = foodcaltalogid;
    }
    public Long getCustomerid() {
        return customerid;
    }

    public void setCustomerid(Long customerid) {
        this.customerid = customerid;
    }




}
