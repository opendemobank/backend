package com.opendemobank.backend;

import com.opendemobank.backend.customer.Customer;
import com.opendemobank.backend.customer.CustomerController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    String exampleCustomerJson = "{\n" +
            "    \"email\": \"paul@pahkel.ee\",\n" +
            "    \"password\": \"hunter2\",\n" +
            "    \"fullName\": \"Paul Pähkel\",\n" +
            "    \"phoneNumber\": \"56565656\"\n" +
            "}";

    @Test
    public void testAddCustomer() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/customers/")
                .accept(MediaType.APPLICATION_JSON).content(exampleCustomerJson)
                .header("charset", "utf-8")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(result.getResponse());

        JSONAssert.assertEquals(exampleCustomerJson, result.getResponse()
                .getContentAsString(StandardCharsets.UTF_8), false);
    }


}
