package com.jameselford.octojar.classloaders;


public class ChildFirstClassLoader extends ClassLoader {

    private final ClassLoader childClassLoader;

    public ChildFirstClassLoader(final ClassLoader childClassLoader) {
        this.childClassLoader = childClassLoader;
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve)
            throws ClassNotFoundException {

        try {
            return childClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            return super.loadClass(name, resolve);
        }
    }




}
