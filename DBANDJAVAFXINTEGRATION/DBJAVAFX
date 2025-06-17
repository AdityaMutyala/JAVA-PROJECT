import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import java.util.stream.Collectors;

public class DBJAVAFX extends Application {
    private String url;
    private String user;
    private String password;
    public static class RowWrapper {
        private final ObservableList<String> data;
        private final BooleanProperty selected = new SimpleBooleanProperty(false);
        public RowWrapper(ObservableList<String> data) {
            this.data = data;
        }
        public ObservableList<String> getData() {
            return data;
        }
        public BooleanProperty selectedProperty() {
            return selected;
        }
        public boolean isSelected() {
            return selected.get();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
    private VBox leftPane,dynamicContentBox,messageBox;
    private TableView<RowWrapper> tableView;
    private VBox rightPane;
    private String currentTableName = null;
    @Override
    public void start(Stage primaryStage) {
        showLoginPane(primaryStage);  // Step 1 is just showing login
    }

    public void showMainUI(Stage primaryStage) {
        // Initialize BorderPane and panes FIRST
        
        BorderPane root = new BorderPane();
        dynamicContentBox = new VBox(10);
        ScrollPane dynamicScroll = new ScrollPane(dynamicContentBox);
        dynamicScroll.setFitToWidth(true);
        dynamicScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        dynamicScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(dynamicScroll, Priority.ALWAYS); // let it expand
        messageBox = new VBox(5);
        ScrollPane messageScroll = new ScrollPane(messageBox);
        messageScroll.setFitToWidth(true);
        messageScroll.setPrefHeight(250);
        messageScroll.setMinHeight(250);
        messageScroll.setMaxHeight(250);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftPane = new VBox(10, dynamicScroll, messageScroll);
        leftPane.setPadding(new Insets(15));
        leftPane.setMinWidth(280);
        rightPane = new VBox(15);
        rightPane.setPadding(new Insets(15));
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPlaceholder(new Label("No data to display"));
        VBox.setVgrow(tableView, Priority.ALWAYS);
        rightPane.getChildren().add(tableView);
        // Initially, only rightPane is visible
        root.setCenter(rightPane);
        // Create MenuBar and menus
        MenuBar menuBar = new MenuBar();
        Menu tableMenu = new Menu("Table");
        MenuItem createTableItem = new MenuItem("Create Table");
        MenuItem dropTableItem = new MenuItem("Drop Table");
        MenuItem truncateTableItem = new MenuItem("Truncate Table");
        
        tableMenu.getItems().addAll(createTableItem, dropTableItem, truncateTableItem);
        Menu operationsMenu = new Menu("Operations");
        MenuItem insertItem = new MenuItem("Insert");
        MenuItem selectItem = new MenuItem("Select");
        MenuItem updateItem = new MenuItem("Update");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        
        operationsMenu.getItems().addAll(insertItem, selectItem, updateItem, deleteMenuItem);
        menuBar.getMenus().addAll(tableMenu, operationsMenu);
        root.setTop(menuBar);
        // Attach event handlers (example: show left pane when any operation menu item clicked)
        EventHandler<ActionEvent> showLeftPaneHandler = e -> {
            SplitPane splitPane = new SplitPane(leftPane, rightPane);
            splitPane.setDividerPositions(0.35);
            root.setCenter(splitPane);
        };
        insertItem.setOnAction(e -> {
            showLeftPaneHandler.handle(e);
            showInsertWindow2();
        });
        selectItem.setOnAction(e -> {
            showLeftPaneHandler.handle(e);
            showSelectTableForm();
        });
        updateItem.setOnAction(e -> {
            showLeftPaneHandler.handle(e);
            showUpdatePane();
        });
        deleteMenuItem.setOnAction(e -> {
            showLeftPaneHandler.handle(e);
            showDeletePane();
        });
        // Table Menu Actions
        createTableItem.setOnAction(e -> {
            showLeftPaneHandler.handle(e);
            showCreateTableForm();
        });
        dropTableItem.setOnAction(e -> {
            showLeftPaneHandler.handle(e);
            showSimpleTableActionPane("Drop", "DROP TABLE ", dynamicContentBox);
        });
        truncateTableItem.setOnAction(e -> {
            showLeftPaneHandler.handle(e);
            showSimpleTableActionPane("Truncate", "TRUNCATE TABLE ", dynamicContentBox);
        });
        
        dynamicContentBox.setId("dynamic-content");
        messageBox.setId("message-box");
        root.setId("root");
        leftPane.setId("leftPane");
        rightPane.setId("rightPane");
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("sty.css").toExternalForm());
        primaryStage.setTitle("JavaFX MySQL Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCreateTableForm() {
        dynamicContentBox.getChildren().clear();
        Label tableLabel = new Label("Enter Table Name:");
        TextField tableField = new TextField();
        Button submitBtn = new Button("Submit");
        dynamicContentBox.getChildren().addAll(tableLabel, tableField, submitBtn);
        submitBtn.setOnAction(ev -> {
            String tableName = tableField.getText().trim();
            if (tableName.isEmpty()) {
                showMessage("Table name cannot be empty!",true);
                return;
            }
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                DatabaseMetaData dbm = conn.getMetaData();
                ResultSet tables = dbm.getTables(null, null, tableName, null);
                if (tables.next()) {
                    showMessage("Table already exists!",true);
                } else {
                    showDynamicTableCreator(tableName);
                }
            } catch (SQLException ex) {
                showMessage("Error: " + ex.getMessage(),true);
            }
        });
    }

    private void showInsertWindow2() {
        dynamicContentBox.getChildren().clear();
        VBox formBox = new VBox(10);
        dynamicContentBox.getChildren().addAll(new Label("Insert Into Table"), formBox);
        addTableSelector(formBox, "Table Name:", selectedTable -> {
            loadInsertFormForTable(selectedTable);
            displayTableInRightPane(selectedTable);
        });
    }

    private void displayTableInRightPane(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            showMessage("Table name required.",true);
            return;
        }
        currentTableName = tableName;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            tableView.getColumns().clear();
            ObservableList<RowWrapper> rows = FXCollections.observableArrayList();
            // Checkbox column
            TableColumn<RowWrapper, Boolean> checkCol = new TableColumn<>("Select rows");
            checkCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
            checkCol.setCellFactory(CheckBoxTableCell.forTableColumn(checkCol));
            checkCol.setEditable(true);
            tableView.getColumns().add(checkCol);
            tableView.setEditable(true);
            // Dynamic DB columns
            for (int i = 1; i <= columnCount; i++) {
                final int colIndex = i - 1;
                String colName = meta.getColumnName(i);
                // Skip editing the 'sno' column
                boolean isEditable = !colName.equalsIgnoreCase("sno");
                TableColumn<RowWrapper, String> col = new TableColumn<>(colName);
                col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getData().get(colIndex)));
                if (isEditable) {
                    col.setCellFactory(TextFieldTableCell.forTableColumn());
                    col.setOnEditCommit(event -> {
                        RowWrapper row = event.getRowValue();
                        String newValue = event.getNewValue();
                        row.getData().set(colIndex, newValue); // Update in JavaFX
                        String idValue = row.getData().get(0); // assuming 'id' is first column
                        String columnToUpdate = colName;
                        String updateQuery = "UPDATE " + tableName + " SET " + columnToUpdate + " = ? WHERE sno = ?";
                        try (Connection connn = DriverManager.getConnection(url, user, password);
                            PreparedStatement stmtt = connn.prepareStatement(updateQuery)) {
                            stmtt.setString(1, newValue);
                            stmtt.setString(2, idValue);
                            stmtt.executeUpdate();
                            // Show success using your custom message method
                            showMessage(" Updated row [sno: " + idValue + "]  " + columnToUpdate + " = '" + newValue + "'", false);
                        } catch (SQLException ex) {
                            showMessage(" Failed to update DB: " + ex.getMessage(), true);
                        }
                    });
                }
                tableView.getColumns().add(col);
            }
            // Add data rows
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                rows.add(new RowWrapper(row));
            }
            tableView.setItems(rows);
            // UI Controls
            Label tableLabel = new Label("Table: " + tableName);
            tableLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
            Button refreshBtn = new Button("Refresh");
            refreshBtn.setOnAction(e -> displayTableInRightPane(currentTableName));
            Button deleteBtn = new Button("Delete Selected");
            deleteBtn.setOnAction(e -> {
                ObservableList<RowWrapper> toDelete = rows.filtered(RowWrapper::isSelected);
                for (RowWrapper row : toDelete) {
                    deleteRowFromDB(tableName, meta, row.getData());
                }
                rows.removeAll(toDelete);
            });
            HBox topBox = new HBox(10, tableLabel, refreshBtn, deleteBtn);
            topBox.setPadding(new Insets(10));
            VBox newRightPane = new VBox(10, topBox, tableView);
            newRightPane.setPadding(new Insets(15));
            VBox.setVgrow(tableView, Priority.ALWAYS);
            rightPane.getChildren().clear();
            rightPane.getChildren().addAll(newRightPane.getChildren());
        } catch (SQLException ex) {
            showMessage("Error: " + ex.getMessage(),true);
        }
    }

    private void showSelectTableForm() {
        dynamicContentBox.getChildren().clear();
        VBox formBox = new VBox(10);
        dynamicContentBox.getChildren().addAll(new Label("Select Table to Display:"), formBox);
        addTableSelector(formBox, "Table Name:", tableName -> {
            displayTableInRightPane(tableName);
        });
    }

    private void showDeletePane() {
        dynamicContentBox.getChildren().clear();
        VBox formBox = new VBox(10);
        VBox whereBox = new VBox(5);
        List<TextField> whereFields = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        dynamicContentBox.getChildren().addAll(new Label("Delete Rows"), formBox);
        addTableSelector(formBox, "Table Name:", tableName -> {
            displayTableInRightPane(tableName);
            whereBox.getChildren().clear();
            whereFields.clear();
            columnNames.clear();
            try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    String col = meta.getColumnName(i);
                    columnNames.add(col);
                    TextField field = new TextField();
                    field.setPromptText("Condition for " + col);
                    whereFields.add(field);
                    whereBox.getChildren().add(new HBox(5, new Label(col + ":"), field));
                }
                if (!formBox.getChildren().contains(whereBox)) {
                    formBox.getChildren().addAll(new Label("Optional WHERE conditions:"), whereBox);
                }
                Button deleteBtn = new Button("Delete");
                formBox.getChildren().add(deleteBtn);
                deleteBtn.setOnAction(e -> {
                    StringBuilder whereClause = new StringBuilder();
                    List<String> values = new ArrayList<>();
                    List<String> whereParts = new ArrayList<>();
                    for (int i = 0; i < columnNames.size(); i++) {
                        String val = whereFields.get(i).getText().trim();
                        if (!val.isEmpty()) {
                            whereParts.add(columnNames.get(i) + " = ?");
                            values.add(val);
                        }
                    }
                    if (!whereParts.isEmpty()) {
                        whereClause.append(" WHERE ").append(String.join(" AND ", whereParts));
                    }
                    String selectQuery = "SELECT * FROM " + tableName + whereClause;
                    String deleteQuery = "DELETE FROM " + tableName + whereClause;
                    try (Connection conn2 = DriverManager.getConnection(url, user, password);
                        PreparedStatement selectStmt = conn2.prepareStatement(selectQuery);
                        PreparedStatement deleteStmt = conn2.prepareStatement(deleteQuery)) {
                        for (int i = 0; i < values.size(); i++) {
                            selectStmt.setString(i + 1, values.get(i));
                            deleteStmt.setString(i + 1, values.get(i));
                        }
                        ResultSet rss = selectStmt.executeQuery();
                        List<String> deletedRows = new ArrayList<>();
                        ResultSetMetaData metaa = rss.getMetaData();
                        int colCountt = metaa.getColumnCount();
                        while (rss.next()) {
                            StringBuilder rowInfo = new StringBuilder("[");
                            for (int i = 1; i <= colCountt; i++) {
                                rowInfo.append(metaa.getColumnName(i)).append("=").append(rss.getString(i));
                                if (i < colCountt) rowInfo.append(", ");
                            }
                            rowInfo.append("]");
                            deletedRows.add(rowInfo.toString());
                        }
                        int affected = deleteStmt.executeUpdate();
                        if (deletedRows.isEmpty()) {
                            showMessage("No matching rows found to delete.", true);
                        } else {
                            showMessage(" Deleted " + affected + " row(s):\n" + String.join("\n", deletedRows), false);
                            displayTableInRightPane(tableName);
                        }
                    } catch (SQLException ex) {
                        showMessage("Deletion failed: " + ex.getMessage(), true);
                    }
                });
            } catch (SQLException ex) {
                showMessage("Error: " + ex.getMessage(), true);
            }
        });
    }

    private void showUpdatePane() {
        dynamicContentBox.getChildren().clear();
        VBox formBox = new VBox(10);
        VBox setBox = new VBox(5);
        VBox whereBox = new VBox(5);
        List<TextField> setFields = new ArrayList<>();
        List<TextField> whereFields = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        dynamicContentBox.getChildren().addAll(new Label("Update Table"), formBox);
        addTableSelector(formBox, "Table Name:", tableName -> {
            displayTableInRightPane(tableName);
            setBox.getChildren().clear();
            whereBox.getChildren().clear();
            setFields.clear();
            whereFields.clear();
            columnNames.clear();
            try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                formBox.getChildren().clear(); // Clear old children
                formBox.getChildren().add(new Label("Set New Values"));
                for (int i = 1; i <= colCount; i++) {
                    String colName = meta.getColumnName(i);
                    columnNames.add(colName);
                    TextField setField = new TextField();
                    setField.setPromptText("New value for " + colName);
                    setFields.add(setField);
                    setBox.getChildren().add(new HBox(5, new Label(colName + ":"), setField));
                }
                formBox.getChildren().addAll(setBox, new Label("Optional WHERE clause"));
                for (int i = 0; i < colCount; i++) {
                    TextField whereField = new TextField();
                    whereField.setPromptText("Condition for " + columnNames.get(i));
                    whereFields.add(whereField);
                    whereBox.getChildren().add(new HBox(5, new Label(columnNames.get(i) + ":"), whereField));
                }
                formBox.getChildren().add(whereBox);
                Button updateBtn = new Button("Update");
                formBox.getChildren().add(updateBtn);
                updateBtn.setOnAction(e -> {
                    StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
                    List<String> setParts = new ArrayList<>();
                    List<String> whereParts = new ArrayList<>();
                    List<String> allValues = new ArrayList<>();
                    for (int i = 0; i < colCount; i++) {
                        String val = setFields.get(i).getText().trim();
                        if (!val.isEmpty()) {
                            setParts.add(columnNames.get(i) + " = ?");
                            allValues.add(val);
                        }
                    }
                    if (setParts.isEmpty()) {
                        showMessage("At least one column must be set for update.", true);
                        return;
                    }
                    for (int i = 0; i < colCount; i++) {
                        String val = whereFields.get(i).getText().trim();
                        if (!val.isEmpty()) {
                            whereParts.add(columnNames.get(i) + " = ?");
                            allValues.add(val);
                        }
                    }
                    query.append(String.join(", ", setParts));
                    if (!whereParts.isEmpty()) {
                        query.append(" WHERE ").append(String.join(" AND ", whereParts));
                    }
                    try (Connection conn2 = DriverManager.getConnection(url, user, password);
                        PreparedStatement pstmt = conn2.prepareStatement(query.toString())) {
                        for (int i = 0; i < allValues.size(); i++) {
                            pstmt.setString(i + 1, allValues.get(i));
                        }
                        // Step 1: Fetch rows BEFORE update for reporting
                        StringBuilder selectQuery = new StringBuilder("SELECT * FROM " + tableName);
                        if (!whereParts.isEmpty()) {
                            selectQuery.append(" WHERE ").append(String.join(" AND ", whereParts));
                        }
                        List<String> affectedRows = new ArrayList<>();
                        try (PreparedStatement selectStmt = conn2.prepareStatement(selectQuery.toString())) {
                            for (int i = 0; i < allValues.size() - setParts.size(); i++) {
                                selectStmt.setString(i + 1, allValues.get(setParts.size() + i));
                            }
                            try (ResultSet rss = selectStmt.executeQuery()) {
                                ResultSetMetaData metaData = rss.getMetaData();
                                int colCount2 = metaData.getColumnCount();
                                while (rss.next()) {
                                    StringBuilder rowBuilder = new StringBuilder("[");
                                    for (int j = 1; j <= colCount2; j++) {
                                        rowBuilder.append(metaData.getColumnName(j))
                                                .append("=")
                                                .append(rss.getString(j));
                                        if (j < colCount2) rowBuilder.append(", ");
                                    }
                                    rowBuilder.append("]");
                                    affectedRows.add(rowBuilder.toString());
                                }
                            }
                        }
                        // Step 2: Perform the actual update
                        int rows = pstmt.executeUpdate();
                        // Step 3: Show message
                        if (rows > 0 && !affectedRows.isEmpty()) {
                            showMessage("Updated " + rows + " row(s) in " + tableName + ":\n" +
                                        affectedRows.stream().collect(Collectors.joining("\n")), false);
                        } else {
                            showMessage("No matching rows found for update.", true);
                        }
                        displayTableInRightPane(tableName);
                        setFields.forEach(TextField::clear);
                        whereFields.forEach(TextField::clear);
                    } catch (SQLException ex) {
                        showMessage("Update failed: " + ex.getMessage(), true);
                    }
                });
            } catch (SQLException ex) {
                showMessage("Error: " + ex.getMessage(), true);
            }
        });
    }

    private void showSimpleTableActionPane(String action, String sqlPrefix, VBox dynamicContentBox) {
        dynamicContentBox.getChildren().clear();
        VBox formBox = new VBox(10);
        dynamicContentBox.getChildren().addAll(new Label(action + " Table"), formBox);
        final AtomicReference<ComboBox<String>> tableFieldRef = new AtomicReference<>();
        tableFieldRef.set(addTableSelector(formBox, "Table Name:", tableName -> {
            try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sqlPrefix + tableName);
                showMessage(action + " table successful!", false);
                if (action.equalsIgnoreCase("Drop")) {
                    // Clear rightPane view
                    rightPane.getChildren().clear();
                    tableView = new TableView<>();
                    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                    tableView.setPlaceholder(new Label("No data to display"));
                    VBox.setVgrow(tableView, Priority.ALWAYS);
                    rightPane.getChildren().add(tableView);
                    // Clear and Refresh tableField list to avoid ghost tables
                    ComboBox<String> tableField = tableFieldRef.get();
                    tableField.setItems(FXCollections.observableArrayList()); // clear
                try (Connection conn2 = DriverManager.getConnection(url, user, password);
                        PreparedStatement ps = conn2.prepareStatement(
                        "SELECT table_name FROM information_schema.tables WHERE table_schema = ? AND table_type = 'BASE TABLE'")) {
                        ps.setString(1, conn2.getCatalog());
                        ResultSet rs = ps.executeQuery();
                        List<String> updatedList = new ArrayList<>();
                        while (rs.next()) {
                            String table = rs.getString("table_name");
                            updatedList.add(table);
                            System.out.println("REFRESH found table: " + table);  // Debug output
                        }
                        tableField.setItems(FXCollections.observableArrayList(updatedList)); // force full refresh
                    } catch (SQLException e2) {
                        showMessage("Error refreshing table list: " + e2.getMessage(), true);
                    }
                } else {
                    displayTableInRightPane(tableName);
                }
            } catch (SQLException ex) {
                showMessage(ex.getMessage(), true);
            }
        }));
    }

    private void showDynamicTableCreator(String tableName) {
        dynamicContentBox.getChildren().clear();
        dynamicContentBox.setSpacing(10);
        Label title = new Label("Define columns for: " + tableName);
        VBox columnBox = new VBox(10);
        // Add column row
        Runnable addColumnRow = () -> {
            TextField nameField = new TextField();
            nameField.setPromptText("Enter Column Name");
            ComboBox<String> typeBox = new ComboBox<>();
            typeBox.getItems().addAll("INT", "VARCHAR(100)", "DOUBLE", "DATE", "BOOLEAN");
            typeBox.setValue("VARCHAR(100)");
            Button removeBtn = new Button("X");
            HBox row = new HBox(10, nameField, typeBox, removeBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            removeBtn.setOnAction(e -> columnBox.getChildren().remove(row));
            columnBox.getChildren().add(row);
        };
        // Initial user-defined column
        addColumnRow.run();
        Button addColumnBtn = new Button("Add Column");
        addColumnBtn.setOnAction(e -> addColumnRow.run());
        Button createBtn = new Button("Create Table");
        createBtn.setOnAction(e -> {
            if (columnBox.getChildren().isEmpty()) {
                showMessage("Add at least one column.", true);
                return;
            }
            StringBuilder query = new StringBuilder("CREATE TABLE " + tableName + " (");
            // 1. Inject Primary Key column first
            query.append("sno INT AUTO_INCREMENT PRIMARY KEY, ");
            List<HBox> rows = columnBox.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .collect(Collectors.toList());
            for (int i = 0; i < rows.size(); i++) {
                HBox row = rows.get(i);
                TextField nameField = (TextField) row.getChildren().get(0);
                @SuppressWarnings("unchecked")
                ComboBox<String> typeBox = (ComboBox<String>) row.getChildren().get(1);
                String colName = nameField.getText().trim();
                String colType = typeBox.getValue();
                if (colName.isEmpty()) {
                    showMessage("Column name cannot be empty.", true);
                    return;
                }
                // Prevent duplicate "id" from UI
                if (colName.equalsIgnoreCase("id")) {
                    showMessage("Column name 'id' is reserved as primary key.", true);
                    return;
                }
                query.append(colName).append(" ").append(colType);
                if (i != rows.size() - 1) query.append(", ");
            }
            query.append(")");
            try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(query.toString());
                showMessage("Table created successfully!", false);
                currentTableName = tableName;
                displayTableInRightPane(tableName);
                dynamicContentBox.getChildren().clear();
            } catch (SQLException ex) {
                showMessage("Error creating table: " + ex.getMessage(), true);
            }
        });
        dynamicContentBox.getChildren().addAll(title, columnBox, addColumnBtn, createBtn);
    }

    private void deleteRowFromDB(String tableName, ResultSetMetaData meta, ObservableList<String> rowData) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            StringBuilder where = new StringBuilder(" WHERE ");
            List<String> conditions = new ArrayList<>();
            List<String> debugValues = new ArrayList<>();
            for (int i = 1; i <= rowData.size(); i++) {
                String col = meta.getColumnName(i);
                String val = rowData.get(i - 1);
                conditions.add(col + " = ?");
                debugValues.add(col + "=" + (val == null || val.isEmpty() ? "NULL" : "'" + val + "'"));
            }
            String sql = "DELETE FROM " + tableName + where.append(String.join(" AND ", conditions));
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < rowData.size(); i++) {
                    String val = rowData.get(i);
                    if (val == null || val.isEmpty()) {
                        pstmt.setNull(i + 1, Types.NULL);
                    } else {
                        pstmt.setString(i + 1, val);
                    }
                }
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    showMessage("Deleted row: [" + String.join(", ", debugValues) + "]", false);
                } else {
                    showMessage("No matching row found to delete.", true);
                }
            }
        } catch (SQLException ex) {
            showMessage("Delete error: " + ex.getMessage(), true);
        }
    }

   private void showMessage(String message, boolean isError) {
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + "; -fx-font-size: 13px;");
        msgLabel.setPadding(new Insets(2, 5, 2, 5));
        msgLabel.setMaxWidth(Double.MAX_VALUE);
        messageBox.getChildren().add(0, msgLabel); // Add to top, latest first
    }

    private void loadInsertFormForTable(String tableName) {
        dynamicContentBox.getChildren().clear();
        VBox formBox = new VBox(10);
        List<TextField> valueFields = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        List<String> columnTypes = new ArrayList<>();
        // Step 1: Pre-fetch all metadata inside try
        try (Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(meta.getColumnName(i));
                columnTypes.add(meta.getColumnTypeName(i));
            }
        } catch (SQLException ex) {
            showMessage("Error fetching columns: " + ex.getMessage(), true);
            return;
        }
        // Step 2: Create form based on extracted column data
        for (int i = 0; i < columnNames.size(); i++) {
            String name = columnNames.get(i);
            String type = columnTypes.get(i);
            Label label = new Label(name + " (" + type + "):");
            TextField input = new TextField();
            input.setPromptText("Enter value for " + name);
            valueFields.add(input);
            formBox.getChildren().addAll(label, input);
        }
        // Step 3: Insert button logic
        Button insertBtn = new Button("Insert");
        insertBtn.setOnAction(e -> {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < valueFields.size(); i++) {
                    placeholders.append("?");
                    if (i < valueFields.size() - 1) placeholders.append(",");
                }
                String queryStr = "INSERT INTO " + tableName + " VALUES(" + placeholders + ")";
                PreparedStatement pstmt = conn.prepareStatement(queryStr);
                List<String> insertedData = new ArrayList<>();
                for (int i = 0; i < valueFields.size(); i++) {
                    String input = valueFields.get(i).getText().trim();
                    String type = columnTypes.get(i).toUpperCase();
                    if (input.isEmpty()) {
                        pstmt.setNull(i + 1, Types.NULL);
                        insertedData.add(columnNames.get(i) + "=NULL");
                        continue;
                    }
                    switch (type) {
                        case "INT":
                        case "INTEGER":
                            pstmt.setInt(i + 1, Integer.parseInt(input));
                            insertedData.add(columnNames.get(i) + "=" + Integer.parseInt(input));
                            break;
                        case "DOUBLE":
                        case "FLOAT":
                        case "DECIMAL":
                        case "NUMERIC":
                            pstmt.setDouble(i + 1, Double.parseDouble(input));
                            insertedData.add(columnNames.get(i) + "=" + Double.parseDouble(input));
                            break;
                        case "BOOLEAN":
                        case "BIT":
                            pstmt.setBoolean(i + 1, Boolean.parseBoolean(input));
                            insertedData.add(columnNames.get(i) + "=" + Boolean.parseBoolean(input));
                            break;
                        case "DATE":
                            pstmt.setDate(i + 1, java.sql.Date.valueOf(input));
                            insertedData.add(columnNames.get(i) + "=" + input);
                            break;
                        default:
                            pstmt.setString(i + 1, input);
                            insertedData.add(columnNames.get(i) + "='" + input + "'");
                    }
                }
                int rows = pstmt.executeUpdate();
                showMessage("Inserted " + rows + " row(s) into " + tableName + ":\n[" + String.join(", ", insertedData) + "]", false);
                displayTableInRightPane(tableName);
            } catch (Exception ex) {
                showMessage("Invalid insertion: " + ex.getMessage(), true);
            }
            valueFields.forEach(TextField::clear);
        });
        dynamicContentBox.getChildren().addAll(new Label("Insert Into: " + tableName), formBox, insertBtn);
    }

    private ComboBox<String> addTableSelector(VBox parent, String labelText, Consumer<String> onSelect) {
        Label label = new Label(labelText);
        ComboBox<String> tableField = new ComboBox<>();
        tableField.setEditable(true);
        tableField.setPromptText("Enter or select table name");
        // Always fetch live table names here
        List<String> tableNames = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = conn.prepareStatement(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = ? AND table_type = 'BASE TABLE'")) {
            ps.setString(1, conn.getCatalog()); // your current DB/schema
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }
            tableField.setItems(FXCollections.observableArrayList(tableNames));
        }
    catch (SQLException e) {
            showMessage("Error fetching table names: " + e.getMessage(), true);
        }
        // Filtering suggestions
        FilteredList<String> filteredItems = new FilteredList<>(tableField.getItems(), p -> true);
        tableField.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            final TextField editor = tableField.getEditor();
            final String selected = tableField.getSelectionModel().getSelectedItem();
            if (selected == null || !selected.equals(editor.getText())) {
                filteredItems.setPredicate(item -> item.toLowerCase().contains(newVal.toLowerCase()));
                tableField.setItems(FXCollections.observableArrayList(filteredItems));
                tableField.show();
            }
        });
        // Select handler
        Button submitBtn = new Button("Proceed");
        submitBtn.setOnAction(e -> {
            String selected = tableField.getEditor().getText().trim();
            if (!tableNames.contains(selected)) {
                showMessage("Invalid or non-existent table selected.", true);
            } else {
                onSelect.accept(selected);
            }
        });
        parent.getChildren().addAll(label, tableField, submitBtn);
        return tableField;
    }

    
    private void showLoginPane(Stage primaryStage) {
        VBox loginBox = new VBox(12);
        loginBox.setPadding(new Insets(25));
        loginBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Login to MySQL");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        ComboBox<String> dbComboBox = new ComboBox<>();
        dbComboBox.setPromptText("Select or type database");
        dbComboBox.setEditable(true);
        dbComboBox.setPrefWidth(220);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadDatabases(userField, passField, dbComboBox)); // ✅ Only manual refresh

        HBox dbBox = new HBox(8, dbComboBox, refreshBtn);

        Button loginBtn = new Button("Login");
        loginBtn.setDefaultButton(true);

        // ✅ Initialize message box (no NPE)
        this.messageBox = new VBox();
        messageBox.setSpacing(5);

        loginBtn.setOnAction(e -> {
            String inputUser = userField.getText().trim();
            String inputPass = passField.getText().trim();
            String dbName = dbComboBox.getEditor().getText().trim();

            if (inputUser.isEmpty() || inputPass.isEmpty() || dbName.isEmpty()) {
                showLoginMessage("All fields are required.", false);
                return;
            }

            String fullUrl = "jdbc:mysql://localhost:3306/" + dbName;
            try (Connection conn = DriverManager.getConnection(fullUrl, inputUser, inputPass)) {
                this.user = inputUser;
                this.password = inputPass;
                this.url = fullUrl;

                showLoginMessage("Login successful. Connected to " + dbName, true);
                showMainUI(primaryStage);
            } catch (SQLException ex) {
                showLoginMessage("Login failed: " + ex.getMessage(), false);
            }
        });

        loginBox.getChildren().addAll(
                title,
                new Label("Username:"), userField,
                new Label("Password:"), passField,
                new Label("Database:"), dbBox,
                loginBtn,
                messageBox // ✅ Visible for all login messages
        );

        Scene loginScene = new Scene(loginBox, 400, 350);
        loginScene.getStylesheets().add(getClass().getResource("sty.css").toExternalForm());
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Database Login");
        primaryStage.show();
    }



    // ✅ Utility to load databases
    private void loadDatabases(TextField userField, PasswordField passField, ComboBox<String> dbComboBox) {
        String u = userField.getText().trim();
        String p = passField.getText().trim();

        if (u.isEmpty() || p.isEmpty()) return;

        new Thread(() -> {
            List<String> dbList = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", u, p);
                ResultSet rs = conn.getMetaData().getCatalogs()) {

                while (rs.next()) {
                    String db = rs.getString(1);
                    if (!db.matches("(?i)mysql|information_schema|performance_schema|sys"))
                        dbList.add(db);
                }

            } catch (SQLException ex) {
                Platform.runLater(() -> {
                    dbComboBox.getItems().clear();
                    showLoginMessage("DB fetch error: " + ex.getMessage(), false);
                });
                return;
            }

            Platform.runLater(() -> {
                dbComboBox.getItems().setAll(dbList);
                showLoginMessage(
                    dbList.isEmpty() ? "No user databases found." : "Databases loaded.",
                    !dbList.isEmpty()
                );
            });
        }).start();
    }

    private void showLoginMessage(String msg, boolean success) {
        Label label = new Label(msg);
        label.setStyle("-fx-text-fill: " + (success ? "limegreen" : "red") + "; -fx-font-size: 13px;");
        messageBox.getChildren().add(label);
    }



}
