package com.aeromatx.back.config;

import com.aeromatx.back.entity.ERole;
import com.aeromatx.back.entity.Role;
import com.aeromatx.back.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(ERole.ROLE_CUSTOMER).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_CUSTOMER));
            System.out.println("ROLE_CUSTOMER created.");
        } else {
            System.out.println("ROLE_CUSTOMER already present.");
        }

        if (roleRepository.findByName(ERole.ROLE_OEM).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_OEM));
            System.out.println("ROLE_OEM created.");
        } else {
            System.out.println("ROLE_OEM already present.");
        }

        System.out.println("Role initialization process complete.");
    }
}