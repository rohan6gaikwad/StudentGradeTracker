# Student Grade Tracker — Java

A production-grade Java desktop application for managing student grades.

## Features
- Add/remove students with ID and name
- Add, update, and remove grades per student (ArrayList)
- Auto-calculate average, highest, and lowest scores
- Letter grades (A–F) and Pass/Fail status
- Class-wide statistics: class average, top student, pass/fail counts
- Ranked summary report
- Full Swing GUI with dark theme
- Console-based interface (ConsoleRunner)
- Unit test suite (GradeTrackerTest)

## Project Structure
```
StudentGradeTracker/
├── src/
│   ├── Main.java             ← Launch GUI
│   ├── Student.java          ← Student model + grade logic
│   ├── GradeTracker.java     ← Service/manager (ArrayList operations)
│   ├── GradeTrackerGUI.java  ← Swing GUI
│   ├── ConsoleRunner.java    ← Console-based interface
│   └── GradeTrackerTest.java ← Unit tests
└── README.md
```

## How to Compile & Run

### Compile all sources
```bash
mkdir -p out
javac -d out src/*.java
```

### Run GUI
```bash
java -cp out src.Main
```

### Run Console Mode
```bash
java -cp out src.ConsoleRunner
```

### Run Tests
```bash
java -cp out src.GradeTrackerTest
```

## Skills Demonstrated
| Skill | Where |
|---|---|
| ArrayList | `Student.java` (grades), `GradeTracker.java` (students list) |
| Collections.max/min | `Student.java` — getHighestGrade / getLowestGrade |
| Encapsulation | `Student.java` — private fields, validated setters |
| Streams & Lambdas | `GradeTracker.java` — filter, sort, map, collect |
| Swing GUI | `GradeTrackerGUI.java` — JFrame, JTable, JTextField, custom renderers |
| Console I/O | `ConsoleRunner.java` — Scanner, switch expression |
| Exception Handling | `Student.java`, `GradeTracker.java` — IllegalArgumentException, IndexOutOfBoundsException |
| Optional | `GradeTracker.java` — findById, getTopStudent |
| Unit Testing | `GradeTrackerTest.java` — plain-Java test assertions |
| Javadoc | All classes — fully documented |

## Requirements
- Java 17 or higher (uses switch expressions)
