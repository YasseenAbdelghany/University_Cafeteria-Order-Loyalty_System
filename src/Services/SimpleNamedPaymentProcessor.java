package Services;

import Interfaces.IPaymentProcessor;

public class SimpleNamedPaymentProcessor implements IPaymentProcessor {
    private final String name;

    public SimpleNamedPaymentProcessor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean processPayment(double amount) {
        if (amount <= 0) return false;
        System.out.println("Processing payment via '" + name + "' of " + amount + " EGP");
        return true; // naive success; replace with real integration as needed
    }
}
