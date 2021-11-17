package io.github.kgress.scaffold;

/**
 * A simple Singleton approach for creating a new instance of a {@link WebDriverManager} and Test.
 */
public class TestContext extends BaseTestContext {

    private TestContext() {
    }        // Uninstantiable

    // TestContext is a lazy singleton, therefore we use the Initialization on Demand Holder
    private static class Holder {
        private static final TestContext INSTANCE = new TestContext();
    }

    public static TestContext baseContext() {
        return Holder.INSTANCE;
    }
}
