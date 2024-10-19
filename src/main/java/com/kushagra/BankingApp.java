package com.kushagra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/banking_system";
    public static final String username = "root";
    public static final String password = "root";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.printf(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(JDBC_URL, username, password);
            Scanner sc = new Scanner(System.in);
            User user = new User(connection, sc);
            Accounts accounts = new Accounts(connection, sc);
            AccountManager accountManager = new AccountManager(connection, sc);

            String email;
            long acc_no;

            while(true){
                System.out.printf("#### This is My Banking System ####\n");
                System.out.printf("1. Register\n" + "2. Login\n" + "3. Exit\n" + "Enter your choice 😁 :");
                int choice = sc.nextInt();

                switch (choice){
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email = user.login();
                        if(email != null){
                            System.out.printf("\n You have successfully logged in\n");
                            if(!(accounts.account_exists(email))){
                                System.out.printf("\n 1: Open an account\n 2: Exit\n Enter your choice ?? : ");
                                int ch = sc.nextInt();

                                if(ch == 1){
                                    acc_no = accounts.open_account(email);
                                    System.out.printf("\n Account created successfully with account number %d\n", acc_no);
                                }else{
                                    break;
                                }
                            }

                            acc_no = accounts.getAccountNumber(email);
                            int choice2 = 0;

                            while( choice2 != 5) {
                                System.out.printf("\n 1: Credit money\n 2: Debit money\n 3: Transfer money\n 4: Check balance\n 5: Log Out\n Enter your choice ?? : ");
                                choice2 = sc.nextInt();


                                switch (choice2) {
                                    case 1:
                                        accountManager.credit_money(acc_no);
                                        break;
                                    case 2:
                                        accountManager.debit_money(acc_no);
                                        break;
                                    case 3:
                                        accountManager.transfer_money(acc_no);
                                        break;
                                    case 4:
                                        accountManager.getBalance(acc_no);
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.printf("\n Invalid choice\n");
                                        break;
                                }
                            }
                        }else{
                            System.out.printf("\n Invalid email or password\n");
                        }
                    case 3:
                        System.out.printf("\n Thank you for using our services\n");
                        System.out.printf("Exiting...\n");
                        return;
                    default:
                        System.out.printf("\n Invalid choice\n");
                        break;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
