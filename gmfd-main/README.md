# CNA Azure 1차수 (5조_든든한 아침식사)

# 서비스 시나리오

## 든든한 아침식사

기능적 요구사항

1. 결제가 완료되면 마일리지 점수가 쌓인다. (res,req)
1. 결제가 취소되면 마일리지 점수를 제거한다. (pub,sub)

비기능적 요구사항

1. 트랜잭션
    1. 결제가 되지 않은 건은 마일리지가 생성되지 않아야한다.
1. 장애격리
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
    1. 결제시스템이 과중되면 취소 요청을 잠시동안 받지않고 잠시후에 하도록 유도한다. Circit breaker, fallback
1. 성능
    1. 마일리지 정보를 확인 할 수 있어야 한다. CQRS


# Event Storming 모델
 ![event](https://user-images.githubusercontent.com/41769626/105119057-e08fee00-5b12-11eb-8370-cc81b3630b88.PNG)

## 구현 점검

#주문 생성, 취소 , CQRS (req/res,correaltion-key,saga)

주문을 생성하고 결제가 완료되면 마일리지가 생성된다(동기,req/res)

![reqres-2](https://user-images.githubusercontent.com/41769626/105133623-6a9a7f80-5b30-11eb-8d83-1db0f6fb22c7.PNG)
![reqres](https://user-images.githubusercontent.com/41769626/105133651-77b76e80-5b30-11eb-9adf-2edfc8feac2c.PNG)

결제가 취소되면 취소된 결제번호와 동일한 값을 가진 마일리지도 같이 취소된다(비동기,pub/sub)

![saga](https://user-images.githubusercontent.com/41769626/105133790-b3eacf00-5b30-11eb-8c5e-ab4008c590ed.PNG)
![saga2](https://user-images.githubusercontent.com/41769626/105133796-b51bfc00-5b30-11eb-8205-c5f4fd3e1208.PNG)


결제가 완료/취소되면 마일리지의 MileageViews에 데이터가 같이 생성된다.(CQRS)

![cqrs](https://user-images.githubusercontent.com/41769626/105133840-c6650880-5b30-11eb-8921-38b7d063c2a5.PNG)


#장애 격리
결제 취소와 마일리지 취소는 비동기 통신으로 마일리지 취소 서비스가 죽은 상태에서도 정상 동작한다.

![장애차단](https://user-images.githubusercontent.com/41769626/105134333-9c601600-5b31-11eb-915e-68831709ba6f.PNG)

다시 마일리지 서비스를 살릴경우 마일리지 취소 프로스세스가 진행된다.

![장애차단2](https://user-images.githubusercontent.com/41769626/105134460-d03b3b80-5b31-11eb-8e5a-3f08b6686ec5.PNG)


#Gateway
생성된 External-IP 로 Gateway 통신을 하며 Gateway는 ConfigMap의 값을 사용한다.(운영에서 ConfigMap 서술)

![gateway](https://user-images.githubusercontent.com/41769626/105133937-ec8aa880-5b30-11eb-954e-181ca496ffc5.PNG)



#Circuit Breaker

Hystrix 를 사용한 Circuit Breaker
설정

![histrix](https://user-images.githubusercontent.com/41769626/105141871-1e097100-5b3d-11eb-957a-a07c9b0d8fa6.PNG)


Siege 를 사용하여 100클라이언트로 20초간 부하를 발생시킨다.
siege -c100 -t60S -r10 -v --content-type "application/json" 'http://payment:8080/pays POST {"payId":1}'

부하가 발생된 요청은 500으로 빠지며 Availability 가 감소함을 확인한다.

![cir1](https://user-images.githubusercontent.com/41769626/105141802-0c27ce00-5b3d-11eb-8d8f-03df20d32367.PNG)
![cir](https://user-images.githubusercontent.com/41769626/105141805-0d58fb00-5b3d-11eb-9a67-fc6b6291febf.PNG)

#Autoscale(HPA)
autoscale 생성 및 siege 활용 부하 생성

Deploy.yaml 파일 설정

![hpa5](https://user-images.githubusercontent.com/41769626/105137874-5efe8700-5b37-11eb-9d39-ea9fe82275a4.PNG)


![hpa1](https://user-images.githubusercontent.com/41769626/105137057-1397a900-5b36-11eb-9119-014b2580510f.PNG)

부하 생성으로 인한 Pod Scale-Out 확인

![hpa](https://user-images.githubusercontent.com/41769626/105137145-2f9b4a80-5b36-11eb-8ddb-edc2b7b91381.PNG)
![hpa2](https://user-images.githubusercontent.com/41769626/105137128-2ad69680-5b36-11eb-957d-c1a824e35522.PNG)
![hpa3](https://user-images.githubusercontent.com/41769626/105137131-2c07c380-5b36-11eb-963f-f95fc524c331.PNG)


#Zero-downtime deploy(Readiness Probe)

Deploy.yaml 에 설정 적용 후 이미지 교체와 동시에 siege 테스트 수행

kubectl set image deployment order order=final05crg.azurecr.io/mileages:v10C
siege -c100 -t20S -v 'http://mileage:8080/mileages'

![readi2](https://user-images.githubusercontent.com/41769626/105137674-0929df00-5b37-11eb-83a4-d1eec543d47f.PNG)

수행 결과 Avaliability 100%

![readi](https://user-images.githubusercontent.com/41769626/105137803-442c1280-5b37-11eb-8cda-4dd716c0ea75.PNG)

#ConfigMap/Presistence Volume

-- ConfigMap 적용

![configmap](https://user-images.githubusercontent.com/41769626/105134686-24deb680-5b32-11eb-8944-cde155518bc4.PNG)
![cm](https://user-images.githubusercontent.com/41769626/105134884-6ec79c80-5b32-11eb-9b66-ce58a839aea8.PNG)

-- PVC 사용하여 Pod 접근 후 Mount 된 Volume 확인

![pvc](https://user-images.githubusercontent.com/41769626/105125453-bbee4300-5b1f-11eb-9be6-53d64068771a.PNG)

#Polyglot

#Self-Healing(Liveness Probe)

아래 조건으로 Deploy

![liveness](https://user-images.githubusercontent.com/41769626/105143130-c8ce5f00-5b3e-11eb-93a2-11abceea70bd.PNG)

cat /test 가 없으면 발생하기에 pod에 접근하여 test dir 생성

![liveness3](https://user-images.githubusercontent.com/41769626/105143493-472b0100-5b3f-11eb-992d-e1a1cfc43ca4.PNG)

생성 후 조건을 만족하여 더 이상 restart 되지 않음

![liveness2](https://user-images.githubusercontent.com/41769626/105143524-4eeaa580-5b3f-11eb-9baf-a87c6ea7ada3.PNG)

