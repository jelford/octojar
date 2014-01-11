package com.jameselford.octojar;

import static com.jameselford.octojar.DynamicUtils.dynamicallyImplement;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class DynamicUtilsTest {

    private static interface NoMethodInterface {
    }

    private static interface OneMethodInterface {
        void one();
    }

    private static interface TwoMethodInterface {
        void one();
        void two();
    }

    private static interface Concatable {
        String concat(String to);
    }

    @Test
    public void anInstanceOfAnInterfaceCanBeDynamicallyCastToThatInterface() throws Exception {
        NoMethodInterface castObject = dynamicallyImplement(NoMethodInterface.class, new Object());

        assertThat(castObject, isA(NoMethodInterface.class));
    }

    @Test
    public void canDynamicallyCastToAnInterfaceWithASubsetOfMethod() throws Exception {
        TwoMethodInterface instance = new TwoMethodInterface() {

            @Override
            public void two() {}

            @Override
            public void one() {}
        };

        OneMethodInterface castObject = dynamicallyImplement(OneMethodInterface.class, instance);

        assertThat(castObject, isA(OneMethodInterface.class));
    }

    @Test(expected = ClassCastException.class)
    public void attemptingToCastToAnInterfaceWithMoreMethodsThanImplementedThrowsAClassCastException() {
        dynamicallyImplement(OneMethodInterface.class, new Object());
    }

    @Test
    public void canUseNewlyCastMethods() {
        Concatable castObject = dynamicallyImplement(Concatable.class, "hello");
        String result = castObject.concat(" world");

        assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("hello world")));
    }
}
