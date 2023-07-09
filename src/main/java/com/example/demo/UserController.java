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

import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.exceptions.InvalidDateException;

import jakarta.validation.Valid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository repository;

    
    /** 
     * This API Saves/updates the given user's name and date of birth in the database
     * @param userName  
     * @param date   
     */
    @PutMapping("/hello/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveUser(@PathVariable(value = "name") @Valid String userName, @RequestBody Birthday date) 
    		throws InvalidDateException {
        
    	logger.info("Save/Update given user");
        
    	//XAA: Check given date is valid and complies with format
        DateFormat df;
        df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        try {
        	df.parse(date.getDateOfBirth());
        }catch(Exception e){
        	logger.error(" InvalidDateException : Invalid date, please use format yyyy-MM-dd");
        	throw new InvalidDateException("Invalid date, please use format yyyy-MM-dd");
        }
           
        //XAA: Check given date is a date before today
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate dateBirthday = LocalDate.parse(date.getDateOfBirth(), dtf);
     	LocalDate dateToday = LocalDateTime.now().atZone(ZoneId.of("Europe/Madrid")).toLocalDate();
    	if ((dateBirthday.isAfter(dateToday))||(dateBirthday.isEqual(dateToday))){
    		logger.error(" InvalidDateException : Invalid date, must be a date before today date");
    		throw new InvalidDateException("Invalid date, must be a date before today date");
    	}	
    	
    	
        User userdb = repository.findByName(userName);
        if ((userdb != null) && (!userdb.equals(null)))
        	//XAA: Change date of birth of an existing entry
        	userdb.setDateOfBirth(dateBirthday);
        else
        {
        	//XAA: Create a new entry
        	userdb =new User();
        	userdb.setName(userName);
        	userdb.setDateOfBirth(dateBirthday);
        }
        repository.save(userdb);
        
        return;       
    }
    
    
    /** 
     * This API returns a hello birthday message for the given user
     * @param userName  
     */
    @GetMapping("/hello/{name}")
    public ResponseEntity<Greeting> getBirthday(@PathVariable(value = "name") @Valid String userName) 
    		throws UserNotFoundException {
        
    	logger.info("Get a hello birthday message");
    	Greeting greeting = new Greeting();
        
        //XAA: Check if the name exists in the database
        User user = repository.findByName(userName);
        if ((user != null) && (!user.equals(null)))
        {	
        	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        	LocalDate dateToday = LocalDateTime.now().atZone(ZoneId.of("Europe/Madrid")).toLocalDate();
        	LocalDate dateBirth = LocalDate.parse(user.getDateOfBirth().toString(), dtf);
            LocalDate dayBirth = dateBirth.withYear(LocalDateTime.now().atZone(ZoneId.of("Europe/Madrid")).getYear());
            
            //XAA: It's user's birthday
            if (dayBirth.getDayOfYear() == dateToday.getDayOfYear())
            {
            	greeting.setMessage("Hello, " + userName + "! Happy birthday!");
            }else if (dayBirth.getDayOfYear() > dateToday.getDayOfYear()) 
            {
            	    //XAA: This year's birthday is still to come
            		long days = ChronoUnit.DAYS.between(dateToday,dayBirth);
            		greeting.setMessage("Hello, " + userName + "! Your birthday is in " + days + " day(s) ");
            }else {
            	    //XAA: This year's birthday is over, check time until next year's
        		    dayBirth = dayBirth.withYear(dateToday.getYear()+1);
            		long days = ChronoUnit.DAYS.between(dateToday,dayBirth);
            		greeting.setMessage("Hello, " + userName + "! Your birthday is in " + days + " day(s) ");
            }
        }else
        {
        	logger.error(" UserNotFoundException Name not found :: " + userName);
        	throw new UserNotFoundException("Name not found :: " + userName);
        }

        return ResponseEntity.ok().body(greeting);
    }
    
    

    @GetMapping("/all")
    public List<User> getAllUsers() {
        logger.info("Get all users...");
        return repository.findAll();
    }

    @GetMapping("/testpage")
    public ResponseEntity<Greeting> getGreeting() {
        logger.info("Test page reached");
        Greeting greeting = new Greeting();
        greeting.setMessage("Hello this is a test page");
        return ResponseEntity.ok().body(greeting);
    }
}