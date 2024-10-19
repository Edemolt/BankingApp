package com.kushagra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private Connection connection;
    private Scanner sc;

    public User(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    // first i need to check if user exists or not
    // Check if user exists
    public boolean user_exists(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();  // Returns true if user exists
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


//    The method registers a new user by inserting their details into the Users table.
    public void register() {
        sc.nextLine();
        System.out.printf("Enter your full name : ");
        String full_name = sc.nextLine();
        System.out.printf("Enter your email : ");
        String email = sc.nextLine();
        System.out.printf("Enter your password : ");
        String password = sc.nextLine();

        if (user_exists(email)) {
            System.out.printf("User with email %s already exists\n", email);
            return;
        }

        String register_query = "INSERT INTO user(full_name, email, password) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(register_query);
            preparedStatement.setString(1, full_name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            int rows_affected = preparedStatement.executeUpdate();

            if (rows_affected > 0) {
                System.out.printf("User with email %s registered successfully\n", email);
            } else {
                System.out.printf("Failed to register user with email %s\n", email);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // i will need to log in people too ig

//    logs in a user by verifying the email and security pin. If successful, it returns the account number.

    public String login(){
        sc.nextLine();
        System.out.printf("Enter your email : ");
        String email = sc.nextLine();
        System.out.printf("Enter your password : ");
        String password = sc.nextLine();
        String login_query = "SELECT * FROM user WHERE email = ? and password = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                System.out.printf("User with email %s logged in successfully\n", email);
                return email;
            }else{
                System.out.printf("Invalid email or password\n");
                return null;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


}
