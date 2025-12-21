package DataBase;

import Core.LoyaltyProgram;
import Interfaces.ILoyaltyProgram;

import java.sql.*;

public class LoyaltyDAO implements ILoyaltyProgram {
    private final Connection connection;

    public LoyaltyDAO() {
        DBconnection db = new DBconnection();
        this.connection = db.getConnection();
    }

    // Constructor for dependency injection
    public LoyaltyDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int save(LoyaltyProgram prog) {
        String placeholder = "tmp-" + System.currentTimeMillis();
        String insertSql = "INSERT INTO loyalty_program (Points, program_code) VALUES (?, ?)";
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            if (originalAutoCommit) connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, prog.getPoints());
                ps.setString(2, placeholder);
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            int id = rs.getInt(1);
                            String programCode = Core.LoyaltyProgram.generateProgramCode(id);
                            String updateSql = "UPDATE loyalty_program SET program_code = ? WHERE Id = ?";
                            try (PreparedStatement up = connection.prepareStatement(updateSql)) {
                                up.setString(1, programCode);
                                up.setInt(2, id);
                                up.executeUpdate();
                            }
                            if (originalAutoCommit) connection.commit();
                            prog.setId(id);
                            prog.setProgramCode(programCode);
                            return id;
                        }
                    }
                }
            }
            if (originalAutoCommit) connection.rollback();
        } catch (SQLException e) {
            try { if (originalAutoCommit) connection.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
        } finally {
            try { if (connection != null) connection.setAutoCommit(originalAutoCommit); } catch (SQLException ignored) {}
        }
        return -1;
    }

    @Override
    public void update(LoyaltyProgram prog) {
        String sql = "UPDATE loyalty_program SET Points = ?, program_code = ? WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, prog.getPoints());
            ps.setString(2, prog.getProgramCode());
            ps.setInt(3, prog.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LoyaltyProgram findById(int id) {
        String sql = "SELECT Id, Points, program_code FROM loyalty_program WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoyaltyProgram lp = new LoyaltyProgram();
                    lp.setId(rs.getInt("Id"));
                    lp.setPoints(rs.getInt("Points"));
                    String programCode = rs.getString("program_code");
                    if (programCode != null) {
                        lp.setProgramCode(programCode);
                    }
                    return lp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // New method to find by program code
    public LoyaltyProgram findByProgramCode(String programCode) {
        String sql = "SELECT Id, Points, program_code FROM loyalty_program WHERE program_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, programCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoyaltyProgram lp = new LoyaltyProgram();
                    lp.setId(rs.getInt("Id"));
                    lp.setPoints(rs.getInt("Points"));
                    lp.setProgramCode(rs.getString("program_code"));
                    return lp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM loyalty_program WHERE Id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int estimateNextId() {
        String sql = "SELECT IFNULL(MAX(Id),0) + 1 AS next_id FROM loyalty_program";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("next_id");
        } catch (SQLException e) {
            // fallback to 1
        }
        return 1;
    }
}
