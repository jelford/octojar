package com.jameselford.octojar.classloaders;

import com.google.common.io.ByteStreams;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayClassLoaderTest {
    @Test
    public void canLoadClassFromBytes() throws Exception {
        byte[] bytes = byteBytesForThisClass();
        new ByteArrayClassLoader().defineClass(ByteArrayClassLoaderTest.class.getName(), bytes);
    }

    private byte[] byteBytesForThisClass() throws IOException {
        Class<?> thisClass = ByteArrayClassLoaderTest.class;
        try (InputStream resourceAsStream = thisClass.getResourceAsStream("ByteArrayClassLoaderTest.class")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ByteStreams.copy(resourceAsStream, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();


        }
    }
}
