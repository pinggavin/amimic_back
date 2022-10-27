package org.jeecg.api.out.factory;

import feign.hystrix.FallbackFactory;
import org.jeecg.api.out.ISysBaseAPI;
import org.jeecg.api.out.fallback.SysBaseAPIFallback;
import org.springframework.stereotype.Component;

@Component
public class SysBaseAPIFallbackFactory implements FallbackFactory<ISysBaseAPI> {

    @Override
    public ISysBaseAPI create(Throwable throwable) {
        SysBaseAPIFallback fallback = new SysBaseAPIFallback();
        fallback.setCause(throwable);
        return fallback;
    }
}