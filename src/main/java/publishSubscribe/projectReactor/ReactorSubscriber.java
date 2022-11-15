package publishSubscribe.projectReactor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Subscription;
import reactor.core.Exceptions;
import reactor.core.publisher.BaseSubscriber;

import java.util.stream.IntStream;

public class ReactorSubscriber extends BaseSubscriber<Integer> {
    public static final String FIRST = "First";
    public static final String SECOND = "Second";
    private final String subscriberName;
    private int nextValueExpected;
    private int totalAmount;
    private static final Logger log = LogManager.getLogger();

    public ReactorSubscriber(final String subscriberName) {
        this.subscriberName = subscriberName;
        this.nextValueExpected = 1;
        this.totalAmount = 0;
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        subscription.request(1);
    }

    @Override
    protected void hookOnNext(Integer value) {
        if (value != nextValueExpected) {
            IntStream.range(nextValueExpected, value).forEach(
                    (msgNumber) -> log("Value " + msgNumber + " was dropped")
            );
            nextValueExpected = value;
        }
        log("Got a new value: " + value);
        nextValueExpected++;
        totalAmount++;
        log("Next value should be: " + nextValueExpected);
        request(1);
    }

    @Override
    protected void hookOnComplete() {
        log("Completed. In total " + totalAmount + " values");
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        log("Error occurred: " + throwable.getMessage());
        throw Exceptions.errorCallbackNotImplemented(throwable);
    }

    @Override
    protected void hookOnCancel() {
        log("Subscription canceled");
    }

    private void log(final String logMessage) {
        log.info("[" + subscriberName + "] : " + logMessage);
    }
}
