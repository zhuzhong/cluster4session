package com.zz.globalsession.store.support;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.zz.globalsession.serial.Serializer;
import com.zz.globalsession.store.SessionStore;

/**
 * @author sunff 2015年12月5日 下午3:00:06
 * @since 1.0.0
 */
public class RedisSessionStore implements SessionStore {

    private static Log       log = LogFactory.getLog(RedisSessionStore.class);

    private final JedisPool  jedisPool;
    private final Serializer serializer;

    public RedisSessionStore(JedisPool jedisPool,Serializer serializer){
        this.jedisPool = jedisPool;
        this.serializer=serializer;
    }

    private String getMethodCalls(Throwable t) {
        StackTraceElement e1 = t.getStackTrace()[1];
        StackTraceElement e2 = t.getStackTrace()[2];
        StackTraceElement e3 = t.getStackTrace()[3];
        return "(" + e1.getClassName() + "#" + e1.getMethodName() + " <- " + e2.getClassName() + "#"
               + e2.getMethodName() + " <- " + e3.getClassName() + "#" + e3.getMethodName() + ")";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Serializable> V get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            V value = (V) serializer.deserialize(jedis.get(key.getBytes("utf-8")));
            if (log.isDebugEnabled()) {
                Throwable t = new Throwable();
                String message = "___ GET [" + key + " -> " + value + "]";
                log.debug(message + getMethodCalls(t));
            }
            return value;

        } catch (Exception e) {
            log.debug("Failed to get value for " + key, e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    // 过期时间为秒
    @Override
    public <V extends Serializable> void set(String key, int expire, V value) {
        if (log.isDebugEnabled()) {
            Throwable t = new Throwable();
            String message = "$$$ SET (expire:" + expire + ") [" + key + " -> " + value + "]";
            log.debug(message + getMethodCalls(t));
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (value == null) {
                jedis.del(key.getBytes("utf-8"));
            } else {
                jedis.setex(key.getBytes("utf-8"), expire, serializer.serialize(value));
            }
        } catch (Exception e) {
            log.debug("Failed to set value for " + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public void remove(String key) {
        if (log.isDebugEnabled()) {
            Throwable t = new Throwable();
            String message = "*** DELETE: [" + key + "]";
            log.debug(message + getMethodCalls(t));
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key.getBytes("utf-8"));
        } catch (Exception e) {
            log.debug("Failed to delete value for " + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
