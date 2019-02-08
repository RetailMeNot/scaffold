# Contributing
The development of Scaffold is intended to be a community driven effort; because of this, all contributions from the community are important! Regardless of a bug or a major feature addition/change, please file an issue for it. Ticket tracking allows everyone to ensure we're not duplicating efforts and obtaining the highest quality of work prior to merging.

## Code of Conduct
Be sure to view our [Code of Conduct](https://github.com/RetailMeNot/scaffold/CODE_OF_CONDUCT.md) before contributing.

## Check Configuration
If you're having troubles with DesiredCapabilities or see Spring application context issues, it's more than likely a misconfiguration. Check out the project [README](https://github.com/RetailMeNot/scaffold/README.md) with steps on creating the configurations. If you're confident that the configuration is not a problem and you've narrowed it down to a bug in the framework, please follow the bug filing procedure detailed in this document.   

## Filing Bugs
* First ensure the bug has not been filed yet under our Issues.
* If the issue does not exist, open a new issue with a clear and concise title. Please include a detailed description of the problem along with any accompanying code and reproduction steps.

When writing the description of the issue for a new bug, please follow the template/example below.

### Bug Template 
<title of ticket> WebDriver fails to instantiate due to NPE in TestContext </title of ticket>

h1. Bug:
This WebDriverManager isn't working because of a NPE in the TestContext! The NPE is caused by WebDriverContext not wiring in DesiredCapabilities. See code blurb below.

Code blurb:
```
A fancy code example where the bug is happening
```

h1. Expected:
We shouldn't be seeing any NPE's from the instantiation of the WebDriver and the DesiredCapabilities should be wiring in correctly.  

h1. Repro:
* Do a thing
* Then do another thing
* Then do this last thing
* Observe the bug

## New Feature / Feature Improvement Proposals
Do you have a brand new feature idea? Do you think an existing feature could be updated? File an issue for it! Having a formal issue filed will allow for further discussion from other contributors. There is almost always the potential for risks or other considerations that our peers may see. Use this to your advantage! 

* First ensure your feature idea or improvement has not been filed yet under our Issues.
* If the issue does not exist, open a new issue with a clear and concise title. Please include a detailed description of why you think the new feature should be added and how this would be implemented along with relevant Acceptance Criteria. If it's an update to an existing feature, be sure to include the shortcomings of the existing feature and why the update is necessary.

When writing the description for the proposal, please follow the template/example below.

### New Feature Template
<title of ticket> Add Appium Implementation for mWeb Automation </title of ticket>

h1. Summary
Scaffold currently does not support out of the box mWeb testing. There is increasing demand for Appium support in the community; because of this, we should add it!

h1. Details
In order to implement Appium support for mWeb automation, we will do...

<all of the implementation details. Where the files will live, new directories, etc etc>

h1. A/C
* Scaffold should be able to ...
* Scaffold should be able to ...
* User should be able to ...
* Implementing projects should be able to ...

### Feature Improvement Template
<title of ticket> Add additional Browsers to BrowserType </title of ticket>

h1. Summary
A new browser was released a couple months ago and is the most amazing browser in the universe. Everyone is using it! Selenium has added it to their supported browsers; therefore, we should add it to our compatible BrowserTypes. 

h1. Details
To add this new browser type, we should add it to the Browser Type enum.

```
NewAwesomeBrowser ("NewAwesomeBrowser")
```

h1. A/C
* BrowserType should be ...
* Scaffold should be able to ...
* User should be able to ...

## Submitting Work
Interested in fixing a bug or adding/updating a feature? Thank you! Your contributions are incredibly valuable to our community. Please follow the below development process for submitting your PR to our repo.

1. Fork the repository and create your branch from master.
2. Do the work! 
3. *Add Tests!* It's imperative we maintain a high confidence in the code we're outputting. If there are no tests included in the PR, there won't be a merge. The only exception to this is if the work done was not code related.
4. Run a `mvn clean install` to ensure everything compiles and the tests pass.
5. Squash your commits to a single commit and make the commit message the title of the ticket.
6. Create a Pull Request with your forked repo against the core repo's `master` branch.
7. Review process 
8. Code merged into master
9. +100 points to awesomeness for contributing!

## License
By contributing to Scaffold, you agree that your contributions will be licensed. For more information regarding our open source licensing, please view the [license](https://github.com/RetailMeNot/scaffold/LICENSE.txt) document.
