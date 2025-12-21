package Core;

public class Student {
    private int id;
    private String studentCode;
    private String name;
    private String password;
    private LoyaltyAccount account;
    private String programCode; // Changed from Integer programId to String programCode
    private String PhoneNumber;
    public Student() {
        this.account = new LoyaltyAccount();
    }

    public Student(String name, String studentCode) {
        this.name = name;
        this.studentCode = studentCode;
        this.account = new LoyaltyAccount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LoyaltyAccount getAccount() {
        return account;
    }

    public void setAccount(LoyaltyAccount account) {
        this.account = account != null ? account : new LoyaltyAccount();
    }

    // Legacy compatibility methods (keep these for DAO compatibility)
    public String getCode() {
        return studentCode;
    }

    public void setCode(String code) {
        this.studentCode = code;
    }

    public LoyaltyProgram getProgram() {
        LoyaltyProgram program = new LoyaltyProgram();
        if (programCode != null) {
            program.setProgramCode(programCode);
            // Extract ID from program code for backward compatibility
            int id = extractIdFromProgramCode(programCode);
            if (id > 0) program.setId(id);
        }
        program.setPoints(account.balance());
        return program;
    }

    public void setProgram(LoyaltyProgram program) {
        if (program != null) {
            this.programCode = program.getProgramCode();
            this.account = new LoyaltyAccount(program.getPoints());
        } else {
            this.programCode = null;
        }
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    // Legacy compatibility methods for programId
    public Integer getProgramId() {
        return extractIdFromProgramCode(programCode);
    }

    public void setProgramId(Integer programId) {
        if (programId != null && programId > 0) {
            this.programCode = String.format("pr%03d", programId - 1);
        } else {
            this.programCode = null;
        }
    }

    // Helper method to extract ID from program code (pr000 -> 1, pr001 -> 2, etc.)
    private int extractIdFromProgramCode(String programCode) {
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

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentCode='" + studentCode + '\'' +
                ", name='" + name + '\'' +
                ", account=" + account +
                ", programCode='" + programCode + '\'' +
                '}';
    }
}
