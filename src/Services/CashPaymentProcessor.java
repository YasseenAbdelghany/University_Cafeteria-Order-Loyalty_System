package Services;

import Interfaces.IPaymentProcessor;

public class CashPaymentProcessor implements IPaymentProcessor {
    @Override
    public boolean processPayment(double amount) {
        if (amount <= 0) {
            return false;
        }

        // Simulate cash payment processing
        System.out.println("Processing cash payment of " + amount + " EGP");

        // Cash payments are always successful (assuming correct change is available)
        return true;
    }
}
