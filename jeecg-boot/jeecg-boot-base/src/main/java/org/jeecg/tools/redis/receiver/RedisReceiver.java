package org.jeecg.tools.redis.receiver;


import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import org.jeecg.tools.base.BaseMap;
import org.jeecg.tools.constant.GlobalConstants;
import org.jeecg.tools.redis.listener.JeecgRedisListerer;
import org.jeecg.tools.util.SpringContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author zyf
 */
@Component
@Data
public class RedisReceiver {


    /**
     * 接受消息并调用业务逻辑处理器
     *
     * @param params
     */
    public void onMessage(BaseMap params) {
        Object handlerName = params.get(GlobalConstants.HANDLER_NAME);
        JeecgRedisListerer messageListener = SpringContextHolder.getHandler(handlerName.toString(), JeecgRedisListerer.class);
        if (ObjectUtil.isNotEmpty(messageListener)) {
            messageListener.onMessage(params);
        }
    }

}
