# Table of Contents 
* [Check Current Version](#check-current-version)
* [Setting Up Your Project](#setting-up-your-project)
  * [Create The Repo](#create-the-repo)
  * [Add Maven Dependencies](#add-maven-repo-dependencies)
  * [Create Modules](#create-modules)
  * [Create Scaffold Classes](#create-the-scaffold-classes)
    * [Page Objects](#page-objects)
    * [Components](#components)
    * [Navigation](#navigation)
    * [BaseTest](#basetest)
    * [Spring Profiles](#spring-profiles)
* [Running The Testing](#running-the-testing)

## Check Current Version
To view the most current version, [visit the Central Repository](https://search.maven.org/search?q=g:io.github.kgress.scaffold).

## Setting Up Your Project
Use this section for setting up a new project using Scaffold. Setup follows a fairly standard Spring Boot application design by using modules for the code base's environment and main testing.

### Create the Repo
Create a new Java Maven project.

Follow the standard maven [naming conventions](https://maven.apache.org/guides/mini/guide-naming-conventions.html) when creating the new project.

### Add Maven Repo Dependencies
Add the following dependency to your parent POM's DependencyManagement section:

```xml
<dependency>
    <groupId>io.github.kgress.scaffold</groupId>
    <artifactId>scaffold</artifactId>
    <version>current_version</version>
</dependency>

<dependency>
    <groupId>io.github.kgress.scaffold</groupId>
    <artifactId>framework</artifactId>
    <version>current_version</version>
</dependency>

<dependency>
    <groupId>io.github.kgress.scaffold</groupId>
    <artifactId>environment</artifactId>
    <version>current_version</version>
</dependency>
``` 

### Create Modules
Create two new maven modules in your project.

* `environment`
* `core`

The environment module's POM should contain Scaffold's environment dependency.
```xml
<dependency>
    <groupId>io.github.kgress.scaffold</groupId>
    <artifactId>environment</artifactId>
</dependency>
```

The core module's POM should contain Scaffold's framework dependency.
```xml
<dependency>
    <groupId>io.github.kgress.scaffold</groupId>
    <artifactId>framework</artifactId>
</dependency>
```

### Create The Scaffold Classes
The following are files that should be created in your project.

#### Spring Configuration
A spring configuration file is necessary for handling component scanning and the usage of spring profiles when running testing. Component scanning is necessary for dependency injection and spring profiles are used for setting
`DesiredCapabilities` for testing. More detail regarding spring profiles are included in a later section.

Create a new file under the environment module adhering to the naming convention standardization hierarchy set when creating your new project.

E.G: `environment > src > main > java > your > groupID > environment > YourProjectConfiguration.java`

The contents of this file should include:
```java
@Configuration
@ComponentScan(value = "your.group.id")
@PropertySource("classpath:/application.properties")
public class YourProjectConfig {
}
```  

#### Page Objects
Scaffold implements the paradigm of a page object. Page objects are simple representations of web pages as a Java Object. In order to create a representation of a web page, you'll need to create a new class that contains properties
of Scaffold's strongly typed elements. Page Objects should always extend off of the class `BasePage`, as it provides common functionality to 
Page Objects such as `verifyIsOnPage()`.

##### Strongly Typed Elements
Scaffold makes available particular types of WebElements for you to use, which narrow the scope of methods available for any given WebElement, and keep the user focused on the actions they should be performing on these elements.
For example, a user cannot `sendKeys()` to a Button or a Link. The `ButtonWebElement` and `LinkWebElement` objects take that into account and don't expose those methods.

Another advantage of these elements is that they manage all interaction with the WebDriver internally. Most frameworks require the test write/page object maintainer to use the WebDriver to perform all their actions,
which requires a lot of Selenium knowledge, and can also lead to race conditions, thread-safety issues, and the exposure of unnecessary complexity to the testers.

In the millions of test cases run by Scaffold over the years, there has never been a reported occurrence of a `StaleElementReferenceException`. This is possible because the framework manages the WebDriver and the WebElements
internally, in a thread-safe and careful manner. It circumvents the possibility of a `StaleElementReferenceException` by always re-finding the element whenever an interaction occurs on it. This behavior is only possible when using Scaffold strongly typed elements, and not the raw Selenium element.
It is strongly recommended to always use Scaffold strongly typed elements in order to benefit from all Scaffold feature. Usage of raw Selenium objects will result in unexpected behavior. 

A strongly typed element in scaffold can be one of the following:
* ButtonWebElement
* CheckBoxWebElement
* DateWebElement
* DivWebElement
* DropDownWebElement
* ImageWebElement
* InputWebElement
* LinkWebElement
* RadioWebElement
* StaticTextWebElement

##### Page Object Example
Page objects should only be written in a way that makes them agnostic to navigation, the web driver itself, or by any other external means outside the scope of its own representation. They should also never contain any
assertions. While the `WebDriver` is not directly injected into a page object, the underlying `WebDriver` is used for finding elements when a `getElement()` is performed and _not_ when an element is declared.

In other words, simply initializing a strongly typed element does not perform an interaction with the underlying WebDriver; but instead, only creates a reference point with a By locator. This creates a dynamic use case when
performing navigation in that when a new page object is initialized, the elements will not be searched for at the time of the class being constructed. It isn't until you attempt to get the strongly typed element with a getter
that the underlying WebDriver will perform a `getWebElement()`, therefore performing the `findElement()` interaction.

To create a page object, follow the same design as Java Beans, [found here](https://www.javatpoint.com/java-bean).

The page objects should live within the core module in a page module. E.G: `core > src > main > java > your > groupID > page`

**An example page object:**
```java
@Getter
public class LoginPage extends BasePage {

    private final DivWebElement pageHeader = new DivWebElement(By.cssSelector("#someHeader"));
    private final InputWebElement emailInput = new InputWebElement(By.cssSelector("#emailInput"));
    private final InputWebElement passwordInput = new InputWebElement(By.cssSelector("#passwordInput"));
    private final ButtonWebElement loginButton = new ButtonWebElement(By.cssSelector("#loginButton"));
    
    public LoginPage() {
      verifyIsOnPage(getEmailInput(), getPasswordInput());
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

Let's break down what we see in the example above.

1. `BasePage` extension
   1. All Page Objects should inherit functionality from `BasePage`. This provides access to functionality used to handle verification of the Page Object, along with access to the javascript executor and other useful methods.
2. Properties (or aka Java Fields)     
   1. These are the elements on the page that we wish to represent. They can be whatever you feel is necessary to have. They could be headers, inputs, buttons, images, or anything else on the list of Strong Typed Elements above
      and are all merely references to be used later when you're getting the elements. They are located with the `By` class.
3. Getters
   1. Standard getter functions are auto generated with the lombok `@Getter` annotation from the class level. Any time we want to interact with an element, we 
      always call the getter instead of the raw field.
4. Constructor
   1. The constructor should always behave like a default constructor, and not include any params. In addition, it should always handle the verification of the page by calling `BasePage`'s `verifyIsOnPage()` method. 
5. Helper Functions
   1. The Page Object is a good opportunity to include any page specific actions you'd like to abstract. This is yet another level of creating an additional layer that will allow us to maintain our testing a little easier as it scales.

##### Page Object Best Practices
As mentioned in the [Page Objects](#page-objects) section, it's best to instantiate new strongly typed elements at the class level with a "new" keyword. To expand on this, 
it's recommended to never invoke `.findElement()` or `.findElements()` on a class variable. This creates an undesirable point of failure in the event the element(s)
cannot be found during the instantiation of the page; therefore, causing all tests that depend on the Page Object to fail instead of the tests that depend
on the element to fail. 

Scaffold's Page Object performance is driven by the fact elements are not found during the instantiation of the page. Invoking `findElement()` or `findElements()`
bypasses this performance gain and the aforementioned point of failure design. 

**An example of incorrect usage:**
```java
@Getter
public class LoginPage extends BasePage {

    private final DivWebElement pageHeader = findElement(DivWebElement.class, By.cssSelector("#someHeader"));
    private final InputWebElement emailInput = findElement(InputWebElement.class, By.cssSelector("#emailInput"));
    private final InputWebElement passwordInput = findElement(InputWebElement.class, By.cssSelector("#passwordInput"));
    private final ButtonWebElement loginButton = findElement(ButtonWebElement.class, By.cssSelector("#loginButton"));
    private final DivWebElement termsOfServiceListContainer = findElements(DivWebElement.class, By.cssSelector("#tos"));
    private final List<DivWebElement> termsOfServiceLinks = getTermsOfServiceListContainer.findElements(LinkWebElement.class, By.cssSelector("a"));
    
    public LoginPage() {
      verifyIsOnPage(getEmailInput(), getPasswordInput());
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

#### Components
Components are similar to Page Objects in that they define specific properties of a website. What makes them different is that they are intended
to be properties of a website that are shared across multiple Page Objects. This allows for easy code sharing across the Page Objects without
copy/pasting code. 

Components can be written in context of a non-list or list. Non-list Components can be considered "static" in that only
one of its kind exists on a web page. For example, a header is a great example of this. The header will always be a singular
defined set of properties, and you'll never see 4 or 5 of them on the same web page. List Components can be considered "dynamic" in that any
amount of them might be present on the web page. For example, a search results page is a great example of this. A search results page will
show a number of results. Each result contains the same title, price, and add to cart button. These elements
can be written as a Component, and then built as a `List` from the Page Object.

##### Component Example - Non-list Context
The same rules apply to Page Objects in how Components are intended to be agnostic to the web driver and navigation methods. Component classes define
a subset of properties from a website that can appear across any amount of Page Objects. For example, perhaps the same header might exist across
multiple pages. That header might have a large collection of properties, such as a search bar, search button, links, etc. Taking our Page Object
example above, we could update the header property to be its own component, referencing the parent in the `By` locators.

**The Component**
```java
@Getter
public class HeaderComponent extends BaseComponent {
  
    private final ImageWebElement pageCompanyIcon = new ImageWebElement(By.cssSelector("#header #company_icon"));
    private final InputWebElement searchInput = InputWebElement(By.cssSelector("#header #search_input"));
    private final ButtonWebElement searchButton = ButtonWebElement(By.cssSelector("#header #search_button"));
    private final LinkWebElement loginLink = LinkWebElement(By.cssSelector("#header #login"));
    private final LinkWebElement registerLink = LinkWebElement(By.cssSelector("#header #register"));
    
    // Helper functions for clicking links and returning Page Objects. E.G, clickRegisterLink() may return a RegisterPage Page Object.
}
```

**The Updated Page Object**
```java
@Getter
public class LoginPage extends BasePage {

    private final HeaderComponent headerComponent = new HeaderComponent();
    private final InputWebElement emailInput = new InputWebElement(By.cssSelector("#emailInput"));
    private final InputWebElement passwordInput = new InputWebElement(By.cssSelector("#passwordInput"));
    private final ButtonWebElement loginButton = new ButtonWebElement(By.cssSelector("#loginButton"));
    
    public LoginPage() {
      verifyIsOnPage(getEmailInput(), getPasswordInput());
    }

    public void clickLoginButton() {
        getLoginButton().click();
    }
    
    public void login(String username, String password) {
        getEmailInput().clearAndSendKeys(username);
        getPasswordInput().clearAndSendKeys(password);
        clickLoginButton();
    }
}
```

Let's break down what we see in the example above.

1. `BaseComponent` Extension on `HeaderComponent`
   1. All Components should inherit functionality from the `BaseComponent` class. Similar to Page Objects, there are some extremely helpful methods there that allow the same access as mentioned from Page Objects, but also the construction of Component lists. That's covered later.
2. Properties (or aka Java Fields)
   1. Exactly the same fundamentals as the Page Object. These fields are the elements that exist on the page. With a "non-list" component, we always want to provide the fully qualified `By` locator that includes the parent.
3. Getters
   1. Standard getter functions are auto generated with the lombok `@Getter` annotation from the class level. Any time we want to interact with an element, we
      always call the getter instead of the raw field.
4. No Constructor
   1. Here, we don't need to customize our constructor with a verification method. The Page Object is responsible for this.
5. Helper Functions
   1. We can include any additional helper functions here to make our calling code easier to read.
6. Instantiating the Component on the Page Object
   1. The Page Object can then instantiate a new instance of the Component we just wrote. Code calling the loginPage will now have access to getHeaderComponent()'s getters and helper functions!

##### Component Example - List Context
Building a Component under a "list" context is fundamentally the same. In the search results page example mentioned from the Components section, we
can define properties exactly the same way. The only difference is that a list Component doesn't define properties with the parent in the `By` locator.
The search results page would then create the list of the search results with `BaseComponent`'s `buildComponentList()` method. 

**The Component**
```java
@Getter
public class SearchResultItem extends BaseComponent {

   private final DivWebElement itemName = new DivWebElement(".inventory_item_name");
   private final DivWebElement itemDescription = new DivWebElement(".inventory_item_desc");
   private final DivWebElement itemPrice = new DivWebElement(".inventory_item_price");
   private final ButtonWebElement addToCart = new ButtonWebElement(".btn_primary");

   public void clickAddToCartButton() {
      getAddToCart().click();
   }
}
```

**The Page Object**
```java
@Getter
public class SearchResultsPage extends BasePage {
   private final static String INVENTORY_ITEM_SELECTOR = ".inventory_item";
   private final HeaderComponent headerComponent = new HeaderComponent();
   private final DivWebElement inventoryListContainer = new DivWebElement(By.cssSelector(".inventory_list"));
   private final DropDownWebElement sortDropDown = new DropDownWebElement(".product_sort_container");

   public SearchResultsPage() {
      verifyIsOnPage(getInventoryList());
   }

   public List<SearchResultItem> getSearchResultsList() {
      var listOfElements = getInventoryListContainer().findElements(DivWebElement.class, INVENTORY_ITEM_SELECTOR);
      return buildComponentList(listOfElements, SearchResultItem.class);
   }
}
```

Let's break down what we see in the example above.

1. `BaseComponent` Extension on `SearchResultItem`
   1. All Components should inherit functionality from the `BaseComponent` class. Similar to Page Objects, there are some extremely helpful methods there that allow access the same access as mentioned from Page Objects, but also the construction of Component lists, which is what we are doing now!
2. Properties (or aka Java Fields)
   1. Exactly the same fundamentals as the Page Object. These fields are the elements that exist on the page. With a "list" component, we only want to provide the `By` locator that is considered the "child" of the parent. In this case, the inventory name's `By` locator would be `.inventory_item_name`. 
3. Getters
   1. Standard getter functions are auto generated with the lombok `@Getter` annotation from the class level. Any time we want to interact with an element, we
      always call the getter instead of the raw field.
4. No Constructor
   1. Here, we don't need to customize our constructor with a verification method. The Page Object is responsible for this.
5. Helper Functions
   1. We can include any additional helper functions here to make our calling code easier to read.
6. Creating the List
   1. The `SearchResultsPage` handles the creation of the list of Components by invoking the `buildComponentList()` method. It requires a list of the search result elements to iterate through, along with the class of the Component you are building, in order to correctly map each result to a new instance of the Component. Calling code can then interact with the list of results as their own Objects, e.g. `getSearchResultsList().get(0).getItemName()`. It also makes streaming on the list of results much easier, as you can map or filter based on the objects themselves.

##### Component List Best Practices
As mentioned in the [Page Objects](#page-objects) and [Page Objects Best Practices](#page-object-best-practices) section, it's best to instantiate new 
strongly typed elements at the class level with a "new" keyword. The same applies to the usage of Components when building lists. Do not invoke `findElements()`
when creating a list at the class level. Instead, create a new public method that builds the list of elements, and maps them to the component, per the example above.

**An example of incorrect usage:**
```java
@Getter
public class SearchResultsPage extends BasePage {
  private final HeaderComponent headerComponent = new HeaderComponent();
  private final DropDownWebElement sortDropDown = new DropDownWebElement(".product_sort_container");
  private final List<DivWebElement> inventoryList = new DivWebElement(By.cssSelector(".inventory_list").findElements(DivWebElement.class, ".inventory_item"));
  private final List<SearchResultItem> inventoryItems = buildComponentList(getInventoryList(), SearchResultItem.class);

  public SearchResultsPage() {
    verifyIsOnPage(getInventoryList());
  }
}
```

#### Navigation
Your project should have a navigation file that extends `WebDriverNavigation`. This file should live within the core module in a navigation package. E.G: `core > src > main > java > your > groupID > page > Navigation.class`

The navigation file handles the navigation of the page objects. For example, using the example login page object above, let's say your implementing project has a login page and a profile page.
In order to navigate to the profile page, it first requires a login (since the profile is gated). Your page navigation file might contain the method `navigateToProfilePage`:
```java
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

Let's break down what we see in this above example.

1. @Component     
   This is an important annotation to use here because we'll be injecting this class into the BaseTest file later on. Because we have a component scan configuration file (set up earlier), it will detect classes with the `@Component`
   annotation and allow us to perform the injection.
2. WebDriverNavigation extension         
   Extending off of `WebDriverNavigation` is important here so we can get the `WebDriverWrapper` instance from the current thread. That allows us to perform the `.get` call.
3. Constructor dependency injection            
   Seen in the example above, we reference `@Value("${base-environment-url}") String baseEnvironmentUrl`. This is a spring environment variable defined in a spring profile (more on that later). This allows us have a configure what
   we want our url to be based on what environment we're wanting to run the testing on. It's important to know that only Strings can be used for these variables.
4. Navigation method           
   Here is where the actual work happens. We get the WebDriverWrapper from the WebDriverNavigation class (because it's protected) and we perform the `get()` function to navigate to our base environment url set by a spring profile.
   Then, we create a new instance of the LoginPage page object, we perform the login that is defined on the LoginPage class (since it's gated), and then return a new instance of a ProfilePage page object.

The advantage here for separating out the navigation is that it gives us further abstraction in our test writing. Or, in other words, creating an additional layer that will allow us to maintain our testing a little easier as it scales.

#### BaseTest
Your project should have a BaseTest file that extends `ScaffoldBaseTest`. This file should live within the core module's test package. E.G: `core > src > test > java > your > groupID > BaseTest.java`

This BaseTest file should include any project specific spring wiring or configurations along with the Scaffold configuration. Make sure to also attach the annotation listed below, as well.
```java
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { your_own_config.class, ScaffoldConfig.class }
)
public abstract class BaseTest extends ScaffoldBaseTest {

    @Autowired
    protected Navigation navigation;
    
    @BeforeEach
    public void doSomethingAtStart() {
        // Some code here that might do something before each test is started
    }
    
    @AfterEach
    public void doSomethingAtEnd() {
        // Some code here that might do something after each test is finished
    }
}
``` 

Let's break down what we see in this example above.

1. Annotations        
   These annotations are important for configuring what was once known in Junit4 as the test runner, the parallelization mode, and the spring configuration for loading the application context / spring boot test app. Under the
   `@SpringBootTest` annotation, be sure to change `your_own_config.class` to your projects configuration class name.
2. Abstract class identifier          
   It's important to declare this class abstract so any Junit5 methods are not ran as tests.
3. ScaffoldBaseTest extension         
   Extending `ScaffoldBaseTest` gets you the driver initialization and tear down and will also get you the `WebDriverWrapper` instance from the current thread.
4. @Autowired          
   This is a means of dependency injection within Spring Boot known as composition. With this annotation, we can wire in an instance of a `@Component` with ease. Since it's protected, any class that extends off of BaseTest will
   now be able to access it.
5. @BeforeEach and @AfterEach         
   These are just some examples of additional code you can write in your `BaseTest` file to perform any pre req or tear down actions outside of the driver initializing and closing.

#### Spring Profiles
Spring profiles are sets of configurations that can be used when running testing locally or through a test automation framework like Sauce. These configurations determine the `DesiredCapabilities` of the browser and can also
configure Sauce credentials. During a test run, you specify the spring profile to use. This will be explained a little bit later.

The spring profiles should live under the resources package in the environment module that was set up earlier in this guide. E.G: `environment > src > main > resources > application-chrome_sauce.properties`. For more information on spring profiles, [check out this link here](https://www.springboottutorial.com/spring-boot-profiles).

##### Desired Capabilities
Desired Capabilities are browser options that you can set for your instance of WebDriver. Scaffold uses this concept for settings on the browser
and other options, like Sauce a sauce or mobile emulator configuration. To set the `DesiredCapabilites`, include pre configured properties from the `DesiredCapabilitiesConfigurationProperties` file. A full list of these properties [can be found at the following link](https://github.com/kgress/scaffold/blob/master/environment/src/main/java/com/kgress/scaffold/environment/config/DesiredCapabilitiesConfigurationProperties.java).
All of these properties are preceded by the prefix of `desired-capabilities`. So, for example, if you wish to define the run type of sauce, you'd enter `desired-capabilities.run-type=sauce`. Because Scaffold includes an
auto configuration for these properties, you gain the benefit of auto complete, as well. Simply type the first few letters of the word `desired` will show you a list of capabilities that can be set.

If you'd like to learn more about desired capabilities, details on the configuration [options that can be found here](https://wiki.saucelabs.com/display/DOCS/Test+Configuration+Options).

Scaffold handles some high level validation on Desired Capabilities. By default, the Run Type will always be required. For Sauce and
mobile emulation, be sure to inclue with SauceAuthentication or MobileEmulator, respectively. A full list of what is required or optional
can be found on the `DesiredCapabilitiesConfigurationProperties` file

##### Local Chrome Example
One option of a test run could include a local execution. This type of configuration is good for a one off test to debug or for POC'ing a test. It's not recommended that you run a large suite of testing with a local
browser. Typically, this sort of local configuration is included in an overrides spring profile and is not used for CI. If using an overrides profile _do not_ include this file in your commit.
Ensure that this file is added to your .gitignore. Overrides files are also used to include hard coded secrets and should never be exposed publicly.

*Important note: Since a local browser is being used, you must have the local driver installed. E.G: ChromeDriver. Otherwise, the testing will not work.*

For this example, let's assume we have created a new overrides profile. This overrides profile is called `application-overrides.properties`. In that file, we would include the following:
```properties
base-environment-url=some_base_url

desired-capabilities.run-type=local
desired-capabilities.browser-type=chrome
desired-capabilities.run-platform=mac
```
1. The base environment is in this file because it's a required environment variable from the `Navigation` class.
2. The run type is local because we're executing the test against our local browser
3. The browser type is chrome because chrome happens to be our main browser. This can be switched to other browsers but those drivers will be required
4. The run platform is mac because our OS is mac. This can be changed to whatever OS is being run.

##### Sauce Chrome Example
Another option of a test run could include a test execution against Sauce Labs. Because of the auto configuration defined by Scaffold, it's easy to add the sauce credentials to the overrides profile (to run the testing from your machine but sending the testing to sauce labs)
or to a spring profile that is used in the CI/CD pipeline.

*Important note: If adding the sauce credentials to an overrides file, ensure the overrides file is not checked in to your code base. These values should never be publicly exposed.*

For this example, let's create a new spring profile called `application-chrome_test.properties`. This configuration is going to be ran through sauce labs and will execute against a lower environment, TEST, with a windows browser.
This configuration file should be included would have the following:
```properties
base-environment-url=http://www.websitetest.com

# Sauce Config
desired-capabilities.sauce.url=<your sauce url>
desired-capabilities.sauce.user-name=<your username>
desired-capabilities.sauce.password=<your password>
desired-capabilities.sauce.access-key=<your access key>
desired-capabilities.sauce.tunnel-identifier=<your tunnel identifier>

# Base Desired Capabilities
desired-capabilities.run-type=sauce
desired-capabilities.browser-type=chrome
desired-capabilities.run-platform=windows
```

1. The base environment is in this file because it's a required environment variable from the `Navigation` class.
2. The sauce url is the endpoint you'd like to hit for testing. For now, Scaffold does not have a hard requirement to force you to hit the Selenium endpoint.
3. The username is your sauce labs account username
4. The password is your sauce labs account password
5. The access key is your sauce labs account's access key
6. The tunnel identifier is the name of your tunnel that is started in your CI (or locally) to run against.
7. The run type is sauce because the testing is running against sauce labs
8. The browser type is chrome because we'd like to run the testing in chrome. This can be changed to any other browser supported by Sauce Labs.
9. The run platform is windows because we'd like to run the testing on a Windows OS.

##### Sauce Mobile Emulator Example
```properties
base-environment-url=http://www.websitetest.com

desired-capabilities.run-type=sauce_mobile_emulator
desired-capabilities.mobile.platform-name=ios
desired-capabilities.mobile.sauce-device-name=iphone_12_pro
desired-capabilities.mobile.browser-name=safari
```

##### Configuring Constant Values for DesiredCapabilities
Because of the hierarchy of the spring profile system, it is possible to create constant environment variable values that all spring profiles can automatically include. This is useful for sauce credentials since you can include
the configuration in only one profile.

These constants can be included in a file named `application.properties`. It's worth mentioning that any subsequent spring profile that sets the same environment variable will override the existing value in `application.properties`.
This will allow you to set default values for any additional environment variable you create but override them in child profiles if you so wish.

For this example, let's say we'd like to include the sauce credentials in `application.properties`. Since we might have multiple configurations we'd like to have, with all of them requiring sauce credentials, it makes sense to only
include them in one file.That file will include the following:
```properties
desired-capabilities.sauce.url=<your sauce url>
desired-capabilities.sauce.user-name=<your username>
desired-capabilities.sauce.password=<your password>
desired-capabilities.sauce.access-key=<your access key>
desired-capabilities.sauce.tunnel-identifier=<your tunnel identifier>
```  

Taking the example from the Sauce Chrome Example section above, the new `application-chrome_test.properties` file would look like this:
```properties
base-environment-url=http://www.websitetest.com

# Base Desired Capabilities
desired-capabilities.run-type=sauce
desired-capabilities.browser-type=chrome
desired-capabilities.run-platform=windows
```

Since the sauce credentials are already included in `application.properties`, the `application-chrome_test.properties` file does not need the sauce credentials. Therefore, we cut back on a little bit of code!

## Running the Testing

### Locally
There are two potential methods of running the testing locally. The first is maven goal execution and the second is running the testing through the IDE.

### Maven
During your maven goal execution step on your CI, specify the following system property: `-Dspring.profiles.active=your_spring_profile.properties`.

This will pull the environment variables from the profile specified in the system property.

### IDE
If you don't already have one, create a file named `application.OVERRIDES` in `environment > src > main > resources > application-OVERRIDES.properties`. In this file, include the desired capabilities and any other secret values required to run your testing.

*NOTE: Make sure to add `application.OVERRIDES` to your `.gitignore`*

Next, create a new JUNIT run configuration for the testing you'd like to run locally. In the new run configuration, add the new environment variable `SPRING.PROFILES.ACTIVE=OVERRIDES`. This setting will pull in the environment variables from the OVERRIDES profile when that test is run through the IDE.

*As a reminder, it's recommended to not use a spring configuration that contains a local browser desired capability for running a large amount of testing.*

Now your testing is ready to be executed.

### CI
During your maven goal execution step on your CI, specify the following system property: `-Dspring.profiles.active=your_spring_profile.properties`.

This will pull the environment variables from the profile specified in the system property.