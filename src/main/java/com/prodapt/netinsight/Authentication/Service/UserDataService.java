package com.prodapt.netinsight.Authentication.Service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.prodapt.netinsight.Authentication.Entity.UserInfo;
import com.prodapt.netinsight.Authentication.Repository.UserInfoRepo;


/**
 *DataService class for managing user data operations.
 */
@Component
public class UserDataService {
    
    @Autowired
    private UserInfoRepo repo;
    
    /**
     *Updates the role of a User based on the provided email ID.
     *@param emailid The email ID of the User whose role is to be updated.
     *@param role The new role to assign to the User.
     *@return A message indicating the successful update of the User's role.
     *@throws RuntimeException if the User with the given email ID is not found.
     */
    public String changeRole(String emailid, String role) {
        UserInfo userInfo = repo.findByEmailid(emailid)
                .orElseThrow(() -> new RuntimeException("ID is not found: " + emailid));
        userInfo.setRole(role.toUpperCase());
        repo.save(userInfo);
        return "Role updated successfully";
    }
    

}
