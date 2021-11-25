package com.opendemobank.backend;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

public class BaseTest {
    public String DEFAULT_ADMIN_BEARER_TOKEN;
    public String DEFAULT_CUSTOMER_BEARER_TOKEN;
    public String DEFAULT_CUSTOMER_2_BEARER_TOKEN;

    @BeforeEach
    public void setUp() throws Exception {
        DEFAULT_ADMIN_BEARER_TOKEN = getBearerToken("admin@opendemobank.com", "admin");
        DEFAULT_CUSTOMER_BEARER_TOKEN = getBearerToken("customer@opendemobank.com", "customer");
        DEFAULT_CUSTOMER_2_BEARER_TOKEN = getBearerToken("customer2@opendemobank.com", "customer2");
    }

    @Autowired
    public MockMvc mockMvc;

    public String getBearerToken(String email, String password) throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/users/login")
                .param("email", email)
                .param("password", password);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
}
