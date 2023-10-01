package com.ktw.menu;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {

    @Bean
    public ChromeOptions chromeOptions() {
        // chromedriver.exe 파일을 클래스패스에서 가져오기 (local 환경에서)
        ClassLoader classLoader = getClass().getClassLoader();
        String driverPath = classLoader.getResource("chromedriver117.exe").getPath();
        System.setProperty("webdriver.chrome.driver", driverPath);

        // Chrome 옵션 생성 및 user-agent, Referer 설정
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
        chromeOptions.addArguments("-remote-allow-origins=*");        //Chrome 브라우저가 원격 호스트의 요청을 허용하도록 설정
        chromeOptions.addArguments("--disable-popup-blocking");       //팝업안띄움
        chromeOptions.addArguments("headless");                       //브라우저 안띄움
        chromeOptions.addArguments("--disable-gpu");                  //gpu 비활성화 -> headless와 함께 하면 NaverSearch가 안됨
//        chromeOptions.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음 -> 이거 하면 크롤링 안 됨
        return chromeOptions;
    }

}
