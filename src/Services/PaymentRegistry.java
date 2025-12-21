package Services;

import Interfaces.IPaymentProcessor;

import java.util.*;

/**
 * Registry of available payment methods (Open/Closed via Strategy pattern).
 * New payment processors implement IPaymentProcessor and can be registered here at runtime.
 */
public class PaymentRegistry {
    private final Map<String, IPaymentProcessor> methods = new LinkedHashMap<>();

    public PaymentRegistry() {
        // Seed with defaults
        register("Cash", new CashPaymentProcessor());
        register("Credit Card", new CreditCardPaymentProcessor());
        register("Mobile Wallet", new MobileWalletPaymentProcessor());
    }

    public synchronized void register(String name, IPaymentProcessor processor) {
        if (name == null || name.isBlank() || processor == null) return;
        methods.put(name.trim(), processor);
    }

    public synchronized boolean remove(String name) {
        if (name == null) return false;
        return methods.remove(name) != null;
    }

    public synchronized List<String> listNames() {
        return new ArrayList<>(methods.keySet());
    }

    public synchronized IPaymentProcessor get(String name) {
        return methods.get(name);
    }
}

