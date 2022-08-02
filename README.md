# MarketClip

### 😎요즘은 짧은 영상이 대세! 쇼핑에도 영상을 보는 즐거움이 있는 마켓클립!
<p align="center"><img src="https://wook-bucket.s3.ap-northeast-2.amazonaws.com/222222.png" />
  

<br />
<br />
  
# 👥 멤버
- Front-end: [이덕희](https://github.com/ejzl521), [장산](https://github.com/kyngmn)
- Back-end: [임선용](https://github.com/sunyongIM), [김재호](https://github.com/KimjaehoLy), [남신욱](https://github.com/tlsdnr1135)
- Designer: 이수진, 안수현
- [\[Front-End Github\]](https://github.com/TEAM-7E7/7E7-FE)
  
<br />

# 🗓 프로젝트 기간
- 2022년 6월 24일 ~ 2022년 8월 05일
  

<br><br>
# 🎞 프로젝트 발표영상 🎞
https://www.youtube.com/watch?v=UvTk7JV03cs&t=4s

<br><br>
# 🎞 프로젝트 시연영상 🎞
https://youtu.be/1j_bk71_Eaw

<br>

# 🗺 ER Diagram
<center><img src="https://wook-bucket.s3.ap-northeast-2.amazonaws.com/markeclip+erd.PNG" width="1200"></center>
  
<br /><br />
  
# 📌 API 명세서
  
<br /><br />
  
# ⚙️ 기술 스택

### Back-End

<div>
  <img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
  <img src="https://img.shields.io/badge/Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=SpringDataJPA&logoColor=white">
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
  <img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=AmazonAWS&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  

</div>  

### Front-End

<div>
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
  <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black">
  <img src="https://img.shields.io/badge/html-E34F26?style=for-the-badge&logo=html5&logoColor=white">
  <img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
  <img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=black">
  <img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=AmazonAWS&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">  
</div>
<br />
# 🖼 아키텍쳐
<center><img src="https://user-images.githubusercontent.com/25544668/150839035-c1d3a7d3-ca79-49fd-ba8c-c50079d29c5f.png" width:"800"></center>

<br />

# 🔑 프로젝트 주요 기능
* Nginx에 HTTPS SSL 적용
  
* 회원가입 페이지  
  - 아이디 : 중복확인(이메일 형식)
  - 닉네임 : 중복확인 
  - 비밀번호 : 8자 이상 16자 이하, 영문 필수 1자이상,특수문자 하나 이상 필수
  - 비밀번호 확인 : 8자 이상 16자 이하, 영문 필수 1자이상,특수문자 하나 이상 필수 
  
* 로그인 페이지 (일반 로그인, 소셜 카카오 로그인)
  - 가입된 회원의 정보와 일치하는지 확인 후, 일치할 경우 로그인 성공 
  - 로그인시 jwt 토큰 발행
  
* 메인 페이지  
  - 전체 게시글 조회 기능
    - 게시글 주소선택으로 게시물 필터링 기능
    - 게시글 식품,의류,가전 중 1택 카테고리 필터링 기능

* 즐겨찾기
  - 본인 게시물은 즐겨찾기 불가능

* 마이페이지 
  - 프로필 수정
  - 내가 작성한 글 확인
  - 내 즐겨찾기 목록 확인
  
* 메인 페이지 (전체 게시글 조회 기능)
  - 게시글 주소선택으로 게시물 필터링 기능
  - 게시글 식품,의류,가전등 중 1택 카테고리 필터링 기능
  
* 게시글 작성 페이지  
  - 게시물 CRUD (이미지 포함)
  - 해시태그 기능 추가

* 게시글 상세 페이지  
  - 좋아요 : 게시글 즐겨찾기 기능  
  - 댓글 : 게시글 대댓글
  - 채팅 : 1:1 채팅기능
  
* 실시간 채팅 페이지
  - 채팅방 생성, 나가기 ,방 폭파
  - 읽지 않은 채팅 COUNT
  - 채팅방 인원정보 표시
  - 채팅 알람 기능 추가
  
* 게시물 검색 인기순 top 10 
<br />




# 💡 Trouble Shooting
* 무중단 배포를위해 특정 브랜치에 push 이벤트 발생시 자동 빌드,배포가 진행되는 상황에서 yml파일을 같이 push하면 안되는데 ec2서버 내부에서 yml을 읽어들어야 jar파일 구동이 되는 상황 
  - 해결: 
     - 1.ec2서버 내부에 임의의 yml파일 저장공간을 만들어 넣어두고  
 ![image](https://user-images.githubusercontent.com/25544668/151780893-c74936cf-75a0-43c4-bac0-1df8a0b540c0.png)
 
     - 2.무중단 배포를위한 bin/bash 내부에 1번의 특정경로의 yml을  -Dspring.config.location=file 을 사용해 읽어들여 
 ![image](https://user-images.githubusercontent.com/25544668/151781088-07e01f38-04da-4139-8540-5f9ced139e3c.png)
</br>
    

* 카테고리 필터 및 검색기능 적용 시 쿼리문이 복잡해짐 → 쿼리DSL 사용. 
  * 쿼리를 자바코드로 작성하여 컴파일 시점에서 오류를 잡기쉬우면서 동적쿼리를 쉽게 작성
![img.png](img.png)
</br>


* 인기검색어 기능 구현 시 처음에는 검색어를 전부 DB에 저장해 많이 검색된 검색어 순위를 출력 → DB접근을 줄이기위해 cache적용
  * 단순한 정보를 반복적으로 동일하게 제공해야 하고, 정보의 업데이트가 실시간으로 이루어질 필요가 없기 때문
    ![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FSXOX1%2Fbtrr9fKbY8N%2FNNJBh3fFBrn7TCzXhfspw0%2Fimg.png)
