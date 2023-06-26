package com.example.demo;

import jakarta.validation.constraints.NotNull;

public class Birthday{

	@NotNull(message = "Date of birth cannot be null")
    private String dateOfBirth;

    public Birthday() {
        super();
    }

    public Birthday(String dateOfBirth) {
        super();
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}