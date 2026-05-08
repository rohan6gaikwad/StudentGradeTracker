package src;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for Student and GradeTracker.
 * Uses plain Java assertions (no JUnit dependency needed).
 *
 * Run: java -cp out src.GradeTrackerTest
 *
 * @author Senior Java Developer
 * @version 1.0
 */
public class GradeTrackerTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("Running GradeTracker Tests...\n");

        testStudentCreation();
        testAddGrade();
        testInvalidGrade();
        testEmptyGradeStats();
        testCalculateAverage();
        testHighestLowest();
        testLetterGrade();
        testGradeTrackerAddRemove();
        testDuplicateId();
        testSearchByName();
        testRanking();
        testPassFailCount();
        testSummaryReportNotEmpty();

        System.out.println("\n─────────────────────────────");
        System.out.printf("Results: %d passed, %d failed%n", passed, failed);
        if (failed == 0) System.out.println("✅  All tests passed!");
        else             System.out.println("❌  Some tests failed.");
    }

    // ── Test Methods ──────────────────────────────────────────────────────────

    static void testStudentCreation() {
        Student s = new Student("Test User", "T001");
        assertEquals("T001", s.getStudentId(), "studentId uppercase");
        assertEquals("Test User", s.getName(), "name stored");
    }

    static void testAddGrade() {
        Student s = new Student("Alice", "A001");
        s.addGrade(80);
        s.addGrade(90);
        assertEquals(2, s.getGradeCount(), "grade count after adds");
    }

    static void testInvalidGrade() {
        Student s = new Student("Bob", "B001");
        try {
            s.addGrade(110);
            fail("Should throw for grade > 100");
        } catch (IllegalArgumentException e) {
            pass("throws for invalid grade > 100");
        }
        try {
            s.addGrade(-5);
            fail("Should throw for negative grade");
        } catch (IllegalArgumentException e) {
            pass("throws for negative grade");
        }
    }

    static void testEmptyGradeStats() {
        Student s = new Student("Empty", "E001");
        assertEquals(0.0, s.calculateAverage(), "average 0 when no grades");
        assertEquals(0.0, s.getHighestGrade(), "highest 0 when no grades");
        assertEquals(0.0, s.getLowestGrade(), "lowest 0 when no grades");
    }

    static void testCalculateAverage() {
        Student s = new Student("Calc", "C001");
        s.addGrade(70);
        s.addGrade(80);
        s.addGrade(90);
        assertEquals(80.0, s.calculateAverage(), "average = 80.0");
    }

    static void testHighestLowest() {
        Student s = new Student("HL", "H001");
        s.addGrade(55);
        s.addGrade(95);
        s.addGrade(75);
        assertEquals(95.0, s.getHighestGrade(), "highest = 95");
        assertEquals(55.0, s.getLowestGrade(), "lowest = 55");
    }

    static void testLetterGrade() {
        assertLetterGrade(95, "A");
        assertLetterGrade(85, "B");
        assertLetterGrade(75, "C");
        assertLetterGrade(65, "D");
        assertLetterGrade(50, "F");
    }

    static void testGradeTrackerAddRemove() {
        GradeTracker gt = new GradeTracker();
        gt.addStudent(new Student("X", "X001"));
        assertEquals(1, gt.getStudentCount(), "count after add");
        gt.removeStudent("X001");
        assertEquals(0, gt.getStudentCount(), "count after remove");
    }

    static void testDuplicateId() {
        GradeTracker gt = new GradeTracker();
        gt.addStudent(new Student("First", "DUP001"));
        try {
            gt.addStudent(new Student("Second", "DUP001"));
            fail("Should throw for duplicate ID");
        } catch (IllegalArgumentException e) {
            pass("throws on duplicate student ID");
        }
    }

    static void testSearchByName() {
        GradeTracker gt = new GradeTracker();
        gt.addStudent(new Student("Rahul Sharma", "RS001"));
        gt.addStudent(new Student("Priya Patel",  "PP001"));
        List<Student> res = gt.searchByName("rahul");
        assertEquals(1, res.size(), "search finds 1 result");
        assertEquals("RS001", res.get(0).getStudentId(), "correct student found");
    }

    static void testRanking() {
        GradeTracker gt = new GradeTracker();
        Student a = new Student("Low",  "L001"); a.addGrade(50);
        Student b = new Student("High", "H001"); b.addGrade(95);
        gt.addStudent(a);
        gt.addStudent(b);
        List<Student> ranked = gt.getRankedStudents();
        assertEquals("H001", ranked.get(0).getStudentId(), "highest ranked first");
    }

    static void testPassFailCount() {
        GradeTracker gt = new GradeTracker();
        Student p = new Student("Pass", "P001"); p.addGrade(75);
        Student f = new Student("Fail", "F001"); f.addGrade(40);
        gt.addStudent(p);
        gt.addStudent(f);
        assertEquals(1L, gt.getPassCount(), "1 passing");
        assertEquals(1L, gt.getFailCount(), "1 failing");
    }

    static void testSummaryReportNotEmpty() {
        GradeTracker gt = new GradeTracker();
        Student s = new Student("Rep", "R001"); s.addGrade(88);
        gt.addStudent(s);
        String report = gt.generateSummaryReport();
        assertTrue(report.contains("Rep"), "report contains student name");
        pass("summary report contains student data");
    }

    // ── Assertion Helpers ─────────────────────────────────────────────────────

    static void assertEquals(Object expected, Object actual, String label) {
        if (expected.equals(actual)) {
            pass(label);
        } else {
            fail(label + " | expected: " + expected + ", got: " + actual);
        }
    }

    static void assertTrue(boolean condition, String label) {
        if (condition) pass(label);
        else fail(label);
    }

    static void assertLetterGrade(double score, String expected) {
        Student s = new Student("T", "T" + (int)score);
        s.addGrade(score);
        assertEquals(expected, s.getLetterGrade(), "letter grade for " + score);
    }

    static void pass(String label) {
        System.out.println("  ✓ " + label);
        passed++;
    }

    static void fail(String label) {
        System.out.println("  ✗ FAIL: " + label);
        failed++;
    }
}
