package com.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "m2")
    public void testReserveAvailableBook() throws Exception {
        mockMvc.perform(post("/reserve")
                        .param("bookId", "b2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));
    }

    @Test
    @WithMockUser(username = "m1")
    public void testReserveUnavailableBook() throws Exception {
        mockMvc.perform(post("/reserve")
                        .param("bookId", "b1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ko"));
    }

    @Test
    @WithMockUser(username = "m2")
    public void testMakeBookUnavailableAndReserveIt() throws Exception {
        mockMvc.perform(post("/reserve")
                        .param("bookId", "b2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));

        //Not available
        mockMvc.perform(post("/reserve")
                        .param("bookId", "b2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ko"));
    }

    @Test
    @WithMockUser(username = "m1")
    public void testReturnBook() throws Exception {
        mockMvc.perform(post("/return")
                        .param("bookId", "b1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));
    }

    @Test
    @WithMockUser(username = "m2")
    public void testReturnUnreservedBook() throws Exception {
        mockMvc.perform(post("/return")
                        .param("bookId", "b2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ko"));
    }

    @Test
    @WithMockUser(username = "m2")
    public void testReturnWithDifferentAccount() throws Exception {
        mockMvc.perform(post("/return")
                        .param("bookId", "b1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Ko"));
    }

    @Test
    @WithMockUser(username = "m1")
    public void testReturnBookAndThenReserveIt() throws Exception {
        mockMvc.perform(post("/return")
                        .param("bookId", "b1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));

        mockMvc.perform(post("/reserve")
                        .param("bookId", "b1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));
    }
}