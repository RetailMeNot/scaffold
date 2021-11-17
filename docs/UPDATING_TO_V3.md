# Updating to Scaffold 3.x

Scaffold 3.x introduces many breaking changes that require some code updates on implementing projects - Package changes,
web driver access updates, and removal of some deprecated pieces. However, the performance of Scaffold has increased 
substantially with the complete removal of implicit waits in favor of explicit waits. It also offers more functionality 
out of automation waits and will *ALWAYS* re find your element under the hood when you interact with it. It's absolutely 
worth the work to upgrade! 

Below is a list of possible action items for updating and reasoning behind the update.

<details>
<summary>WebDriverContext access update</summary>
<br>

### Summary:
The `WebDriverContext` access has been changed to package private. This will break for users that are attempting to
access the `WebDriverWrapper` with the following command:

```
TestContext.baseContext().getWebDriverContext().getWebDriverManager().getWebDriverWrapper();
```

### Explanation:
Access updated in order to protect users from invoking the raw `WebDriver` on `BasePage`.

### Action(s):
Users can remove the calls from their codebase. Be sure to view `BasePage` for allowed access on Page Objects.
</details>


<details>
<summary>BasePage package update </summary>
<br>

### Summary:
The `BasePage` class has moved from `io.github.kgress.scaffold.WebDriver.BasePage` to `io.github.kgress.scaffold.BasePage`.

### Explanation:
`WebDriverContext` access update required this class to move.

### Action(s):
Use `ReplaceInFiles` in your IDE to change `import io.github.kgress.scaffold.WebDriver.BasePage` to `import io.github.kgress.scaffold.BasePage`
</details>


<details>
<summary>Deprecated method isOnPage() removed from BasePage</summary>
<br>

### Summary
Deprecated method `isOnPage()` has been removed from `BasePage`.

### Explanation
The intention of `isOnPage()` was meant to essentially do what `verifyIsOnPage(...)` does now. However, it quickly became an
assertion that would be used in tests. While it's useful to sometimes have a page verification on tests, the Page Object
itself should not be responsible for it. For users that are invoking `isOnPage()` on tests, I recommend considering
an alternative approach to the test case. What is it you are fundamentally trying to test? If you still require
an assertion on page navigation for a test, you may still assert a page's specific element `isDisplayed()`.

### Action(s):
* Users can remove the `isOnPage()` method from their Page Objects
* Users should use `verifyIsOnPage()` for all Page Objects. Full details on how to use the method are contained in `BasePage`
* Users will have to update any testing that invokes `isOnPage()` to an `page.element.isDisplayed()`.
</details>


<details>
<summary>WebDriverNavigation package update</summary>
<br>

### Summary
The `WebDriverNavigation` class has moved from `io.github.kgress.scaffold.webdriver.WebDriverNavigation` to `io.github.kgress.scaffold.WebDriverNavigation`.

### Explanation
`WebDriverContext` access update required this class to move.

### Action(s):
Use `ReplaceInFiles` in your IDE to change `import io.github.kgress.scaffold.WebDriver.WebDriverNavigation` to `import io.github.kgress.scaffold.WebDriverNavigation`.
</details>


<details>
<summary>ScaffoldBaseTest package update</summary>
<br>

### Summary
The `ScaffoldBaseTest` class has moved from `io.github.kgress.scaffold.webdriver.ScaffoldBaseTest` to `io.github.kgress.scaffold.ScaffoldBaseTest`.

### Explanation
`WebDriverContext` access update required this class to move.

### Action(s):
Use `ReplaceInFiles` in your IDE to change `import io.github.kgress.scaffold.WebDriver.ScaffoldBaseTest` to `import io.github.kgress.scaffold.ScaffoldBaseTest`.
</details>


<details>
<summary>TestContext package update</summary>
<br>

### Summary
The `TestContext` class has been moved from `io.github.kgress.scaffold.webdriver.TestContext` to `io.github.kgress.scaffold.TestContext`.

### Explanation
`WebDriverContext` access update required this class to move.

### Action(s):
The `TestContext` will no longer provide access the `WebDriverContext`. Therefore, any custom coding attempting to access
this can be removed. Be sure to check appropriate parent classes for access to `WebDriverWrapper` such as `WebDriverNavigation`,
`BasePage`, or `AutomationWait`.
</details>


<details>
<summary>Renaming AbstractWebElement to BaseWebElement and package update</summary>
<br>

### Summary
`AbstractWebElement` has been renamed to `BaseWebElement` and has moved from `io.github.kgress.scaffold.WebElements.AbstractWebElement` to `io.github.kgress.scaffold.BaseWebElement`.

### Explanation
`WebDriverContext` access update required this class to move.

### Action(s):
Any custom code that uses an `AbstractWebElement` will need to be updated to use `BaseWebElement` instead.
</details>


<details>
<summary>BaseWebElement interface removal</summary>
<br>

### Summary
The `BaseWebElement` interface has been removed.

### Explanation
Over bloated design and clutter. Scaffold did not use the interface in any meaningful way. Removed in favor for
renaming the class `AbstractWebElement` to `BaseWebElement`.

### Action(s):
Any custom code that uses the `BaseWebElement` interface will have to be removed. Or, the logic must be updated to use elements
that extend from the newly renamed `BaseWebElement` class.
</details>


<details>
<summary>enableExplicitWaits() removal</summary>
<br>

### Summary
The `enableExplicitWaits()` option has been removed.

### Explanation
Scaffold has now moved to a fully functioning explicit wait system. An option to enable or disable the explicit wait
is no loner required.

### Action(s):
* Users can remove the method call in their implementing projects.
* Users can define a custom timeout in their spring profiles with `desired-capabilities.wait-timeout-in-seconds=`. Check `DesiredCapabilitiesConfigurationProperties` for further details on the option.
</details>


<details>
<summary>Explicit wait timeout defined in the Spring Profile</summary>
<br>

### Summary:
The explicit wait timeout is now defined in the Spring profile with: `desired-capabilities.wait-timeout-in-seconds=`

### Explanation:
Scaffold has now moved to a fully functioning explicit wait system. By default, the timeout is set to five seconds.
Users can set a custom timeout in their spring profile with `desired-capabilities.wait-timeout-in-seconds=`

### Action(s):
Users can update their spring profile with `desired-capabilities.wait-timeout-in-seconds=`
</details>


<details>
<summary>Redundant findElement() and findElements() methods removed from WebDriverWrapper</summary>
<br>

### Summary
Redundant `findElement()` and `findElements()` that wrap in Scaffold elements from `WebDriverWrapper` have been removed.

### Explanation
I believe the spirit of the `WebDriverWrapper` is to deliver the raw driver implementation to the user. So, when a user
tries to find an element in this way, they should get the `WebElement`.

### Action(s):
Users performing a `WebDriverWrapper#findElement()` or `WebDriverWrapper#findElements()` for a Scaffold element outside of
Page Objects can remove these method calls from their codebase and replace with instantiation of a new Scaffold element.
E.G:
```
    var InputWebElement = new InputWebElement("inputSelector");
```
</details>