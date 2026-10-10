package com.careerlens.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.careerlens.api.dto.LoginRequest;
import com.careerlens.api.entity.User;
import com.careerlens.api.service.JwtService;
import com.careerlens.api.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService,
                          JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        System.out.println("REGISTER API CALLED");

        try {

            User registeredUser = userService.registerUser(user);

            // Password ni response lo pampakunda null chestunnam
            registeredUser.setPassword(null);

            return ResponseEntity.ok(registeredUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }


    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        System.out.println("LOGIN API CALLED");
        System.out.println("Login Email: " + request.getEmail());

        try {

            // Find user using email
            User user = userService.findByEmail(
                    request.getEmail()
            );

            System.out.println("USER FOUND: " + user.getEmail());

            // Check password
            boolean passwordCorrect =
                    userService.checkPassword(
                            request.getPassword(),
                            user.getPassword()
                    );

            if (!passwordCorrect) {

                System.out.println(
                        "LOGIN FAILED - WRONG PASSWORD"
                );

                return ResponseEntity
                        .status(401)
                        .body(Map.of(
                                "message",
                                "Invalid email or password"
                        ));
            }

            // Generate JWT token
            String token =
                    jwtService.generateToken(
                            user.getEmail()
                    );

            System.out.println(
                    "LOGIN SUCCESS - JWT GENERATED"
            );

            // Response
            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Login successful"
            );

            response.put(
                    "token",
                    token
            );

            response.put(
                    "email",
                    user.getEmail()
            );

            response.put(
                    "role",
                    user.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            System.out.println(
                    "LOGIN ERROR: " + e.getMessage()
            );

            return ResponseEntity
                    .status(401)
                    .body(Map.of(
                            "message",
                            "Invalid email or password"
                    ));
        }
    }
}