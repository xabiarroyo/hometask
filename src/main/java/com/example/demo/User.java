package com.example.demo;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "users")
public class User{

    @Id
    @NotNull (message = "Name cannot be null")
    @Pattern(regexp = "[a-zA-Z]+", message = "Name must contain only letters")
    private String name;

    @Column(name = "dateOfBirth")
    @NotNull(message = "Date of birth cannot be null")
    private LocalDate dateOfBirth;

    public User() {
        super();
    }

    public User(String name, LocalDate dateOfBirth) {
        super();
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}