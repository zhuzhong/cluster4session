package com.zz.globalsession.serial.support;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zz.globalsession.serial.Serializer;

public class JdkSerializerTest {

    private User user;
    private Serializer serializer;
    private byte[] serialized;

    @Before
    public void setUp() throws Exception {
        user = new User();
        user.setName("test");
        user.setAge(30);

        serializer = new JdkSerializer();
    }

    @After
    public void tearDown() throws Exception {
        user = null;
        serializer = null;
    }

    @Test
    public void testSerialize() {
        // fail("Not yet implemented");
        // serialized = serializer.serialize(user);
        // Assertt
    }

    @Test
    public void testDeserialize() {
        // fail("Not yet implemented");
        serialized = serializer.serialize(user);
        User u = serializer.deserialize(serialized);
        assertEquals(user, u);
    }

    private static class User implements Serializable {
        private String name;

        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + age;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            User other = (User) obj;
            if (age != other.age)
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

    }

}
