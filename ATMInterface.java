import java.sql.*;
import java.util.Scanner;

public class ATMInterface {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;
    private static Scanner scanner;

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm","root","");
            statement = connection.createStatement();
            scanner = new Scanner(System.in);

            System.out.println("Welcome to the ATM!");

            // User authentication
            System.out.println("User ID: ");
            String userId = scanner.next();
            System.out.println("User PIN: ");
            String userPin =scanner.next();

            if (!authenticateUser(userId, userPin)) {
                System.out.println("Authentication failed. Exiting...");
                return;
            }

            System.out.println("Authentication successful!");

            int choice;
            do {
            	System.out.println("\nATM Menu:");
                System.out.println("1. Transactions History");
                System.out.println("2. Withdraw");
                System.out.println("3. Deposit");
                System.out.println("4. Transfer");
                System.out.println("5. Quit");
                System.out.print("Enter your choice: ");
                choice =scanner.nextInt();

                switch (choice) {
                    case 1:
                        displayTransactionHistory(userId);
                        break;
                    case 2:
                        performWithdrawal(userId);
                        break;
                    case 3:
                        performDeposit(userId);
                        break;
                    case 4:
                        performTransfer(userId);
                        break;
                    case 5:
                        System.out.println("Thank you for using the ATM. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 5);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
                if (scanner != null) {
                    scanner.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean authenticateUser(String userId, String userPin) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ? AND pin = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userId);
        preparedStatement.setString(2, userPin);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

//    private static void displayMenu() {
//        System.out.println("\nATM Menu:");
//        System.out.println("1. Transactions History");
//        System.out.println("2. Withdraw");
//        System.out.println("3. Deposit");
//        System.out.println("4. Transfer");
//        System.out.println("5. Quit");
//    }


    private static void displayTransactionHistory(String userId) throws SQLException {
        String query = "SELECT * FROM transaction WHERE user_id = ? ";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userId);
        resultSet = preparedStatement.executeQuery();

        System.out.println("\nTransaction History:");
        System.out.println("Transaction ID | Transaction Type | Amount | Date");
        System.out.println("-----------------------------------------------");
        while (resultSet.next()) {
            int transactionId = resultSet.getInt("transaction_id");
            String transactionType = resultSet.getString("type");
            double amount = resultSet.getDouble("amount");
            Date transactionDate = resultSet.getDate("date");

            System.out.printf("%-15d %-18s %-7.2f %s%n", transactionId, transactionType, amount, transactionDate);
        }
    }

    private static void performWithdrawal(String userId) throws SQLException {
    	System.out.print("Enter the amount to withdraw: ");
        double withdrawalAmount = scanner.nextDouble();

        if (withdrawalAmount <= 0) {
            System.out.println("Invalid amount. Please try again.");
            return;
        }

        double balance = getBalance(userId);

        if (balance < withdrawalAmount) {
            System.out.println("Insufficient funds. Please try again.");
            return;
        }

        updateBalance(userId, balance - withdrawalAmount);
        addTransaction(userId, "Withdrawal", withdrawalAmount);
        System.out.println("Withdrawal successful.");
    }

    private static void performDeposit(String userId) throws SQLException {
    	System.out.print("Enter the amount to deposit: ");
        double depositAmount = scanner.nextDouble();

        if (depositAmount <= 0) {
            System.out.println("Invalid amount. Please try again.");
            return;
        }

        double balance = getBalance(userId);
        updateBalance(userId, balance + depositAmount);
        addTransaction(userId, "Deposit", depositAmount);
        System.out.println("Deposit successful.");
    }

    private static void performTransfer(String userId) throws SQLException {
    	System.out.print("Enter the recipient's ID: ");
        String recipientId = scanner.next();

        if (!userExists(recipientId)) {
            System.out.println("Recipient not found. Please try again.");
            return;
        }
        System.out.println("Enter the amount to transfer: ");
        double transferAmount = scanner.nextDouble();

        if (transferAmount <= 0) {
            System.out.println("Invalid amount. Please try again.");
            return;
        }

        double balance = getBalance(userId);

        if (balance < transferAmount) {
            System.out.println("Insufficient funds. Please try again.");
            return;
        }

        updateBalance(userId, balance - transferAmount);
        updateBalance(recipientId, getBalance(recipientId) + transferAmount);
        addTransaction(userId, "Transfer to " + recipientId, transferAmount);
        System.out.println("Transfer successful.");
    }

    private static double getBalance(String userId) throws SQLException {
        String query = "SELECT balance FROM users WHERE user_id = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userId);
        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getDouble("balance");
        }

        throw new SQLException("User not found.");
    }

    private static void updateBalance(String userId, double newBalance) throws SQLException {
        String query = "UPDATE users SET balance = ? WHERE user_id = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setDouble(1, newBalance);
        preparedStatement.setString(2, userId);
        preparedStatement.executeUpdate();
    }

    private static void addTransaction(String userId, String transactionType, double amount) throws SQLException {
        String query = "INSERT INTO transaction (user_id, type, amount) VALUES (?, ?, ?)";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userId);
        preparedStatement.setString(2, transactionType);
        preparedStatement.setDouble(3, amount);
        preparedStatement.executeUpdate();
    }

    private static boolean userExists(String userId) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userId);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }
}
