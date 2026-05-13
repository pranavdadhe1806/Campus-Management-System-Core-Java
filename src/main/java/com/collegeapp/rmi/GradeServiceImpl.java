package com.collegeapp.rmi;

import com.collegeapp.DBConnection;
import com.collegeapp.util.LoggerUtil;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeServiceImpl extends UnicastRemoteObject implements GradeService {

    public static final int DEFAULT_RMI_PORT = 1099;

    public GradeServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String getGrade(int studentId, int courseId) throws RemoteException {
        String sql = """
                SELECT gs.grade
                FROM marks m
                JOIN courses c ON c.course_id = m.course_id
                JOIN grading_scale gs
                  ON gs.university_id = c.university_id
                 AND m.marks_obtained BETWEEN gs.min_marks AND gs.max_marks
                WHERE m.student_id = ? AND m.course_id = ?
                ORDER BY m.created_at DESC
                LIMIT 1
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("grade") : "N/A";
            }
        } catch (SQLException e) {
            LoggerUtil.error("RMI grade lookup failed", e);
            throw new RemoteException("Unable to fetch grade.", e);
        }
    }

    @Override
    public double getAttendance(int studentId, int courseId) throws RemoteException {
        String sql = """
                SELECT
                    COUNT(*) AS total_classes,
                    SUM(CASE WHEN status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) AS attended_classes
                FROM attendance
                WHERE student_id = ? AND course_id = ?
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt("total_classes") == 0) {
                    return 0.0;
                }
                return (rs.getDouble("attended_classes") / rs.getDouble("total_classes")) * 100.0;
            }
        } catch (SQLException e) {
            LoggerUtil.error("RMI attendance lookup failed", e);
            throw new RemoteException("Unable to fetch attendance.", e);
        }
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_RMI_PORT;
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(port);
        }
        registry.rebind("GradeService", new GradeServiceImpl());
        LoggerUtil.info("GradeService bound on RMI port " + port);
    }
}
