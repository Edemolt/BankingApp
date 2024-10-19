package com.kushagra;

// handles operations like crediting, debiting, transferring money and checking the balance


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {

    // database connection object
    private Connection connection;
    // Scanner for reading the user input
    private Scanner sc;

    // constructor to accept database connection and scaneer
    AccountManager(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }



    

//    This method allows the user to credit money into their account after verifying the account number and security pin.
//    It updates the balance in the Accounts table and commits the transaction if successful.
    public void credit_money( long acc_no){
        // reads amt and security pin from the customer
        sc.nextLine();
        System.out.printf("Enter the amount : ");
        double amt = sc.nextDouble();
        sc.nextLine();
        System.out.printf("Enter the security pin : ");
        String security_pin = sc.nextLine();

        // will initiate a transaction by setting auto comit to false
        try{
            connection.setAutoCommit(false);
            if( acc_no != 0){
                // verify account with account number and security pin
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?"); // prepare statement
                preparedStatement.setLong(1, acc_no); // set account number
                preparedStatement.setString(2, security_pin); // set security pin
                ResultSet resultSet = preparedStatement.executeQuery(); // execute query

                // if the account is valid, update the balance by adding the amount
                if(resultSet.next()){
                    String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1, amt);
                    preparedStatement1.setLong(2, acc_no);
                    int rows_affected = preparedStatement1.executeUpdate();

                    // if the query is successful, commit the transaction
                    if(rows_affected > 0){
                        System.out.printf("₹%.2f credited successfully to account number %d\n", amt, acc_no);
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    }else{
                        System.out.printf("Failed to credit ₹%.2f to account number %d\n", amt, acc_no);
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                }else{
                    System.out.printf("Invalid account number or security pin\n");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


//    This method debits money from the account, ensuring that the user has sufficient balance before proceeding.
    public void debit_money(long acc_no) throws SQLException {
        sc.nextLine();
        System.out.printf("Enter the amount : ");
        double amt = sc.nextDouble();
        sc.nextLine();
        System.out.printf("Enter the security pin : ");
        String security_pin = sc.nextLine();

        try{
            connection.setAutoCommit(false);
            if(acc_no != 0){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
                preparedStatement.setLong(1, acc_no);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if( resultSet.next()){
                    double curr_balance = resultSet.getDouble("balance");

                    if(amt <= curr_balance){
                        // deduct the amt from the balance and commit
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amt);
                        preparedStatement1.setLong(2, acc_no);
                        int rows_affected = preparedStatement1.executeUpdate();

                        if(rows_affected > 0) {
                            System.out.printf("₹%.2f debited successfully from account number %d\n", amt, acc_no);
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        }else{
                            System.out.printf("Failed to debit ₹%.2f from account number %d\n", amt, acc_no);
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }else{
                        System.out.printf("Insufficient balance\n");
                    }
                }else{
                    System.out.printf("Insufficient balance\n");
                }
            }else{
                System.out.printf("Invalid account number or security pin\n");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connection.setAutoCommit(true);
        }
    }

    // This method transfers money between accounts after verifying both sender's and receiver's accounts and debiting the sender's account.
    public void transfer_money(long sender_acc_no) throws SQLException {
        // Ask for receiver account number, amount, and security pin
        sc.nextLine();
        System.out.printf("Enter the receiver account number : ");
        long receiver_acc_no = sc.nextLong();
        System.out.printf("Enter the amount : ");
        double amt = sc.nextDouble();
        sc.nextLine();
        System.out.printf("Enter the security pin : ");
        String security_pin = sc.nextLine();

        // Ensure both accounts exist and transfer money by debiting the sender and crediting the receiver
        try {
            connection.setAutoCommit(false);  // Disable auto-commit for the transaction

            if (sender_acc_no != 0 && receiver_acc_no != 0) {
                // Verify sender's account and security pin
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?");
                preparedStatement.setLong(1, sender_acc_no);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                // If sender's account is valid and has sufficient balance
                if (resultSet.next()) {
                    double curr_bal = resultSet.getDouble("balance");
                    if (amt <= curr_bal) {
                        // Deduct the amount from the sender's balance
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement debit_statement = connection.prepareStatement(debit_query);
                        debit_statement.setDouble(1, amt);
                        debit_statement.setLong(2, sender_acc_no);

                        // Add the amount to the receiver's balance
                        String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement credit_statement = connection.prepareStatement(credit_query);
                        credit_statement.setDouble(1, amt);
                        credit_statement.setLong(2, receiver_acc_no);

                        // Execute both debit and credit
                        int rows_affected1 = debit_statement.executeUpdate();
                        int rows_affected2 = credit_statement.executeUpdate();

                        // If both operations are successful, commit the transaction
                        if (rows_affected1 > 0 && rows_affected2 > 0) {
                            System.out.printf("₹%.2f transferred successfully from account number %d to account number %d\n", amt, sender_acc_no, receiver_acc_no);
                            connection.commit();
                            connection.setAutoCommit(true);  // Re-enable auto-commit
                            return;
                        } else {
                            // Rollback if either operation fails
                            System.out.printf("Failed to transfer ₹%.2f from account number %d to account number %d\n", amt, sender_acc_no, receiver_acc_no);
                            connection.rollback();
                        }
                    } else {
                        System.out.printf("Insufficient balance\n");
                    }
                } else {
                    System.out.printf("Invalid account number or security pin\n");
                }
            } else {
                System.out.printf("Invalid account number or security pin\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);  // Ensure auto-commit is re-enabled in case of failure
        }
    }

    public void getBalance(long acc_no){
        sc.nextLine();
        System.out.printf("Enter security pin : ");
        String security_pin = sc.nextLine();

        try{
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
            preparedStatement.setLong(1, acc_no);
            preparedStatement.setString(2, security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                System.out.printf("Your account balance is ₹%.2f\n", resultSet.getDouble("balance"));
            }else{
                System.out.printf("Invalid account number or security pin\n");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}
