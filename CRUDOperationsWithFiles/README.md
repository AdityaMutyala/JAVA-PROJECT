# 📄 File-Based SQL Executor with MySQL Integration

This project is a **Java console-based application** that integrates with **MySQL** to perform **file-driven and manual SQL CRUD operations**. It is designed for users who want a flexible system to execute SQL statements stored in files, along with safe and interactive terminal-based SQL execution.

---

## ✨ Features

- 📁 **Execute SQL from File**  
  Load and run SQL statements for table creation or data insertion directly from `.txt` files.

- ✍️ **Manual Update & Delete with Safety Checks**  
  Accepts typed `UPDATE` and `DELETE` queries with operation-specific validation and confirmation prompts.

- 👁️ **Display Table Contents**  
  View all rows of any user-specified table using dynamic metadata handling.

- 🔄 **Dynamic SQL Parsing**  
  Supports multiple SQL statements per file, with auto-detection of statement boundaries using `;`.

- 🛡️ **Execution Feedback**  
  Displays affected rows, identifies the type of operation (CREATE, INSERT, UPDATE, DELETE), and provides clear status messages.

---

## 🛠️ Technologies Used

- Java (JDK 17+)
- JDBC
- MySQL Database
- Text Files for SQL Scripts

---

## 📋 Menu Options

1. **Create Table from File** – Provide a filename containing `CREATE TABLE` SQL to run.  
2. **Insert Data from File** – Execute multiple `INSERT INTO` statements from file.  
3. **Update Data (manual)** – Enter your `UPDATE` SQL manually in the terminal.  
4. **Delete Data (manual)** – Enter a `DELETE` SQL statement and confirm before execution.  
5. **Display Table** – Type any table name and view its entire data.  
6. **Exit** – Close the application.

---

## 🗂️ Sample SQL File (Example: `create_emp.txt`)
```sql
CREATE TABLE employee (
    id INT PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(50)
);
INSERT INTO employee VALUES (1, 'Aditya', 'IT');
INSERT INTO employee VALUES (2, 'Rahul', 'HR');
