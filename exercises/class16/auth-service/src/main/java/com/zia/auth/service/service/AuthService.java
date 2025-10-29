package com.zia.auth.service.service;

import com.zia.auth.service.payload.LoginDto;
import com.zia.auth.service.payload.RegisterDto;

public interface AuthService {

    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);
    String addNewRole(String roleName);

}
