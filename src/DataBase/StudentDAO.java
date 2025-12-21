package DataBase;

import Core.LoyaltyProgram;
import Core.Student;
import Interfaces.IStudentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements IStudentRepository {
    private final Connection conn;

    public StudentDAO() {
        DBconnection db = new DBconnection();
        this.conn = db.getConnection();
    }

    public StudentDAO(Connection conn) { this.conn = conn; }

    @Override
    public boolean Save(Student student) {
        String sql = "INSERT INTO student (Code, Name, Phone_Number, Password, ProgramCode, Points) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getCode());
            ps.setString(2, student.getName());
            ps.setString(3, student.getPhoneNumber());
            ps.setString(4, student.getPassword());
            if (student.getProgramCode() != null) ps.setString(5, student.getProgramCode()); else ps.setNull(5, Types.VARCHAR);
            ps.setInt(6, student.getAccount() != null ? student.getAccount().balance() : 0);
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) student.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public Student FindByCode(String code) {
        String sql = "SELECT s.Id, s.Code, s.Name, s.Phone_Number, s.Password, s.ProgramCode, s.Points, lp.Id AS LPId, lp.Points AS LPPoints, lp.program_code AS LPCode " +
                "FROM student s LEFT JOIN loyalty_program lp ON s.ProgramCode = lp.program_code WHERE s.Code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void Update(Student student) {
        String sql = "UPDATE student SET Code = ?, Name = ?, Phone_Number= ?, Password = ?, ProgramCode = ?, Points = ? WHERE Id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getCode());
            ps.setString(2, student.getName());
            ps.setString(3, student.getPhoneNumber());
            ps.setString(4, student.getPassword());
            if (student.getProgramCode() != null) ps.setString(5, student.getProgramCode()); else ps.setNull(5, Types.VARCHAR);
            ps.setInt(6, student.getAccount() != null ? student.getAccount().balance() : 0);
            ps.setInt(7, student.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public boolean deleteStudent(String code) {
        String sql = "DELETE FROM student WHERE Code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Student> getAllStudents() {
        String sql = "SELECT s.Id, s.Code, s.Name, s.Phone_Number, s.Password, s.ProgramCode, s.Points, lp.Id AS LPId, lp.Points AS LPPoints, lp.program_code AS LPCode " +
                "FROM student s LEFT JOIN loyalty_program lp ON s.ProgramCode = lp.program_code";
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public int countStudents() {
        String sql = "SELECT COUNT(*) FROM student";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Authenticate student with code and password
    public Student authenticateStudent(String code, String password) {
        if (code == null || code.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        
        String sql = "SELECT s.Id, s.Code, s.Name, s.Phone_Number, s.Password, s.ProgramCode, s.Points, lp.Id AS LPId, lp.Points AS LPPoints, lp.program_code AS LPCode " +
                "FROM student s LEFT JOIN loyalty_program lp ON s.ProgramCode = lp.program_code WHERE s.Code = ? AND s.Password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            ps.setString(2, password.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStudent(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("Id"));
        s.setCode(rs.getString("Code"));
        s.setName(rs.getString("Name"));
        s.setPhoneNumber(rs.getString("Phone_Number"));
        s.setPassword(rs.getString("Password"));
        String progCode = rs.getString("ProgramCode");
        if (progCode != null && !rs.wasNull()) {
            s.setProgramCode(progCode);
        }

        // Set student points from student table
        int studentPoints = rs.getInt("Points");
        s.getAccount().setPoints(studentPoints);

        return s;
    }
}
