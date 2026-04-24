package com.collegeapp.model;

import com.collegeapp.util.Validator;

public class Department {

    private int departmentId;
    private String deptName;
    private String deptCode;

    public Department() {
    }

    public Department(int departmentId, String deptName, String deptCode) {
        setDepartmentId(departmentId);
        setDeptName(deptName);
        setDeptCode(deptCode);
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        if (deptName == null || deptName.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty.");
        }
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        if (!Validator.isValidDeptCode(deptCode)) {
            throw new IllegalArgumentException(
                    "Invalid department code: " + deptCode + ".");
        }
        this.deptCode = deptCode;
    }

    @Override
    public String toString() {
        return "Department{" +
                "departmentId=" + departmentId +
                ", deptName='" + deptName + '\'' +
                ", deptCode='" + deptCode + '\'' +
                '}';
    }
}