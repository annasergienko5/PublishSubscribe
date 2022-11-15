package publishSubscribe.flowApi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class FlowPublisher {
    private static final long MAX_BUFFER_TIME = 2000L;
    private static final int MAX_BUFFER_CAPACITY = 8;
    private static final long SLEEP_TIME_FIRST = 1000L;
    private static final long SLEEP_TIME_SECOND = 3000L;
    private static final Logger log = LogManager.getLogger();

    //A slow Second subscriber and a limited buffer size on the publisher's side,
    // dropping current element if buffer is full for more than MAX_BUFFER_TIME
    public void runSubscriptions() {
        final SubmissionPublisher<Integer> publisher =
                new SubmissionPublisher<>(ForkJoinPool.commonPool(), MAX_BUFFER_CAPACITY);
        final FlowSubscriber firstSubscriber = new FlowSubscriber(SLEEP_TIME_FIRST, FlowSubscriber.FIRST);
        final FlowSubscriber secondSubscriber = new FlowSubscriber(SLEEP_TIME_SECOND, FlowSubscriber.SECOND);

        publisher.subscribe(firstSubscriber);
        publisher.subscribe(secondSubscriber);

        IntStream.rangeClosed(1, 20).forEach((value) -> {
            log.info("Offering value " + value + " to subscribers");
            publisher.offer(value, MAX_BUFFER_TIME, TimeUnit.MILLISECONDS, (subscriber, msg) -> {
                subscriber.onError(new RuntimeException("Dropping value " + msg + " for subscriber "
                        + ((FlowSubscriber) subscriber).getSubscriberName()));
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
