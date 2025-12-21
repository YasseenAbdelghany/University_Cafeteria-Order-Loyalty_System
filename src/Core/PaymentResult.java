package Core;

public class PaymentResult {
    private boolean success;
    private String txId;

    public PaymentResult() {}

    public PaymentResult(boolean success, String txId) {
        this.success = success;
        this.txId = txId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    @Override
    public String toString() {
        return "PaymentResult{success=" + success + ", txId='" + txId + "'}";
    }
}
