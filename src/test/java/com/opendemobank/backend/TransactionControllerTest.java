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
public class TransactionControllerTest extends BaseTest {

    @Test
    public void testShowTransactionById() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transactions/1")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response doesn't contain correct transaction id.");
        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"description\":\"December's Salary\""), "Response doesn't contain correct description.");
    }

    @Test
    public void testShowTransactionByIdAdmin() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transactions/1")
                .header("Authorization", "Bearer " + DEFAULT_ADMIN_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response doesn't contain correct transaction id.");
        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"description\":\"December's Salary\""), "Response doesn't contain correct description.");
    }

    @Test
    public void testShowTransactionByIdNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transactions/172")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getStatus() == 404, "Response should be unauthorized.");
    }

    @Test
    public void testDontShowTransactionByIdToUnauthorizedUser() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transactions/1")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_2_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getStatus() == 401, "Response should be unauthorized.");
    }

    @Test
    public void testShowAllTransactionsToAdmin() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transactions")
                .header("Authorization", "Bearer " + DEFAULT_ADMIN_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response doesn't contain all transactions.");
    }

    @Test
    public void testShowAllTransactionsUnauthorized() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transactions")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_2_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getStatus() == 401, "Response should be unauthorized.");
    }

    @Test
    public void testShowAllSessionUserTransactions() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transactions/user/2")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response doesn't contain all transactions.");
    }

    @Test
    public void testShowAllSessionUserTransactionsUnauthorized() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/transactions/user/2")
                .header("Authorization", "Bearer " + DEFAULT_CUSTOMER_2_BEARER_TOKEN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        Assert.isTrue(!result.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("\"id\":1"), "Response does contain a transaction which it should not.");
    }

    // TODO: testCreateTransaction

}
