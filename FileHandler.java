import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler.java
 * Handles persistent storage of academic data.
 * Each semester is saved as a separate CSV file: semester_1.csv, semester_2.csv, etc.
 *
 * File format (pipe-delimited subject rows):
 *   Line 1: semNumber|label
 *   Line 2+: subject CSV strings (pipe-delimited fields)
 *
 * Demonstrates: File I/O with BufferedReader/BufferedWriter,
 *               directory listing, try-with-resources.
 *
 * Author: Aritra Chowdhury
 * Course: Programming in Java - BYOP Project
 */
public class FileHandler {

    private static final String DATA_DIR = "data/";

    static {
        // Create the data directory if it does not exist
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ── Save a single semester ────────────────────────────────────────────

    /**
     * Saves one Semester object to its own CSV file.
     *
     * @param sem The semester to save
     */
    public static void saveSemester(Semester sem) {
        String filePath = DATA_DIR + "semester_" + sem.getSemNumber() + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Header line: metadata
            writer.write(sem.getSemNumber() + "|" + sem.getLabel());
            writer.newLine();

            // One line per subject
            for (Subject s : sem.getSubjects()) {
                writer.write(s.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [Error] Could not save semester " + sem.getSemNumber()
                    + ": " + e.getMessage());
        }
    }

    // ── Load all semesters ────────────────────────────────────────────────

    /**
     * Scans the data directory and loads all semester CSV files.
     * Files are loaded in order (semester_1, semester_2, ...).
     *
     * @return List of Semester objects, sorted by semester number
     */
    public static List<Semester> loadAllSemesters() {
        List<Semester> semesters = new ArrayList<>();
        File dir = new File(DATA_DIR);

        File[] files = dir.listFiles((d, name) ->
                name.startsWith("semester_") && name.endsWith(".csv"));

        if (files == null || files.length == 0) return semesters;

        // Sort files by semester number
        java.util.Arrays.sort(files, (a, b) -> {
            int numA = extractNumber(a.getName());
            int numB = extractNumber(b.getName());
            return Integer.compare(numA, numB);
        });

        for (File file : files) {
            Semester sem = loadSemesterFile(file);
            if (sem != null) semesters.add(sem);
        }

        return semesters;
    }

    // ── Load one semester file ────────────────────────────────────────────

    private static Semester loadSemesterFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // First line: metadata
            String headerLine = reader.readLine();
            if (headerLine == null) return null;

            String[] header = headerLine.split("\\|", 2);
            if (header.length < 2) return null;

            int    semNumber = Integer.parseInt(header[0].trim());
            String label     = header[1].trim();

            Semester sem = new Semester(semNumber, label);

            // Remaining lines: subjects
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Subject s = Subject.fromCsvString(line);
                if (s != null) sem.addSubject(s);
            }

            return sem;

        } catch (IOException | NumberFormatException e) {
            System.out.println("  [Warning] Could not read " + file.getName()
                    + ": " + e.getMessage());
            return null;
        }
    }

    // ── Delete a semester file ────────────────────────────────────────────

    /**
     * Deletes the saved file for a given semester number.
     *
     * @param semNumber The semester number to delete
     * @return true if deleted, false if file not found
     */
    public static boolean deleteSemester(int semNumber) {
        File file = new File(DATA_DIR + "semester_" + semNumber + ".csv");
        return file.exists() && file.delete();
    }

    // ── Utility ───────────────────────────────────────────────────────────

    /**
     * Extracts the numeric part from a filename like "semester_3.csv" → 3.
     */
    private static int extractNumber(String filename) {
        try {
            String num = filename.replace("semester_", "").replace(".csv", "");
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
