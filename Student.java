package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Student with their grades.
 * Uses ArrayList internally to store and manage grade data.
 *
 * Demonstrates:
 *  - Encapsulation (private fields + getters/setters)
 *  - ArrayList usage
 *  - Input validation
 *  - Grade statistics (avg, highest, lowest)
 *
 * @author Senior Java Developer
 * @version 1.0
 */
public class Student {

    // ── Fields ────────────────────────────────────────────────────────────────
    private String name;
    private String studentId;
    private ArrayList<Double> grades;   // ArrayList stores individual grades

    // ── Constructors ──────────────────────────────────────────────────────────

    public Student(String name, String studentId) {
        setName(name);
        setStudentId(studentId);
        this.grades = new ArrayList<>();
    }

    // ── Grade Management ──────────────────────────────────────────────────────

    /**
     * Adds a grade after validating the range [0, 100].
     *
     * @param grade the grade value to add
     * @throws IllegalArgumentException if grade is out of range
     */
    public void addGrade(double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException(
                "Grade must be between 0 and 100. Received: " + grade);
        }
        grades.add(grade);
    }

    /**
     * Removes a grade at the given index.
     *
     * @param index zero-based position
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public void removeGrade(int index) {
        if (index < 0 || index >= grades.size()) {
            throw new IndexOutOfBoundsException(
                "Invalid grade index: " + index + ". Size: " + grades.size());
        }
        grades.remove(index);
    }

    /**
     * Updates an existing grade.
     *
     * @param index zero-based position
     * @param newGrade replacement value [0,100]
     */
    public void updateGrade(int index, double newGrade) {
        if (newGrade < 0 || newGrade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100.");
        }
        if (index < 0 || index >= grades.size()) {
            throw new IndexOutOfBoundsException("Invalid grade index: " + index);
        }
        grades.set(index, newGrade);
    }

    // ── Statistics ────────────────────────────────────────────────────────────

    /**
     * Calculates the average of all grades.
     *
     * @return average or 0.0 if no grades exist
     */
    public double calculateAverage() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0;
        for (double g : grades) {
            sum += g;
        }
        return sum / grades.size();
    }

    /**
     * Returns the highest grade using Collections utility.
     *
     * @return highest grade or 0.0 if no grades
     */
    public double getHighestGrade() {
        if (grades.isEmpty()) return 0.0;
        return Collections.max(grades);
    }

    /**
     * Returns the lowest grade using Collections utility.
     *
     * @return lowest grade or 0.0 if no grades
     */
    public double getLowestGrade() {
        if (grades.isEmpty()) return 0.0;
        return Collections.min(grades);
    }

    /**
     * Converts numeric average to letter grade.
     *
     * @return letter grade string
     */
    public String getLetterGrade() {
        double avg = calculateAverage();
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }

    /**
     * Returns grade status (Pass/Fail threshold: 60).
     */
    public String getStatus() {
        return calculateAverage() >= 60 ? "PASS" : "FAIL";
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        this.studentId = studentId.trim().toUpperCase();
    }

    /** Returns an unmodifiable view of the grades list */
    public List<Double> getGrades() {
        return Collections.unmodifiableList(grades);
    }

    public int getGradeCount() {
        return grades.size();
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', grades=%d, avg=%.2f, letter='%s'}",
            studentId, name, grades.size(), calculateAverage(), getLetterGrade());
    }
}
