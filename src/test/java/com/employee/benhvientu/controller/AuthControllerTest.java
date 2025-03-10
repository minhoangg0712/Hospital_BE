package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.RegisterRequest;
import com.employee.benhvientu.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(authService.authenticate("validUser", "correctPass"))
                .thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"validUser\", \"password\": \"correctPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        when(authService.authenticate("validUser", "wrongPass"))
                .thenThrow(new RuntimeException("Mật khẩu không chính xác!"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"validUser\", \"password\": \"wrongPass\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Mật khẩu không chính xác!"));
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        when(authService.authenticate("unknownUser", "password"))
                .thenThrow(new RuntimeException("Người dùng không tồn tại!"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"unknownUser\", \"password\": \"password\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Người dùng không tồn tại!"));
    }

    @Test
    void testLoginWithEmptyFields() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Gửi request rỗng
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPassword("password123");
        request.setEmail("newuser@example.com");

        when(authService.registerUser(request)).thenReturn(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"newUser\", \"password\": \"password123\", \"email\": \"newuser@example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterWithExistingUsername() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setPassword("password123");
        request.setEmail("existing@example.com");

        when(authService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username đã tồn tại!"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"existingUser\", \"password\": \"password123\", \"email\": \"existing@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username đã tồn tại!"));
    }



    @Test
    void testRegisterWithEmptyFields() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Gửi request rỗng
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Thiếu thông tin đăng ký"));
    }


    @Test
    void testRegisterWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"newUser\", \"password\": \"password123\", \"email\": \"invalidemail\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
