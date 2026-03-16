package com.collegeapp.model;

/**
 * Admin entity representing an admin user in the Campus Management System.
 * Extends User with no additional attributes.
 * Role is fixed to ADMIN.
 */
public class Admin extends User {
    
    // -------------------
    // Constructors
    // -------------------

    // Default Constructor
    public Admin() {
        super();
    }

    // Parameterized Constructor
    public Admin(long id, String name, String email, String password) {
        super(id, name, email, password, "ADMIN");
    }

    // -------------------
    // Abstract Method Implementation
    // -------------------

    @Override
    public void displayDetails() {
        System.out.println("========== Admin Details ==========");
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Role: " + getRole());
        System.out.println("===================================");
    }

    // toString (do NOT print password or system ID)
    @Override
    public String toString() {
        return "Admin{" +
                "name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
