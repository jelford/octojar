package com.jameselford.octojar.classloaders;

import java.net.URL;
import java.net.URLClassLoader;

public class UrlFirstClassLoader extends ClassLoader {

    private final URLClassLoader pfocl;

    public UrlFirstClassLoader(final URL[] urls) {
        pfocl = new URLClassLoader(urls);
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve)
            throws ClassNotFoundException {

        try {
            return pfocl.loadClass(name);
        } catch (ClassNotFoundException e) {
            return super.loadClass(name, resolve);
        }
    }




}
