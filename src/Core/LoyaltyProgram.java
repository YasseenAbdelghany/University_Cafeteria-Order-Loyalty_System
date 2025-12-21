package Core;

public class LoyaltyProgram {
        private int id ;
        private int points ;
        private String programCode;

        public LoyaltyProgram() {
            this.points = 0;
        }

        public LoyaltyProgram(int id, int points) {
            this.id = id;
            this.points = points;
            // programCode is managed externally; do not auto-generate to avoid desync with DB
        }

        public LoyaltyProgram(String programCode, int points) {
            this.programCode = programCode;
            this.points = points;
            // id is managed externally; do not auto-extract from code to avoid accidental mismatch
        }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        if (points >= 0) {
            this.points = points;
        } else {
            throw new IllegalArgumentException("Points cannot be negative");
        }
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    // Helper: Generate standard program code from a 1-based ID (pr000 for id=1)
    public static String generateProgramCode(int id) {
        if (id <= 0) return null;
        return String.format("pr%03d", id - 1);
    }

    // Helper: Extract 1-based ID from a standard program code (pr000 -> 1)
    public static int extractIdFromProgramCode(String programCode) {
        if (programCode == null || !programCode.startsWith("pr") || programCode.length() != 5) {
            return 0;
        }
        try {
            int index = Integer.parseInt(programCode.substring(2));
            return index + 1; // Convert 0-based index to 1-based ID
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Method from class diagram
    public void add(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.points += points;
    }

    // Legacy methods for compatibility
    public boolean addPoints(int amount) {
        try {
            add(amount);
            System.out.println("Points added to LoyaltyProgram Successfully");
            return true;
        }catch (Exception e) {
            System.out.println("Invalid add points");
            return false;
        }
    }

    public void deduct(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        } else if (this.points < points) {
            throw new IllegalArgumentException("Insufficient points. Available: " + this.points + ", Required: " + points);
        }
        this.points -= points;
    }

    public boolean deductPoints(int amount) {
        try {
            deduct(amount);
            System.out.println("Points deducted from LoyaltyProgram Successfully");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid deduct points");
            return false;
        }
    }


    @Override
    public String toString() {
        return "LoyaltyProgram{" +
                "id=" + id +
                ", programCode='" + programCode + '\'' +
                ", points=" + points +
                '}';
    }
}
