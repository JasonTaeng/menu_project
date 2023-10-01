package com.ktw.menu.service;

import com.ktw.menu.ObjectPool;
import com.ktw.menu.controller.MenuSearchController;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Service
public class NaverSearch {

    private final ObjectPool objectPool;

    @Autowired
    public NaverSearch (ObjectPool objectPool) {
        this.objectPool = objectPool;
    }

    // 멀티쓰레드로 실행
    public void getGradeReviewNum(int index, String name, String oldLocation, String newLocation) {
        // ChromeDriver 인스턴스 획득
        WebDriver driver = objectPool.acquire();

        // WebDriverWait 인스턴스 생성 (10초 동안 대기)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        String url = "https://map.naver.com/v5/search/";
        String parameter = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String URL = url + parameter;

        // 웹 페이지 로드
        driver.get(URL);

        /**
         * 1. 검색결과가 없으면, 뒤에 공백 하나 짤라서 다시 검색
         * 2. 검색결과가 하나면 pass
         * 3. 검색결과가 다수면 주소 모달창 클릭 후 도로명 주소 하나씩 비교
         * 4. 검색결과가 하나일 때, <li></li>로 나오기도 하고, 아니기도 함
         * 검색결과가 하니이면 id="searchIframe"만 있고, id="entryIframe"이 없음.
         */
        // iframe의 path를 list로 담기
        List<WebElement> pathToSearchFrame = null;
        try {
            pathToSearchFrame = SeleniumWebUtils.getFramePath(driver, "searchIframe", "iframe");
        } catch (TimeoutException t) {
            MenuSearchController.menus.get(index).setNaverGrade("-3");
            return;
        }

        // 선택한 iframe으로 switch하기
        driver = SeleniumWebUtils.switchToFrameByPath(driver, pathToSearchFrame, Duration.ofSeconds(5));

        // 검색 결과가 없는 경우 나오는 태그
        List<WebElement> noResult = driver.findElements(By.cssSelector("div.vEAWt"));

        String grade = "";
        String reviewNum = "";

        // 검색결과가 없다면
        if (noResult.size() == 1) {
            grade = "-2";

            int lastSpaceIndex = name.trim().lastIndexOf(" ");
            String trimmedName = (lastSpaceIndex >= 0) ? name.substring(0, lastSpaceIndex) : name;
            if (trimmedName.equals(name)) {

            }
        }

        // 검색결과가 있다면
        else {
            List<WebElement> photoLists = driver.findElements(By.cssSelector("[data-laim-exp-id='undefined']")); // 사진 있을 때
            List<WebElement> lists = driver.findElements(By.cssSelector("[data-laim-exp-id='undefinedundefined']")); // 사진 없을 때
            // 검색결과가 하나만 있을 때
            if (photoLists.size() == 1 || lists.size() == 1) {
                try {
                    // iframe의 path를 list로 담기
                    List<WebElement> pathToDetailFrame = SeleniumWebUtils.getFramePath(driver, "entryIframe", "iframe");

                    // 선택한 iframe으로 switch하기
                    driver = SeleniumWebUtils.switchToFrameByPath(driver, pathToDetailFrame, Duration.ofSeconds(5));

                    grade = driver.findElement(By.cssSelector("span.PXMot.LXIwF em")).getText();
                    reviewNum = driver.findElement(By.cssSelector("span.PXMot:not(.LXIwF) em")).getText();
                } catch (NoSuchElementException n) {
                    try {
                        grade = driver.findElement(By.cssSelector("span.h69bs.a2RFq em")).getText();
                        reviewNum = driver.findElement(By.cssSelector("span.h69bs:not(.a2RFq)")).getText();
                        // 평점이 없는 경우
                    } catch (NoSuchElementException o) {
                        grade = "-1";
                        reviewNum = driver.findElement(By.cssSelector("span.PXMot:not(.LXIwF) em")).getText();
                    }
                }
                // 검색결과가 여러개일 때
            } else if (photoLists.size() > 1 || lists.size() > 1) {
                // 리스트에 주소가 나오는 경우
                try {
                    // 지번, 도로명 주소 나오는 태그(예외 처리)
                    WebElement locationElm = driver.findElement(By.cssSelector("a.vcshc"));
                    List<WebElement> links = driver.findElements(By.cssSelector("a.vcshc"));
                    // 몇 번째 상호가 원하는 검색 상호인지 카운트
                    int cnt = 0;
                    String targetLoc = "";
                    for (WebElement link : links) {
                        link.click(); // 지번 주소 나오게 클릭
                        // 도로명 주소 태그
                        List<WebElement> elements = driver.findElements(By.cssSelector("div.o8CtQ"));
                        String fullTargetLoc = elements.get(0).getText();
                        String[] parts = fullTargetLoc.substring(0, fullTargetLoc.length() - 2).split(" ");
                        targetLoc = parts[2] + " " + parts[3];

                        // 타겟 음식점인 경우
                        if (targetLoc.equals(newLocation)) {
                            link.click();
                            break;
                        } else { // 타겟 음식점이 아닌 경우
                            link.click();
                            cnt++;
                        }
                    }

                    // 타겟 음식점이 리스트에 없을 때
                    if (cnt == links.size()) {
                        grade = "-2";
                        MenuSearchController.menus.get(index).setNaverGrade(grade);
                        return;
                    }

                    // 타겟 음식점이 리스트에 있을 때 - 1.리스트 페이지에서 평점이 있는 경우
                    List<WebElement> grades = driver.findElements(By.cssSelector("span.h69bs.a2RFq em"));
                    List<WebElement> reviewNums;
                    if (grades.size() > 0) {
                        reviewNums = driver.findElements(By.xpath("//*[@id='_pcmap_list_scroll_container']/ul/li[1]/div[1]/div[2]/span[3]/text()[2]"));
                        //*[@id="_pcmap_list_scroll_container"]/ul/li[1]/div[1]/div[2]/span[3]/text()[2]
                        //*[@id="_pcmap_list_scroll_container"]/ul/li[2]/div[1]/div[2]/span[3]/text()[2]
                        grade = grades.get(cnt).getText();
                        reviewNum = reviewNums.get(cnt).getText();
                    }
                    // 타겟 음식점이 리스트에 있을 때 - 2.리스트 페이지에 평점이 없는 경우
                    else {
                        // 클릭해서 상세 div 열기
                        List<WebElement> elements = driver.findElements(By.cssSelector("a.P7gyV"));
                        WebElement element = elements.get(cnt * 2);
                        element.click();

                        try {
                            // iframe의 path를 list로 담기
                            List<WebElement> pathToDetailFrame = SeleniumWebUtils.getFramePath(driver, "entryIframe", "iframe");

                            // 선택한 iframe으로 switch하기
                            driver = SeleniumWebUtils.switchToFrameByPath(driver, pathToDetailFrame, Duration.ofSeconds(5));

                            grade = driver.findElement(By.cssSelector("span.PXMot.LXIwF em")).getText();
                            reviewNum = driver.findElement(By.cssSelector("span.PXMot:not(.LXIwF) em")).getText();
                        } catch (NoSuchElementException n) {
                            grade = "-1";
                            reviewNum = driver.findElement(By.cssSelector("span.PXMot:not(.LXIwF) em")).getText();
                        }
                    }

                    // 리스트에 주소가 나오지 않는 경우
                } catch (NoSuchElementException e) {
                    // 상호 클릭 태그
                    List<WebElement> nameClicks = driver.findElements(By.cssSelector("a.tzwk0"));

                    // 도로명 주소로 타겟 음식점 찾기
                    /**
                     * 두번째 클릭부터 제대로 안 되는 듯
                     */
                    for (int i = 0; i < nameClicks.size(); i++) {
                        // 상호 클릭 태그
                        nameClicks = driver.findElements(By.cssSelector("a.tzwk0"));
                        WebElement nameTag = wait.until(ExpectedConditions.elementToBeClickable(nameClicks.get(i)));
                        nameTag.click();

                        // iframe의 path를 list로 담기
                        List<WebElement> pathToEntryFrame;
                        try {
                            pathToEntryFrame = SeleniumWebUtils.getFramePath(driver, "entryIframe", "iframe");
                        } catch (TimeoutException t) {
                            MenuSearchController.menus.get(index).setNaverGrade("-3");
                            return;
                        } catch (StaleElementReferenceException s) {
                            MenuSearchController.menus.get(index).setNaverGrade("-3");
                            return;
                        }

                        // 선택한 iframe으로 switch하기
                        driver = SeleniumWebUtils.switchToFrameByPath(driver, pathToEntryFrame, Duration.ofSeconds(5));

                        String targetLoc = driver.findElement(By.cssSelector("span.LDgIH")).getText();
                        String[] parts = targetLoc.split(" ");
                        targetLoc = parts[2] + " " + parts[3];

                        if (targetLoc.equals(newLocation)) {
                            grade = driver.findElement(By.cssSelector("span.PXMot.LXIwF em")).getText();
                            reviewNum = driver.findElement(By.cssSelector("span.PXMot:not(.LXIwF) em")).getText();
                            break;
                        }

                        // 선택한 iframe으로 switch하기
                        driver = SeleniumWebUtils.switchToFrameByPath(driver, pathToSearchFrame, Duration.ofSeconds(5));
                    }
                }

            }
        }

        MenuSearchController.menus.get(index).setNaverGrade(grade);
        MenuSearchController.menus.get(index).setNaverReviewNum(reviewNum.replaceAll(",", ""));

        objectPool.release(driver);
    }

}
