package publishSubscribe.flowApi;

public class App {
    public static void main(String[] args) {
        final Publisher publisher = new Publisher();
        publisher.runSubscriptions();
    }
}
