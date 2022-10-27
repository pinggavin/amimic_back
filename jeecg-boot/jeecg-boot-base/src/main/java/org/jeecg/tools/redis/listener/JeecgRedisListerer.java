package org.jeecg.tools.redis.listener;

import org.jeecg.tools.base.BaseMap;

/**
 * 自定义消息监听
 */
public interface JeecgRedisListerer {

    void onMessage(BaseMap message);

}
