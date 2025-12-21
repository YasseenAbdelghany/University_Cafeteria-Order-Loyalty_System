package Enums;

public enum Currency {
    EGP, USD, EUR;

    public static Currency getDefault() {
        return EGP;
    }
}
