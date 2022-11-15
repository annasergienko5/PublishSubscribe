package publishSubscribe.flowApi;

public class FlowApp {
    public static void main(String[] args) {
        final FlowPublisher flowPublisher = new FlowPublisher();
        flowPublisher.runSubscriptions();
    }
}
