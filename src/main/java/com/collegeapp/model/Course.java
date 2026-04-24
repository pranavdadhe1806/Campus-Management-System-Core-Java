package com.collegeapp.model;

import com.collegeapp.util.Validator;

public class Course {

    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private int totalMarks;
    private int lectureHours;
    private int semester;
    private int deptId;
    private int facultyId;

    public Course() {
    }

    public Course(int courseId, String courseCode, String courseName,
            int credits, int totalMarks, int lectureHours,
            int semester, int deptId, int facultyId) {
        setCourseId(courseId);
        setCourseCode(courseCode);
        setCourseName(courseName);
        setCredits(credits);
        setTotalMarks(totalMarks);
        setLectureHours(lectureHours);
        setSemester(semester);
        setDeptId(deptId);
        setFacultyId(facultyId);
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        if (!Validator.isValidCourseCode(courseCode)) {
            throw new IllegalArgumentException(
                    "Invalid course code: " + courseCode + ".");
        }
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be null or empty.");
        }
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException("Credits must be between 1 and 6.");
        }
        this.credits = credits;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        if (totalMarks <= 0) {
            throw new IllegalArgumentException("Total marks must be greater than 0.");
        }
        this.totalMarks = totalMarks;
    }

    public int getLectureHours() {
        return lectureHours;
    }

    public void setLectureHours(int lectureHours) {
        if (lectureHours <= 0) {
            throw new IllegalArgumentException("Lecture hours must be greater than 0.");
        }
        this.lectureHours = lectureHours;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        if (semester < 1 || semester > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8.");
        }
        this.semester = semester;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", totalMarks=" + totalMarks +
                ", lectureHours=" + lectureHours +
                ", semester=" + semester +
                ", deptId=" + deptId +
                ", facultyId=" + facultyId +
                '}';
    }
}
