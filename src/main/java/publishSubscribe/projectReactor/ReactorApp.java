package publishSubscribe.projectReactor;

public class ReactorApp {
    public static void main(String[] args) {
        ReactorPublisher reactorPublisher = new ReactorPublisher();
        reactorPublisher.runSubscriptions();
    }
}
