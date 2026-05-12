package dev.aloc.spring;

import dev.aloc.spring.web.DispatcherServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class App {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        // 1. Jetty 서버 생성
        int port = resolvePort();
        Server server = new Server(port);
        
        // 2. ServletContextHandler 생성
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        // 3. DispatcherServlet 등록
        DispatcherServlet dispatcher = new DispatcherServlet();
        ServletHolder dispatcherHolder = new ServletHolder("dispatcher", dispatcher);
        context.addServlet(dispatcherHolder, "/"); // 모든 요청을 DispatcherServlet으로 전달
        
        // 4. Handler 연결
        server.setHandler(context);
        
        // 종료 시 graceful stop
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Stopping Jetty...");
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        
        // 서버 시작
        server.start();
        System.out.println("Jetty started at http://localhost:" + port);
        server.join(); // main 스레드가 종료되지 않게 대기
    }

    private static int resolvePort() {
        String raw = System.getenv("PORT");
        if (raw == null || raw.isBlank()) {
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid PORT env value '" + raw + "', falling back to " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
}
