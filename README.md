# 나만의 산책 기록을 위한 어플리케이션

나의 산책경로를 기록하고 메모와 사진을 남길 수 있습니다 🚶🏻

# 아키텍처

안드로이드 권장 아키텍처 기반으로 작성하였습니다.  

![권장 아키텍처](https://github.com/user-attachments/assets/22737d5b-5d3c-4c97-afab-c630904b884f)


![MVVM](https://github.com/user-attachments/assets/7395af0b-90c8-44a9-8100-1832d3cbdcd8)

# 스크린샷



# 기능 소개

- ForegroundService를 통해 사용자의 산책 경로를 트랙킹합니다.
- 현재 날씨를 공공데이터포털 기상예보API를 사용해 보여줍니다.
- 저장된 산책경로와 메모, 산책 시 찍은 사진을 리스트로 보여줍니다.
- DarkMode 변경이 가능합니다.

# 사용 기술

* Kotlin
* Coroutine + Flow
* Room
* Retrofit2
* Moshi
* AAC ViewModel
* Hilt
* NaverMap
* ViewBinding
