package io.github.kgress.scaffold;

import io.github.kgress.scaffold.exception.ComponentException;
import io.github.kgress.scaffold.exception.WebDriverWrapperException;
import io.github.kgress.scaffold.util.AutomationUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;

/**
 * Components are similar to Page Objects in that they define specific properties of a website.
 * What makes them different is that they are intended to be properties of a website that are
 * shared across multiple Page Objects. This allows for easy code sharing across the Page Objects
 * without copy/pasting code.
 *
 * Components can be written in context of a non-list or list. Non-list Components can be
 * considered "static" in that only one of its kind exists on a web page. For example, a header is
 * a great example of this. The header will always be a singular defined set of properties,
 * and you'll never see 4 or 5 of them on the same web page. List Components can be considered
 * "dynamic" in that any amount of them might be present on the web page. For example, a search
 * results page is a great example of this. A search results page will show a number of results.
 * Each result contains the same title, price, and add to cart button. These elements
 * can be written as a Component, and then built as a `List` from the Page Object.
 *
 * An Example non list context component:
 *
 * The Component:
 * <pre>{@code
 * &#64;Getter
 * public class HeaderComponent extends BaseComponent {
 *    private final ImageWebElement pageCompanyIcon = new ImageWebElement(By.cssSelector("#header #company_icon"));
 *    private final InputWebElement searchInput = InputWebElement(By.cssSelector("#header #search_input"));
 *    private final ButtonWebElement searchButton = ButtonWebElement(By.cssSelector("#header #search_button"));
 *    private final LinkWebElement loginLink = LinkWebElement(By.cssSelector("#header #login"));
 *    private final LinkWebElement registerLink = LinkWebElement(By.cssSelector("#header #register"));
 *
 *    // Helper functions for clicking links and returning Page Objects. E.G, clickRegisterLink() may return a RegisterPage Page Object.
 * }
 * }
 * </pre>
 *
 * The Page Object:
 * <pre>{@code
 * &#64;Getter
 * public class LoginPage extends BasePage {
 *    private final HeaderComponent headerComponent = new HeaderComponent();
 *    private final InputWebElement emailInput = new InputWebElement(By.cssSelector("#emailInput"));
 *    private final InputWebElement passwordInput = new InputWebElement(By.cssSelector("#passwordInput"));
 *    private final ButtonWebElement loginButton = new ButtonWebElement(By.cssSelector("#loginButton"));
 *
 *    public LoginPage() {
 *      verifyIsOnPage(getEmailInput(), getPasswordInput());
 *    }
 *
 *    public void clickLoginButton() {
 *        getLoginButton().click();
 *    }
 *
 *    public void login(String username, String password) {
 *        getEmailInput().clearAndSendKeys(username);
 *        getPasswordInput().clearAndSendKeys(password);
 *        clickLoginButton();
 *    }
 * }
 * }
 * </pre>
 */
@Slf4j
public class BaseComponent {

  /**
   * Builds a list of a {@link BaseComponent}'s using an already found list of elements from a
   * web page by converting the {@link BaseComponent}'s fields to accessible and then mapping a
   * new instance of it with a combined locator. The combined locator becomes the prefix, and the
   * suffix of the locator becomes an :nth-child of the index + 1.
   *
   * This mapping only works if the list of elements from the parent container, provided as
   * the parameter "listOfElements", match the exact element items from the DOM. I.E, if there
   * is a difference between the size of "listOfElements" and the size of the elements contained
   * under the DOM's parent, the nth-child mapping will not function as expected.
   *
   * Take the DOM from the SauceLabs demo site, for example, on the cart page:
   * <pre>{@code
   * <div class="cart_list">
   *   <div class="cart_quantity_label">QTY</div>
   *   <div class="cart_desc_label">DESCRIPTION</div>
   *   <div class="cart_item">
   *     <div class="cart_quantity">1</div>
   *     <div class="cart_item_label">
   *       <a href="#" id="item_4_title_link">
   *         <div class="inventory_item_name">Sauce Labs Backpack</div>
   *         </a><div class="inventory_item_desc">some great description.</div>
   *         <div class="item_pricebar">
   *           <div class="inventory_item_price">$29.99</div>
   *           <button class="btn btn_secondary btn_small cart_button" data-test="remove-sauce-labs-backpack" id="remove-sauce-labs-backpack" name="remove-sauce-labs-backpack">Remove</button>
   *         </div>
   *     </div>
   *   </div>
   *   <div class="cart_item">....</div>
   * }
   * </pre>
   *
   * The parent is ".cart list" and the child elements we wish to map as a component list are
   * ".cart_item". In this case, findElements() for ".cart_list .cart_item" will give us a size
   * 2 list, but the DOM has a size 4 list, counting the ".cart_quantity_label,"
   * ".cart_desc_label," and the two elements we care about. The ideal scenario for building
   * a list of components is when the sizes of the lists match between the found elements and the
   * DOM, i.e. if all the child elements were ONLY ".cart_item." But, there is a way to
   * circumvent THIS specific scenario by specifying an index correction to this method. With
   * this example above, we can pass an index correction of 2, since there are 2 elements above
   * the first element we care about, and the css selector nth-child locators will map correctly.
   *
   * This isn't the most ideal usage, and is meant only for situations where there are nth elements
   * before the first element we care about. This method is not (yet) dynamic enough
   * to perform mapping on situations that are more complicated, such as random element locations
   * all throughout the DOM along with the list of elements we care about.
   *
   * An alternative to building component lists is to find all elements, stream through them to
   * select a specific index, and then perform a findElement() for the specific child locator
   * you'd like to interact with. This alternative is useful when you're dealing with singular
   * elements on a page that have a difficult design structure. For example, let's say we're
   * interacting with a sidebar that contains filter radio buttons.
   *
   * With this filter example, let's say we've created an Enum that contains String values for
   * the possible filter title's we'd like to select. On the page object for the search results
   * page, we could write the following function:
   *
   * <pre>{@code
   * public void selectCategoryFilterByEnum(CategoryFilterEnum filterChoice) {
   *    var categoryList = new DivWebElement(".sidebar_container").findElements(DivWebElement.class, ".filters_container");
   *    var categoryLink = categoryList
   *          .stream()
   *          .filter(result -> {
   *              var resultTitle = result.getText();
   *              return resultTitle.contains(filterChoice.getCategory());
   *          })
   *          .reduce((a,b) -> {
   *              throw new ExceptionOfYourChoice(String.format(
   *                  "Multiple Elements found [%s] and [%s]", a, b));
   *          });
   *    if (categoryLink.isPresent()) {
   *        categoryLink.get().click();
   *    } else {
   *        throw new ExceptionOfYourChoice(String.format(
   *            "Could not find a selection with choice [%s]", filterChoice.getCategoryAsString()));
   *    }
   * }
   * }
   * </pre>
   *
   * This above example illustrates that sometimes building a component list of all the things
   * isn't always the best option and is just another tool in the toolbox. With the above example,
   * we've limited the interaction to a singular method on a page object and cut down on our
   * overall code.
   *
   * If the design of your website adheres to the expected conditions, here's an example of
   * how component list building works:
   *
   * Example Component:
   * <pre>{@code
   * &#64;Getter
   * public class SearchResultItem extends BaseComponent {
   *    private final DivWebElement itemName = new DivWebElement(".inventory_item_name");
   *    private final DivWebElement itemDescription = new DivWebElement(".inventory_item_desc");
   *    private final DivWebElement itemPrice = new DivWebElement(".inventory_item_price");
   *    private final ButtonWebElement addToCart = new ButtonWebElement(".btn_primary");
   *
   *    public void clickAddToCartButton() {
   *      getAddToCart().click();
   *    }
   * }
   * }
   * </pre>
   *
   * Example Page Object calling the component's buildComponentList method
   * <pre>{@code
   * &#64;Getter
   * public class SearchResultsPage extends BasePage {
   *    private final static String INVENTORY_ITEM_SELECTOR = ".inventory_item";
   *    private final HeaderComponent headerComponent = new HeaderComponent();
   *    private final DivWebElement inventoryListContainer = new DivWebElement(By.cssSelector(".inventory_list"));
   *    private final DropDownWebElement sortDropDown = new DropDownWebElement(".product_sort_container");
   *
   *    public SearchResultsPage() {
   *      verifyIsOnPage(getInventoryList());
   *    }
   *
   *    public List<SearchResultItem> getSearchResultsList() {
   *      var listOfElements = getInventoryListContainer().findElements(DivWebElement.class, INVENTORY_ITEM_SELECTOR);
   *      return buildComponentList(listOfElements, SearchResultItem.class);
   *    }
   * }
   * }
   * </pre>
   *
   * @param listOfElements the list of elements to iterate through and convert to components
   * @param component      the {@link BaseComponent} class of the component we are converting the
   *                       list of elements to
   * @param <T>            the type reference for the components must extend {@link BaseComponent}
   * @param <X>            the type reference for the elements we're iterating through must extend
   *                       {@link BaseWebElement}
   * @return as a new list of components that extend {@link BaseComponent}
   */
  protected <T extends BaseComponent, X extends BaseWebElement> List<T> buildComponentList(
      List<X> listOfElements, Class<T> component, Integer indexCorrection) {
    // Create a new list of an object that extends BaseComponent
    var listOfComponents = new ArrayList<T>();

    /*
     Default an added index correction to 0. Index correction is helpful where there are
     elements in the list container that exist prior to the components starting. This value
     corrects the nth-child value later on.
     */
    var possibleIndexCorrection = Optional.ofNullable(indexCorrection)
            .orElse(0);

    /*
     Iterate through the listOfElements and create a new instance of the component, type T, to
     add to the listOfComponents that will be returned to the caller.
     */
    IntStream.range(0, listOfElements.size())
        .forEach(index -> {
          try {
            /*
             Get the CSS selector for the element in the list. This selector will become the new
             parent locator.
             */
            var elementBy = listOfElements.get(index).getBy();

            /*
            Check to see if the parentBy actually exists in this case. If there are any elements
            that have been constructed with a parent in mind, we want to make sure this parent
            isn't a xpath locator, along with the current elementBy.
             */
            var elementParentBy = listOfElements.get(index).getParentBy();

            /*
             Check to make sure the By locator for the parent is a type of CSS selector, where
             type is anything other than XPATH. Then, get the underlying locator as string.
             */
            if (elementBy instanceof By.ByXPath || elementParentBy instanceof By.ByXPath) {
              throw new ComponentException("Scaffold currently cannot build component lists using "
                  + "XPATH. Please use By locators that are a type of Css selector.");
            }
            var underlyingSelector = AutomationUtils.getUnderlyingLocatorByString(elementBy);

            /*
             Create a new locator that combines the parent (the underlyingLocator) and
             an :nth-child using the index. Because the element list starts at 0, we
             need to add 1 in order to adhere to correct CSS usage.
             */
            final var finalAdjustedCorrection = 1 + possibleIndexCorrection;
            var fullNewSelector = String.format("%s:nth-child(%s)", underlyingSelector,
                index + finalAdjustedCorrection);

            /*
             Create a new instance of the component passed in by the caller. The class
             extending off of BaseComponent should not have a non-empty constructor, otherwise
             this new instance will fail to init.
             */
            var componentInstance = component.getConstructor().newInstance();

            /*
             Iterate through the list of fields on the new instance of the component.
             We should only convert strong typed Scaffold elements but allow for
             additional fields, such as Strings (e.g. if Strings are being used as
             locators).
             */
            convertFieldsWithNewLocator(componentInstance, fullNewSelector);

            /*
             After the fields have been converted on the new instance of the component,
             add it to the list that we will return to the caller.
             */
            listOfComponents.add(componentInstance);
          } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
              NoSuchMethodException e) {
            throw new ComponentException(e);
          }
        });
    return listOfComponents;
  }

  /**
   * Converts {@link Field}'s from a class that extends off of {@link BaseComponent} from an
   * "inaccessible" state to "accessible." We will only convert Scaffold elements. Access is still
   * technically modified for every field, but always set back to private. Afterwards, combines
   * the parent and child together.
   *
   * @param componentInstance  the instance of the {@link BaseComponent}
   * @param fullParentSelector the parent selector being used as the prefix
   * @param <T>                the type reference of {@link BaseComponent}
   */
  private <T extends BaseComponent> void convertFieldsWithNewLocator(T componentInstance,
      String fullParentSelector) {
    var classFields = componentInstance.getClass().getDeclaredFields();

    Arrays.stream(classFields).forEach(field -> {
      try {
        field.setAccessible(true);
        if (field.get(componentInstance) instanceof BaseWebElement) {
          convertField(componentInstance, (BaseWebElement) field.get(componentInstance), field,
              fullParentSelector);
        } else {
          log.debug(String.format(
              "Scaffold detected the field [%s] during component list building that "
              + "is not a defined as a strongly typed element. Skipping conversion of field.",
              field));
        }
        field.setAccessible(false);
      } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException |
          InstantiationException e) {
        throw new ComponentException(e);
      }
    });
  }

  /**
   * Converts a {@link Field}'s {@link By} locator from a {@link BaseComponent} class. Takes a full
   * parent selector and converted element to combine it into a fully qualified parent + child
   * {@link By} locator.
   *
   * @param componentInstance  the instance of the {@link BaseComponent}
   * @param convertedElement   the converted {@link BaseWebElement}
   * @param field              the {@link Field} we are converting
   * @param fullParentSelector the fully qualified parent selector
   * @param <T>                the type reference {@link BaseComponent}
   * @param <X>                the type referece {@link BaseWebElement}
   */
  private <T extends BaseComponent, X extends BaseWebElement> void convertField(T componentInstance,
      X convertedElement, Field field, String fullParentSelector)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException,
      InstantiationException {
    var convertedElementUnderlyingLocator = AutomationUtils.getUnderlyingLocatorByString(
        convertedElement.getBy());
    var newByLocator = By.cssSelector(
        String.format("%s %s", fullParentSelector, convertedElementUnderlyingLocator));
    var constructor = convertedElement.getClass().getConstructor(By.class);
    var newElement = constructor.newInstance(newByLocator);
    field.set(componentInstance, newElement);
  }

  /**
   * Gets the Selenium based {@link Actions} object for the current thread. This is currently not
   * strongly typed and should be added in a future update.
   * <p>
   * TODO add a strongly typed {@link Actions} object
   *
   * @return {@link Actions}
   */
  protected Actions getActions() {
    return getWebDriverWrapper().getActions();
  }

  /**
   * Gets the selenium based {@link JavascriptExecutor} for the current thread.
   *
   * @return {@link JavascriptExecutor}
   */
  protected JavascriptExecutor getJavascriptExecutor() {
    return getWebDriverWrapper().getJavascriptExecutor();
  }

  /**
   * Gets the {@link AutomationWait} from the current thread's {@link WebDriverWrapper}
   *
   * @return as {@link AutomationWait}
   */
  protected AutomationWait getAutomationWait() {
    return getWebDriverWrapper().getAutomationWait();
  }

  /**
   * Gets the {@link WebDriverWrapper} for the current thread.
   *
   * @return {@link WebDriverWrapper}
   */
  private WebDriverWrapper getWebDriverWrapper() {
    var webDriverWrapper = TestContext.baseContext().getWebDriverContext().getWebDriverManager().getWebDriverWrapper();
    if (webDriverWrapper != null) {
      return webDriverWrapper;
    } else {
      throw new WebDriverWrapperException("Could not find a web driver wrapper for the current thread.");
    }
  }
}
