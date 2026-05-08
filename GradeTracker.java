package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Core service class that manages the collection of Students.
 *
 * Demonstrates:
 *  - ArrayList<Student> for data management
 *  - Searching, sorting, filtering
 *  - Summary report generation
 *  - Business logic separation (Service layer pattern)
 *
 * @author Senior Java Developer
 * @version 1.0
 */
public class GradeTracker {

    // ── Data Store ────────────────────────────────────────────────────────────
    private final ArrayList<Student> students;   // Primary ArrayList store

    // ── Constructor ───────────────────────────────────────────────────────────

    public GradeTracker() {
        this.students = new ArrayList<>();
    }

    // ── Student CRUD ──────────────────────────────────────────────────────────

    /**
     * Adds a new student to the tracker.
     *
     * @param student Student object to add
     * @throws IllegalArgumentException if student with same ID already exists
     */
    public void addStudent(Student student) {
        if (student == null) throw new IllegalArgumentException("Student cannot be null.");
        if (findById(student.getStudentId()).isPresent()) {
            throw new IllegalArgumentException(
                "Student with ID '" + student.getStudentId() + "' already exists.");
        }
        students.add(student);
    }

    /**
     * Removes a student by their ID.
     *
     * @param studentId ID of the student to remove
     * @return true if removed, false if not found
     */
    public boolean removeStudent(String studentId) {
        return students.removeIf(s -> s.getStudentId().equalsIgnoreCase(studentId));
    }

    /**
     * Finds a student by ID (case-insensitive).
     *
     * @param studentId Student ID to search
     * @return Optional<Student>
     */
    public Optional<Student> findById(String studentId) {
        return students.stream()
            .filter(s -> s.getStudentId().equalsIgnoreCase(studentId))
            .findFirst();
    }

    /**
     * Searches students by name (partial, case-insensitive).
     *
     * @param query search term
     * @return list of matching students
     */
    public List<Student> searchByName(String query) {
        String lower = query.toLowerCase();
        return students.stream()
            .filter(s -> s.getName().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    // ── Statistics Across All Students ────────────────────────────────────────

    /**
     * Class-wide average across all student averages.
     */
    public double getClassAverage() {
        if (students.isEmpty()) return 0.0;
        return students.stream()
            .mapToDouble(Student::calculateAverage)
            .average()
            .orElse(0.0);
    }

    /**
     * Student with the highest average grade.
     */
    public Optional<Student> getTopStudent() {
        return students.stream()
            .filter(s -> s.getGradeCount() > 0)
            .max(Comparator.comparingDouble(Student::calculateAverage));
    }

    /**
     * Student with the lowest average grade.
     */
    public Optional<Student> getLowestStudent() {
        return students.stream()
            .filter(s -> s.getGradeCount() > 0)
            .min(Comparator.comparingDouble(Student::calculateAverage));
    }

    /**
     * Returns students sorted by average grade (descending = rank order).
     */
    public List<Student> getRankedStudents() {
        return students.stream()
            .filter(s -> s.getGradeCount() > 0)
            .sorted(Comparator.comparingDouble(Student::calculateAverage).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Returns count of passing students (avg >= 60).
     */
    public long getPassCount() {
        return students.stream()
            .filter(s -> s.calculateAverage() >= 60)
            .count();
    }

    /**
     * Returns count of failing students (avg < 60).
     */
    public long getFailCount() {
        return students.stream()
            .filter(s -> s.calculateAverage() < 60)
            .count();
    }

    // ── Summary Report ────────────────────────────────────────────────────────

    /**
     * Generates a full plain-text summary report.
     *
     * @return formatted report string
     */
    public String generateSummaryReport() {
        if (students.isEmpty()) {
            return "No students enrolled yet.";
        }

        StringBuilder sb = new StringBuilder();
        String line = "═".repeat(60);
        String dline = "─".repeat(60);

        sb.append(line).append("\n");
        sb.append("         STUDENT GRADE TRACKER — SUMMARY REPORT\n");
        sb.append(line).append("\n\n");

        // Class-level stats
        sb.append(String.format("  Total Students  : %d\n", students.size()));
        sb.append(String.format("  Class Average   : %.2f%%\n", getClassAverage()));
        sb.append(String.format("  Passing         : %d  |  Failing: %d\n",
            getPassCount(), getFailCount()));

        getTopStudent().ifPresent(t ->
            sb.append(String.format("  Top Student     : %s (%.2f%%)\n",
                t.getName(), t.calculateAverage())));
        getLowestStudent().ifPresent(l ->
            sb.append(String.format("  Needs Support   : %s (%.2f%%)\n",
                l.getName(), l.calculateAverage())));

        sb.append("\n").append(dline).append("\n");
        sb.append(String.format("  %-6s %-20s %-8s %-8s %-8s %-5s %-6s %s\n",
            "RANK", "NAME", "ID", "AVG", "HIGH", "LOW", "GRADE", "STATUS"));
        sb.append(dline).append("\n");

        List<Student> ranked = getRankedStudents();
        // Also include students with no grades at bottom
        List<Student> noGrades = students.stream()
            .filter(s -> s.getGradeCount() == 0)
            .collect(Collectors.toList());

        int rank = 1;
        for (Student s : ranked) {
            sb.append(String.format("  %-6d %-20s %-8s %-8.2f %-8.2f %-5.2f %-6s %s\n",
                rank++,
                truncate(s.getName(), 20),
                s.getStudentId(),
                s.calculateAverage(),
                s.getHighestGrade(),
                s.getLowestGrade(),
                s.getLetterGrade(),
                s.getStatus()));
        }

        for (Student s : noGrades) {
            sb.append(String.format("  %-6s %-20s %-8s %-8s %-8s %-5s %-6s %s\n",
                "-", truncate(s.getName(), 20), s.getStudentId(),
                "N/A", "N/A", "N/A", "N/A", "N/A"));
        }

        sb.append(line).append("\n");
        return sb.toString();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);   // defensive copy
    }

    public int getStudentCount() {
        return students.size();
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }
}
