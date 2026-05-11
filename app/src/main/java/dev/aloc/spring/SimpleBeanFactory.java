package dev.aloc.spring;

import dev.aloc.spring.aop.AspectProxyHandler;
import dev.aloc.spring.enums.CreationStatus;
import dev.aloc.spring.exception.BeanCreationException;
import dev.aloc.spring.exception.CircularDependencyException;
import dev.aloc.spring.exception.NoSuchBeanDefinitionException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Component 어노테이션이 붙은 클래스들을 Bean으로 만들기 위한 클래스이다.
 */
public class SimpleBeanFactory implements BeanFactory {
    // Bean에 대한 메타정보를 담아두는 Map
    private final Map<Class<?>, BeanDefinition> defs = new ConcurrentHashMap<>();
    // 실제 Bean 싱글턴을 담는 Map
    private final Map<Class<?>, Object> beans = new ConcurrentHashMap<>();

    /**
     * @Component 어노테이션이 붙은 클래스들을 받아서 defs와 beans에 차례로 등록해준다.
     *
     * @param classesToRegister Component 어노테이션이 붙은 클래스들의 집합.
     */
    @Override
    public void registerBeans(Set<Class<?>> classesToRegister) {
        // 1. BeanDefinition 생성 및 등록
        for (Class<?> clazz : classesToRegister) {
            defs.put(clazz, new BeanDefinition(clazz));
        }

        // 2. Bean 생성 및 등록 (Eager Initialization)
        for (Class<?> clazz : classesToRegister) {
            if (!beans.containsKey(clazz)) {
                getBean(clazz);
            }
        }
    }

    /**
     * 이미 등록된 Bean을 찾거나 없으면 새로 만들어 반환한다.
     *
     * @param beanType Bean으로 등록되었는지 확인하고자 하는 클래스.
     * @return 찾았거나 새로 생성한 Bean을 맵에 넣을 수 있도록 Object 형태로 되돌려 준다.
     */
    @Override
    public Object getBean(Class<?> beanType) {
        Object bean = beans.get(beanType);
        if (bean != null) {
            return bean;
        }

        BeanDefinition def = defs.get(beanType);
        if (def != null) {
            // 순환 참조 감지
            if (def.getStatus() == CreationStatus.CREATING) {
                throw new CircularDependencyException("순환 참조가 발견되었습니다: " + beanType.getName());
            }
            return createBean(def);
        }

        throw new NoSuchBeanDefinitionException(beanType.getName() + " 타입의 Bean 정의를 찾을 수 없습니다.");
    }

    /**
     * 아직 생성되지 않은 Bean을 만들기 위한 메소드이다.
     *
     * @param def Bean으로 생성하고자 하는 클래스의 BeanDefinition.
     * @return 생성된 Bean을 beans 맵에 들어갈 수 있도록 Object 형태로 되돌려 준다.
     */
    @Override
    public Object createBean(BeanDefinition def) {
        try {
            // '생성 중'으로 상태 전환
            def.setStatus(CreationStatus.CREATING);

            Constructor<?> ctor = def.getInjectionCtor();
            Class<?>[] paramTypes = def.getParamTypes();
            Object[] args = new Object[paramTypes.length];

            // 의존성 주입 (재귀 호출)
            for (int i = 0; i < args.length; i++) {
                Class<?> type = paramTypes[i];

                if (defs.containsKey(type)) {
                    args[i] = getBean(type);
                } else if (type.isInterface()) {
                    // 인터페이스 타입의 의존성 해결 (구현체 찾기)
                    boolean found = false;
                    for (Class<?> clazz : defs.keySet()) {
                        if (type.isAssignableFrom(clazz)) {
                            args[i] = getBean(clazz);
                            found = true;
                            break;
                        }
                    }
                    if (!found) args[i] = null; // 못 찾으면 null
                } else if (type.isPrimitive()) {
                    args[i] = defaultPrimitive(type);
                } else {
                    args[i] = null;
                }
            }

            // 1. 원본 객체 생성
            Object originalBean = ctor.newInstance(args);

            // 2. 최종적으로 내보낼 Bean 변수 (기본값은 원본)
            Object beanToExpose = originalBean;

            // 3. [AOP 초안] 인터페이스가 있으면 무조건 프록시 적용
            // (JDK Dynamic Proxy는 인터페이스가 필수입니다)
            if (originalBean.getClass().getInterfaces().length > 0) {
                try {
                    // 프록시 생성 (우리가 새로 만든 AspectProxyHandler 사용)
                    beanToExpose = java.lang.reflect.Proxy.newProxyInstance(
                        originalBean.getClass().getClassLoader(),
                        originalBean.getClass().getInterfaces(),
                        new AspectProxyHandler(originalBean) // Target만 넘겨줌 (심플 버전)
                    );
                    System.out.println("[AOP] 프록시 생성 완료: " + def.getBeanType().getName());

                } catch (Exception e) {
                    System.out.println("[AOP] 프록시 생성 실패 (원본 사용): " + e.getMessage());
                    beanToExpose = originalBean;
                }
            }

            // 4. 최종 Bean 저장 (원본 대신 프록시가 저장될 수 있음)
            if (!beans.containsKey(def.getBeanType())) {
                beans.put(def.getBeanType(), beanToExpose);
            }

            // 5. 생성 완료 상태로 변경
            def.setStatus(CreationStatus.CREATED);
            return beanToExpose;

        } catch (NoSuchBeanDefinitionException e) {
            String message = "Bean '" + def.getBeanType().getName() + "' 생성 실패: 의존성 주입 실패 ("
                + e.getMessage() + ")";
            throw new BeanCreationException(message, e);

        } catch (Exception e) {
            String message = "Bean '" + def.getBeanType().getName() + "' 생성 실패";
            throw new BeanCreationException(message, e);
        }
    }

    @Override
    public <T> T getExistingBean(Class<T> beanType) {
        Object bean = beans.get(beanType);
        if (bean == null) {
            throw new NoSuchBeanDefinitionException(
                beanType.getName() + " 타입으로 등록된 Bean 인스턴스를 찾을 수 없습니다."
            );
        }
        return beanType.cast(bean);
    }

    private Object defaultPrimitive(Class<?> t) {
        if (t == boolean.class) return false;
        if (t == char.class) return '\0';
        if (t == byte.class) return (byte) 0;
        if (t == short.class) return (short) 0;
        if (t == int.class) return 0;
        if (t == long.class) return 0L;
        if (t == float.class) return 0f;
        if (t == double.class) return 0d;
        throw new IllegalArgumentException("지원하지 않는 primitive 타입: " + t);
    }
}