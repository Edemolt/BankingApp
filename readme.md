# BankingApp

This is a simple banking application that allows users to manage their accounts securely.

## Prerequisites

- Java Development Kit (JDK)
- MySQL Server

## Getting Started

Follow these instructions to set up the BankingApp on your local machine.

### 1. Clone the Repository

First, clone the repository to your local machine:

```bash
git clone git@github.com:Edemolt/BankingApp.git
```
### 2. In terminal 

```bash
mysql -u root -p
```

```bash
CREATE DATABASE banking_system;
USE banking_system;
```

### 3. Create tables

```bash
CREATE TABLE accounts (
    account_number BIGINT NOT NULL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    balance DECIMAL(10, 2) NOT NULL,
    security_pin CHAR(4) NOT NULL
);
```

```bash
CREATE TABLE user (
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);
```

### 4. Go to banking app dir
```bash
cd BankingApp
```

### 5. Run the application
```bash 
java BankingApp
```



