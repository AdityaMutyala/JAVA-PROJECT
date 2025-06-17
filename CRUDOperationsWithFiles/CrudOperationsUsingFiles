import java.io.*;
import java.sql.*;
import java.util.*;

public class CrudOperationsUsingFiles {
    static final String DB_URL = "jdbc:mysql://localhost:3306/filedemo";
    static final String USER = "root"; 
    static final String PASS = "@aditya10"; 

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                System.out.println("Connected to MySQL Database successfully.");
                while (true) {
                    printMenu();
                    int choice = getChoice();

                    switch (choice) {
                        case 1 -> executeFromUserFile(conn, "CREATE TABLE");
                        case 2 -> executeFromUserFile(conn, "INSERT INTO");
                        case 3 -> executeUserSQL(conn, "UPDATE");
                        case 4 -> executeUserSQLWithConfirmation(conn, "DELETE");
                        case 5 -> displayTableFromUser(conn);
                        case 6 -> {
                            System.out.println("Exiting program. Goodbye!");
                            return;
                        }
                        default -> System.out.println("Invalid choice. Try again.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Connection Error:");
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Create Table from File");
        System.out.println("2. Insert Data from File");
        System.out.println("3. Update Data (manual)");
        System.out.println("4. Delete Data (manual)");
        System.out.println("5. Display Table");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getChoice() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void executeFromUserFile(Connection conn, String operationType) {
        System.out.print("Enter SQL filename (e.g., create_emp.txt): ");
        String fileName = sc.nextLine().trim();
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            return;
        }
        executeFromFile(fileName, conn);
    }

    private static void executeUserSQL(Connection conn, String action) {
        System.out.println("Enter your SQL statement for " + action + ":");
        String sql = sc.nextLine().trim();
        if (!sql.toLowerCase().startsWith(action.toLowerCase())) {
            System.out.println("SQL must start with '" + action + "'");
            return;
        }
        executeSQL(sql, conn);
    }

    private static void executeUserSQLWithConfirmation(Connection conn, String action) {
        System.out.println("Enter your SQL statement for " + action + ":");
        String sql = sc.nextLine().trim();
        if (!sql.toLowerCase().startsWith(action.toLowerCase())) {
            System.out.println("SQL must start with '" + action + "'");
            return;
        }
        System.out.print("Are you sure? This may delete data (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            executeSQL(sql, conn);
        } else {
            System.out.println("Delete operation canceled.");
        }
    }

    private static void executeFromFile(String fileName, Connection conn) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            int statementCount = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) continue;

                sqlBuilder.append(line).append(" ");

                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().replaceAll(";$", "").trim();
                    sql = sql.replaceAll(";", "");
                    executeSQL(sql, conn);
                    statementCount++;
                    sqlBuilder.setLength(0);
                }
            }
            if (sqlBuilder.length() > 0) {
                String sql = sqlBuilder.toString().trim();
                sql = sql.replaceAll(";", "");
                executeSQL(sql, conn);
                statementCount++;
            }

            System.out.println("Total SQL statements executed from file: " + statementCount);
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    private static void executeSQL(String sql, Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            String lowerSql = sql.toLowerCase();
            String tableName = extractTableName(lowerSql);

            if (lowerSql.startsWith("create table")) {
                System.out.println("Table '" + tableName + "' created successfully.");
            } else if (lowerSql.startsWith("insert into")) {
                System.out.println("Data inserted into table '" + tableName + "'. Rows affected: " + affectedRows);
            } else if (lowerSql.startsWith("update")) {
                System.out.println("Table '" + tableName + "' updated. Rows affected: " + affectedRows);
            } else if (lowerSql.startsWith("delete from")) {
                System.out.println("Data deleted from table '" + tableName + "'. Rows affected: " + affectedRows);
            } else {
                System.out.println("SQL executed successfully.");
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private static String extractTableName(String lowerSql) {
        String[] tokens = lowerSql.split("\\s+");
        if (lowerSql.startsWith("create table") || lowerSql.startsWith("insert into") || lowerSql.startsWith("delete from")) {
            if (tokens.length >= 3) return tokens[2].replaceAll("[\"'`;]", "");
        } else if (lowerSql.startsWith("update")) {
            if (tokens.length >= 2) return tokens[1].replaceAll("[\"'`;]", "");
        }
        return "UnknownTable";
    }

    private static void displayTableFromUser(Connection conn) {
        System.out.print("Enter table name to display: ");
        String tableName = sc.nextLine().trim();
        displayTable(tableName, conn);
    }

    private static void displayTable(String tableName, Connection conn) {
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            System.out.println("\nData from table: " + tableName);
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-15s", meta.getColumnName(i));
            }
            System.out.println("\n" + "-".repeat(15 * columnCount));

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-15s", rs.getString(i));
                }
                System.out.println();
            }

            if (!hasData) {
                System.out.println("No data found in table '" + tableName + "'.");
            }
        } catch (SQLException e) {
            System.out.println("Display Error: " + e.getMessage());
        }
    }
}
