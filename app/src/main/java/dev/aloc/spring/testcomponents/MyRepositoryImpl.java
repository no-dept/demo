package dev.aloc.spring.testcomponents;

import dev.aloc.spring.annotation.Component;

/**
 * Bean 생성 및 원시자료형 주입 테스트를 위한 임시 리포지토리 클래스이다.
 */
@Component
public class MyRepositoryImpl implements MyRepository {
    
    // 원시자료형 주입이 잘 되는지 확인하기 위한 int 필드.
    int testno;
    
    /**
     * 매개변수가 더 많은 생성자 (SimpleBeanFactory가 선택해야 할 생성자).
     *
     * @param testno SimpleBeanFactory가 testno에 기본값을 잘 주입했다면 0이 되어야 한다.
     */
    public MyRepositoryImpl(int testno) {
        this.testno = testno;
        System.out.println(">>> [1] MyRepositoryImpl 인스턴스 생성 완료 (testno: " + testno + ")");
    }

    @Override // 인터페이스 메서드 구현
    public String getData() {
        // DB에서 데이터를 가져오는 척하는 로직
        return "AlocData_" + testno;
    }
}
