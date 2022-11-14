package publishSubscribe.flowApi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Publisher {
    private static final long MAX_BUFFER_TIME = 2000L;
    private static final int MAX_BUFFER_CAPACITY = 8;
    private static final long SLEEP_TIME_FIRST = 1000L;
    private static final long SLEEP_TIME_SECOND = 3000L;
    private static final Logger log = LogManager.getLogger();

    public void runSubscriptions() {
        log.info("A slow Second subscriber and a limited buffer size on the publisher's side");
        final SubmissionPublisher<Integer> publisher =
                new SubmissionPublisher<>(ForkJoinPool.commonPool(), MAX_BUFFER_CAPACITY);
        final Subscriber firstSubscriber = new Subscriber(SLEEP_TIME_FIRST, Subscriber.FIRST);
        final Subscriber secondSubscriber = new Subscriber(SLEEP_TIME_SECOND, Subscriber.SECOND);

        publisher.subscribe(firstSubscriber);
        publisher.subscribe(secondSubscriber);

        IntStream.rangeClosed(1, 20).forEach((number) -> {
            log.info("Offering number " + number + " to subscribers");
            publisher.offer(number, MAX_BUFFER_TIME, TimeUnit.MILLISECONDS, (subscriber, msg) -> {
                subscriber.onError(new RuntimeException("Dropping number " + msg + " for subscriber "
                        + ((Subscriber) subscriber).getSubscriberName()));
                return false; // don't retry
                });
        });

        // Blocks until all subscribers are done
        while (publisher.estimateMaximumLag() > 0) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        publisher.close();
    }
}
