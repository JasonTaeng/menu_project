# 점심메뉴 추천 사이트
1. 주제: spring boot 기반 크롤링을 통해 음식점의 랭킹을 종합해 추천해주는 사이트
<br>

2. 설명: Selenium Library를 이용해 사용자의 검색 단어를 카카오 맵, 구글 맵, 네이버 맵에 검색 후 평점 및 리뷰 수 크롤링 후 합산하여 카카오 맵 api를 통해 음식점 추천
<br>
   &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp1) 프로그램 실행시 15개의 ChromeDriver 인스턴스 생성 후 객체 풀(Object Pool)로 관리<br>
   <br>
   &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2) 카카오 맵에 해당 키워드 검색 후 람다 스트림을 이용해 평점 있는 곳 필터링
   <img src="https://github.com/JasonTaeng/menu_project/assets/134661987/1a9deed7-9ac6-4b38-bdc6-0a503ad31d4b" width="85%"></img>
   <br><br>
   &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp3) 필터링 된 음식점을 멀티 스레드로 네이버 맵과 구글 맵에 검색(주소로 비교)
   <img src="https://github.com/JasonTaeng/menu_project/assets/134661987/bb44214d-afa6-4bb1-a054-f33cf17ce072" width="85%"></img>
   <br><br>
   &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4) 카카오, 구글, 네이버 맵의 평점과 리뷰 수를 종합하여 카카오 맵 api로 점심 메뉴 추천
<br>

3. 느낀점:
   - static 선언과 싱글톤 클래스 사용의 장,단점과 서비스에 따른 설계방법에 대해 미흡함을 느낌.
   - 디자인 패턴에 대한 공부 필요성
   - 싱글톤에서 Thread safe한 자료구조 쓰기, 읽기 시 최적화 방법에 대한 공부가 부족함을 느낌.
<br>

4. 제작기간 / 참여인원: 2023.06.19 ~ 2023.09.08 / 개인 프로젝트
<br>

5. 기술스택

<table>
  <tr>
    <td>Frontend</td>
    <td>JavaScript, Bootstrap(open source), Ajax(fetch)</td>
  </tr>
  <tr>
    <td>Backend</td>
    <td>Java</td>
  </tr>
  <tr>
    <td>배포</td>
    <td>AWS Lightsail</td>
  </tr>
</table>

6. 개발환경

<table>
  <tr>
    <td>OS</td>
    <td>Window</td>
  </tr>
  <tr>
    <td>개발 환경 (IDE)</td>
    <td>Intelli J</td>
  </tr>
</table>

<br><br>

## ScreenShots

- 메인 페이지

bootstrap 오픈 소스 일부 수정하여 제작

<img src="https://github.com/JasonTaeng/menu_project/assets/134661987/6774afba-1798-47d6-b3df-c9f6dfee8f8f" width="85%"></img>
<br><br>

- 검색 후 대기화면

<img src="https://github.com/JasonTaeng/menu_project/assets/134661987/27e83dab-9723-49a1-b834-d65fa40c51f7" width="85%"></img>
<br><br>

- 검색 완료 후 화면

<img src="https://github.com/JasonTaeng/menu_project/assets/134661987/daa7d5d8-145a-402d-80e1-2f343f3fbea7" width="85%"></img>
