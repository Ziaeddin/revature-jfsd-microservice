package com.zia.auth.jwt.service.service;

import com.zia.auth.jwt.service.payload.LoginDto;
import com.zia.auth.jwt.service.payload.RegisterDto;

public interface AuthService {

    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);
    String addNewRole(String roleName);

}
