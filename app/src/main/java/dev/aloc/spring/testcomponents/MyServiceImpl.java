package dev.aloc.spring.testcomponents;

import dev.aloc.spring.annotation.Autowired;
import dev.aloc.spring.annotation.Component;

/**
 * Bean 생성 및 의존성 주입 테스트를 위한 임시 서비스 클래스이다.
 */
@Component
public class MyServiceImpl implements MyService {
    // Bean에 해당하는 MyRepository를 필드로 가진다.
    private final MyRepository repository;
    // 테스트를 위해 만든 String 타입의 필드
    private String testString;

    /**
     * 의존성 주입 테스트를 위한 @Autowired 생성자.
     * <p>
     * Autowired가 붙은 유일한 생성자이기 때문에 InstantiationUtil에서는 이 생성자를 정상적으로 선택해야 한다.
     *
     * @param repository SimpleBeanFactory에서 Bean으로 주입해주어야 하는 클래스.
     * @param testString SimpleBeanFactory에서 null로 주입해주어야 하는 클래스.
     */
    @Autowired
    public MyServiceImpl(MyRepository repository, String testString) {
        this.repository = repository;
        this.testString = testString;

        System.out.println(">>> [2] MyServiceImpl 인스턴스 생성 완료 (MyRepository 주입받음: " + repository.getClass().getName() + ")");
    }

    @Override // 인터페이스의 메서드를 여기서 실제로 구현
    public String getHelloMessage() {
        return "Hello from Service! Data: " + repository.getData(); // 가정: repository 메서드 호출
    }
}
