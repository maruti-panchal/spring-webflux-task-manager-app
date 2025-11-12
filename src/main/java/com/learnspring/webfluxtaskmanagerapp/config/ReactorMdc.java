package com.learnspring.webfluxtaskmanagerapp.config;

import com.learnspring.webfluxtaskmanagerapp.dtos.FakeStoreDto;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;
public final class ReactorMdc {

    private ReactorMdc() {} // prevent instantiation

    // For Mono<T> pipelines
    public static <T> Function<Mono<T>, Mono<T>> mdcLifterMono() {
        return mono -> mono
                .doOnEach(signal -> {
                    if (signal.getContextView().hasKey("requestId")) {
                        String id = signal.getContextView().get("requestId");
                        MDC.put("requestId", id);
                    }
                })
                .doFinally(sig -> MDC.remove("requestId"));
    }

    // For Flux<T> pipelines
    public static <T> Function<Flux<T>, Flux<T>> mdcLifterFlux() {
        return flux -> flux
                .doOnEach(signal -> {
                    if (signal.getContextView().hasKey("requestId")) {
                        String id = signal.getContextView().get("requestId");
                        MDC.put("requestId", id);
                    }
                })
                .doFinally(sig -> MDC.remove("requestId"));
    }
}