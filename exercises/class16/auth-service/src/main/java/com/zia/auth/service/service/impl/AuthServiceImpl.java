package com.zia.auth.service.service.impl;

import com.zia.auth.service.entity.Role;
import com.zia.auth.service.payload.LoginDto;
import com.zia.auth.service.payload.RegisterDto;
import com.zia.auth.service.repository.RoleRepository;
import com.zia.auth.service.repository.UserRepository;
import com.zia.auth.service.security.JwtTokenProvider;
import com.zia.auth.service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }

    @Override
    public String register(RegisterDto registerDto) {
        //add check for username exists in database
        if(userRepository.existsByUsername(registerDto.getUsername())){
            throw new RuntimeException("Username is already taken!");
        }
        //add check for email exists in database
        if(userRepository.existsByEmail(registerDto.getEmail())){
            throw new RuntimeException("Email is already taken!");
        }

        //create user object
        com.zia.auth.service.entity.User user = new com.zia.auth.service.entity.User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // set roles to user
        System.out.println(registerDto.getRoleName());
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(registerDto.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found")));
        user.setRoles(roles);

        //save user to database
        userRepository.save(user);

        return "User registered successfully";
    }

    @Override
    public String addNewRole(String roleName) {
        if(roleRepository.existsByName(roleName)){
            throw new RuntimeException("Role: " +roleName+"is already taken!");
        }

        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
        return "Role added successfully";
    }
}
