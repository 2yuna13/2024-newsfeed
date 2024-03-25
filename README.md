# SportsFeed
### 스포츠 관련 이야기 공유 서비스  
<br/>

## 💾 사용 기술
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" /> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" /> <img src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white" /> <img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white" /> <img src="https://img.shields.io/badge/json%20web%20tokens-323330?style=for-the-badge&logo=json-web-tokens&logoColor=pink" /> <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white" /> <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white" />  
<br/>

## 📝 ERD
![ERD](https://github.com/2yuna13/2024-newsfeed/assets/121540949/f9075285-d886-421b-92e5-179eb5e86197)

## 📝 API
[API 명세서](https://documenter.getpostman.com/view/26860170/2sA2xb5apD)  
<br/>

## 🔶 주요 기능
### 1. 회원가입 / 로그인
- JWT 토큰과 Security를 이용한 인증, 인가 처리
- 회원 권한 부여 (USER, ADMIN)

**회원가입 페이지**
![회원가입](https://github.com/2yuna13/2024-newsfeed/assets/121540949/ae5affb2-1512-4c92-98d1-81f054962fb9)

**로그인 페이지**
![로그인](https://github.com/2yuna13/2024-newsfeed/assets/121540949/7246f77c-176f-4975-aba3-83aa6e04e126)

### 2. 사용자 기능
- 팔로우 기능
- AWS S3를 이용한 프로필 사진 업로드
- 비밀번호 수정 시 최근 3번 안에 사용한 비밀번호 사용 제한

### 3. 게시물, 댓글 기능
- 게시물, 댓글 조회를 제외한 모든 기능에 인가 적용
- 수정과 삭제는 작성자만 가능
- AWS S3를 이용한 멀티미디어 업로드
- 자신의 게시물, 댓글 제외 좋아요 및 취소 기능

**게시물 목록 페이지**
![게시물 목록](https://github.com/2yuna13/2024-newsfeed/assets/121540949/53c32432-9ed9-454e-aa5a-9b4e070a868d)

### 4. 백오피스(관리자) 기능
- 사용자 권한 수정 및 강제 탈퇴 시키는 사용자 관리 기능
- 게시물, 댓글 수정과 삭제하는 게시물 관리 기능
