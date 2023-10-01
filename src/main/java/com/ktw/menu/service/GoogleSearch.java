package com.ktw.menu.service;

import com.ktw.menu.ObjectPool;
import com.ktw.menu.controller.MenuSearchController;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleSearch {

    private final ObjectPool objectPool;

    @Autowired
    public GoogleSearch(ObjectPool objectPool) {
        this.objectPool = objectPool;
    }

    // 멀티쓰레드로 실행
    public void getGradeReviewNum(int index, String name) {
        // ChromeDriver 인스턴스 획득
        WebDriver driver = objectPool.acquire();

        String url = "https://www.google.co.kr/maps/search/";
        //String parameter = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String URL = url + name;

        // 웹 페이지 로드
        driver.get(URL);

        // 검색 결과가 없는 경우 나오는 태그
        List<WebElement> noResult = driver.findElements(By.cssSelector("div.Q2vNVc"));

        String grade = "";
        String reviewNum = "";

        // 검색결과가 없다면
        if(noResult.size()==1) {
            grade = "-2";

            int lastSpaceIndex = name.trim().lastIndexOf(" ");
            String trimmedName = (lastSpaceIndex >= 0) ? name.substring(0, lastSpaceIndex) : name;
            if(trimmedName.equals(name)) {

            }
        }

        // 검색결과가 있다면
        else {
            List<WebElement> lists = driver.findElements(By.cssSelector("a.hfpxzc")); // 검색결과 몇 개인지 판단

            // 검색결과가 하나만 있을 때
            if(lists.size()==0) {
                try {
                    grade = driver.findElement(By.cssSelector("div.F7nice span[aria-hidden='true']")).getText();
                } catch (NoSuchElementException n) {
                    MenuSearchController.menus.get(index).setGoogleGrade("-3");
                    return;
                }
                reviewNum = driver.findElement(By.cssSelector("div.F7nice span[aria-label^='리뷰']")).getText();
                int length = reviewNum.length();
                reviewNum = reviewNum.substring(1, length-1);

            // 검색결과가 여러개일 때
            } else {
                List<WebElement> locationElements = driver.findElements(By.cssSelector("div.UaQhfb.fontBodyMedium div:last-child div:first-child span:last-child span:last-child"));

                // 몇 번째 상호가 원하는 검색 상호인지 카운트
                /**
                 * 주소 한국식으로 후처리하는 방법 구현해야 함
                 */
                int cnt = 0;
                String location = "";
                for (WebElement element : locationElements) {
                    String locations = element.getText();
                    String[] parts = locations.split(" ");
                    if(parts.length>2) {
                        location = parts[1] + " " + parts[2];
                    }
                    // 타겟 음식적인 경우
                    if (location.equals(MenuSearchController.menus.get(index).getOldLocation())
                        || location.equals(MenuSearchController.menus.get(index).getNewLocation())) {
                        break;
                    } else {
                        cnt++;
                    }
                }

                // 타겟 음식점이 리스트에 없을 때
                if(cnt==locationElements.size()) {
                    grade = "-2";
                    MenuSearchController.menus.get(index).setGoogleGrade(grade);
                    return;
                }

                // 타겟 음식점이 리스트에 있을 때
                List<WebElement> gradeElements = driver.findElements(By.cssSelector("span.MW4etd"));
                List<WebElement> reviewNumElements = driver.findElements(By.cssSelector("span.UY7F9"));

                grade = gradeElements.get(cnt).getText();
                reviewNum = reviewNumElements.get(cnt).getText();
                int length = reviewNum.length();
                reviewNum = reviewNum.substring(1, length-1);
            }
        }

        MenuSearchController.menus.get(index).setGoogleGrade(grade);
        MenuSearchController.menus.get(index).setGoogleReviewNum(reviewNum.replaceAll(",", ""));

        objectPool.release(driver);
    }

}