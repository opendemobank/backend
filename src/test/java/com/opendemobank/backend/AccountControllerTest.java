package com.opendemobank.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest extends BaseTest {

    @Test
    public void testShowCustomerAccount() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/accounts/customer")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"iban\":\"EE909900123456789012\""), "Response doesn't contain correct IBAN.");
    }

    @Test
    public void testShowCustomerAccountById() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/accounts/1")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"iban\":\"EE909900123456789012\""), "Response doesn't contain correct IBAN.");

        /* Check that other customers can view only their own accounts */
        requestBuilder = MockMvcRequestBuilders
                .get("/api/accounts/1")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_2_BEARER_TOKEN);

        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

}
