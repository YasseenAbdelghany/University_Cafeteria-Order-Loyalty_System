package Services;

import Interfaces.IPaymentProcessor;

public class MobileWalletPaymentProcessor implements IPaymentProcessor {
    @Override
    public boolean processPayment(double amount) {
        if (amount <= 0) {
            return false;
        }

        // Simulate mobile wallet payment processing (e.g., Vodafone Cash, Orange Money)
        System.out.println("Processing mobile wallet payment of " + amount + " EGP");

        // Simulate network call to mobile payment provider
        try {
            Thread.sleep(150); // Simulate processing delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate 92% success rate for mobile wallet payments
        return Math.random() > 0.08;
    }
}
