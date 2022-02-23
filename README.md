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

# Docs
- [Usage Guide](docs/USAGE_GUIDE.md)
- [Updating from pre v3 to Scaffold 3.x](docs/UPDATING_TO_V3.md)

# Important Links
- [Contributing Guide](CONTRIBUTING.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [License](LICENSE.txt)
    
# Required Tools for Dev
* Java 11
* Maven 3.x

# CI
CI jobs can be viewed on [github actions](https://github.com/kgress/scaffold/actions) page of this repo.

# Getting Started
* For manual setup, view the [Usage Guide](docs/USAGE_GUIDE.md) for a complete explanation on how to get started.
* For an automated setup, our Scaffold Archetype is the best approach. However, it is under an update process and is not considered a "stable" state. Once this has been updated, we will update this doc with the instructions! 
