package com.mind.links;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-22 10:18
 * @version v1.0.0
 */
public class MinioApplicationTest {
    @Test
    public void testSplitPathIsUsed() {
        System.out.println("11111");
        StepVerifier.create(processOrFallback(Mono.just("just a  phrase with    tabs!"),
                Mono.just("EMPTY_PHRASE")))
                .expectNext("just", "a2", "phrase", "with", "tabs!")
                .verifyComplete();

    }

    @Test
    public void testEmptyPathIsUsed() {
        StepVerifier.create(processOrFallback(Mono.empty(), Mono.just("EMPTY_PHRASE")))
                .expectNext("EMPTY_PHRASE")
                .verifyComplete();
    }
    public Flux<String> processOrFallback(Mono<String> source, Publisher<String> fallback) {
        Flux<String> stringFlux = source
                .flatMapMany(phrase -> Flux.fromArray(phrase.split("\\s+")))
                .switchIfEmpty(fallback);
        stringFlux.subscribe(System.out::println);
        return stringFlux;
    }
}
