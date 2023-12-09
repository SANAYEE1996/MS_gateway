package com.ms.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Component
public class ScheduleFilter extends AbstractGatewayFilterFactory<ScheduleFilter.Config> {

    private final StopWatch stopWatch;

    public ScheduleFilter(){
        super(ScheduleFilter.Config.class);
        stopWatch = new StopWatch("schedule server");
    }

    public static class Config{}

    @Override
    public GatewayFilter apply(ScheduleFilter.Config config) {
        return ((exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            stopWatch.start();
            log.info("[Schedule Filter] REQUEST >> IP : {}, URI : {}",
                    Objects.requireNonNull(request.getRemoteAddress()).getAddress(),
                    request.getURI());

            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                stopWatch.stop();
                log.info("[Schedule Filter] Response >>  IP : {}, URI : {}, RESPONSE CODE : {} --> TIME :  {} ms",
                        request.getRemoteAddress(),
                        request.getURI(),
                        response.getStatusCode(),
                        stopWatch.getLastTaskTimeMillis());
            }));
        });
    }
}
