package com.jameselford.octojar;

import static com.google.common.collect.Maps.newHashMap;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.google.common.reflect.AbstractInvocationHandler;

public final class DynamicUtils {

    @SuppressWarnings("unchecked")
    public static <T> T dynamicallyImplement(
            final Class<T> interfaceToImplement, final Object impl) {

        final Class<?> actualClass = impl.getClass();

        final Map<Method, Method> implementations = newHashMap();

        for (final Method m : interfaceToImplement.getMethods()) {
            try {
                implementations.put(m, actualClass.getMethod(m.getName(), m.getParameterTypes()));

            } catch (final NoSuchMethodException e) {
                throw new ClassCastException("Cannot dynamically cast "
                        + classloaderQualifiedNameFor(actualClass) + " to "
                        + classloaderQualifiedNameFor(interfaceToImplement)
                        + "; no such method: " + e.getMessage());
            }
        }

        return (T) Proxy.newProxyInstance(DynamicUtils.class.getClassLoader(), new Class[] { interfaceToImplement }, new AbstractInvocationHandler() {

            @Override
            protected Object handleInvocation(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                return implementations.get(method).invoke(impl, args);
            }
        });

    }

    private static String classloaderQualifiedNameFor(final Class<?> clazz) {
        return "[" + clazz.getClassLoader() + "->" + clazz.getCanonicalName() + "]";
    }
}
