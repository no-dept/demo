package dev.aloc.spring.testcomponents;

import dev.aloc.spring.annotation.Autowired;
import dev.aloc.spring.annotation.Controller;
import dev.aloc.spring.annotation.GetMapping;

@Controller
public class HelloController {

    // 인터페이스 타입으로 주입받음 (여기에 프록시가 들어옴)
    private final MyService myService;

    @Autowired
    public HelloController(MyService myService) {
        this.myService = myService;
        // 실제 주입된 객체가 원본인지 프록시인지 로그로 확인 가능
        System.out.println(">>> [3] HelloController 생성 (Service 주입 완료. 타입: "
            + myService.getClass().getName() + ")");
    }

    @GetMapping("/hello")
    public String hello() {
        // Service 메서드 호출 -> AOP 로그 출력 -> 결과 반환
        return myService.getHelloMessage();
    }
}