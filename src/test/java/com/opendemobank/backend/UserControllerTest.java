package com.opendemobank.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends BaseTest {

    @Test
    public void testLoggedInAsAdmin() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/users/current")
                .header("Authorization", "Bearer " + DEFAULT_ADMIN_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"role\":\"ADMIN\""), "Role is not admin in admin account.");
    }

    @Test
    public void testLoggedInAsCustomer() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/users/current")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"role\":\"USER\""), "Role is not USER in customer account.");
    }

}
