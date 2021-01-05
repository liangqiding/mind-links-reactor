package com.mind.links.minio;


import com.mind.links.minio.controller.MinioController;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-22 10:18
 * @version v1.0.0
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MinioApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void api0() {
        webTestClient
                .get()
                .uri("/minio/create/bucket?bucketName=links")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testSplitPathIsUsed() {
        StepVerifier.create(processOrFallback(Mono.just("just a  phrase with    tabs!"),
                Mono.just("EMPTY_PHRASE")))
                .expectNext("just", "a2", "phrase", "with", "tabs!")
                .verifyComplete();

    }

    @Test
    public Flux<String> processOrFallback(Mono<String> source, Publisher<String> fallback) {
        Flux<String> stringFlux = source
                .flatMapMany(phrase -> Flux.fromArray(phrase.split("\\s+")))
                .switchIfEmpty(fallback);
        stringFlux.subscribe(System.out::println);
        return stringFlux;
    }




}
