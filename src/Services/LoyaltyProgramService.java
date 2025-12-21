package Services;

import Core.*;
import Interfaces.ILoyaltyService;
import Interfaces.IStudentRepository;
import Interfaces.ILoyaltyProgram;
import Values.Discount;
import Values.Money;

public class LoyaltyProgramService implements ILoyaltyService {
    private static final double EGP_PER_POINT = 0.1; // 1 point = 0.1 EGP discount when redeemed

    private final IStudentRepository studentRepo;
    private final ILoyaltyProgram programRepo;

    public LoyaltyProgramService(IStudentRepository studentRepo, ILoyaltyProgram programRepo) {
        this.studentRepo = studentRepo;
        this.programRepo = programRepo;
    }

    @Override
    public void awardPoints(Student student, Money amount) {
        awardPoints(student, amount, null, "Points awarded for purchase");
    }

    public void awardPoints(Student student, Money amount, String orderCode, String description) {
        if (student == null || amount == null) {
            throw new IllegalArgumentException("Student and amount cannot be null");
        }
        // 1 point for every EGP 10 spent (floor)
        int pointsToAward = (int) Math.floor(amount.getAmount().doubleValue() / 10.0);
        if (pointsToAward <= 0) return;

        // Update in-memory account
        student.getAccount().add(pointsToAward);

        // Persist to loyalty_program using programCode
        String programCode = student.getProgramCode();
        if (programCode == null || programCode.isEmpty()) {
            // Create new loyalty program with programCode
            LoyaltyProgram lp = new LoyaltyProgram();
            lp.setPoints(student.getAccount().balance());
            int newId = programRepo.save(lp);
            if (newId > 0) {
                String newProgramCode = lp.getProgramCode(); // Generated during save
                student.setProgramCode(newProgramCode);
                studentRepo.Update(student);
            }
        } else {
            // Update existing loyalty program by programCode
            LoyaltyProgram lp = findByProgramCode(programCode);
            if (lp != null) {
                lp.setPoints(student.getAccount().balance());
                programRepo.update(lp);
            }
            studentRepo.Update(student);
        }
    }

    // New: award points directly (e.g., Student Manager action)
    public void addPoints(Student student, int points) {
        if (student == null || points <= 0) {
            throw new IllegalArgumentException("Invalid student or points");
        }
        student.getAccount().add(points);

        String programCode = student.getProgramCode();
        if (programCode == null || programCode.isEmpty()) {
            // Create new loyalty program
            LoyaltyProgram lp = new LoyaltyProgram();
            lp.setPoints(student.getAccount().balance());
            int newId = programRepo.save(lp);
            if (newId > 0) {
                String newProgramCode = lp.getProgramCode();
                student.setProgramCode(newProgramCode);
                studentRepo.Update(student);
            }
        } else {
            // Update existing loyalty program
            LoyaltyProgram lp = findByProgramCode(programCode);
            if (lp != null) {
                lp.setPoints(student.getAccount().balance());
                programRepo.update(lp);
            }
            studentRepo.Update(student);
        }
    }

    @Override
    public Discount redeem(Student student, int points) {
        return redeem(student, points, null, "Points redeemed for discount");
    }

    public Discount redeem(Student student, int points, String orderCode, String description) {
        if (student == null || points <= 0) {
            throw new IllegalArgumentException("Invalid student or points");
        }

        if (student.getAccount().balance() < points) {
            throw new IllegalArgumentException("Insufficient points");
        }

        student.getAccount().deduct(points);

        // Persist to loyalty_program using programCode
        String programCode = student.getProgramCode();
        if (programCode == null || programCode.isEmpty()) {
            // Create new loyalty program
            LoyaltyProgram lp = new LoyaltyProgram();
            lp.setPoints(student.getAccount().balance());
            int newId = programRepo.save(lp);
            if (newId > 0) {
                String newProgramCode = lp.getProgramCode();
                student.setProgramCode(newProgramCode);
                studentRepo.Update(student);
            }
        } else {
            // Update existing loyalty program
            LoyaltyProgram lp = findByProgramCode(programCode);
            if (lp != null) {
                lp.setPoints(student.getAccount().balance());
                programRepo.update(lp);
            }
            studentRepo.Update(student);
        }

        double discountAmount = points * EGP_PER_POINT;

        return new Discount(discountAmount, "Loyalty discount: " + points + " points redeemed");
    }

    @Override
    public int getBalance(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        return student.getAccount().balance();
    }

    // Helper method to find loyalty program by program code
    private LoyaltyProgram findByProgramCode(String programCode) {
        if (programCode == null || programCode.isEmpty()) {
            return null;
        }

        // Check if LoyaltyDAO has findByProgramCode method
        if (programRepo instanceof DataBase.LoyaltyDAO) {
            return ((DataBase.LoyaltyDAO) programRepo).findByProgramCode(programCode);
        }

        // Fallback: extract ID from program code and use findById
        try {
            if (programCode.startsWith("pr") && programCode.length() == 5) {
                int index = Integer.parseInt(programCode.substring(2));
                int id = index + 1; // Convert 0-based index to 1-based ID
                return programRepo.findById(id);
            }
        } catch (NumberFormatException e) {
            // Invalid program code format
        }

        return null;
    }
}
