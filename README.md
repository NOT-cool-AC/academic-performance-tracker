# 🎓 Academic Performance Tracker & CGPA Analyzer

A command-line Java application that tracks semester-wise academic performance, computes SGPA and CGPA using the **CBCS 10-point grading system** (used by VTU, Anna University, AKTU, and most Indian universities), visualizes trends, and predicts the grade needed to hit a target CGPA.

Built as a **BYOP (Bring Your Own Project)** capstone for the *Programming in Java* course.

---

## 📌 Problem Statement

Every semester, students get their results and wonder: *"What's my CGPA now? Do I have backlogs? What SGPA do I need next semester to get a First Class?"*

Most students calculate these manually — prone to errors, and the data is lost after each calculation. This application solves that by providing a **persistent, multi-semester academic record** with smart analysis features.

---

## ✨ Features

| Feature | Description |
|---|---|
| ➕ **Add Semester** | Enter all subjects with grades, credits, internal/external marks |
| 📊 **Dashboard** | Complete semester-wise SGPA table, CGPA, classification, backlog alert |
| 📈 **ASCII Trend Chart** | Visual bar chart of SGPA progression across semesters |
| 🎯 **Grade Predictor** | "What SGPA do I need next semester to reach CGPA X?" |
| 🏷️ **Core vs Elective** | Average grade points broken down by subject type |
| ⚠️ **Backlog Detection** | Automatically flags failed subjects across all semesters |
| 💾 **Persistent Storage** | One CSV file per semester in `data/` — survives restarts |
| 🗑️ **Delete Semester** | Remove a semester and its data file |

---

## 🏛️ Grading System (CBCS 10-Point Scale)

| Grade | Description | Points |
|---|---|---|
| O | Outstanding | 10.0 |
| A+ | Excellent | 9.0 |
| A | Very Good | 8.0 |
| B+ | Good | 7.0 |
| B | Above Average | 6.0 |
| C | Average | 5.0 |
| P | Pass | 4.0 |
| F | Fail | 0.0 |

**SGPA** = Σ(credit × grade point) / Σ(credits) for one semester  
**CGPA** = Σ(all weighted points) / Σ(all credits) across all semesters

---

## 🛠️ Tech Stack

- **Language:** Java (JDK 8 or above)  
- **Input:** `java.util.Scanner`  
- **Storage:** One CSV file per semester via `BufferedReader` / `BufferedWriter`  
- **Data Structures:** `ArrayList` (subjects, semesters), `HashMap` (type analysis)  
- **Design:** Layered architecture — Model → Business Logic → File I/O → UI

---

## 📁 Project Structure

```
AcademicTracker/
├── src/
│   ├── Main.java              # Entry point — interactive CLI menu
│   ├── AcademicManager.java   # Core logic: CGPA, trend, predictor, analysis
│   ├── Semester.java          # Model: one semester with its subjects
│   ├── Subject.java           # Model: one subject/course entry
│   ├── GradePoint.java        # Enum: 10-point CBCS grade scale
│   └── FileHandler.java       # File I/O: one CSV per semester in data/
├── data/                      # Auto-created; holds semester_1.csv, semester_2.csv...
└── README.md
```

---

## ▶️ How to Run

### Prerequisites
- Java JDK 8 or above installed
- A terminal / command prompt

### Step 1 — Clone or Download
```bash
git clone https://github.com/YOUR_USERNAME/academic-tracker.git
cd academic-tracker
```

### Step 2 — Compile
```bash
javac -d out src/*.java
```

### Step 3 — Run
```bash
java -cp out Main
```

> **Windows:** Use `\` instead of `/`. Or open the project in IntelliJ IDEA / Eclipse.

---

## 🖥️ Usage Walkthrough

### Adding a Semester (Option 1)
```
  Semester Number: 3
  Semester Label : 2025-26 Odd

  Subject Name   : Data Structures
  Credit Hours   : 4
  Grade (1-8)    : 2        ← A+ (9.0)
  Internal /50   : 44
  External /100  : 88
  Type           : 1        ← Core

  ✓ Added: Data Structures → A+ (9.0 GP) | 4 credits
```

### Dashboard (Option 3)
```
  ─────────────────────────────────────────────────────────────────
  Sem    Label                               SGPA   Credits  Backlogs
  ─────────────────────────────────────────────────────────────────
  1      Semester 1 (2023-24 Odd)            7.80       22     Clear
  2      Semester 2 (2023-24 Even)           8.10       22     Clear
  3      Semester 3 (2024-25 Odd)            8.60       24     Clear
  ─────────────────────────────────────────────────────────────────
  CGPA (Credit-Weighted)                    8.19       68  All Clear
  ─────────────────────────────────────────────────────────────────

  Classification : First Class with Distinction
```

### SGPA Trend Chart (Option 4)
```
  10.0 |
       |
   9.0 |         ██
       |         ██
   8.0 |   ▓▓    ██
       |   ▓▓  ▓▓██
   7.0 |   ▓▓  ▓▓██
       +─────────────
         S1  S2  S3
        7.80 8.10 8.60
```

### Grade Predictor (Option 5)
```
  Current CGPA      : 8.19 (over 3 semesters, 68 credits)
  Target  CGPA      : 9.00
  Next sem credits  : 24
  ────────────────────────────────
  Required SGPA     : 9.58
  Equivalent to     : All O (Outstanding) grades
```

---

## 💾 Data Storage

Each semester is saved as `data/semester_N.csv`:

```
3|Semester 3 (2024-25 Odd)
Data Structures|4|A+|44.0|88.0|Core
Operating Systems|3|A|38.0|76.0|Core
Computer Networks|3|B+|35.0|70.0|Core
```

No database or external library required.

---

## 📖 Java Concepts Used

- **OOP** — Four model/logic classes, encapsulation, single responsibility
- **Enum** — `GradePoint` with methods (`fromIndex`, `fromName`, `shortCode`)
- **ArrayList** — dynamic lists of subjects and semesters
- **HashMap** — aggregating performance by subject type in `showTypeAnalysis()`
- **File I/O** — `BufferedReader`/`BufferedWriter`, `File`, directory listing
- **Exception Handling** — `try-catch` for I/O errors and number parsing
- **Algorithms** — CGPA formula, grade prediction formula (algebraic rearrangement)
- **String.format** — all table output aligned with `printf`-style formatting
- **Sorting** — `Arrays.sort` with lambda comparator for ordering semester files

---

## 🙋 Author

- **Name:** Aritra Chowdhury
- **Reg No.:** 24BAI10023
- **Course:** B.Tech CSE (AI-ML)  
- **Subject:** Programming in Java (BYOP)

---

## 📄 License

Submitted for academic evaluation. Free for learning and reference.
