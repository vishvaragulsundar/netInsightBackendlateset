package com.prodapt.netinsight.Authentication.Controller;

import com.prodapt.netinsight.Authentication.Entity.AuthRequest;
import com.prodapt.netinsight.Authentication.Entity.UserInfo;
import com.prodapt.netinsight.Authentication.Repository.UserInfoRepo;
import com.prodapt.netinsight.Authentication.Service.JwtService;
import com.prodapt.netinsight.Authentication.Service.UserDataService;
import com.prodapt.netinsight.exceptionsHandler.AppExceptionHandler;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


/**
 * Controller class for database operations.
 */
@RestController
public class UserAccountManagementAPIs {

    @Autowired
    private UserDataService Service;
    @Autowired
    private UserInfoRepo repo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    AppExceptionHandler appExceptionHandler;

    Logger logger = LoggerFactory.getLogger(UserAccountManagementAPIs.class);

    /**
     * This PostMapping is responsible for handling the addition of a new User to the database.
     *
     * @param userInfo The User information to be added, provided as a JSON request body.
     * @return A message indicating the successful creation of the User.
     */
    @PostMapping("/auth/adduser")
    public JSONObject addUser(@RequestBody UserInfo userInfo) {

        JSONObject response = new JSONObject();
        try {
            Optional<UserInfo> userInfo1 = repo.findByName(userInfo.getName());
            if (userInfo1.isPresent()) {
                appExceptionHandler.raiseException("Given Username: " + userInfo.getName().toString() +
                        " already exist !!!");
            }

            Optional<UserInfo> userInfo2 = repo.findByEmailid(userInfo.getEmailid());
            if (userInfo2.isPresent()) {
                appExceptionHandler.raiseException("Given Email id: " +userInfo.getEmailid().toString()+
                        " already exist !!!");
            }

            userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
            userInfo.setRole(userInfo.getRole().toUpperCase());
            repo.save(userInfo);
            response.put("status", "Success");

        } catch (Exception e) {
            e.printStackTrace();
            appExceptionHandler.raiseException(e.getMessage());
        }
        return response;

    }

    /**
     * This function is responsible for update the role
     *
     * @param emailid the emailId of the user to update
     * @param role    the new role to assign to the user
     * @return a message indicating the success or failure of the update
     */
    @PutMapping("/auth/updateuser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String changeRoleByRole(@RequestParam String emailid, @RequestParam String role) {
        return Service.changeRole(emailid, role);
    }

    /**
     * This GetMapping is responsible for retrieving all User data from the database.
     * Only users with the 'ADMIN' authority are allowed to access this endpoint.
     *
     * @return A list of UserInfo objects representing all the Users in the database.
     */
    @GetMapping("/auth/getalluser")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserInfo> getUser() {
        return repo.findAll();
    }

    /**
     * Endpoint for authenticating a user and generating a JWT token.
     *
     * @param authRequest the authentication request containing the username and password
     * @return the generated JWT token if the user is authenticated
     * @throws UsernameNotFoundException if the user is not found or the authentication fails
     */
    @PostMapping("/authenticate")
    public JSONObject authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        logger.debug("authRequest.getUsername()"+authRequest.getUsername());
        logger.debug("authRequest.getPassword()"+authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            JSONObject response = jwtService.generateToken(authRequest.getUsername());
            return response;
        } 
        else {
            logger.debug("invalid user request !!!");
            throw new AppExceptionHandler.UnauthorizedException("invalid user request !!!");
        }
    }
}
