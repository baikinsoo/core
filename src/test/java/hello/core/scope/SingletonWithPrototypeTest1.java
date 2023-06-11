package hello.core.scope;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;

public class SingletonWithPrototypeTest1 {

    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        prototypeBean1.addCount();
        Assertions.assertThat(prototypeBean1.getCount()).isEqualTo(1);

        PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        prototypeBean2.addCount();
        Assertions.assertThat(prototypeBean1.getCount()).isEqualTo(1);
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        Assertions.assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        Assertions.assertThat(count2).isEqualTo(1);
    }

    @Scope("singleton")
    static class ClientBean {

//        private final PrototypeBean prototypeBean; // 생성시점에 주입

//        private ApplicationContext applicationContext;

//        public ClientBean(ApplicationContext applicationContext) {
//            this.applicationContext = applicationContext;
//            System.out.println(applicationContext);
//        }

        @Autowired
        private Provider<PrototypeBean> prototypeBeansProvider;

//        @Autowired
//        ClientBean(PrototypeBean prototypeBean) {
//            this.prototypeBean = prototypeBean;
//        }

        // 생성된 프로토타입 빈을 사용한다. 생성된 시점에 주입된 빈을 계속해서 사용한다.
        // 싱글톤 빈에 속에 있는 프로토타입은 계속해서 생성되지 않는다.
        public int logic() {
//            PrototypeBean prototypeBean = applicationContext.getBean(PrototypeBean.class); // applicationContext가 호출 될 때마다 생성하기 위해서
            PrototypeBean prototypeBean = prototypeBeansProvider.get(); // get으로 호출 할때만 가져와서 쓴다.
            prototypeBean.addCount();
            return prototypeBean.getCount();

        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init" + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
