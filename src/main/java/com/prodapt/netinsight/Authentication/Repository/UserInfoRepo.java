package com.prodapt.netinsight.Authentication.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.prodapt.netinsight.Authentication.Entity.UserInfo;

public interface UserInfoRepo extends JpaRepository<UserInfo, Integer> {

    Optional<UserInfo> findByName(String name);

    //Update the Role
    Optional<UserInfo> findByEmailid(String emailid);
}
