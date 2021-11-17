package io.github.kgress.scaffold.extensions;

import com.saucelabs.saucerest.SauceREST;
import io.github.kgress.scaffold.TestContext;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class SauceExtension implements BeforeAllCallback, TestWatcher {

    private SauceREST sauce;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        try {
            var environment = SpringExtension.getApplicationContext(context).getEnvironment();
            var sauceUsername = environment.getProperty("desired-capabilities.sauce.user-name");
            var sauceAccessKey = environment.getProperty("desired-capabilities.sauce.access-key");
            sauce = new SauceREST(sauceUsername, sauceAccessKey);
        } catch (Exception e) {
            throw new Exception("Error initializing the Sauce Labs API: Please check your configuration.");
        }
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        // TODO what to do with disabled test?
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        sauce.jobPassed(getSessionId());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        sauce.jobFailed(getSessionId());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        sauce.jobFailed(getSessionId());
    }

    private String getSessionId() {
        return TestContext.baseContext().getSetting(String.class, "SESSION_ID");
    }
}
