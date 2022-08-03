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
<center><img src="https://wook-bucket.s3.ap-northeast-2.amazonaws.com/API+%EC%9A%94%EC%95%BD+%EC%82%AC%EC%A7%84.png" width:"1200"></center>
<br /><br />
  
# ⚙️ 기술 스택
  
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

# 🖼 아키텍쳐
<center><img src="https://user-images.githubusercontent.com/25544668/150839035-c1d3a7d3-ca79-49fd-ba8c-c50079d29c5f.png" width:"800"></center>

<br />

# 🔑 프로젝트 주요 기능
  
* 로그인, 소셜 로그인 
  - 로그인 시 JWT 토큰, Refresh 토큰 발행
  - google, kakao 소셜 로그인 가능.
  
* 채팅
  - 허ㅗㅎ

* querydsl
  - querydsl로 동적쿼리 사용.

* redis 캐시 
  - 캐시 기능을 사용해 좀더 빨리 데이터를 불러올 수 있음.
<br />




# 💡 Trouble Shooting
<details>
  <summary>
    <b> CICD 작업 이후 프로젝트 명 변경으로 인한 서버 에러 발생 </b>
  </summary>
  
```bash
REPOSITORY=/home/ubuntu/
cd $REPOSITORY

APP_NAME=marketclip
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

echo ">현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z $CURRENT_PID ]
then
  echo ">현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -9 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi
```
* ###### 프로젝트 프로젝트 명 변경으로 인해 kill 명령어가 실행되지 않아서 일어나는 오류
##### 해결
* ###### 단순히 EC2의 (전 프로젝트명의)프로젝트를 강제적으로 kill하고 재실행 해줬다
</details>

<details>
  <summary>
    <b> 스프링 시큐리티 예외처리 </b>
  </summary>
  
* ###### 스프링 시큐리티는 서블릿 필터에서 발생하는 오류라 ControllerAdvice에서 잡지 못한다.
* ###### 그래서 필터 계층에서 예외 처리를 해주어야 했다.
  
##### 해결
* ###### 시큐리티에 등록한 필터들 마다 예외 처리를 해주었다
</details>

<details>
  <summary>
    <b> 게시글 작성 시 이미지 순서 문제 </b>
  </summary>
  
* ###### 게시글을 작성 시 여러개의 사진을 올릴 수 있는데 첫번째 사진만 미리보기 사진으로 등록이 됨.
* ###### 선용님 저희가 왜 이미지를 리스트로 안받고 url을 받았나요??
  
###### 해결
</details>

<br />

ppt 구성
1. 소개(2분)
  - 서비스 계획 배경(30초) : 중고나라에는 택배로 많이 거래하는데 사기가 많다.
  - 보다 안전한 거래를 위해 파는 물건을 영상으로 올려서 사기를 방지한다.
  - 시연 영상(1분 30초) : 로그인 + 게시글 작성 + 구매자가 대화신청 + 판매자가 대화 받고 + 거래 요청 함 + 구매자 수락!
2. 아키텍쳐(1~2분)
  - DB -> S3 -> EC2 -> 레디스 -> CI/CD -> 로드 밸런서 -> 라우트 53 -> https -> 프론트
3. 프론트엔드(2분)
  - 
4. 백엔드(2분)
  - 주요기술 1분. 
  - 트러블 슈팅 1분. 
5. 피드백(1분)
  - 
6. 앞으로의 계획?(30초)
  - 
