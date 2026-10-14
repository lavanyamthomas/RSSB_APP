package com.rssb;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    private String token() throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"sewadar\",\"password\":\"Sewadar@123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return om.readTree(body).get("token").asText();
    }

    @Test void healthIsPublic() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk());
    }

    @Test void protectedRequiresToken() throws Exception {
        mvc.perform(get("/api/persons/search")).andExpect(status().isUnauthorized());
    }

    @Test void badLogin() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"sewadar\",\"password\":\"x\"}")).andExpect(status().isUnauthorized());
    }

    @Test void registerThenSearch() throws Exception {
        String t = "Bearer " + token();
        mvc.perform(post("/api/registrations").header("Authorization", t).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Ram Singh\",\"location\":\"Beas\"}")).andExpect(status().isCreated());
        mvc.perform(get("/api/persons/search?q=ram&location=beas&status=registered&sortBy=name&sortDir=asc")
                .header("Authorization", t)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Ram Singh"));
        mvc.perform(get("/api/records/completed?dateFrom=2026-05-01&dateTo=2026-05-31")
                .header("Authorization", t)).andExpect(status().isOk());
        mvc.perform(post("/api/walkie/transmit").header("Authorization", t).contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\"}")).andExpect(status().isCreated());
    }
}
