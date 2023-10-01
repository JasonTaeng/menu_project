package com.ktw.menu.controller;

import com.ktw.menu.model.Menu;
import com.ktw.menu.model.MenuRank;
import com.ktw.menu.service.GoogleSearch;
import com.ktw.menu.service.KakaoSearch;
import com.ktw.menu.service.NaverSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class MenuSearchController {

    private final KakaoSearch kakaoSearch;

    private final NaverSearch naverSearch;

    private final GoogleSearch googleSearch;

    public static CopyOnWriteArrayList<Menu> menus = new CopyOnWriteArrayList<>();

    @Autowired
    public MenuSearchController(KakaoSearch kakaoSearch, NaverSearch naverSearch, GoogleSearch googleSearch) {
        this.kakaoSearch = kakaoSearch;
        this.naverSearch = naverSearch;
        this.googleSearch = googleSearch;
    }

    @GetMapping("/menu-search")
    public ResponseEntity<ArrayList<MenuRank>> crawl(@RequestParam("keyword") String keyword) {
        /**
         * menus 초기화(이후 수정 요망)
         */
        menus = new CopyOnWriteArrayList<>();

        // 크롤링 작업 수행 및 결과 추출
        kakaoSearch.getList(keyword);

        // ArrayList의 size에 따라 쓰레드 개수 설정
        int numThreads = menus.size();

        // 쓰레드 배열 생성
        /**
         * 쓰레드 풀 고려하기?
         */
        Thread[] naverThreads = new Thread[numThreads];
        Thread[] googleThreads = new Thread[numThreads];

        // 각 쓰레드별로 작업 정의
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            naverThreads[i] = new Thread(() -> {
                // 인자를 다르게 전달하여 메서드 호출
                naverSearch.getGradeReviewNum(index, menus.get(index).getName(), menus.get(index).getOldLocation(), menus.get(index).getNewLocation()); // index에는 쓰레드별로 다른 값을 전달
            }, "NaverThread-" + menus.get(index).getName());
        }

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            googleThreads[i] = new Thread(() -> {
                // 인자를 다르게 전달하여 메서드 호출
                googleSearch.getGradeReviewNum(index, menus.get(index).getName()); // index에는 쓰레드별로 다른 값을 전달
            }, "GoogleThread-" + menus.get(index).getName());
        }

        // 쓰레드 시작
        for (Thread thread : naverThreads) {
            thread.start();
        }
        for (Thread thread : googleThreads) {
            thread.start();
        }

        // 모든 스레드가 종료될 때까지 기다림
        for (Thread thread : naverThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Thread thread : googleThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 평점 및 리뷰 수 계산
        ArrayList<MenuRank> menuRankList = new ArrayList<>();
        for (Menu menu :menus) {
            MenuRank menuRank = new MenuRank();
            menuRank.setName(menu.getName());
            menuRank.setNewLoc(menu.getNewLocation());

            // 평균 평점 넣기
            int cnt = 3;
            float kakaoGrade = 0.0f;
            float naverGrade = 0.0f;
            float googleGrade = 0.0f;
            if (menu.getKakaoGrade() != null && menu.getKakaoGrade() != "") {
                kakaoGrade = Float.parseFloat(menu.getKakaoGrade());
            }
            if (kakaoGrade < 0) {
                kakaoGrade = 0;
                cnt--;
            }

            if (menu.getNaverGrade() != null && menu.getNaverGrade() != "") {
                naverGrade = Float.parseFloat(menu.getNaverGrade());
            }
            if (naverGrade < 0) {
                naverGrade = 0;
                cnt--;
            }

            if (menu.getGoogleGrade() != null && menu.getGoogleGrade() != "") {
                googleGrade = Float.parseFloat(menu.getGoogleGrade());
            }
            if (googleGrade < 0) {
                googleGrade = 0;
                cnt--;
            }

            float avgGrade = 0;
            if (cnt == 3) {
                avgGrade = (kakaoGrade + naverGrade + googleGrade) / 3.0f;
            } else if (cnt == 2) {
                avgGrade = (kakaoGrade + naverGrade + googleGrade) / 2.0f;
            } else if (cnt == 1) {
                avgGrade = (kakaoGrade + naverGrade + googleGrade);
            } else {
                //평점 없을때
            }
            menuRank.setAvgGrade(avgGrade);

            // 총 리뷰수 넣기
            int totalReviewNum = 0;
            if (menu.getKakaoReviewNum() != null && menu.getKakaoReviewNum() != "" && Integer.parseInt(menu.getKakaoReviewNum())>0) {
                totalReviewNum += Integer.parseInt(menu.getKakaoReviewNum());
            }
            if (menu.getNaverReviewNum() != null && menu.getNaverReviewNum() != "" && Integer.parseInt(menu.getNaverReviewNum())>0) {
                totalReviewNum += Integer.parseInt(menu.getNaverReviewNum());
            }
            if (menu.getGoogleReviewNum() != null && menu.getGoogleReviewNum() != "" && Integer.parseInt(menu.getGoogleReviewNum())>0) {
                totalReviewNum += Integer.parseInt(menu.getGoogleReviewNum());
            }
            menuRank.setTotalReviewNum(totalReviewNum);

            menuRankList.add(menuRank);
        }

        Collections.sort(menuRankList, new Comparator<MenuRank>() {
            @Override
            public int compare(MenuRank o1, MenuRank o2) {
                return Float.compare(o2.getAvgGrade(), o1.getAvgGrade());
            }
        });

        for (int i=0; i < menuRankList.size(); i++) {
            MenuRank menuRank = menuRankList.get(i);
            float roundAvgGrade = Math.round(menuRank.getAvgGrade() * 100) / 100.0f;
            menuRank.setAvgGrade(roundAvgGrade);
            menuRank.setRank(i+1);
        }

//        for (MenuRank list : menuRankList) {
//            System.out.println(list.getName() + " " + list.getAvgGrade() + " " + list.getNewLoc());
//        }

//        for (Menu list : menus) {
//            System.out.println("MenuSearch: " + list.getName() + " oldLoc: " + list.getOldLocation() + " newLoc: " + list.getNewLocation()
//                    + " kakao: " + list.getKakaoGrade() + " " + list.getKakaoReviewNum()
//                    + " naver: " + list.getNaverGrade() + " " + list.getNaverReviewNum()
//                    + " google: " + list.getGoogleGrade() + " " + list.getGoogleReviewNum()
//            );
//        }
        return ResponseEntity.status(HttpStatus.OK).body(menuRankList);
    }
}
