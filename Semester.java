import java.util.ArrayList;
import java.util.List;

/**
 * Semester.java
 * Represents one academic semester. Contains a list of Subject objects
 * and can compute the Semester Grade Point Average (SGPA).
 *
 * SGPA formula (CBCS):
 *   SGPA = Σ(credit_i × grade_point_i) / Σ(credit_i)
 *
 * Demonstrates: ArrayList, iteration, arithmetic logic, method design.
 *
 * Author: [Your Name]
 * Course: Programming in Java - BYOP Project
 */
public class Semester {

    private int           semNumber;   // e.g., 1, 2, 3 ...
    private String        label;       // e.g., "Semester 1 (2024-25 Odd)"
    private List<Subject> subjects;    // all subjects this semester

    public Semester(int semNumber, String label) {
        this.semNumber = semNumber;
        this.label     = label;
        this.subjects  = new ArrayList<>();
    }

    // ── Subject management ────────────────────────────────────────────────

    public void addSubject(Subject s) {
        subjects.add(s);
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public int    getSemNumber() { return semNumber; }
    public String getLabel()     { return label; }

    // ── Calculations ──────────────────────────────────────────────────────

    /**
     * Total credits in this semester.
     */
    public int getTotalCredits() {
        int total = 0;
        for (Subject s : subjects) total += s.getCredits();
        return total;
    }

    /**
     * Sum of (credit × grade_point) for all subjects.
     */
    public double getTotalWeightedPoints() {
        double total = 0;
        for (Subject s : subjects) total += s.getWeightedPoints();
        return total;
    }

    /**
     * Computes SGPA for this semester.
     * Returns 0.0 if no subjects have been added yet.
     */
    public double getSGPA() {
        int totalCredits = getTotalCredits();
        if (totalCredits == 0) return 0.0;
        return getTotalWeightedPoints() / totalCredits;
    }

    /**
     * Returns a list of subjects where the student received a failing grade.
     */
    public List<Subject> getFailedSubjects() {
        List<Subject> failed = new ArrayList<>();
        for (Subject s : subjects) {
            if (s.isFailed()) failed.add(s);
        }
        return failed;
    }

    /**
     * Number of backlogs (failed subjects) in this semester.
     */
    public int getBacklogCount() {
        return getFailedSubjects().size();
    }

    /**
     * Whether the student passed all subjects this semester.
     */
    public boolean isFullyCleared() {
        return getBacklogCount() == 0;
    }

    /**
     * Displays all subjects of this semester in a formatted table.
     */
    public void displaySubjects() {
        if (subjects.isEmpty()) {
            System.out.println("  (No subjects added yet.)");
            return;
        }
        System.out.printf("  %-30s | %-4s | %s | %-5s | %-12s | %-9s | %s%n",
                "Subject", "Grd", "GP", "Cr", "Internal/50", "External", "Type");
        System.out.println("  " + "─".repeat(90));
        for (Subject s : subjects) System.out.println(s);
        System.out.println("  " + "─".repeat(90));
        System.out.printf("  Total Credits: %-4d | SGPA: %.2f%n",
                getTotalCredits(), getSGPA());
        List<Subject> failed = getFailedSubjects();
        if (!failed.isEmpty()) {
            System.out.println("  ⚠  Backlogs: " + failed.size());
            for (Subject f : failed) System.out.println("      - " + f.getName());
        }
    }

    @Override
    public String toString() {
        return String.format("Sem %-2d | %-38s | SGPA: %.2f | Credits: %-3d | Backlogs: %d",
                semNumber, label, getSGPA(), getTotalCredits(), getBacklogCount());
    }
}
