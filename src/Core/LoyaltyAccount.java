package Core;

public class LoyaltyAccount {
    private int points;

    public LoyaltyAccount() {
        this.points = 0;
    }

    public LoyaltyAccount(int points) {
        this.points = Math.max(0, points);
    }

    public int balance() {
        return points;
    }

    public void add(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.points += points;
    }

    public void deduct(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        if (this.points < points) {
            throw new IllegalArgumentException("Insufficient points. Available: " + this.points + ", Required: " + points);
        }
        this.points -= points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = Math.max(0, points);
    }

    @Override
    public String toString() {
        return "LoyaltyAccount{points=" + points + '}';
    }
}
