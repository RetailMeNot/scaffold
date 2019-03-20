# Scaffold
Scaffold is a Selenium WebDriver abstraction built in Java 11 with Spring Boot 2.x / Jersey / Jax Rs. Out of the box, it provides a myriad of additional features on top of the base WebDriver wrapper:

* Bootstrapper - Starts and stops a new thread safe WebDriver instances (parallelizable).
* Provides an abstraction of WebElements to depict literal object representations of what exists in the DOM (e.g. InputWebElement or LinkWebElement).
* Implicitly waits for elements to be located during page object instantiation and element manipulation
* Automatically handles StaleElementException issues when the state of the DOM has changed.
* Provides a Navigation class to give users a separation of concerns between test setup and test assertions.
* Provides a Spring Boot auto-configuration for configuring a browser when creating Spring Boot profiles in an implementing project (DesiredCapabilities, e.g. browser type, environment type, etc).
* Provides a Spring Boot auto-configuration for managing configuring connections to SauceLabs.
* Configures Junit Jupiter's parallel testing when running the testing through an automated framework like Sauce or Grid
* Provides all dependencies to implementing projects

# Current Version
1.0.0-beta

# Links
- [Contributing Guide](https://github.com/RetailMeNot/scaffold/blob/master/CONTRIBUTING.md)
- [Code of Conduct](https://github.com/RetailMeNot/scaffold/blob/master/CODE_OF_CONDUCT.md)
- [License](https://github.com/RetailMeNot/scaffold/blob/master/LICENSE.txt)
    
# Required Tools for Dev
* Java 11
* Maven 3.x

# Setting Up Your Project
Use this section for setting up a new project using Scaffold. Setup follows a fairly standard Spring Boot application design by using modules for the code base's environment and main testing.

Coming in a future update, we will provide an example implementation project and a maven archetype to easily start up new projects. 

## Create the Project
Create a new Java Maven project.

Follow the standard maven [naming conventions](https://maven.apache.org/guides/mini/guide-naming-conventions.html) when creating the new project.

## Add Maven Repo Dependencies
Add the following dependency to your parent POM's DependencyManagement section:

```
<dependency>
    <groupId>com.retailmenot.scaffold</groupId>
    <artifactId>scaffold</artifactId>
    <version>1.0.0-beta</version>
</dependency>

<dependency>
    <groupId>com.retailmenot.scaffold</groupId>
    <artifactId>framework</artifactId>
    <version>1.0.0-beta</version>
</dependency>

<dependency>
    <groupId>com.retailmenot.scaffold</groupId>
    <artifactId>environment</artifactId>
    <version>1.0.0-beta</version>
</dependency>
``` 

## Create Modules
Create two new maven modules in your project. 

* `environment`
* `core`

The environment module's POM should contain Scaffold's environment dependency. 
```
<dependency>
    <groupId>com.retailmenot.scaffold</groupId>
    <artifactId>environment</artifactId>
</dependency>
```

The core module's POM should contain Scaffold's framework dependency.
```
<dependency>
    <groupId>com.retailmenot.scaffold</groupId>
    <artifactId>framework</artifactId>
</dependency>
```

## Required Files
The following are files that should be created in your project.

### BaseTest
You project should have a BaseTest file that extends `ScaffoldBaseTest`. 

This BaseTest file should have any project specific configurations along with the Scaffold configuration. Make sure to also attach the annotation listed below, as well.
```
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { your_own_config.class, ScaffoldConfig.class }
)
public abstract class BaseTest extends ScaffoldBaseTest {}
```

### PageObjects
PageObjects are simple representations of pages as a Java Object. They should only be written in a way that makes them agnostic to navigation, the web driver itself, or by any other external means outside the scope of its own representation. They should also never contain any assertions.

Follow the same design as Java Beans, [found here:](https://www.javatpoint.com/java-bean).

An example page object:
```
public class LoginPage {

    private DivWebElement pageHeader = new DivWebElement(By.cssSelector("#someHeader"));
    private InputWebElement emailInput = new InputWebElement(By.cssSelector("#emailInput"));
    private InputWebElement passwordInput = new InputWebElement(By.cssSelector("#passwordInput"));
    private ButtonWebElement loginButton = new ButtonWebElement(By.cssSelector("#loginButton"));

    public DivWebElement getPageHeader() {
        return pageHeader;
    }

    public DivWebElement getEmailInput() {
        return profileGreeting;
    }

    public LinkWebElement getPasswordInput() {
        return editProfileLink;
    }

    public DivWebElement getLoginButton() {
        return emailList;
    }

    public void clickLoginButton() {
        getLoginButton().click();
    }
    
    public void login(String username, String password) {
        getEmailInput().clearAndSendKeys(username);
        getPasswordInput().clearAndSendKeys(password);
        getLogInButton().click();
    }
}
```

### Strongly Typed WebElements
Strongly typed elements are a main feature of Scaffold. They are elements that wrap around the main WebElement.

This allows us to contain only certain actions within specific types of elements.  

### Navigation
Your project should have a file that extends `WebDriverNavigation`. 
 
The navigation file handles the navigation of the PageObjects and sets up your tests. For example, using the example login page object above, let's say your implementing project has a login page and a profile page.
In order to navigate to the profile page, it first requires a login (since the profile is gated). Your page navigation file might contain the method `navigateToProfilePage`:
```
@Component
public class AutomationNavigation extends WebDriverNavigation {

    // Spring environment variable from a configuration file in environment module
    private final String baseEnvironmentUrl;

    public NavigationImpl(@Value("${base-environment-url}") String baseEnvironmentUrl) {
        this.baseEnvironmentUrl = baseEnvironmentUrl;
    }

    public ProfilePage navigateToProfilePage(String username, String password) {    
        // Start the web test from a base environment URL pulled in from a Spring env variable
        getWebDriverWrapper().get(baseEnvironmentUrl);
        
        // Create a new instance of the LoginPage
        var logInPage = new LoginPage();
        
        // Use the login method from the LoginPage
        logInPage.login(username, password);
        
        // Return the new ProfilePage
        return new ProfilePage();
    }
}
```  

### Desired Capabilities
Desired Capabilities are set up using Spring profiles in your own project and are some are required prior to launching any tests. The required configuration options are currently:

* Run Type
* Environment Type
* Browser Type

Scaffold is set up as an auto configuration so your implementing project will be able to use auto complete when creating your profiles. The properties for the auto configuration are set up in the `DesiredCapabilities` model.

* More detail on the configuration [options that can be found here](https://wiki.saucelabs.com/display/DOCS/Test+Configuration+Options)
```
# Sauce Config
desired-capabilities.sauce.url=the sauce url 
desired-capabilities.sauce.user-name=the sauce username for the account
desired-capabilities.sauce.password=the sauce password for the account
desired-capabilities.sauce.access-key=the sauce access key for the account
desired-capabilities.sauce.tunnel-identifier=the sauce connect tunnel ID

# Browser/OS Config
desired-capabilities.run-type=where the browser testing is running, e.g. local, sauce, or grid
desired-capabilities.environment-type=the environment the testing is running on, e.g. test or stage.
desired-capabilities.browser-type=the browser type to be launched
desired-capabilities.browserVersion=the version of the browser to be launched
desired-capabilities.runPlatform=the operating system the browser is launching on
desired-capabilities.remote-url=The default grid URL to use
desired-capabilities.upload-screenshots=a boolean to determine if screenshots will be uploaded
```

Sauce configuration should be set up in a means that you are not hardcoding user names, passwords, and secrets into your code base. 

See some examples below on configurations for browsers:

#### Local Chrome Example
This example is for a chrome browser instance from your local system against a TEST environment. It is recommended that you do not run a large suite of testing on your machine. However, this is 
a good option for debugging single tests. 

1. Create a new file located under `environment -> resources -> config` called `application-CHROMELOCAL.properties`.
    * The name of the file can vary. Just replace `CHROMELOCAL` with anything of your choice. `application-whatever_indicates_a_local_config.properties`.
2. In that file, add the following text:
```
desired-capabilities.run-type=local
desired-capabilities.environment-type=test
desired-capabilities.browser-type=chrome
```
3. Create a new Junit test configuration for the test(s) you'd like to run.
4. Run your testing

#### Sauce Chrome Example
This example is for a safari based driver through Sauce Labs against a Stage Environment. It is assumed that you already have sauce connect configured to allow Sauce Labs access to your STAGE instance.

1. Create a new file located under `environments -> resources -> config` called `application-SAFARISAUCE.properties`.
    * The name of the file can vary. Just replace `SAFARISAUCE` with anything of your choice. `application-whatever_indicates_a_safari_sauce_config.properties`.
2. In that file, add the following:
```
# Sauce Config
desired-capabilities.sauce.url=<your url>
desired-capabilities.sauce.user-name=<your username>
desired-capabilities.sauce.password=<your password>
desired-capabilities.sauce.access-key=<your access key>
desired-capabilities.sauce.tunnel-identifier=<your tunnel identifier>

# Base Desired Capabilities
desired-capabilities.run-type=sauce
desired-capabilities.environment-type=stage
desired-capabilities.browser-type=safari
```
3. Create a new Junit test configuration for the tests you'd like to run
4. Run your testing

#### Configuring Constant Values for DesiredCapabilities
It's entirely possible to create constant values for all Spring Profiles to inherit. This would be useful for values that will not change between all of the profiles that are created.

Values such as the sauce url, username, password, access key, and remote url, or even your own projects base url are all values that will typically not change.

To do this, do the following:

1. If it hasn't already been created, create a new file located under `environments -> resources -> config` called `application.properties`.
    * This file name MUST be named `application.properties` in order for Spring to map these values into application context for all profile.
2. In that file, add the following:
```
desired-capabilities.sauce.url=<your url>
desired-capabilities.sauce.user-name=<your username>
desired-capabilities.sauce.password=<your password>
desired-capabilities.sauce.access-key=<your access key>
desired-capabilities.remote-url=<your grid specific remote url if you are doing grid testing instead>
```  
3. Any subsequent profile that is created will auto inherit these values making it easier to define Desired Capabilities for these profiles.
