package com.retailmenot.scaffold.webelements;

import com.retailmenot.scaffold.util.AutomationUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * A strongly typed representation of a Date {@link WebElement}.
 *
 * This will frequently need to be subclassed in order to work with particular javascript calendar controls, etc., but
 * the intention is to provide a simple way to put dates in fields, which can frequently be tricky.
 */
@Slf4j
public class DateWebElement extends AbstractWebElement {

    // Global DateFormat to be used
    private static DateFormat dateFormat;

    // Local DateFormat which will override the global DateFormat
    private DateFormat localDateFormat;

    public DateWebElement(By by) {
        super(by);
    }

    public DateWebElement(By by, WebElement parentElement) {
        super(by, parentElement);
    }

    public DateWebElement(By by, By parentBy) {
        super(by, parentBy);
    }

    public DateWebElement(WebElement element) {
        super(element);
    }

    /**
     * Sets a DateFormat which will be globally used by all DateWebElements.
     *
     * @param dateFormat the {@link DateFormat} to use
     */
    public static void setGlobalDateFormat(DateFormat dateFormat) {
        DateWebElement.dateFormat = dateFormat;
    }

    /**
     * Sets a DateFormat which will be used by this DateWebElement only.
     *
     * @param dateFormat the {@link DateFormat} to use
     */
    public DateWebElement setDateFormat(DateFormat dateFormat) {
        this.localDateFormat = dateFormat;
        return this;
    }

    public Date getValue() {
        var value = getAttribute("value");
        Date d = null;
        if (value != null && value.length() > 0) {
            try {
                d = getDateFormat().parse(value);
            } catch (ParseException p) {
                log.error("Error parsing date: " + AutomationUtils.getStackTrace(p));
            }
        }
        return d;
    }

    /**
     * Returns the applicable DateFormat.
     *
     * @return the {@link DateFormat}
     */
    private DateFormat getDateFormat() {
        if (localDateFormat != null) {
            return localDateFormat;
        } else {
            return dateFormat;
        }
    }
}
