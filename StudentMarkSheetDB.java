import java.util.Scanner;
import java.sql.*;

class Student {
    String name;
    int id;
    protected static int idCounter = 100000000;

    void getStudentDetails(Scanner sc) {
        id = idCounter++;

        while (true) {
            try {
                System.out.print("Enter Student Name: ");
                name = sc.nextLine().trim();

                if (name.isEmpty())
                    throw new Exception("Name cannot be empty!");
                if (!name.matches("[a-zA-Z ]+"))
                    throw new Exception("Name must contain only letters!");

                break;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}

class MarkSheet extends Student {
    int m1, m2, m3;

    void getMarks(Scanner sc) {
        while (true) {
            try {
                System.out.print("Enter subject 1 mark: ");
                m1 = Integer.parseInt(sc.nextLine());

                System.out.print("Enter subject 2 mark: ");
                m2 = Integer.parseInt(sc.nextLine());

                System.out.print("Enter subject 3 mark: ");
                m3 = Integer.parseInt(sc.nextLine());

                if (m1 < 0 || m1 > 100 || m2 < 0 || m2 > 100 || m3 < 0 || m3 > 100)
                    throw new Exception("Marks must be between 0 and 100!");

                break;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    int getTotal() {
        return m1 + m2 + m3;
    }

    double calculateAvg() {
        return getTotal() / 3.0;
    }
}

public class StudentMarkSheetDB {

    static Scanner sc = new Scanner(System.in);

    // 🔹 Database Connection Method
    static Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/studentdb",
                "studentuser","Student@123"   
        );
    }

    public static void main(String[] args) {

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

    // 🔹 INSERT
    static void addStudents() {
        int n;

        try {
            System.out.print("Enter number of students to add: ");
            n = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid number.");
            return;
        }

        try (Connection con = getConnection()) {

            String sql = "INSERT INTO students (id, name, m1, m2, m3) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            for (int i = 0; i < n; i++) {
                System.out.println("\nEnter Details of Student " + (i + 1));

                MarkSheet student = new MarkSheet();
                student.getStudentDetails(sc);
                student.getMarks(sc);

                ps.setInt(1, student.id);
                ps.setString(2, student.name);
                ps.setInt(3, student.m1);
                ps.setInt(4, student.m2);
                ps.setInt(5, student.m3);

                ps.executeUpdate();
                System.out.println("Student added successfully!");
            }

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // 🔹 SELECT ALL
    static void viewAllStudents() {
        try (Connection con = getConnection()) {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");

            while (rs.next()) {
                MarkSheet s = new MarkSheet();
                s.id = rs.getInt("id");
                s.name = rs.getString("name");
                s.m1 = rs.getInt("m1");
                s.m2 = rs.getInt("m2");
                s.m3 = rs.getInt("m3");

                displayStudent(s);
            }

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // 🔹 SEARCH BY ID
    static void searchStudentById() {
        System.out.print("Enter student ID to search: ");

        int searchId;
        try {
            searchId = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid ID format.");
            return;
        }

        try (Connection con = getConnection()) {

            String sql = "SELECT * FROM students WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, searchId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MarkSheet s = new MarkSheet();
                s.id = rs.getInt("id");
                s.name = rs.getString("name");
                s.m1 = rs.getInt("m1");
                s.m2 = rs.getInt("m2");
                s.m3 = rs.getInt("m3");

                displayStudent(s);
            } else {
                System.out.println("Student not found.");
            }

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    static void displayStudent(MarkSheet s) {
        System.out.println("\nStudent ID: " + s.id);
        System.out.println("Student Name: " + s.name);
        System.out.println("Subject 1 mark: " + s.m1);
        System.out.println("Subject 2 mark: " + s.m2);
        System.out.println("Subject 3 mark: " + s.m3);
        System.out.println("Total: " + s.getTotal());
        System.out.println("Average: " + s.calculateAvg());
        System.out.println("--------------------------");
    }
}

