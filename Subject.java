/**
 * Subject.java
 * Represents a single academic subject with its credit hours and grade.
 * Also stores internal + external marks for the grade prediction feature.
 *
 * Demonstrates: Encapsulation, constructors, getters/setters, toString().
 *
 * Author: Aritra Chowdhury
 * Course: Programming in Java - BYOP Project
 */
public class Subject {

    private String  name;
    private int     credits;      // Credit hours (typically 1–4)
    private GradePoint grade;     // Letter grade (enum)
    private double  internalMark; // Marks out of 50 (internal assessment)
    private double  externalMark; // Marks out of 100 (end-semester exam), -1 if not entered
    private String  type;         // "Core" or "Elective"

    // ── Full constructor (used when loading from file) ────────────────────
    public Subject(String name, int credits, GradePoint grade,
                   double internalMark, double externalMark, String type) {
        this.name         = name;
        this.credits      = credits;
        this.grade        = grade;
        this.internalMark = internalMark;
        this.externalMark = externalMark;
        this.type         = type;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String     getName()         { return name; }
    public int        getCredits()      { return credits; }
    public GradePoint getGrade()        { return grade; }
    public double     getGradePoints()  { return grade.getPoints(); }
    public double     getInternalMark() { return internalMark; }
    public double     getExternalMark() { return externalMark; }
    public String     getType()         { return type; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setGrade(GradePoint grade) { this.grade = grade; }

    /** Returns credit × grade_points (used in GPA calculation). */
    public double getWeightedPoints() {
        return credits * grade.getPoints();
    }

    /** True if the subject is failed. */
    public boolean isFailed() {
        return grade.isFail();
    }

    /**
     * Converts this subject to a CSV-safe string.
     * Format: name|credits|gradeCode|internalMark|externalMark|type
     * (pipe-delimited to avoid conflict with commas in subject names)
     */
    public String toCsvString() {
        return name + "|" + credits + "|" + grade.shortCode()
                + "|" + internalMark + "|" + externalMark + "|" + type;
    }

    /**
     * Parses a CSV line (pipe-delimited) back into a Subject object.
     */
    public static Subject fromCsvString(String csv) {
        String[] parts = csv.split("\\|", 6);
        if (parts.length < 6) return null;
        try {
            String     name         = parts[0].trim();
            int        credits      = Integer.parseInt(parts[1].trim());
            GradePoint grade        = GradePoint.fromName(parts[2].trim());
            double     internalMark = Double.parseDouble(parts[3].trim());
            double     externalMark = Double.parseDouble(parts[4].trim());
            String     type         = parts[5].trim();
            return new Subject(name, credits, grade, internalMark, externalMark, type);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        String extStr = (externalMark < 0) ? "N/A  " : String.format("%-5.1f", externalMark);
        return String.format("  %-30s | %-4s | %s | Cr:%-2d | Int:%-5.1f | Ext:%s | %s",
                name,
                grade.shortCode(),
                String.format("%.1f", grade.getPoints()),
                credits,
                internalMark,
                extStr,
                type);
    }
}
