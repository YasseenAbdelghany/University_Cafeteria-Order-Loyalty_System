package Services;

import Interfaces.IPaymentProcessor;

public class CreditCardPaymentProcessor implements IPaymentProcessor {
    @Override
    public boolean processPayment(double amount) {
        if (amount <= 0) {
            return false;
        }

        // Simulate credit card payment processing
        System.out.println("Processing credit card payment of " + amount + " EGP");

        // Simulate network call to payment gateway
        try {
            Thread.sleep(100); // Simulate processing delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate 95% success rate for credit card payments
        return Math.random() > 0.05;
    }
}
