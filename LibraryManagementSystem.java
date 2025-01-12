import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Abstract Class: LibraryItem
abstract class LibraryItem {
    protected String title;
    protected String id;
    protected boolean isBorrowed;
    protected Date borrowDate;
    protected Date returnDeadline;

    public LibraryItem(String title, String id) {
        this.title = title;
        this.id = id;
        this.isBorrowed = false;
        this.borrowDate = null;
        this.returnDeadline = null;
    }

    public abstract double calculateLateFee(int daysLate);

    public abstract int getBorrowPeriod();

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void borrowItem(Date borrowDate) {
        this.isBorrowed = true;
        this.borrowDate = borrowDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(borrowDate);
        calendar.add(Calendar.DAY_OF_YEAR, getBorrowPeriod());
        this.returnDeadline = calendar.getTime();
    }

    public void returnItem() {
        this.isBorrowed = false;
        this.borrowDate = null;
        this.returnDeadline = null;
    }

    public String getStatus() {
        return isBorrowed ? "borrowed" : "available";
    }

    public String getBorrowDateString() {
        return borrowDate != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(borrowDate) : "";
    }

    public String getReturnDeadlineString() {
        return returnDeadline != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(returnDeadline) : "";
    }
}

// Subclasses: Book, Magazine, DVD
class Book extends LibraryItem {
    public Book(String title, String id) {
        super(title, id);
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate * 0.5; // $0.50 per day
    }

    @Override
    public int getBorrowPeriod() {
        return 14; // 14 days
    }
}

class Magazine extends LibraryItem {
    public Magazine(String title, String id) {
        super(title, id);
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate * 0.25; // $0.25 per day
    }

    @Override
    public int getBorrowPeriod() {
        return 7; // 7 days
    }
}

class DVD extends LibraryItem {
    public DVD(String title, String id) {
        super(title, id);
    }

    @Override
    public double calculateLateFee(int daysLate) {
        return daysLate * 1.0; // $1.00 per day
    }

    @Override
    public int getBorrowPeriod() {
        return 3; // 3 days
    }
}

// Main Library Management System Class
public class LibraryManagementSystem {

    private final Map<String, LibraryItem> items = new HashMap<>();
    private final DefaultTableModel tableModel;

    public LibraryManagementSystem() {
        // Adding sample items
        items.put("B1", new Book("Harry Potter", "B1"));
        items.put("B2", new Book("The Hobbit", "B2"));
        items.put("B3", new Book("1984", "B3"));
        items.put("B4", new Book("To Kill a Mockingbird", "B4"));
        items.put("B5", new Book("Pride and Prejudice", "B5"));

        items.put("M1", new Magazine("Time Magazine", "M1"));
        items.put("M2", new Magazine("National Geographic", "M2"));
        items.put("M3", new Magazine("Forbes", "M3"));
        items.put("M4", new Magazine("Vogue", "M4"));
        items.put("M5", new Magazine("Sports Illustrated", "M5"));

        items.put("D1", new DVD("Inception", "D1"));
        items.put("D2", new DVD("The Matrix", "D2"));
        items.put("D3", new DVD("Interstellar", "D3"));
        items.put("D4", new DVD("Titanic", "D4"));
        items.put("D5", new DVD("The Godfather", "D5"));

        // GUI Setup
        JFrame frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);

        JPanel panel = new JPanel(new GridLayout(5, 2));
        JLabel idLabel = new JLabel("Item ID:");
        JTextField idField = new JTextField();
        JButton borrowButton = new JButton("Borrow");
        JButton returnButton = new JButton("Return");
        JButton addItemButton = new JButton("Add Item");

        panel.add(idLabel);
        panel.add(idField);
        panel.add(borrowButton);
        panel.add(returnButton);
        panel.add(addItemButton);

        JTable itemTable = new JTable(new DefaultTableModel(new String[]{"ID", "Title", "Status", "Borrow Date", "Return Deadline"}, 0));
        JScrollPane tableScrollPane = new JScrollPane(itemTable);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        tableModel = (DefaultTableModel) itemTable.getModel();
        updateTable();

        borrowButton.addActionListener(e -> borrowItem(idField.getText().trim()));
        returnButton.addActionListener(e -> returnItem(idField.getText().trim()));
        addItemButton.addActionListener(e -> addItem());

        frame.setVisible(true);
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        items.values().stream()
                .sorted(Comparator.comparing(LibraryItem::getId))
                .forEach(item -> tableModel.addRow(new Object[]{
                        item.getId(),
                        item.getTitle(),
                        item.getStatus(),
                        item.getBorrowDateString(),
                        item.getReturnDeadlineString()
                }));
    }

    private void borrowItem(String id) {
        LibraryItem item = items.get(id);
        if (item == null) {
            JOptionPane.showMessageDialog(null, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (item.isBorrowed()) {
            JOptionPane.showMessageDialog(null, "Item is already borrowed.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JPanel datePanel = new JPanel(new GridLayout(3, 2));
            JTextField dayField = new JTextField();
            JTextField monthField = new JTextField();
            JTextField yearField = new JTextField();

            datePanel.add(new JLabel("Day:"));
            datePanel.add(dayField);
            datePanel.add(new JLabel("Month:"));
            datePanel.add(monthField);
            datePanel.add(new JLabel("Year:"));
            datePanel.add(yearField);

            int result = JOptionPane.showConfirmDialog(null, datePanel, "Enter borrow date", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int day = Integer.parseInt(dayField.getText());
                    int month = Integer.parseInt(monthField.getText()) - 1;
                    int year = Integer.parseInt(yearField.getText());

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);

                    Date borrowDate = calendar.getTime();
                    item.borrowItem(borrowDate);

                    JOptionPane.showMessageDialog(null, "Item borrowed: " + item.getTitle(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    updateTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid date.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void returnItem(String id) {
        LibraryItem item = items.get(id);
        if (item == null) {
            JOptionPane.showMessageDialog(null, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!item.isBorrowed()) {
            JOptionPane.showMessageDialog(null, "Item is not currently borrowed.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JPanel datePanel = new JPanel(new GridLayout(3, 2));
            JTextField dayField = new JTextField();
            JTextField monthField = new JTextField();
            JTextField yearField = new JTextField();

            datePanel.add(new JLabel("Day:"));
            datePanel.add(dayField);
            datePanel.add(new JLabel("Month:"));
            datePanel.add(monthField);
            datePanel.add(new JLabel("Year:"));
            datePanel.add(yearField);

            int result = JOptionPane.showConfirmDialog(null, datePanel, "Enter return date", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int day = Integer.parseInt(dayField.getText());
                    int month = Integer.parseInt(monthField.getText()) - 1;
                    int year = Integer.parseInt(yearField.getText());

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);

                    Date returnDate = calendar.getTime();
                    long diff = returnDate.getTime() - item.returnDeadline.getTime();
                    int daysLate = (int) (diff / (1000 * 60 * 60 * 24));

                    if (daysLate > 0) {
                        double lateFee = item.calculateLateFee(daysLate);
                        JOptionPane.showMessageDialog(null, "Late by " + daysLate + " days. Late fee: $" + lateFee, "Late Fee", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Item returned on time.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }

                    item.returnItem();
                    updateTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid date.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void addItem() {
        JPanel addItemPanel = new JPanel(new GridLayout(4, 2));
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        String[] itemTypes = {"Book", "Magazine", "DVD"};
        JComboBox<String> typeComboBox = new JComboBox<>(itemTypes);

        addItemPanel.add(new JLabel("Item ID:"));
        addItemPanel.add(idField);
        addItemPanel.add(new JLabel("Title:"));
        addItemPanel.add(titleField);
        addItemPanel.add(new JLabel("Type:"));
        addItemPanel.add(typeComboBox);

        int result = JOptionPane.showConfirmDialog(null, addItemPanel, "Add New Item", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();

            if (id.isEmpty() || title.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID and Title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (items.containsKey(id)) {
                JOptionPane.showMessageDialog(null, "Item ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LibraryItem newItem;
            switch (type) {
                case "Book":
                    newItem = new Book(title, id);
                    break;
                case "Magazine":
                    newItem = new Magazine(title, id);
                    break;
                case "DVD":
                    newItem = new DVD(title, id);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            items.put(id, newItem);
            JOptionPane.showMessageDialog(null, "Item added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            updateTable();
        }
    }

    public static void main(String[] args) {
        new LibraryManagementSystem();
    }
}
