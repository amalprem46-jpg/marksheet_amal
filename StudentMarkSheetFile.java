import java.util.Scanner;
import java.io.*;

// Base class: Student
class Student {
    String name;
    int id;
    protected static int idCounter = 100000000;

    // Get student details from user
    void getStudentDetails(Scanner sc) {
        id = idCounter++;

        while (true) {
            try {
                System.out.print("Enter Student Name: ");
                name = sc.nextLine().trim();

                if (name.isEmpty()) {
                    throw new Exception("Name cannot be empty!");
                }
                if (!name.matches("[a-zA-Z ]+")) {
                    throw new Exception("Name must contain only letters!");
                }
                break;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Initialize ID counter from file
    static void initializeIdCounterFromFile(String fileName) {
        File file = new File(fileName);

        if (!file.exists()) return; // File not found → keep default counter

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line, lastLine = null;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            if (lastLine != null) {
                String[] parts = lastLine.split(",");
                int lastId = Integer.parseInt(parts[0]);
                idCounter = lastId + 1;
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Warning: Could not initialize ID counter from file.");
        }
    }
}

// Derived class: MarkSheet
class MarkSheet extends Student {
    int m1, m2, m3;

    // Get marks from user
    void getMarks(Scanner sc) {
        while (true) {
            try {
                System.out.print("Enter subject 1 mark: ");
                String s1 = sc.nextLine().trim();
                System.out.print("Enter subject 2 mark: ");
                String s2 = sc.nextLine().trim();
                System.out.print("Enter subject 3 mark: ");
                String s3 = sc.nextLine().trim();

                try {
                    m1 = Integer.parseInt(s1);
                    m2 = Integer.parseInt(s2);
                    m3 = Integer.parseInt(s3);
                } catch (NumberFormatException ne) {
                    throw new Exception("Marks must be integers!");
                }

                if (m1 < 0 || m1 > 100 || m2 < 0 || m2 > 100 || m3 < 0 || m3 > 100) {
                    throw new Exception("Marks must be between 0 and 100!");
                }
                break;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    int getTotal() { return m1 + m2 + m3; }
    double calculateAvg() { return getTotal() / 3.0; }

    String toFileString() {
        return id + "," + name + "," + m1 + "," + m2 + "," + m3 + "," + getTotal() + "," + calculateAvg();
    }

    static MarkSheet fromFileString(String line) {
        String[] parts = line.split(",");
        MarkSheet s = new MarkSheet();
        s.id = Integer.parseInt(parts[0]);
        s.name = parts[1];
        s.m1 = Integer.parseInt(parts[2]);
        s.m2 = Integer.parseInt(parts[3]);
        s.m3 = Integer.parseInt(parts[4]);
        return s;
    }
}

// Main class
public class StudentMarkSheetFile {
    static String fileName = "students_marksheet.txt";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize ID counter from existing file
        Student.initializeIdCounterFromFile(fileName);

        while (true) {
            showMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addStudents();
                    break;
                case "2":
                    viewAllStudents();
                    break;
                case "3":
                    searchStudentById();
                    break;
                case "4":
                    System.out.println("Exiting program. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void showMenu() {
        System.out.println("\n==== STUDENT MARKSHEET MENU ====");
        System.out.println("1. Add new student(s)");
        System.out.println("2. View all students");
        System.out.println("3. Search student by ID");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }

    static void addStudents() {
        int n = 0;
        while (true) {
            try {
                System.out.print("Enter number of students to add: ");
                String input = sc.nextLine().trim();
                n = Integer.parseInt(input);
                if (n <= 0) throw new Exception("Number must be > 0");
                break;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            for (int i = 0; i < n; i++) {
                System.out.println("\nEnter Details of Student " + (i + 1));
                MarkSheet student = new MarkSheet();
                student.getStudentDetails(sc);
                student.getMarks(sc);

                writer.write(student.toFileString());
                writer.newLine();
                System.out.println("Student added successfully!");
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    static void viewAllStudents() {
        System.out.println("\n------ ALL STUDENTS ------");
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                MarkSheet s = MarkSheet.fromFileString(line);
                displayStudent(s);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No student records found.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    static void searchStudentById() {
        System.out.print("Enter student ID to search: ");
        String input = sc.nextLine().trim();
        int searchId = 0;

        try {
            searchId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
            return;
        }

        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                MarkSheet s = MarkSheet.fromFileString(line);
                if (s.id == searchId) {
                    System.out.println("\n--- STUDENT FOUND ---");
                    displayStudent(s);
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Student with ID " + searchId + " not found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    static void displayStudent(MarkSheet s) {
        System.out.println("Student ID: " + s.id);
        System.out.println("Student Name: " + s.name);
        System.out.println("Subject 1 mark: " + s.m1);
        System.out.println("Subject 2 mark: " + s.m2);
        System.out.println("Subject 3 mark: " + s.m3);
        System.out.println("Total: " + s.getTotal());
        System.out.println("Average: " + s.calculateAvg());
        System.out.println("--------------------------");
    }
}

