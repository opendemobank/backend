package com.opendemobank.backend;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest extends BaseTest {

    String exampleCustomerJson = "{\n" +
            "    \"email\": \"paul@pahkel.ee\",\n" +
            "    \"password\": \"hunter2\",\n" +
            "    \"fullName\": \"Paul Pähkel\",\n" +
            "    \"phoneNumber\": \"56565656\"\n" +
            "}";

    String validationString = "{\n" +
            "    \"email\": \"paul@pahkel.ee\",\n" +
            "    \"fullName\": \"Paul Pähkel\",\n" +
            "    \"phoneNumber\": \"56565656\"\n" +
            "}";


    @Test
    public void testAddCustomerAsAdmin() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/customers/")
                .header("Authorization", "Bearer " + DEFAULT_ADMIN_BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON).content(exampleCustomerJson)
                .header("charset", "utf-8")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        JSONAssert.assertEquals(validationString, result.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), false);
    }

    @Test
    public void testAddCustomerAsCustomer() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/customers/")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON).content(exampleCustomerJson)
                .header("charset", "utf-8")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }
}
