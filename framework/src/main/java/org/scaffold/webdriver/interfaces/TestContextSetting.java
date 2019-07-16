package org.scaffold.webdriver.interfaces;

/**
 * Various config settings available to use with TestContext settings.
 */
public interface TestContextSetting {
    String IMPLICIT_SCROLLING_ENABLED = "implicit_scolling_enabled";
    String PROXY_MANAGER = "proxy_manager";
    String PROXY_ASSIGNER = "proxy_assigner";
    String QUEUE_HOST = "queue_host";
    String QUEUE_NAME = "queue_name";
    String TEST_RUN_ID = "test_run_id";
    String TEST_APPLICATION_CONTEXT = "test_application_context";
    String WAIT_CONDITION = "wait_condition";
    String WAIT_FOR_DISPLAY_ENABLED = "wait_for_display_enabled";
}
