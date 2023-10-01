package com.ktw.menu;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectPool {

    private List<WebDriver> pool;
    private int maxSize = 15;

    private final ChromeOptions chromeOptions;

    @Autowired
    public ObjectPool(ChromeOptions chromeOptions) {
        this.chromeOptions = chromeOptions;
    }

    @PostConstruct
    public void init() {
        this.pool = new ArrayList<WebDriver>(maxSize);
        for (int i=0; i<maxSize; i++) {
            // ChromeDriver 인스턴스 생성
            ChromeDriver driver = new ChromeDriver(chromeOptions);
            pool.add(driver);
        }
    }

//    public ObjectPool(int maxSize) {
//        this.maxSize = maxSize;
//        this.pool = new ArrayList<>();
//    }

    public synchronized WebDriver acquire() {
        if (pool.isEmpty()) {
            return createObject();
        } else {
            return pool.remove(0);
        }
    }

    public synchronized void release(WebDriver obj) {
        if (pool.size() < maxSize) {
            pool.add(obj);
        } else {
            obj.quit();
        }
    }

    private WebDriver createObject() {
        // 객체 생성 로직
        return new ChromeDriver(chromeOptions);
    }
}