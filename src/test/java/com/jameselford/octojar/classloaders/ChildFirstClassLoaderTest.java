package com.jameselford.octojar.classloaders;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;
import com.jameselford.octojar.DynamicUtils;

public class ChildFirstClassLoaderTest {

    private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
    private static final String NEWLY_LOADED_CLASS_NAME = "MyNewClass";

    private static final ClassLoader NEVER_SUCCEED_CLASS_LOADER = new ClassLoader() {
        @Override
        public Class<?> loadClass(final String name) throws ClassNotFoundException {
            throw new ClassNotFoundException();
        }
    };

    public static final class DynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(final String name, final byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    @Test
    public void loadsSystemClassesIfNotFoundInChildClassLoader() throws Exception {
        Class<?> stringClass = new ChildFirstClassLoader(NEVER_SUCCEED_CLASS_LOADER).loadClass("java.lang.String");
        String s = (String) stringClass.newInstance();
        assertThat(s.concat("hello"), is(equalTo("hello")));
    }

    @Test
    public void delegatesToChildClassLoaderFirst() throws Exception {
        ClassLoader child = mock(ClassLoader.class);
        ChildFirstClassLoader classLoader = new ChildFirstClassLoader(child);

        classLoader.loadClass(JAVA_LANG_INTEGER);

        Mockito.verify(child).loadClass(JAVA_LANG_INTEGER);
    }

    @Test(expected = ClassNotFoundException.class)
    public void loadingDoesntAddClassFromChildClassloaderToTheSystemClassloader() throws ClassNotFoundException {
        this.getClass().getClassLoader().loadClass(NEWLY_LOADED_CLASS_NAME);
    }

    @Test(expected = ClassCastException.class)
    public void whenInterfaceIsRedefinedInChildClassLoaderThenThatVersionIsUsedForLoadingNewClasses() throws Exception {
        Class<?> implementation = new ChildFirstClassLoader(classLoaderWithAlternativeImplemention()).loadClass(OneMethodInterfaceImpl.class.getName());
        assertThat(implementation.getInterfaces()[0].getName(), is(equalTo(OneMethodInterface.class.getName())));
        OneMethodInterface omi = (OneMethodInterface) implementation.newInstance();
        omi.one();
    }

    @Test
    public void canDynamicallyCastInstantiationOfNewlyCreatedClassToExistingInterface() throws Exception {
        Class<?> implementation = new ChildFirstClassLoader(classLoaderWithAlternativeImplemention()).loadClass(OneMethodInterfaceImpl.class.getName());
        OneMethodInterface omi = DynamicUtils.dynamicallyImplement(OneMethodInterface.class, implementation.newInstance());
        omi.one();
    }

    private ClassLoader classLoaderWithAlternativeImplemention() throws Exception {

        byte[] interfaceBytes = bytesFromClass(OneMethodInterface.class);
        byte[] implementationBytes = bytesFromClass(OneMethodInterfaceImpl.class);

        DynamicClassLoader dcl = new DynamicClassLoader();
        dcl.defineClass(null, interfaceBytes);
        dcl.defineClass(null, implementationBytes);

        return dcl;
    }

    private byte[] bytesFromClass(final Class<?> clazz) throws IOException {
        try (InputStream is = OneMethodInterface.class.getClassLoader().getResourceAsStream(clazz.getName().replace(".", "/") + ".class")) {
            return ByteStreams.toByteArray(is);

        }
    }

}
