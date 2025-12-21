package Core;

public class Payment {
    private String paymentID;
    private double amount;
    private String status;
    private Interfaces.IPaymentProcessor processor;

    public Payment() {
        this.status = "PENDING";
    }

    public Payment(String paymentID, double amount, Interfaces.IPaymentProcessor processor) {
        this.paymentID = paymentID;
        this.amount = amount;
        this.processor = processor;
        this.status = "PENDING";
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Interfaces.IPaymentProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(Interfaces.IPaymentProcessor processor) {
        this.processor = processor;
    }

    public boolean initiatePayment() {
        if (processor != null) {
            boolean result = processor.processPayment(amount);
            this.status = result ? "SUCCESS" : "FAILED";
            return result;
        }
        return false;
    }

    public boolean confirmPayment() {
        if ("SUCCESS".equals(status)) {
            this.status = "CONFIRMED";
            return true;
        }
        return false;
    }

    public boolean refund() {
        if ("CONFIRMED".equals(status)) {
            this.status = "REFUNDED";
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID='" + paymentID + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
