package com.ktw.menu.service;

import com.ktw.menu.ObjectPool;
import com.ktw.menu.controller.MenuSearchController;
import com.ktw.menu.model.Menu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class KakaoSearch {

    private final ObjectPool objectPool;

    @Autowired
    public KakaoSearch (ObjectPool objectPool) {
        this.objectPool = objectPool;
    }

    public void getList(String keyword) {
        // ChromeDriver 인스턴스 획득
        WebDriver driver = objectPool.acquire();

        // 웹 페이지에 접속하여 HTML 문서를 가져옴
        String url = "https://map.kakao.com/?nil_profile=title&nil_src=local";
        String encodedValue = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String parameter = "?location=on&callback=jQuery181011982297148981846_1686841910396&q=" + encodedValue + "&msFlag=A&mcheck=Y&rect=&sort=0";
        String URL = url + parameter;

        // 웹 페이지 로드
        driver.get(URL);

        // 식당 정보 추출
        List<WebElement> list = driver.findElements(By.cssSelector(".PlaceItem.clickArea"));

        // 평점이 있는 곳만 필터링하기
        List<WebElement> filteredList = list.stream()
                .filter(li -> !li.findElement(By.cssSelector("[data-id='scoreNum']")).getText().equals("")) //후기 미제공 걸러주나 확인
                .filter(li -> !li.findElement(By.cssSelector("[data-id='scoreNum']")).getText().equals("0.0"))
                .limit(5) // 최대 5개의 웹 요소로 제한
                .toList();

        // List<Menu>로 만들기
        for (WebElement li : filteredList) {
            String name = li.findElement(By.className("link_name")).getText();
            String newLocations = li.findElement(By.cssSelector("[data-id='address']")).getText();
            String newLocation = "";
            String[] parts = newLocations.split(" ");
            if(parts.length>2) {
                newLocation = parts[2] + " " + parts[3];
            }
            String oldLocation = li.findElement(By.cssSelector("[data-id='otherAddr']")).getAttribute("title");
            String grade = li.findElement(By.cssSelector("[data-id='scoreNum']")).getText();
            String reviewNum = li.findElement(By.cssSelector("[data-id='numberofscore']")).getText();
            reviewNum = reviewNum.substring(0, reviewNum.length() - 1);

            Menu menu = new Menu();
            menu.setName(name);
            menu.setNewLocation(newLocation);
            menu.setOldLocation(oldLocation);
            menu.setKakaoGrade(grade);
            menu.setKakaoReviewNum(reviewNum.replaceAll(",", ""));
            MenuSearchController.menus.add(menu);
        }

        objectPool.release(driver);
    }
}
