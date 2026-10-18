package com.katza.adamproject20277;

public class Student {
    private String fullName;
    private String id;
    private int grade;

    // בנאי ריק (חובה עבור Firebase)
    public Student() {
    }

    // בנאי מלא
    public Student(String fullName, String id, int grade) {
        this.fullName = fullName;
        this.id = id;
        this.grade = grade;
    }

    // Getters ו-Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Student{" +
                "fullName= '" + fullName + '\'' +
                ", id='" + id + '\'' +
                ", grade=" + grade +
                '}';
    }
}
