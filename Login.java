
package com.mycompany.lastpoe;


public class Login {
private String storedUsername;
    private String storedPassword;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public Login(String username, String password, String firstName, String lastName, String phoneNumber) {
        this.storedUsername = username;
        this.storedPassword = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public boolean loginUser(String username, String password) {
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

    public static boolean isValidPassword(String password) {
        boolean hasUppercase = false, hasDigit = false, hasSpecialChar = false;
        if (password.length() < 8) return false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }
        return hasUppercase && hasDigit && hasSpecialChar;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

//cc

