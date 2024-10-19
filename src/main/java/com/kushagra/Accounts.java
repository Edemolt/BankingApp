package com.kushagra;

import java.sql.*;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner sc;

    public Accounts(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

//    first need to check if account exists
    public boolean account_exists(String email){
        String query = "SELECT account_number FROM Accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // here i will generate an account no
    public long generateAccNo(){
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT account_number FROM Accounts ORDER BY account_number DESC LIMIT 1");

            if(resultSet.next()){
                long last_acc_no = resultSet.getLong("account_number");
                return last_acc_no + 1;
            }else{
                return 10000100;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }

    public long open_account(String mail){
        if(!(account_exists(mail))){
            String open_acc_query = "INSERT INTO Accounts(account_number, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?)";
            sc.nextLine();
            System.out.printf("Enter your full name : ");
            String full_name = sc.nextLine();
            System.out.printf("Enter the initial balance : ");
            double balance = sc.nextDouble();
            sc.nextLine();
            System.out.printf("Enter the security pin : ");
            String security_pin = sc.nextLine();

            try{
                long acc_no = generateAccNo();
                PreparedStatement preparedStatement = connection.prepareStatement(open_acc_query);
                preparedStatement.setLong(1, acc_no);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, mail);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, security_pin);
                int rows_affected = preparedStatement.executeUpdate();

                if(rows_affected > 0){
                    System.out.printf("Account number %d created successfully\n", acc_no);
                    return acc_no;
                }else{
                    throw new RuntimeException("Failed to create account");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account already exists");
    }

    public long getAccountNumber(String email){
        String query = "SELECT account_number FROM Accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return resultSet.getLong("account_number");
            }else{
                throw new RuntimeException("Account does not exist");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Account does not exist");
    }

}
