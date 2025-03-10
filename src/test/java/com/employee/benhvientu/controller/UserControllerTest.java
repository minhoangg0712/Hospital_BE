package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetPatientProfilesSuccess() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn("doctorUser");
        when(userService.getPatientProfiles("doctorUser"))
                .thenReturn(Collections.singletonList(new UserDTO()));

        mockMvc.perform(get("/api/patient/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetPatientProfilesUnauthorized() throws Exception {
        mockMvc.perform(get("/api/patient/profiles"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdatePatientProfileSuccess() throws Exception {
        UserDTO request = new UserDTO();
        request.setName("Updated Name");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn("patientUser");
        when(userService.updatePatientProfile("patientUser", request))
                .thenReturn(request);

        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void testUpdatePatientProfileUnauthorized() throws Exception {
        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Name\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdatePatientProfileInvalidData() throws Exception {
        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePatientProfileWithUnauthorizedUser() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn("doctorUser");

        mockMvc.perform(put("/api/patient/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Name\"}"))
                .andExpect(status().isForbidden());
    }
}
