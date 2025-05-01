package com.littlestore.controller;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.littlestore.entity.Customer;
import com.littlestore.service.CustomerService;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void testResetPassword_withValidTokenAndPassword_shouldUpdateAndRedirect() throws Exception {
        String validToken = "abc123";
        String newPassword = "newSecurePassword123";
        Customer mockCustomer = new Customer();
        mockCustomer.setEmail("user@example.com");

        when(customerService.findByResetToken(validToken)).thenReturn(mockCustomer);
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encryptedPassword");

        mockMvc.perform(post("/resetPassword")
                .param("token", validToken)
                .param("password", newPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertNull(mockCustomer.getResetToken());
        verify(customerService).update(mockCustomer);
    }
}
