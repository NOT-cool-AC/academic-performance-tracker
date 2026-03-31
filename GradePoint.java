/**
 * GradePoint.java
 * Enum representing letter grades under the CBCS 10-point grading system
 * used by most Indian universities (VTU, Anna University, AKTU, etc.).
 *
 * Each grade stores its display label and the corresponding grade point value.
 *
 * Author: Aritra Chowdhury
 * Course: Programming in Java - BYOP Project
 */
public enum GradePoint {

    O  ("O  — Outstanding",   10.0),
    A_PLUS("A+ — Excellent",  9.0),
    A  ("A  — Very Good",      8.0),
    B_PLUS("B+ — Good",        7.0),
    B  ("B  — Above Average",  6.0),
    C  ("C  — Average",        5.0),
    P  ("P  — Pass",           4.0),
    F  ("F  — Fail",           0.0);

    private final String label;
    private final double points;

    GradePoint(String label, double points) {
        this.label  = label;
        this.points = points;
    }

    public String getLabel()  { return label; }
    public double getPoints() { return points; }

    /** Whether this grade is a failing grade. */
    public boolean isFail() {
        return this == F;
    }

    /**
     * Returns a numbered menu string listing all grade options.
     */
    public static String listAll() {
        StringBuilder sb = new StringBuilder();
        GradePoint[] vals = values();
        for (int i = 0; i < vals.length; i++) {
            sb.append("    ").append(i + 1).append(". ").append(vals[i].label);
            if (i < vals.length - 1) sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a GradePoint from a 1-based menu index.
     * Defaults to F if out of range.
     */
    public static GradePoint fromIndex(int index) {
        GradePoint[] vals = values();
        if (index >= 1 && index <= vals.length) return vals[index - 1];
        return F;
    }

    /**
     * Returns a GradePoint from a stored string name (for CSV loading).
     * Defaults to F if unrecognized.
     */
    public static GradePoint fromName(String name) {
        switch (name.trim().toUpperCase()) {
            case "O":      return O;
            case "A+":     return A_PLUS;
            case "A":      return A;
            case "B+":     return B_PLUS;
            case "B":      return B;
            case "C":      return C;
            case "P":      return P;
            default:       return F;
        }
    }

    /**
     * Returns the short code (used for storage in CSV).
     */
    public String shortCode() {
        switch (this) {
            case O:      return "O";
            case A_PLUS: return "A+";
            case A:      return "A";
            case B_PLUS: return "B+";
            case B:      return "B";
            case C:      return "C";
            case P:      return "P";
            default:     return "F";
        }
    }
}
