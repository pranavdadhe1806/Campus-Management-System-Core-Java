package com.collegeapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.collegeapp.util.Validator;

public class Student extends User {

    private int studentId;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private String mobileNo;
    private LocalDate dob;
    private int academicYear;
    private String division;
    private String batch;
    private int sem;
    private int deptId;

    public Student() {
        super();
    }

    public Student(int userId, String username, String email,
            String passwordHash, String role, boolean isFirstLogin,
            LocalDateTime createdAt, int studentId, String rollNumber,
            String firstName, String lastName, String mobileNo,
            LocalDate dob, int academicYear, String division,
            String batch, int sem, int deptId) {
        super(userId, username, email, passwordHash, role, isFirstLogin, createdAt);
        setStudentId(studentId);
        setRollNumber(rollNumber);
        setFirstName(firstName);
        setLastName(lastName);
        setMobileNo(mobileNo);
        setDob(dob);
        setAcademicYear(academicYear);
        setDivision(division);
        setBatch(batch);
        setSem(sem);
        setDeptId(deptId);
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        if (!Validator.isValidRollNo(rollNumber)) {
            throw new IllegalArgumentException(
                    "Invalid roll number: " + rollNumber + ". Expected format: CS21IT001");
        }
        this.rollNumber = rollNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!Validator.isValidName(firstName)) {
            throw new IllegalArgumentException(
                    "Invalid first name: " + firstName + ".");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!Validator.isValidName(lastName)) {
            throw new IllegalArgumentException(
                    "Invalid last name: " + lastName + ".");
        }
        this.lastName = lastName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        if (mobileNo != null && !Validator.isValidPhone(mobileNo)) {
            throw new IllegalArgumentException(
                    "Invalid mobile number: " + mobileNo +
                            ". Must be a 10-digit Indian number starting with 6-9.");
        }
        this.mobileNo = mobileNo;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        if (dob == null) {
            throw new IllegalArgumentException("Date of birth cannot be null.");
        }
        this.dob = dob;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        if (academicYear < 1 || academicYear > 4) {
            throw new IllegalArgumentException("Academic year must be between 1 and 4.");
        }
        this.academicYear = academicYear;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        if (division == null || division.trim().isEmpty()) {
            throw new IllegalArgumentException("Division cannot be null or empty.");
        }
        this.division = division;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        if (batch == null || batch.trim().isEmpty()) {
            throw new IllegalArgumentException("Batch cannot be null or empty.");
        }
        this.batch = batch;
    }

    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        if (sem < 1 || sem > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8.");
        }
        this.sem = sem;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return "Student{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                ", isFirstLogin=" + isFirstLogin() +
                ", createdAt=" + getCreatedAt() +
                ", studentId=" + studentId +
                ", rollNumber='" + rollNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", dob=" + dob +
                ", academicYear=" + academicYear +
                ", division='" + division + '\'' +
                ", batch='" + batch + '\'' +
                ", sem=" + sem +
                ", deptId=" + deptId +
                '}';
    }
}