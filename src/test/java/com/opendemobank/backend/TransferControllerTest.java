package com.opendemobank.backend;

import org.junit.jupiter.api.Disabled;
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
public class TransferControllerTest extends BaseTest {

    @Test
    public void testShowTransferById() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transfers/1")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response doesn't contain correct transfer id.");
        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"description\":\"December's Salary\""), "Response doesn't contain correct description.");
    }

    @Test
    public void testShowTransferByIdAdmin() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transfers/1")
                .header("Authorization", "Bearer " + DEFAULT_ADMIN_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response doesn't contain correct transfer id.");
        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"description\":\"December's Salary\""), "Response doesn't contain correct description.");
    }

    @Test
    public void testShowTransferByIdNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transfers/172")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getStatus() == 404, "Response should be unauthorized.");
    }

    @Test
    @Disabled
    public void testDontShowTransferByIdToUnauthorizedUser() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transfers/1")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_2_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getStatus() == 401, "Response should be unauthorized.");
    }

    @Test
    public void testShowAllTransfersToAdmin() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transfers")
                .header("Authorization", "Bearer " + DEFAULT_ADMIN_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response doesn't contain all transactions.");
    }

    @Test
    public void testShowAllSessionUserTransfers() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transfers/user/2")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response doesn't contain all transactions.");
    }


    @Test
    public void testShowAllTransfersUnauthorized() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transfers")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_2_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getStatus() == 401, "Response should be unauthorized.");
    }

}
