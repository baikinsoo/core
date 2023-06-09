package hello.core.singleton;


public class SingletonService {

    private static final SingletonTest instance = new SingletonTest();

    public static SingletonTest getInstance() {
        return instance;
    }

    private SingletonService() {

    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
