# MarketClip

### 😎요즘은 짧은 영상이 대세! 쇼핑에도 영상을 보는 즐거움이 있는 마켓클립!
<p align="center"><img src="https://wook-bucket.s3.ap-northeast-2.amazonaws.com/222222.png" />
  
<br />
<br />
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
  
<br />
<br />

# 🗺 ER Diagram
<center><img src="https://wook-bucket.s3.ap-northeast-2.amazonaws.com/markeclip+erd.PNG" width="1200"></center>
  
<br />

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
