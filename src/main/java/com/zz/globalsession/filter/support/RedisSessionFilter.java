package com.zz.globalsession.filter.support;

import javax.servlet.Filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.JedisPool;

import com.zz.globalsession.filter.AbstractGlobalSessionFilter;
import com.zz.globalsession.serial.Serializer;
import com.zz.globalsession.serial.support.JdkSerializer;
import com.zz.globalsession.store.support.RedisSessionStore;

/**
 * @author sunff 2015年12月5日 下午3:23:45
 * @since 1.0.0
 */
public class RedisSessionFilter extends AbstractGlobalSessionFilter implements Filter {

    private static Log log = LogFactory.getLog(RedisSessionFilter.class);

    private JedisPool jedisPool;
    private Serializer serializer;

    // @Override
    // public void init(FilterConfig config) throws ServletException {
    // super.init(config);
    // if (serializer == null) {
    // serializer = new JdkSerializer();
    // }
    // if (jedisPool != null) {
    // store = new RedisSessionStore(jedisPool, serializer);
    // }
    //
    // }

    @Override
    public void initSettings() {
        super.initSettings();
        if (serializer == null) {
            serializer = new JdkSerializer();
        }
        if (jedisPool != null) {
            store = new RedisSessionStore(jedisPool, serializer);
        }
    }

    private void shutdownMemcachedClientPool() {
        try {
            if (jedisPool != null) {
                jedisPool.close();
            }
        } catch (Exception e) {
            log.info("Failed to shutdown memcached client pool", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            shutdownMemcachedClientPool();
        }
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

}
