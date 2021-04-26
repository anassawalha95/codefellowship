package com.example.codefellowship;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser,Integer> {
    public ApplicationUser findByUsername(String userName);
}
