package src;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based interface for the Student Grade Tracker.
 *
 * Run this class directly to use the app in terminal mode.
 * Demonstrates: Scanner input, loops, switch-expressions, error handling.
 *
 * @author Senior Java Developer
 * @version 1.0
 */
public class ConsoleRunner {

    private static final GradeTracker tracker = new GradeTracker();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   STUDENT GRADE TRACKER — Console   ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            System.out.println();

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> addGrade();
                case 3 -> removeStudent();
                case 4 -> viewAllStudents();
                case 5 -> viewStudentDetail();
                case 6 -> showReport();
                case 7 -> searchStudent();
                case 0 -> { running = false; System.out.println("Goodbye! 👋"); }
                default -> System.out.println("❌  Invalid option. Try again.\n");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("┌─────────────────────────────┐");
        System.out.println("│  1. Add Student             │");
        System.out.println("│  2. Add Grade to Student    │");
        System.out.println("│  3. Remove Student          │");
        System.out.println("│  4. View All Students       │");
        System.out.println("│  5. View Student Details    │");
        System.out.println("│  6. Summary Report          │");
        System.out.println("│  7. Search by Name          │");
        System.out.println("│  0. Exit                    │");
        System.out.println("└─────────────────────────────┘");
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private static void addStudent() {
        System.out.print("Enter student name : ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter student ID   : ");
        String id = scanner.nextLine().trim();
        try {
            tracker.addStudent(new Student(name, id));
            System.out.println("✅  Student '" + name + "' added.\n");
        } catch (Exception e) {
            System.out.println("❌  " + e.getMessage() + "\n");
        }
    }

    private static void addGrade() {
        System.out.print("Enter student ID   : ");
        String id = scanner.nextLine().trim();
        Optional<Student> opt = tracker.findById(id);
        if (opt.isEmpty()) {
            System.out.println("❌  Student not found.\n");
            return;
        }
        double grade = readDouble("Enter grade (0-100): ");
        try {
            opt.get().addGrade(grade);
            System.out.printf("✅  Grade %.1f added to %s.%n%n", grade, opt.get().getName());
        } catch (Exception e) {
            System.out.println("❌  " + e.getMessage() + "\n");
        }
    }

    private static void removeStudent() {
        System.out.print("Enter student ID to remove: ");
        String id = scanner.nextLine().trim();
        boolean removed = tracker.removeStudent(id);
        System.out.println(removed
            ? "✅  Student removed.\n"
            : "❌  Student not found.\n");
    }

    private static void viewAllStudents() {
        if (tracker.isEmpty()) {
            System.out.println("No students yet.\n");
            return;
        }
        System.out.printf("%-8s %-20s %-6s %-8s %-8s %-8s %-6s %s%n",
            "ID", "Name", "Count", "Avg", "High", "Low", "Grade", "Status");
        System.out.println("─".repeat(74));
        for (Student s : tracker.getAllStudents()) {
            System.out.printf("%-8s %-20s %-6d %-8.2f %-8.2f %-8.2f %-6s %s%n",
                s.getStudentId(), s.getName(), s.getGradeCount(),
                s.calculateAverage(), s.getHighestGrade(), s.getLowestGrade(),
                s.getLetterGrade(), s.getStatus());
        }
        System.out.println();
    }

    private static void viewStudentDetail() {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine().trim();
        Optional<Student> opt = tracker.findById(id);
        if (opt.isEmpty()) {
            System.out.println("❌  Student not found.\n");
            return;
        }
        Student s = opt.get();
        System.out.println("\n── Student Detail ──────────────────");
        System.out.println("  Name     : " + s.getName());
        System.out.println("  ID       : " + s.getStudentId());
        System.out.println("  Grades   : " + s.getGrades());
        System.out.printf ("  Average  : %.2f%%%n", s.calculateAverage());
        System.out.printf ("  Highest  : %.2f%n", s.getHighestGrade());
        System.out.printf ("  Lowest   : %.2f%n", s.getLowestGrade());
        System.out.println("  Letter   : " + s.getLetterGrade());
        System.out.println("  Status   : " + s.getStatus());
        System.out.println("────────────────────────────────────\n");
    }

    private static void showReport() {
        System.out.println(tracker.generateSummaryReport());
    }

    private static void searchStudent() {
        System.out.print("Enter name to search: ");
        String query = scanner.nextLine().trim();
        List<Student> results = tracker.searchByName(query);
        if (results.isEmpty()) {
            System.out.println("No students found matching '" + query + "'.\n");
        } else {
            System.out.println("Found " + results.size() + " result(s):");
            results.forEach(s -> System.out.println("  • " + s));
            System.out.println();
        }
    }

    // ── I/O Helpers ───────────────────────────────────────────────────────────

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = scanner.nextInt();
                scanner.nextLine();
                return v;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("❌  Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = scanner.nextDouble();
                scanner.nextLine();
                return v;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("❌  Please enter a valid number.");
            }
        }
    }
}
