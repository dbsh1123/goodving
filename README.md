# CNA Azure 1차수 (5조_든든한 아침식사)

# 서비스 시나리오

## 든든한 아침식사

기능적 요구사항

- 결제가 완료되면 배달이 됨과 동시에 포인트가 적립이 된다.
- 결제가 취소되면 적립되었던 포인트가 삭제된다.

비기능적 요구사항

1. 트랜잭션
    1. 결제가 안된 건에 대해서는 포인트가 적립되지 않아야 한다.
1. 장애격리
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
    1. 결제시스템이 과중되면 취소 요청을 잠시동안 받지않고 잠시후에 하도록 유도한다. Circit breaker, fallback
1. 성능
    1. 적립금 현황을 확인 가능 CQRS


# Event Storming 모델
 ![images](https://user-images.githubusercontent.com/40571451/105222649-1fb75100-5b9e-11eb-8b48-f6e6e55aa505.png)


## 구현 Test

#주문 생성&취소 CQRS (Correaltion-Key,SAGA)

주문 성공 (OrderId: 3)  
![주문](https://user-images.githubusercontent.com/40571451/105226228-14b2ef80-5ba3-11eb-92aa-c758e4605472.PNG)  
![주문성공](https://user-images.githubusercontent.com/40571451/105226236-167cb300-5ba3-11eb-98cb-4aa2c4519f66.PNG)

주문 뒤, 결제가 성공한다면 아래와 같이 포인트 생성 (Req/Res --- 동기)


해당 주문 건(OrderId: 3)을 취소한다면, 결제가 취소되며 결제번호와 매핑된 포인트 적립 취소 (Pub/Sub --- 비동기)
![주문취소](https://user-images.githubusercontent.com/40571451/105226247-18467680-5ba3-11eb-956b-1b52052f0e13.PNG)
![취소적립현황](https://user-images.githubusercontent.com/40571451/105226251-1a103a00-5ba3-11eb-9656-aefa575f6953.PNG)


#CQRS

결제가 완료/취소되면 포인트View (MyPoint)에 데이터가 같이 생성된다.

amount: 주문량  
rate : 주문량이 5건 이상일 시, 적립율 20% 이외 10%  
![포인트적립성공](https://user-images.githubusercontent.com/40571451/105259746-b6e8cc80-5bcf-11eb-80cb-60ea5f8ea7af.PNG)  

결제 취소시 조회  
![포인트조회](https://user-images.githubusercontent.com/40571451/105219181-8128f100-5b99-11eb-86c5-94775a5adbe9.PNG)


#장애 격리

결제/포인트 취소는 Pub/Sub이기 때문에  포인트 적립취소가 서비스가 죽은 와중에도 정상 작동

죽은 서비스를 다시 살릴 경우 포인트 적립취소 서비스가 다시 실행되면서 해당 프로세스가 진행된다.



#Gateway

생성된 External-IP 로 Gateway 통신을 하며 Gateway는 ConfigMap의 값 사용



#Circuit Breaker

Hystrix 를 이용한 Circuit Breaker



#ConfigMap/Presistence Volume

-- ConfigMap 적용

![gate](https://user-images.githubusercontent.com/41769626/105149128-8c9efc80-5b46-11eb-95bc-6b47e3251642.PNG)

![cm](https://user-images.githubusercontent.com/41769626/105134884-6ec79c80-5b32-11eb-9b66-ce58a839aea8.PNG)
