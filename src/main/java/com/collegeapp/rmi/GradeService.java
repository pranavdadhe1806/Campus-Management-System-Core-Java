package com.collegeapp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GradeService extends Remote {

    String getGrade(int studentId, int courseId) throws RemoteException;

    double getAttendance(int studentId, int courseId) throws RemoteException;
}
