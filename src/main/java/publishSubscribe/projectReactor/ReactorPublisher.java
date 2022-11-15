package publishSubscribe.projectReactor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ReactorPublisher {
    private static final long MAX_BUFFER_TIME = 9000L;
    private static final int MAX_BUFFER_CAPACITY = 8;
    private static final long SLEEP_TIME_FIRST = 1000L;
    private static final long SLEEP_TIME_SECOND = 3000L;
    private static final long PUBLISHER_DELAY = 1000L;
    private static final Logger log = LogManager.getLogger();

    //A slow Second subscriber, limited buffer size on the publisher's side, publisher delay is controlled,
    // dropping element in buffer after MAX_BUFFER_TIME
    public void runSubscriptions() {
        Flux<Integer> integerFlux = Flux.range(1, 20)
                .delayElements(Duration.ofMillis(PUBLISHER_DELAY))
                .onBackpressureBuffer(Duration.ofMillis(MAX_BUFFER_TIME), MAX_BUFFER_CAPACITY,
                        d -> log.info("Dropping value: " + d))
                .log();

        integerFlux.delayElements(Duration.ofMillis(SLEEP_TIME_FIRST))
                .subscribe(new ReactorSubscriber(ReactorSubscriber.FIRST));
        integerFlux.delayElements(Duration.ofMillis(SLEEP_TIME_SECOND))
                .subscribe(new ReactorSubscriber(ReactorSubscriber.SECOND));

        // Blocks until all subscribers are done
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
