import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AcademicManager.java
 * Core business logic layer for the Academic Performance Tracker.
 *
 * Responsibilities:
 *   - Manage list of semesters (add, view, delete)
 *   - Compute CGPA across all semesters
 *   - Identify all backlogs (failed subjects)
 *   - Show an ASCII trend chart of SGPA over semesters
 *   - Predict the grade needed to reach a target CGPA (grade predictor)
 *   - Summarize performance by subject type (Core vs Elective)
 *
 * Java concepts: ArrayList, HashMap, nested loops, arithmetic,
 *                enhanced for-loop, static methods.
 *
 * Author: [Your Name]
 * Course: Programming in Java - BYOP Project
 */
public class AcademicManager {

    private List<Semester> semesters;

    public AcademicManager() {
        semesters = FileHandler.loadAllSemesters();
    }

    // ── Semester Management ───────────────────────────────────────────────

    public void addSemester(Semester sem) {
        // Prevent duplicate semester numbers
        for (Semester existing : semesters) {
            if (existing.getSemNumber() == sem.getSemNumber()) {
                System.out.println("  [Error] Semester " + sem.getSemNumber()
                        + " already exists. Delete it first to re-enter.");
                return;
            }
        }
        semesters.add(sem);
        // Keep sorted by semester number
        semesters.sort((a, b) -> Integer.compare(a.getSemNumber(), b.getSemNumber()));
        FileHandler.saveSemester(sem);
        System.out.println("  Semester saved successfully.");
    }

    public Semester getSemester(int semNumber) {
        for (Semester s : semesters) {
            if (s.getSemNumber() == semNumber) return s;
        }
        return null;
    }

    public List<Semester> getAllSemesters() {
        return semesters;
    }

    public int getSemesterCount() {
        return semesters.size();
    }

    public void deleteSemester(int semNumber) {
        Semester toRemove = null;
        for (Semester s : semesters) {
            if (s.getSemNumber() == semNumber) { toRemove = s; break; }
        }
        if (toRemove == null) {
            System.out.println("  [Error] Semester " + semNumber + " not found.");
            return;
        }
        semesters.remove(toRemove);
        FileHandler.deleteSemester(semNumber);
        System.out.println("  Semester " + semNumber + " deleted.");
    }

    // ── CGPA Calculation ──────────────────────────────────────────────────

    /**
     * Computes CGPA using the CBCS formula:
     *   CGPA = Σ(semester weighted points) / Σ(semester total credits)
     *
     * This is the credit-weighted average across ALL semesters.
     */
    public double getCGPA() {
        double totalWeighted = 0;
        int    totalCredits  = 0;
        for (Semester s : semesters) {
            totalWeighted += s.getTotalWeightedPoints();
            totalCredits  += s.getTotalCredits();
        }
        return (totalCredits == 0) ? 0.0 : totalWeighted / totalCredits;
    }

    // ── Dashboard Overview ────────────────────────────────────────────────

    /**
     * Prints the complete dashboard: semester list, CGPA, total credits, backlogs.
     */
    public void showDashboard() {
        if (semesters.isEmpty()) {
            System.out.println("\n  No semesters recorded yet. Add your first semester!");
            return;
        }

        System.out.println("\n  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║              ACADEMIC PERFORMANCE DASHBOARD                  ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝");

        // Semester-wise table
        System.out.println("\n  ─────────────────────────────────────────────────────────────────");
        System.out.printf("  %-6s %-38s %6s  %7s  %8s%n",
                "Sem", "Label", "SGPA", "Credits", "Backlogs");
        System.out.println("  ─────────────────────────────────────────────────────────────────");

        int totalCredits = 0;
        int totalBacklogs = 0;
        for (Semester s : semesters) {
            System.out.printf("  %-6d %-38s %6.2f  %7d  %8s%n",
                    s.getSemNumber(),
                    truncate(s.getLabel(), 38),
                    s.getSGPA(),
                    s.getTotalCredits(),
                    s.getBacklogCount() > 0 ? "⚠ " + s.getBacklogCount() : "Clear");
            totalCredits  += s.getTotalCredits();
            totalBacklogs += s.getBacklogCount();
        }

        System.out.println("  ─────────────────────────────────────────────────────────────────");
        System.out.printf("  %-45s %6.2f  %7d  %8s%n",
                "CGPA (Credit-Weighted)", getCGPA(), totalCredits,
                totalBacklogs > 0 ? "⚠ " + totalBacklogs : "All Clear");
        System.out.println("  ─────────────────────────────────────────────────────────────────");

        // Classification
        System.out.println("\n  Classification : " + getClassification(getCGPA()));

        // Backlog summary
        if (totalBacklogs > 0) {
            System.out.println("\n  ⚠  BACKLOG ALERT:");
            for (Semester s : semesters) {
                for (Subject sub : s.getFailedSubjects()) {
                    System.out.println("     Sem " + s.getSemNumber()
                            + " → " + sub.getName()
                            + " (" + sub.getCredits() + " cr)");
                }
            }
        }
    }

    // ── ASCII Trend Chart ─────────────────────────────────────────────────

    /**
     * Draws a vertical ASCII bar chart of SGPA per semester.
     * Each bar represents one semester; height is proportional to SGPA (max 10).
     */
    public void showTrendChart() {
        if (semesters.isEmpty()) {
            System.out.println("\n  No data to plot yet.");
            return;
        }

        System.out.println("\n  ── SGPA Trend Chart ─────────────────────────────────────────");
        System.out.println("  (Each row = 0.5 SGPA. Max = 10.0)\n");

        int maxRows = 20; // 20 rows × 0.5 = 10.0 max
        int barWidth = 6;

        for (int row = maxRows; row >= 1; row--) {
            double threshold = row * 0.5;
            // Y-axis label every 2 rows (every 1.0 point)
            if (row % 2 == 0) {
                System.out.printf("  %4.1f |", threshold);
            } else {
                System.out.print("       |");
            }
            for (Semester s : semesters) {
                if (s.getSGPA() >= threshold) {
                    // Filled bar segment
                    String fill = (s.getSGPA() >= 9.0) ? "█" :
                                  (s.getSGPA() >= 7.0) ? "▓" :
                                  (s.getSGPA() >= 5.0) ? "░" : "·";
                    System.out.print("  " + fill + fill + "  ");
                } else {
                    System.out.print("       ");
                }
            }
            System.out.println();
        }

        // X-axis line
        System.out.print("  0.0  +" + "─".repeat(semesters.size() * 7));
        System.out.println();

        // X-axis labels (semester numbers)
        System.out.print("         ");
        for (Semester s : semesters) {
            System.out.printf("  S%-4d", s.getSemNumber());
        }
        System.out.println();

        // SGPA values below chart
        System.out.print("         ");
        for (Semester s : semesters) {
            System.out.printf(" %.2f ", s.getSGPA());
        }
        System.out.println("\n");
        System.out.println("  Legend: █ = 9.0+  ▓ = 7.0-8.9  ░ = 5.0-6.9  · = below 5.0");
    }

    // ── Grade Predictor ───────────────────────────────────────────────────

    /**
     * Predicts what SGPA the student needs in the NEXT semester
     * to reach a desired CGPA by end of that semester.
     *
     * Formula (derived from CGPA definition):
     *   targetCGPA = (currentTotalWeighted + nextSGPA × nextCredits)
     *                / (currentTotalCredits + nextCredits)
     *
     *   => nextSGPA = (targetCGPA × (currentCredits + nextCredits)
     *                  - currentTotalWeighted) / nextCredits
     *
     * @param targetCGPA  Desired CGPA after next semester
     * @param nextCredits Expected total credits in the next semester
     */
    public void predictRequiredSGPA(double targetCGPA, int nextCredits) {
        if (semesters.isEmpty()) {
            System.out.println("\n  No existing semester data. Add at least one semester first.");
            return;
        }
        if (targetCGPA < 0 || targetCGPA > 10) {
            System.out.println("\n  [Error] Target CGPA must be between 0 and 10.");
            return;
        }
        if (nextCredits <= 0) {
            System.out.println("\n  [Error] Next semester credits must be a positive number.");
            return;
        }

        double currentWeighted = 0;
        int    currentCredits  = 0;
        for (Semester s : semesters) {
            currentWeighted += s.getTotalWeightedPoints();
            currentCredits  += s.getTotalCredits();
        }

        double requiredSGPA = (targetCGPA * (currentCredits + nextCredits) - currentWeighted)
                               / nextCredits;

        System.out.println("\n  ── Grade Predictor Results ─────────────────────────────────");
        System.out.printf("  Current CGPA      : %.2f (over %d semesters, %d credits)%n",
                getCGPA(), semesters.size(), currentCredits);
        System.out.printf("  Target  CGPA      : %.2f%n", targetCGPA);
        System.out.printf("  Next sem credits  : %d%n", nextCredits);
        System.out.println("  ────────────────────────────────────────────────────────────");

        if (requiredSGPA <= 0) {
            System.out.println("  You have already exceeded your target CGPA!");
        } else if (requiredSGPA > 10.0) {
            System.out.printf("  Required SGPA: %.2f — NOT achievable (max is 10.0).%n", requiredSGPA);
            System.out.println("  Consider setting a lower target CGPA.");
        } else {
            System.out.printf("  Required SGPA : %.2f%n", requiredSGPA);
            System.out.println("  Equivalent to : " + sgpaToGradeHint(requiredSGPA));
        }
        System.out.println("  ────────────────────────────────────────────────────────────");
    }

    // ── Subject Type Analysis (Core vs Elective) ──────────────────────────

    /**
     * Breaks down average grade points by subject type using HashMap.
     * Demonstrates: HashMap aggregation pattern.
     */
    public void showTypeAnalysis() {
        if (semesters.isEmpty()) {
            System.out.println("\n  No data available.");
            return;
        }

        // Map: type → [totalWeighted, totalCredits, subjectCount]
        Map<String, double[]> typeMap = new HashMap<>();

        for (Semester sem : semesters) {
            for (Subject sub : sem.getSubjects()) {
                String type = sub.getType();
                typeMap.putIfAbsent(type, new double[]{0, 0, 0});
                double[] arr = typeMap.get(type);
                arr[0] += sub.getWeightedPoints();
                arr[1] += sub.getCredits();
                arr[2] += 1;
            }
        }

        System.out.println("\n  ── Performance by Subject Type ──────────────────────────────");
        System.out.printf("  %-15s  %-10s  %-10s  %-12s%n",
                "Type", "Subjects", "Credits", "Avg GP");
        System.out.println("  " + "─".repeat(52));

        for (Map.Entry<String, double[]> entry : typeMap.entrySet()) {
            double[] arr = entry.getValue();
            double avgGP = arr[1] > 0 ? arr[0] / arr[1] : 0;
            System.out.printf("  %-15s  %-10.0f  %-10.0f  %-12.2f%n",
                    entry.getKey(), arr[2], arr[1], avgGP);
        }
        System.out.println("  " + "─".repeat(52));
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Returns a classification string based on CGPA.
     */
    public static String getClassification(double cgpa) {
        if (cgpa >= 9.0) return "Outstanding (O)";
        if (cgpa >= 8.0) return "First Class with Distinction";
        if (cgpa >= 6.5) return "First Class";
        if (cgpa >= 5.5) return "Second Class";
        if (cgpa >= 4.0) return "Pass";
        return "Fail — Below minimum";
    }

    /**
     * Returns a human-friendly grade hint for a target SGPA.
     */
    private String sgpaToGradeHint(double sgpa) {
        if (sgpa >= 9.5) return "All O (Outstanding) grades";
        if (sgpa >= 8.5) return "Mostly A+ (9.0) grades";
        if (sgpa >= 7.5) return "Mostly A (8.0) grades";
        if (sgpa >= 6.5) return "Mostly B+ (7.0) grades";
        if (sgpa >= 5.5) return "Mostly B (6.0) grades";
        if (sgpa >= 4.5) return "Mostly C (5.0) grades";
        return "Minimum passing grades (P = 4.0)";
    }

    /** Truncates a string to maxLen, appending "…" if needed. */
    private static String truncate(String s, int maxLen) {
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 1) + "…";
    }
}
