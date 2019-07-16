package io.github.kgress.scaffold.models.unittests;

import org.openqa.selenium.Beta;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.logging.Logs;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.WebDriver.Options;
import static org.openqa.selenium.WebDriver.Timeouts;
import static org.openqa.selenium.WebDriver.ImeHandler;
import static org.openqa.selenium.WebDriver.Window;

public class MockOptions implements Options {

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public void deleteCookieNamed(String name) {

    }

    @Override
    public void deleteCookie(Cookie cookie) {

    }

    @Override
    public void deleteAllCookies() {

    }

    @Override
    public Set<Cookie> getCookies() {
        return null;
    }

    @Override
    public Cookie getCookieNamed(String name) {
        return new Cookie( name, name );
    }

    @Override
    public Timeouts timeouts() {
        return new Timeouts() {

            @Override
            public Timeouts implicitlyWait(long time, TimeUnit unit) {
                return null;
            }

            @Override
            public Timeouts setScriptTimeout(long time, TimeUnit unit) {
                return null;
            }

            @Override
            public Timeouts pageLoadTimeout(long time, TimeUnit unit) {
                return null;
            }

        };
    }

    @Override
    public ImeHandler ime() {
        return null;
    }

    @Override
    @Beta
    public Window window() {
        return null;
    }

    @Override
    @Beta
    public Logs logs() {
        return new MockLogs();
    }
}
