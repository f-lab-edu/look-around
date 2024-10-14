# Lookaround

나의 산책경로를 기록하고 메모와 사진을 남길 수 있습니다 🚶🏻

# 기능 소개

- ForegroundService를 통해 사용자의 산책 경로를 트랙킹합니다.
- 현재 날씨를 공공데이터포털 기상예보API를 사용해 보여줍니다.
- 저장된 산책경로와 메모, 산책 시 찍은 사진을 리스트로 보여줍니다.
- 일주일 or 한달 or 일년 단위 산책 요약을 보여줍니다.
- DarkMode 변경이 가능합니다.

# 아키텍처

- ### CleanArchitecture
![CleanArchitecture](https://github.com/user-attachments/assets/d7d56f3e-021e-46c5-b085-34776e224b61)

- ### MVVM
![MVVM](https://github.com/user-attachments/assets/7395af0b-90c8-44a9-8100-1832d3cbdcd8)

- ### 멀티 모듈화
![Modulization](https://github.com/user-attachments/assets/b3f10000-4bad-405a-8cf5-27764b9d7dee)


# 스크린샷

<p align="center">  <img src="https://github.com/user-attachments/assets/0e0558ed-ad64-47ff-b51a-da7597898f9c" align="center" width="32%">  
  <img src="https://github.com/user-attachments/assets/e8642de6-6f05-4f85-a00e-49f84e6fa197" align="center" width="32%">  
  <img src="https://github.com/user-attachments/assets/b62b298e-9e3e-4c7d-8d59-7dd5b50f55ee" align="center" width="32%">  
</p>
<p align="center">  <img src="https://github.com/user-attachments/assets/7ba4c91d-c2d6-4dff-b3c0-f356672b0fa0" align="center" width="32%">  
  <img src="https://github.com/user-attachments/assets/4a077f05-1ddb-4150-bffb-39e538cbf3e4" align="center" width="32%">  
</p>

# 사용 기술

* Kotlin
* Coroutine + Flow
* Room
* Retrofit2
* Moshi
* AAC ViewModel
* Hilt
* NaverMap
* Compose
