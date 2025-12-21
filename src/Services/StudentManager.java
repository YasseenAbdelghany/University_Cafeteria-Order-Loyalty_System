package Services;

import Core.*;
import Interfaces.IStudentRepository;
import Interfaces.ILoyaltyProgram;
import DataBase.LoyaltyDAO;

import java.util.Objects;

public class StudentManager {
    private final IStudentRepository repository;
    private final ILoyaltyProgram loyaltyDAO;

    public StudentManager() { this(new DataBase.StudentDAO(), new LoyaltyDAO()); }
    public StudentManager(IStudentRepository repository) { this(repository, new LoyaltyDAO()); }
    public StudentManager(IStudentRepository repository, ILoyaltyProgram loyaltyDAO) {
        this.repository = Objects.requireNonNull(repository, "IStudentRepository cannot be null");
        this.loyaltyDAO = Objects.requireNonNull(loyaltyDAO, "ILoyaltyProgram cannot be null");
    }

    public Student register(String name , String phoneNumber) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        // 1) Create loyalty program first
        LoyaltyProgram loyaltyProgram = new LoyaltyProgram();
        loyaltyProgram.setPoints(0);
        int loyaltyProgramId = loyaltyDAO.save(loyaltyProgram);
        if (loyaltyProgramId <= 0) {
            throw new RuntimeException("Failed to create loyalty program");
        }
        // / /////////
        String programCode = loyaltyProgram.getProgramCode();
        if (programCode == null || programCode.trim().isEmpty()) {
            throw new RuntimeException("Failed to retrieve loyalty program code");
        }
        // 2) Generate next student code like st000, st001, ... with collision check
        String nextCode = generateNextCode();

        // 3) Create and save student with programCode
        Student student = new Student();
        student.setName(name.trim());
        student.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
        student.setCode(nextCode);
        student.setStudentCode(nextCode);
        student.setProgramCode(loyaltyProgram.getProgramCode()); // Use programCode instead of programId

        if (!repository.Save(student)) {
            // rollback loyalty program if student save failed
            loyaltyDAO.delete(loyaltyProgramId);
            throw new RuntimeException("Failed to save student");
        }

        // Sync account in memory
        student.setAccount(new LoyaltyAccount(loyaltyProgram.getPoints()));
        return student;
    }

    public Student login(String studentCode) {
        if (studentCode == null || studentCode.trim().isEmpty()) return null;
        return repository.FindByCode(studentCode.trim());
    }

    private String generateNextCode() {
        int base = repository.countStudents();
        // Try count-based first, then increment until available to avoid duplicates after deletions
        int attempt = base;
        while (true) {
            String code = String.format("st%03d", attempt);
            if (repository.FindByCode(code) == null) return code;
            attempt++;
        }
    }

    // Convenience wrappers
    public boolean register(Student student) { return repository.Save(student); }
    public Student findByCode(String code) { return repository.FindByCode(code); }
    public void update(Student student) { repository.Update(student); }
    public boolean delete(String code) { return repository.deleteStudent(code); }
    public java.util.List<Student> listAll() { return repository.getAllStudents(); }
    
    // Getter for repository access (needed for password authentication)
    public IStudentRepository getStudentDAO() { return repository; }
}
