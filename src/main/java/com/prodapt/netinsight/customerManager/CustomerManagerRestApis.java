package com.prodapt.netinsight.customerManager;

import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import com.prodapt.netinsight.exceptionsHandler.ExceptionDetails;
import com.prodapt.netinsight.exceptionsHandler.ServiceException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This class holds all the REST API endpoints for handling Customer
 */
@RestController
public class CustomerManagerRestApis {

    Logger logger = LoggerFactory.getLogger(CustomerManagerRestApis.class);

    @Autowired
    AppExceptionHandler appExceptionHandler;

    @Autowired
    CustomerRepository customerRepository;

    @PostMapping("/createCustomer")
    public JSONObject createCustomer(@RequestBody Customer customer) {

        logger.info("Inside createCustomer, customer value received: {}", customer.getName());
        JSONObject response = new JSONObject();
        try {
            checkName(customer.getName());
            Customer existingCustomerName = customerRepository.findByCustomerName(customer.getName().toLowerCase());
            if (existingCustomerName != null) {
                appExceptionHandler.raiseException("Given Customer Name is already used");
            }
            if (customer.getCustomerGroupName() == null) {
                appExceptionHandler.raiseException("Customer GroupName is Mandatory");
            }
            if (customer.getEmailId() == null) {
                appExceptionHandler.raiseException("EmailId is Mandatory");
            }
            if (customer.getContactNo() == null) {
                appExceptionHandler.raiseException("ContactNo is Mandatory");
            }

            String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
            Pattern pattern = Pattern.compile(emailRegex);
            if (!(pattern.matcher(customer.getEmailId()).matches())) {
                appExceptionHandler.raiseException("Please provide valid emailId");
            }

            Customer customerDetails = customerRepository.createCustomer(customer.getName().toLowerCase(),
                    customer.getDescription(), customer.getEmailId(), customer.getContactNo());

            response.put("status", "Success");
            response.put("customer", customerDetails);
        } catch (ServiceException e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }

    @PutMapping("/updateCustomer")
    public JSONObject updateCustomer(@RequestParam(name = "customerName") String customerName, @RequestBody Customer customer) {
        customerName = customerName.toLowerCase();
        logger.info("Inside updateCustomer, customerName value received: {}", customerName);
        JSONObject response = new JSONObject();
        try {


            Customer existingCustomerName = customerRepository.findByCustomerName(customer.getName().toLowerCase());
            if (existingCustomerName == null) {
                appExceptionHandler.raiseException("Given Customer Name is not available");
            }

            if (customer.getCustomerGroupName() == null) {
                appExceptionHandler.raiseException("Customer CustomerGroupName is Mandatory");
            }
            if (customer.getEmailId() == null) {
                appExceptionHandler.raiseException("EmailId is Mandatory");
            }
            if (customer.getContactNo() == null) {
                appExceptionHandler.raiseException("ContactNo is Mandatory");
            }

            String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
            Pattern pattern = Pattern.compile(emailRegex);
            if (!(pattern.matcher(customer.getEmailId()).matches())) {
                appExceptionHandler.raiseException("Please provide valid emailId");
            }

            if (!customerName.equalsIgnoreCase(customer.getName().toLowerCase())) {
                appExceptionHandler.raiseException("Request parameter does not match with the request body");
            }

            checkName(customer.getName());
            Customer customerDetails = customerRepository.updateCustomer(customer.getName().toLowerCase(),
                    customer.getDescription(), customer.getEmailId(), customer.getContactNo());
            response.put("status", "Success");
            response.put("customer", customerDetails);
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
    }


    @GetMapping("/getCustomerByName")
    public Customer getCustomerByName(@RequestParam(name = "customerName") String customerName) {
        customerName = customerName.toLowerCase();
        logger.info("Inside getCustomerByName, customerName value received: {}", customerName);
        Customer customer = null;
        try {
            customer = customerRepository.findByCustomerName(customerName);
            if (customer == null) {
                appExceptionHandler.raiseException("Customer not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return customer;
    }


    @GetMapping("/getAllCustomers")
    public ArrayList<Customer> getAllCustomers() {
        logger.info("Inside getAllCustomers");
        ArrayList<Customer> customers = null;
        try {
            customers = customerRepository.getAllCustomers();
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return customers;
    }

    @DeleteMapping("/deleteCustomerByName")
    public JSONObject deleteCustomer(@RequestParam(name = "Customer") String name) {
        JSONObject response = new JSONObject();
        Customer existingCustomer = null;
        try{
        existingCustomer = customerRepository.findByCustomerName(name);
        if(existingCustomer == null) {
            appExceptionHandler.raiseException("The customer " + name + " is not Found");
        }
        customerRepository.delete(existingCustomer);
            response.put("customer", existingCustomer);
            response.put("status", "Deleted");
        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;
        }

    public static void checkName(String Name) throws ServiceException {
        if (Name == null || Name.trim().isEmpty()) {
            throw new ServiceException("Name cannot be empty or null", new ExceptionDetails(
                    "Name cannot be empty or null", "Name cannot be empty or null"));
        }
    }
}
