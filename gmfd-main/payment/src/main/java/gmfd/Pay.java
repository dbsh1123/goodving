package gmfd;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Entity
@Table(name="Pay_table")
public class Pay {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer amout;
    private String status;
    private Long orderid;

    @PostPersist
    public void onPostPersist(){

        Paid paid = new Paid();
        BeanUtils.copyProperties(this, paid);
        paid.publish();


        gmfd.external.Mileage mileage = new gmfd.external.Mileage();
        // mappings goes here
        mileage.setPayId(this.getId());
        mileage.setOrderId(this.getOrderid());
        mileage.setCnt(this.getAmout());

        PaymentApplication.applicationContext.getBean(gmfd.external.MileageService.class).mileage(mileage);

       /* TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void beforeCommit(boolean readonly ) {
            }
        });


        try {
            Thread.currentThread().sleep((long) (500 + Math.random() * 220));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }

    @PreRemove
    public void onPreRemove(){
        Cancelled cancelled = new Cancelled();
        BeanUtils.copyProperties(this, cancelled);
        cancelled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getAmout() {
        return amout;
    }

    public void setAmout(Integer amout) {
        this.amout = amout;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }




}
