package ving;

public class Paid extends AbstractEvent {

    private Long id;
    private Double amout;
    private String status;
    private Long orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Double getAmout() {
        return amout;
    }

    public void setAmout(Double amout) {
        this.amout = amout;
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

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}