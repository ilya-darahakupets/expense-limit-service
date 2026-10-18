package com.example.expenselimit.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.expenselimit.support.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = "app.demo-data.enabled=true")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class DemoDataInitializerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accountOverLimitHasProductAndServiceExceeded() throws Exception {
        mockMvc.perform(get("/api/accounts/{a}/transactions/exceeded", DemoDataInitializer.ACCOUNT_OVER_LIMIT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].expense_category").value("product"))
                .andExpect(jsonPath("$[0].currency_shortname").value("RUB"))
                .andExpect(jsonPath("$[0].limit_sum").value(2000.00))
                .andExpect(jsonPath("$[1].expense_category").value("service"))
                .andExpect(jsonPath("$[1].limit_sum").value(1000.00));
    }

    @Test
    void accountWithDefaultLimitHasNothingExceeded() throws Exception {
        mockMvc.perform(get("/api/accounts/{a}/transactions/exceeded", DemoDataInitializer.ACCOUNT_DEFAULT_LIMIT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/accounts/{a}/limits", DemoDataInitializer.ACCOUNT_DEFAULT_LIMIT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void raisedLimitDoesNotRewriteEarlierExceededTransaction() throws Exception {
        mockMvc.perform(get("/api/accounts/{a}/transactions/exceeded", DemoDataInitializer.ACCOUNT_LIMIT_CHANGED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sum").value(150.00))
                .andExpect(jsonPath("$[0].limit_sum").value(100.00));
        mockMvc.perform(get("/api/accounts/{a}/limits", DemoDataInitializer.ACCOUNT_LIMIT_CHANGED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void invalidAccountNumberReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/accounts/{a}/limits", "abc")).andExpect(status().isBadRequest());
    }
}
