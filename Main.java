import java.util.Scanner;

/**
 * Main.java
 * Entry point for the Academic Performance Tracker & CGPA Analyzer.
 * Provides a two-level interactive CLI menu system.
 *
 * Menu structure:
 *   1. Add New Semester  (guided subject-by-subject entry)
 *   2. View Semester Detail
 *   3. Dashboard (all semesters, CGPA, backlogs)
 *   4. SGPA Trend Chart (ASCII bar chart)
 *   5. Grade Predictor  (target CGPA → required next SGPA)
 *   6. Core vs Elective Analysis
 *   7. Delete a Semester
 *   0. Exit
 *
 * Demonstrates: Scanner, loops, switch-case, input validation,
 *               calling methods across multiple classes.
 *
 * Author: Aritra Chowdhury
 * Course: Programming in Java - BYOP Project
 */
public class Main {

    private static final Scanner        scanner = new Scanner(System.in);
    private static final AcademicManager manager = new AcademicManager();

    public static void main(String[] args) {
        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt();

            switch (choice) {
                case 1: addSemesterFlow();       break;
                case 2: viewSemesterDetail();    break;
                case 3: manager.showDashboard(); break;
                case 4: manager.showTrendChart();break;
                case 5: predictorFlow();         break;
                case 6: manager.showTypeAnalysis(); break;
                case 7: deleteSemesterFlow();    break;
                case 0:
                    System.out.println("\n  Goodbye! Keep up the great academic work!\n");
                    running = false;
                    break;
                default:
                    System.out.println("  Invalid option. Try again.");
            }

            if (running) {
                System.out.println("\n  Press ENTER to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    // ── Add Semester ──────────────────────────────────────────────────────

    private static void addSemesterFlow() {
        System.out.println("\n  ─── Add New Semester ──────────────────────────────────────");

        System.out.print("  Semester Number (e.g., 1, 2, 3 ...): ");
        int semNum = readInt();
        if (semNum <= 0) { System.out.println("  [Error] Invalid semester number."); return; }

        System.out.print("  Semester Label  (e.g., 2024-25 Odd)  : ");
        String labelInput = scanner.nextLine().trim();
        String label = "Semester " + semNum
                + (labelInput.isEmpty() ? "" : " (" + labelInput + ")");

        Semester sem = new Semester(semNum, label);

        System.out.println("\n  Now enter subjects one by one.");
        System.out.println("  Type 'done' as subject name when finished.\n");

        int subjectIndex = 1;
        while (true) {
            System.out.println("  ── Subject " + subjectIndex + " ─────────────────────────");

            System.out.print("  Subject Name (or 'done'): ");
            String name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("done")) break;
            if (name.isEmpty()) { System.out.println("  [Skipped] Name cannot be empty."); continue; }

            System.out.print("  Credit Hours (1-4)      : ");
            int credits = readInt();
            if (credits < 1 || credits > 4) {
                System.out.println("  [Warning] Unusual credit value. Using as entered.");
            }

            System.out.println("  Grade:");
            System.out.println(GradePoint.listAll());
            System.out.print("  Select (1-8)            : ");
            int gradeChoice = readInt();
            GradePoint grade = GradePoint.fromIndex(gradeChoice);

            System.out.print("  Internal Marks (/50)    : ");
            double internal = readDouble();

            System.out.print("  External Marks (/100, -1 if not yet): ");
            double external = readDouble();

            System.out.print("  Type (1=Core, 2=Elective): ");
            int typeChoice = readInt();
            String type = (typeChoice == 2) ? "Elective" : "Core";

            Subject subject = new Subject(name, credits, grade, internal, external, type);
            sem.addSubject(subject);

            System.out.println("  ✓ Added: " + name + " → " + grade.shortCode()
                    + " (" + grade.getPoints() + " GP) | " + credits + " credits");
            subjectIndex++;
        }

        if (sem.getSubjects().isEmpty()) {
            System.out.println("  No subjects entered. Semester not saved.");
            return;
        }

        System.out.println("\n  ── Semester Summary ──────────────────────────────────────");
        sem.displaySubjects();
        System.out.printf("  SGPA: %.2f%n", sem.getSGPA());

        System.out.print("\n  Save this semester? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            manager.addSemester(sem);
        } else {
            System.out.println("  Semester discarded.");
        }
    }

    // ── View Semester Detail ──────────────────────────────────────────────

    private static void viewSemesterDetail() {
        if (manager.getSemesterCount() == 0) {
            System.out.println("\n  No semesters added yet.");
            return;
        }
        System.out.println("\n  Available semesters:");
        for (Semester s : manager.getAllSemesters()) {
            System.out.println("    [" + s.getSemNumber() + "] " + s.getLabel()
                    + "  (SGPA: " + String.format("%.2f", s.getSGPA()) + ")");
        }
        System.out.print("  Enter semester number: ");
        int num = readInt();
        Semester sem = manager.getSemester(num);
        if (sem == null) {
            System.out.println("  Semester " + num + " not found.");
            return;
        }
        System.out.println("\n  " + sem.getLabel());
        sem.displaySubjects();
    }

    // ── Grade Predictor ───────────────────────────────────────────────────

    private static void predictorFlow() {
        System.out.println("\n  ─── Grade Predictor ───────────────────────────────────────");
        System.out.println("  Enter your target CGPA after the NEXT semester.");
        System.out.print("  Target CGPA (0.0 – 10.0): ");
        double target = readDouble();

        System.out.print("  Expected credits in next semester: ");
        int credits = readInt();

        manager.predictRequiredSGPA(target, credits);
    }

    // ── Delete Semester ───────────────────────────────────────────────────

    private static void deleteSemesterFlow() {
        if (manager.getSemesterCount() == 0) {
            System.out.println("\n  No semesters to delete.");
            return;
        }
        System.out.println("\n  Available semesters:");
        for (Semester s : manager.getAllSemesters()) {
            System.out.println("    [" + s.getSemNumber() + "] " + s.getLabel());
        }
        System.out.print("  Enter semester number to delete: ");
        int num = readInt();
        System.out.print("  Confirm delete Semester " + num + "? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            manager.deleteSemester(num);
        } else {
            System.out.println("  Delete cancelled.");
        }
    }

    // ── UI Helpers ────────────────────────────────────────────────────────

    private static void printBanner() {
        int loaded = manager.getSemesterCount();
        System.out.println("\n  ╔══════════════════════════════════════════════════════╗");
        System.out.println("  ║    ACADEMIC PERFORMANCE TRACKER & CGPA ANALYZER      ║");
        System.out.println("  ║         CBCS 10-Point Grading System (India)          ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
        System.out.println("  Semesters loaded : " + loaded);
        if (loaded > 0) {
            System.out.printf("  Current CGPA     : %.2f  (%s)%n",
                    manager.getCGPA(),
                    AcademicManager.getClassification(manager.getCGPA()));
        }
    }

    private static void printMenu() {
        System.out.println("\n  ┌──────────────────────────────────────────┐");
        System.out.println("  │                 MAIN MENU                 │");
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.println("  │  1. Add New Semester                      │");
        System.out.println("  │  2. View Semester Details                 │");
        System.out.println("  │  3. Dashboard  (CGPA + Overview)          │");
        System.out.println("  │  4. SGPA Trend Chart  (ASCII)             │");
        System.out.println("  │  5. Grade Predictor  (Target CGPA)        │");
        System.out.println("  │  6. Core vs Elective Analysis             │");
        System.out.println("  │  7. Delete a Semester                     │");
        System.out.println("  │  0. Exit                                  │");
        System.out.println("  └──────────────────────────────────────────┘");
        System.out.print("  Choose: ");
    }

    /** Safe integer read — returns -1 on invalid input. */
    private static int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Safe double read — returns 0.0 on invalid input. */
    private static double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
