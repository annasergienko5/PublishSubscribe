package publishSubscribe.flowApi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Flow;
import java.util.stream.IntStream;

public class FlowSubscriber implements Flow.Subscriber<Integer> {
    public static final String FIRST = "First";
    public static final String SECOND = "Second";
    private static final Logger log = LogManager.getLogger();
    private final long sleepTime;
    private final String subscriberName;
    private Flow.Subscription subscription;
    private int nextValueExpected;
    private int totalAmount;

    public FlowSubscriber(final long sleepTime, final String subscriberName) {
        this.sleepTime = sleepTime;
        this.subscriberName = subscriberName;
        this.nextValueExpected = 1;
        this.totalAmount = 0;
    }

    @Override
    public void onSubscribe(final Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(final Integer value) {
        if (value != nextValueExpected) {
            IntStream.range(nextValueExpected, value).forEach(
                    (msgNumber) -> log("Value " + msgNumber + " was dropped")
            );
            nextValueExpected = value;
        }
        log("Got a new value: " + value);
        takeSomeRest();
        nextValueExpected++;
        totalAmount++;

        log("Next value should be: " + nextValueExpected);
        subscription.request(1);
    }

    @Override
    public void onError(final Throwable throwable) {
        log("Error occurred: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log("Completed. In total " + totalAmount + " values");
    }

    private void log(final String logMessage) {
        log.info("[" + subscriberName + "] : " + logMessage);
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    private void takeSomeRest() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
