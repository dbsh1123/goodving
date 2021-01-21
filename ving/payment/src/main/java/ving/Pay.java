package ving;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Pay_table")
public class Pay {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer amount;
    private String status;
    private Long orderId;

    @PostPersist
    public void onPostPersist(){
        Paid paid = new Paid();
        BeanUtils.copyProperties(this, paid);

        paid.publish();

        // System.out.println("테스트 해봅시다!!!!!!!!!!!!!!!!!!! ------------ " + paid.getAmount() + " --- " + paid.getStatus() + " --- " + paid.getOrderId() + " ---------------------------");
        ving.external.Point point = new ving.external.Point();

        point.setPayId(this.getId());
        point.setOrderId(this.getOrderId());
        point.setAmount(this.getAmount());

        System.out.println("Payment의 Pay 이건 어디쯤 발생하는 이벤트일까???~~~~~~~  " + this.getAmount());
        // mappings goes here
        PaymentApplication.applicationContext.getBean(ving.external.PointService.class)
            .add(point);
    }

    @PreRemove
    public void onPreRemove(){
        Cancelled cancelled = new Cancelled();
        BeanUtils.copyProperties(this, cancelled);
        cancelled.publishAfterCommit();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amout) {
        this.amount = amout;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderid) {
        this.orderId = orderid;
    }




}
