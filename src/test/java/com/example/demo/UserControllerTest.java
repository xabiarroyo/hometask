package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.InvalidDateException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.UserRepository;
import com.example.demo.User;

import jakarta.validation.Valid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verify;

@RestController
public class UserControllerTest {

    	private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private UserController userController;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        
        /** 
         * These are the test methods for the getBirthday API
         */
        
        @Test
        void testGetBirthday_HappyBirthday() throws UserNotFoundException {
            // Mock user
            User mockUser = new User();
            mockUser.setName("John");
            mockUser.setDateOfBirth(LocalDate.now());

            // Mock repository
            when(userRepository.findByName(anyString())).thenReturn(mockUser);

            // Invoke the API
            ResponseEntity<Greeting> response = userController.getBirthday("John");

            // Verify the response
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertEquals("Hello, John! Happy birthday!", response.getBody().getMessage());
        }

        @Test
        void testGetBirthday_UpcomingBirthday() throws UserNotFoundException {
            // Mock user
            User mockUser = new User();
            mockUser.setName("Jane");
            mockUser.setDateOfBirth(LocalDate.now().plusDays(5));

            // Mock repository
            when(userRepository.findByName(anyString())).thenReturn(mockUser);

            // Invoke the API
            ResponseEntity<Greeting> response = userController.getBirthday("Jane");

            // Verify the response
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertTrue(response.getBody().getMessage().contains("Your birthday is in 5 day(s)"));
        }

        @Test
        void testGetBirthday_PastBirthday() throws UserNotFoundException {
            // Mock user
            User mockUser = new User();
            mockUser.setName("Alice");
            mockUser.setDateOfBirth(LocalDate.now().minusDays(10));

            // Mock repository
            when(userRepository.findByName(anyString())).thenReturn(mockUser);

            // Invoke the API
            ResponseEntity<Greeting> response = userController.getBirthday("Alice");

            // Verify the response
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertTrue(response.getBody().getMessage().contains("Your birthday is in"));
        }

        @Test
        void testGetBirthday_UserNotFound() {
            // Mock repository returning null
            when(userRepository.findByName(anyString())).thenReturn(null);

            // Invoke the API and expect UserNotFoundException
            Assertions.assertThrows(UserNotFoundException.class, () -> {
                userController.getBirthday("NonExistingUser");
            });
        }
        
        
        /** 
         *  These are the test methods for the saveUser API
         */
        
        @Test
        void testSaveUser_ValidInput() throws InvalidDateException {
        	
        	// Mock repository
            when(userRepository.findByName(anyString())).thenReturn(null);

            // Invoke the API
            userController.saveUser("John", new Birthday("1990-01-01"));

            // Verify the repository interaction
            verify(userRepository).findByName("John");                          
        }

        @Test
        void testSaveUser_UpdateExistingUser() throws InvalidDateException {
            // Mock existing user
            User existingUser = new User("Jane", LocalDate.parse("1985-05-10", DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            // Mock repository
            when(userRepository.findByName(anyString())).thenReturn(existingUser);

            // Invoke the API
            userController.saveUser("Jane", new Birthday("1990-01-01"));

            // Verify the repository interaction
            verify(userRepository).save(existingUser);
            Assertions.assertEquals(LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    existingUser.getDateOfBirth());
        }

        @Test
        void testSaveUser_InvalidDateFormat() {
            // Invoke the API and expect InvalidDateException
            Assertions.assertThrows(InvalidDateException.class, () -> {
                userController.saveUser("John", new Birthday("2022/01/01"));
            });
        }

        
        @Test
        void testSaveUser_FutureDateOfBirth() {
            // Invoke the API and expect InvalidDateException
            Assertions.assertThrows(InvalidDateException.class, () -> {
                userController.saveUser("John", new Birthday(LocalDate.now().plusDays(1).toString()));
            });
        }

}