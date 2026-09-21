package epam.cucumber.integration.steps;

import io.cucumber.spring.ScenarioScope;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@Component
@ScenarioScope
public class IntegrationTestContext {

    private MvcResult lastResult;
    private RequestPostProcessor securityProcessor;

    public MvcResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(MvcResult lastResult) {
        this.lastResult = lastResult;
    }

    public int getLastStatus() throws Exception {
        return lastResult.getResponse().getStatus();
    }

    public String getLastResponseBody() throws Exception {
        return lastResult.getResponse().getContentAsString();
    }

    public void setAuthentication(String username, String role) {
        this.securityProcessor = SecurityMockMvcRequestPostProcessors.user(username).roles(role);
    }

    public void clearAuthentication() {
        this.securityProcessor = null;
    }

    public boolean isAuthenticated() {
        return securityProcessor != null;
    }

    public RequestPostProcessor getSecurityProcessor() {
        return securityProcessor;
    }
}
