package com.aeromatx.back.config;

import com.aeromatx.back.entity.ERole;
import com.aeromatx.back.entity.Role;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.repository.RoleRepository;
import com.aeromatx.back.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RoleInitializer(RoleRepository roleRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles
        initializeRole(ERole.ROLE_CUSTOMER);
        initializeRole(ERole.ROLE_OEM);
        initializeRole(ERole.ROLE_ADMIN); // Add ADMIN role
        
        // Initialize admin user
        initializeAdminUser();
        
        System.out.println("Initialization process complete.");
    }

    private void initializeRole(ERole roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(new Role(roleName));
            System.out.println(roleName + " created.");
        } else {
            System.out.println(roleName + " already present.");
        }
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@aeromatx.com");
            admin.setPassword(passwordEncoder.encode("admin@123"));
            
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            admin.setRoles(Collections.singleton(adminRole));
            userRepository.save(admin);
            
            System.out.println("Default admin user created. Username: admin, Password: admin@123");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}