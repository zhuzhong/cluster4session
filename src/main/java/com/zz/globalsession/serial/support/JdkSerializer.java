package com.zz.globalsession.serial.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zz.globalsession.serial.Serializer;

/**
 * @author sunff 2015年12月5日 下午4:43:18
 * @param <T>
 * @since 1.0.0
 */
public class JdkSerializer implements Serializer {

    private static final Log log = LogFactory.getLog(JdkSerializer.class);

    @Override
    public <T> byte[] serialize(T t) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(baos);
            oos.writeObject(t);
            return baos.toByteArray();
        } catch (IOException e) {

            log.error(e);
            e.printStackTrace();
            return null;
        }
        // return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] bytes) {

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream bis;
            bis = new ObjectInputStream(bais);
            return (T) bis.readObject();
        } catch (IOException e) {

            log.error(e);
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block

            log.error(e);
            e.printStackTrace();
            return null;
        }
        // return null;
    }

}
