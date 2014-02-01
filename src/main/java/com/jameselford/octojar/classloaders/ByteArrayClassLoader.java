package com.jameselford.octojar.classloaders;

public class ByteArrayClassLoader extends ClassLoader {
    public Class defineClass(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}
