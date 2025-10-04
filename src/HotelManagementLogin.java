import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class HotelManagementLogin {
    public static void main(String[] args) {
        JFrame frame = new JFrame("CHURULI HOTELS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(3, 2));
        JLabel labelUser = new JLabel("Username:");
        JTextField textUser = new JTextField();
        JLabel labelPass = new JLabel("Password:");
        JPasswordField textPass = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        frame.add(labelUser);
        frame.add(textUser);
        frame.add(labelPass);
        frame.add(textPass);
        frame.add(loginButton);
        frame.add(cancelButton);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textUser.getText();
                String password = new String(textPass.getPassword());
                String role = authenticate(username, password);
                if (role != null) {
                    if (role.equals("admin")) {
                        JOptionPane.showMessageDialog(frame, "Login successful! Adich KERI VAA !!.");
                        openAdminDashboard();
                    } else if (role.equals("staff")) {
                        JOptionPane.showMessageDialog(frame, "Login successful! KERI VADA MAKKALEY!!");
                        openStaffDashboard();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "NINEK THANKAN CHETTANEY ARIYO !!");
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }
    public static String authenticate(String username, String password) {
        String role = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            String query = "SELECT role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                role = rs.getString("role");
            }
            rs.close();
            pst.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return role;
    }
    public static void openAdminDashboard() {
        JFrame adminFrame = new JFrame("Admin Dashboard");
        adminFrame.setSize(600, 400);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setLayout(new BorderLayout());
        JPanel roomPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        roomPanel.setBorder(BorderFactory.createTitledBorder("Room Management"));
        JLabel roomNumberLabel = new JLabel("Room Number:");
        JTextField roomNumberField = new JTextField();
        JLabel roomTypeLabel = new JLabel("Room Type:");
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[]{"single", "double", "suite"});
        JLabel roomPriceLabel = new JLabel("Room Price:");
        JTextField roomPriceField = new JTextField();
        JButton addRoomButton = new JButton("Add Room");
        JButton deleteRoomButton = new JButton("Delete Room");
        roomPanel.add(roomNumberLabel);
        roomPanel.add(roomNumberField);
        roomPanel.add(roomTypeLabel);
        roomPanel.add(roomTypeCombo);
        roomPanel.add(roomPriceLabel);
        roomPanel.add(roomPriceField);
        roomPanel.add(addRoomButton);
        roomPanel.add(deleteRoomButton);
        String[] columnNames = {"Room ID", "Room Number", "Type", "Price", "Status"};
        JTable roomTable = new JTable(new Object[][]{}, columnNames);
        JScrollPane roomTableScroll = new JScrollPane(roomTable);
        roomTableScroll.setBorder(BorderFactory.createTitledBorder("Available Rooms"));
        loadRoomData(roomTable);
        addRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int roomNumber = Integer.parseInt(roomNumberField.getText());
                String roomType = (String) roomTypeCombo.getSelectedItem();
                double roomPrice = Double.parseDouble(roomPriceField.getText());
                addRoom(roomNumber, roomType, roomPrice);
                loadRoomData(roomTable);
            }
        });
        deleteRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = roomTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int roomId = (int) roomTable.getValueAt(selectedRow, 0);
                    deleteRoom(roomId);
                    loadRoomData(roomTable);
                } else {
                    JOptionPane.showMessageDialog(adminFrame, "Please select a room to delete.");
                }
            }
        });
        adminFrame.add(roomPanel, BorderLayout.NORTH);
        adminFrame.add(roomTableScroll, BorderLayout.CENTER);
        adminFrame.setVisible(true);
    }
    public static void addRoom(int roomNumber, String roomType, double roomPrice) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            String query = "INSERT INTO rooms (room_number, room_type, price, status) VALUES (?, ?, ?, 'available')";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, roomNumber);
            pst.setString(2, roomType);
            pst.setDouble(3, roomPrice);
            pst.executeUpdate();
            pst.close();
            conn.close();
            JOptionPane.showMessageDialog(null, "Etha mood POLI MOOD !!.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "NINEK ROOM ILLA!! POKKO");
        }
    }
    public static void deleteRoom(int roomId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            String query = "DELETE FROM rooms WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, roomId);
            pst.executeUpdate();
            pst.close();
            conn.close();
            JOptionPane.showMessageDialog(null, "Room deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to delete room.");
        }
    }
    public static void loadRoomData(JTable roomTable) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            String query = "SELECT * FROM rooms WHERE status = 'available'";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            roomTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Room ID", "Room Number", "Type", "Price", "Status"}
            ));
            javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) roomTable.getModel();
            while (rs.next()) {
                int roomId = rs.getInt("id");
                int roomNumber = rs.getInt("room_number");
                String roomType = rs.getString("room_type");
                double roomPrice = rs.getDouble("price");
                String roomStatus = rs.getString("status");
                model.addRow(new Object[]{roomId, roomNumber, roomType, roomPrice, roomStatus});
            }
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void openStaffDashboard() {
        JFrame staffFrame = new JFrame("Staff Dashboard");
        staffFrame.setSize(600, 400);
        staffFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        staffFrame.setLayout(new GridLayout(4, 1, 10, 10));
        JButton addDeleteCustomerButton = new JButton("Add/Delete Customer");
        JButton addBookingButton = new JButton("Add Booking");
        JButton paymentDetailsButton = new JButton("Payment Details");
        staffFrame.add(addDeleteCustomerButton);
        staffFrame.add(addBookingButton);
        staffFrame.add(paymentDetailsButton);
        addDeleteCustomerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCustomerManagementDialog();
            }
        });
        addBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openBookingDialog();
            }
        });
        paymentDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPaymentDetailsDialog();
            }
        });
        staffFrame.setVisible(true);
    }
    public static void openCustomerManagementDialog() {
        JDialog customerDialog = new JDialog();
        customerDialog.setTitle("Add/Delete Customer");
        customerDialog.setSize(500, 400);
        customerDialog.setLayout(new BorderLayout());
        JPanel customerPanel = new JPanel(new GridLayout(3, 2));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        JLabel customerNameLabel = new JLabel("Customer Name:");
        JTextField customerNameField = new JTextField();
        JLabel customerPhoneLabel = new JLabel("Phone:");
        JTextField customerPhoneField = new JTextField();
        JButton addCustomerButton = new JButton("Add Customer");
        JButton deleteCustomerButton = new JButton("Delete Customer");
        customerPanel.add(customerNameLabel);
        customerPanel.add(customerNameField);
        customerPanel.add(customerPhoneLabel);
        customerPanel.add(customerPhoneField);
        customerPanel.add(addCustomerButton);
        customerPanel.add(deleteCustomerButton);
        customerDialog.add(customerPanel, BorderLayout.NORTH);
        JTable customerTable = new JTable(new Object[][]{}, new String[]{"Customer ID", "Name", "Phone"});
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        customerDialog.add(customerScrollPane, BorderLayout.CENTER);
        loadCustomerData(customerTable);
        addCustomerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String customerName = customerNameField.getText();
                String customerPhone = customerPhoneField.getText();
                addCustomer(customerName, customerPhone);
                loadCustomerData(customerTable);
            }
        });
        deleteCustomerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int customerId = (int) customerTable.getValueAt(selectedRow, 0);
                    deleteCustomer(customerId);
                    loadCustomerData(customerTable);
                } else {
                    JOptionPane.showMessageDialog(customerDialog, "Please select a customer to delete.");
                }
            }
        });
        customerDialog.setVisible(true);
    }
    public static void addCustomer(String name, String phone) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            String query = "INSERT INTO customers (name, phone) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, phone);
            pst.executeUpdate();
            pst.close();
            conn.close();
            JOptionPane.showMessageDialog(null, "Customer added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to add customer.");
        }
    }
    public static void deleteCustomer(int customerId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            String query = "DELETE FROM customers WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, customerId);
            pst.executeUpdate();
            pst.close();
            conn.close();
            JOptionPane.showMessageDialog(null, "Customer deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to delete customer.");
        }
    }
    public static void loadCustomerData(JTable customerTable) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            String query = "SELECT * FROM customers";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            customerTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Customer ID", "Name", "Phone"}
            ));
            javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) customerTable.getModel();
            while (rs.next()) {
                int customerId = rs.getInt("id");
                String customerName = rs.getString("name");
                String customerPhone = rs.getString("phone");
                model.addRow(new Object[]{customerId, customerName, customerPhone});
            }
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void openBookingDialog() {
        JDialog bookingDialog = new JDialog();
        bookingDialog.setTitle("Add Booking");
        bookingDialog.setSize(500, 400);
        bookingDialog.setLayout(new BorderLayout());
        JPanel bookingPanel = new JPanel(new GridLayout(4, 2));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        
        JLabel customerIdLabel = new JLabel("Customer ID:");
        JTextField customerIdField = new JTextField();
        JLabel roomIdLabel = new JLabel("Room ID:");
        JTextField roomIdField = new JTextField();
        JLabel checkInLabel = new JLabel("Check-in Date (YYYY-MM-DD):");
        JTextField checkInField = new JTextField();
        JButton addBookingButton = new JButton("Add Booking");
        
        bookingPanel.add(customerIdLabel);
        bookingPanel.add(customerIdField);
        bookingPanel.add(roomIdLabel);
        bookingPanel.add(roomIdField);
        bookingPanel.add(checkInLabel);
        bookingPanel.add(checkInField);
        bookingPanel.add(addBookingButton);
        
        bookingDialog.add(bookingPanel, BorderLayout.NORTH);
        
        addBookingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int customerId = Integer.parseInt(customerIdField.getText());
                int roomId = Integer.parseInt(roomIdField.getText());
                String checkInDate = checkInField.getText();
                addBooking(customerId, roomId, checkInDate);
            }
        });
        
        bookingDialog.setVisible(true);
    }
    public static void addBooking(int customerId, int roomId, String checkInDate) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            
            // Check if the customer exists
            String checkCustomerQuery = "SELECT id FROM customers WHERE id = ?";
            PreparedStatement checkCustomerPst = conn.prepareStatement(checkCustomerQuery);
            checkCustomerPst.setInt(1, customerId);
            ResultSet customerResult = checkCustomerPst.executeQuery();
            
            // Check if the room is available
            String checkRoomQuery = "SELECT status FROM rooms WHERE id = ?";
            PreparedStatement checkRoomPst = conn.prepareStatement(checkRoomQuery);
            checkRoomPst.setInt(1, roomId);
            ResultSet roomResult = checkRoomPst.executeQuery();
            
            if (customerResult.next() && roomResult.next() && roomResult.getString("status").equals("available")) {
                String query = "INSERT INTO bookings (customer_id, room_id, check_in) VALUES (?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, customerId);
                pst.setInt(2, roomId);
                pst.setString(3, checkInDate);
                pst.executeUpdate();
                pst.close();
                JOptionPane.showMessageDialog(null, "Booking added successfully!");
                
                // Optionally update room status to booked
                String updateRoomQuery = "UPDATE rooms SET status = 'booked' WHERE id = ?";
                PreparedStatement updateRoomPst = conn.prepareStatement(updateRoomQuery);
                updateRoomPst.setInt(1, roomId);
                updateRoomPst.executeUpdate();
                updateRoomPst.close();
            } else {
                if (!customerResult.next()) {
                    JOptionPane.showMessageDialog(null, "Customer ID does not exist.");
                } else if (!roomResult.next()) {
                    JOptionPane.showMessageDialog(null, "Room ID does not exist.");
                } else {
                    JOptionPane.showMessageDialog(null, "Room is not available for booking.");
                }
            }
            
            customerResult.close();
            roomResult.close();
            checkCustomerPst.close();
            checkRoomPst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to add booking.");
        }
    }
    public static void openPaymentDetailsDialog() {
        JDialog paymentDialog = new JDialog();
        paymentDialog.setTitle("Payment Details");
        paymentDialog.setSize(600, 400);
        paymentDialog.setLayout(new BorderLayout());
    
        // Define table with additional columns for payment
        String[] columnNames = {"Customer Name", "Room Number", "Booking Date", "Payment Method", "Action"};
        JTable paymentTable = new JTable(new Object[][]{}, columnNames);
        JScrollPane paymentScrollPane = new JScrollPane(paymentTable);
        paymentDialog.add(paymentScrollPane, BorderLayout.CENTER);
    
        // Load payment details into the table
        loadPaymentDetails(paymentTable);
    
        paymentDialog.setVisible(true);
    }
    
    public static void loadPaymentDetails(JTable paymentTable) {
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");

        String query = "SELECT c.name AS customer_name, r.room_number, b.check_in AS booking_date, b.id AS booking_id " +
                       "FROM bookings b " +
                       "JOIN customers c ON b.customer_id = c.id " +
                       "JOIN rooms r ON b.room_id = r.id";

        PreparedStatement pst = conn.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object[][] {},
            new String[]{"Customer Name", "Room Number", "Booking Date", "Payment Method", "Action"}
        );
        paymentTable.setModel(model);
        
        while (rs.next()) {
            String customerName = rs.getString("customer_name");
            String roomNumber = rs.getString("room_number");
            String bookingDate = rs.getString("booking_date");
            int bookingId = rs.getInt("booking_id");

            // Create a JComboBox for payment methods
            String[] paymentMethods = {"cash", "credit_card", "debit_card", "online"};
            JComboBox<String> paymentMethodComboBox = new JComboBox<>(paymentMethods);
            
            // Create a button to process payment
            JButton payButton = new JButton("Pay");
            payButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedPaymentMethod = (String) paymentMethodComboBox.getSelectedItem();
                    if (selectedPaymentMethod != null && !selectedPaymentMethod.trim().isEmpty()) {
                        processPayment(bookingId, selectedPaymentMethod);
                    } else {
                        JOptionPane.showMessageDialog(paymentTable, "Please select a payment method.");
                    }
                }
            });

            // Add a row to the table model
            model.addRow(new Object[]{customerName, roomNumber, bookingDate, paymentMethodComboBox, payButton});
        }

        rs.close();
        pst.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    public static void processPayment(int bookingId, String paymentMethod) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hotel", "root", "root");
            String query = "INSERT INTO payments (booking_id, payment_method) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, bookingId);
            pst.setString(2, paymentMethod);
            pst.executeUpdate();
            pst.close();
            conn.close();
    
            JOptionPane.showMessageDialog(null, "Payment processed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to process payment.");
        }
    }
       
}