package com.ktw.menu.service;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
@Service
public class SeleniumWebUtils {
    private static final List<Class<? extends Exception>> IGNORED_EXCEPTIONS = new ArrayList<>();
    static {
        IGNORED_EXCEPTIONS.add(StaleElementReferenceException.class);
    }

    /**
     * Waits for the specified condition until default timeout, ignoring exception
     */
    private static <T> T waitFor(WebDriver webDriver, ExpectedCondition<T> waitCondition, Duration duration) {
        return new WebDriverWait(webDriver, duration).ignoreAll(SeleniumWebUtils.IGNORED_EXCEPTIONS).until(waitCondition);
    }


    public static WebElement findElementBySelector(WebDriver driver, String selector, Duration duration) {
        return waitFor(driver, ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)), duration);
    }

    public static void sleep(long msec)
    {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //==========================================================================================================================
    // Frame handles
    //==========================================================================================================================


    // wait for specific frame by name or id
    // this works only for one layer below
    public static List<WebElement> getFramePath(WebDriver driver, String frameName, String frameType) {
        // switch to the top level frame to traverse the frames tree
        driver.switchTo().defaultContent();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By iframeLocator = By.id(frameName);
        WebElement iframeElement = wait.until(ExpectedConditions.presenceOfElementLocated(iframeLocator));

        List<WebElement> framePath = findFrames(driver, frameName, frameType);
        Collections.reverse(framePath);
        return framePath;
    }

    private static List<WebElement> findFrames(SearchContext context, String frameName, String frameType) {
        List<WebElement> requiredFramePath = new LinkedList<>();
        List<WebElement> frames = context.findElements(By.tagName(frameType));
        for (WebElement frame : frames) {
            if (frameName.equals(frame.getAttribute("id")) || frameName.equals(frame.getAttribute("name")) || frameName.equals(frame.getAttribute("src"))) {
                requiredFramePath.add(frame);
            } else {
                requiredFramePath.addAll(findFrames(frame, frameName, frameType));
            }
//            if (!requiredFramePath.isEmpty()) {
//                requiredFramePath.add(frame);
//                break;
//            }
        }
        return requiredFramePath;
    }
    public static WebDriver switchToFrameByPath(WebDriver driver, List<WebElement> pathToFrame, Duration duration) {
        driver = driver.switchTo().defaultContent();
        for (WebElement frame : pathToFrame) {
            driver = switchFrame(driver, frame, duration);
        }
        return driver;
    }

    public static WebDriver switchFrame(WebDriver driver, WebElement frame, Duration duration) {
        return waitFor(driver, ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame), duration);
    }

}
