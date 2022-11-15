package publishSubscribe.projectReactor;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class ReactorPublisher {
    private static final long MAX_BUFFER_TIME = 2000L;
    private static final int MAX_BUFFER_CAPACITY = 8;
    private static final long SLEEP_TIME_FIRST = 1000L;
    private static final long SLEEP_TIME_SECOND = 3000L;
    private static final long PUBLISHER_DELAY = 1000L;
    public void runSubscriptions() {
        Flux<Integer> integerFlux = Flux.range(1, 20)
                .delayElements(Duration.ofMillis(PUBLISHER_DELAY))
                .onBackpressureBuffer(Duration.ofMillis(MAX_BUFFER_TIME), MAX_BUFFER_CAPACITY, d -> {})
                .log();
        integerFlux.delayElements(Duration.ofMillis(SLEEP_TIME_FIRST))
                .subscribe(new ReactorSubscriber(ReactorSubscriber.FIRST));
        integerFlux.delayElements(Duration.ofMillis(SLEEP_TIME_SECOND))
                .subscribe(new ReactorSubscriber(ReactorSubscriber.SECOND));

        // Blocks until all subscribers are done
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
