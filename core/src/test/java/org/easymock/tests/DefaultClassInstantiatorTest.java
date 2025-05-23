/*
 * Copyright 2001-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.easymock.tests;

import java.io.Serializable;

import org.easymock.internal.ClassInstantiatorFactory;
import org.easymock.internal.DefaultClassInstantiator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class testing the default instantiator. I'm cheating a little here since
 * I'm not unit testing directly the class. The reason I'm doing this is that I
 * want to make sure it works well with the ByteBuddy class and not the actual
 * mocked class.
 *
 * @author Henri Tremblay
 */
class DefaultClassInstantiatorTest {

    public static class PrimitiveParamClass {
        public PrimitiveParamClass(int i) {
        }
    }

    public static class FinalParamClass {
        public FinalParamClass(String i) {
        }
    }

    public static class ProtectedConstructorClass {
        protected ProtectedConstructorClass() {
        }
    }

    public static class ProtectedWithPrimitiveConstructorClass {
        protected ProtectedWithPrimitiveConstructorClass(int i) {
        }
    }

    public static class ParamClass {
        public ParamClass(FinalParamClass f) {
        }
    }

    public static class ObjectClass {
        public ObjectClass(Object c) {
        }
    }

    public static class ObjectParamClass {
        public ObjectParamClass(ParamClass c) {
        }
    }

    public static class PrivateConstructorClass {
        private PrivateConstructorClass() {
        }
    }

    public static class ConstructorWithCodeClass {
        public ConstructorWithCodeClass() {
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("serial")
    public static class SerializableClass implements Serializable {
        public SerializableClass() {
            throw new RuntimeException();
        }
    }

    public static class SerializableWithUIDClass implements Serializable {

        private static final long serialVersionUID = -1;

        public SerializableWithUIDClass() {
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("serial")
    public static class BadlyDoneSerializableClass implements Serializable {

        private final long serialVersionUID = 2; // not static

        public BadlyDoneSerializableClass() {
            throw new RuntimeException();
        }
    }

    private final String vendor = null;

    @BeforeAll
    static void setUp() {
        // Set the default instantiator
        ClassInstantiatorFactory.setInstantiator(new DefaultClassInstantiator());
    }

    @AfterAll
    static void tearDown() {
        // Set the value back to be clean
        ClassInstantiatorFactory.setDefaultInstantiator();
    }

    @Test
    void emptyConstructor() {
        checkInstantiation(DefaultClassInstantiator.class);
    }

    @Test
    void primitiveType() {
        checkInstantiation(PrimitiveParamClass.class);
    }

    // Fails on Java 7 for a currently unknown reason
    @Test
    @Disabled
    void finalType() {
        checkInstantiation(FinalParamClass.class);
    }

    @Test
    void protectedConstructor() {
        checkInstantiation(ProtectedConstructorClass.class);
    }

    @Test
    void protectedWithPrimitiveConstructor() {
        checkInstantiation(ProtectedWithPrimitiveConstructorClass.class);
    }

    @Test
    void object() {
        checkInstantiation(ObjectClass.class);
    }

    // Fails on Java 7 for a currently unknown reason
    @Test
    @Disabled
    void objectParamRecursion() {
        checkInstantiation(ObjectParamClass.class);
    }

    @Test
    void constructorWithCodeLimitation() {
        Exception e = assertThrows(Exception.class, () -> createMock(ConstructorWithCodeClass.class));
        assertEquals("Failed to mock class org.easymock.tests.DefaultClassInstantiatorTest$ConstructorWithCodeClass with provider DefaultClassInfoProvider", e.getMessage());
    }

    @Test
    void privateConstructorLimitation() {
        Exception e = assertThrows(Exception.class, () -> createMock(PrivateConstructorClass.class));
        assertEquals("Failed to mock class org.easymock.tests.DefaultClassInstantiatorTest$PrivateConstructorClass with provider DefaultClassInfoProvider", e.getMessage());
        assertEquals("No visible constructors in class org.easymock.tests.DefaultClassInstantiatorTest$PrivateConstructorClass", e.getCause().getMessage());
    }

    @Test
    void privateConstructor() {
        DefaultClassInstantiator instantiator = new DefaultClassInstantiator();
        Exception e = assertThrows(Exception.class, () -> instantiator.newInstance(PrivateConstructorClass.class));
        assertEquals("No visible constructors in class org.easymock.tests.DefaultClassInstantiatorTest$PrivateConstructorClass", e.getMessage());
    }

    @Test
    void newInstance() {
        checkInstantiation(DefaultClassInstantiator.class);
    }

    @Test
    @Disabled("requires --add-opens java.base/java.io=ALL-UNNAMED with Java 9+")
    void serializable() {
        checkInstantiation(SerializableClass.class);
    }

    @Test
    @Disabled("requires --add-opens java.base/java.io=ALL-UNNAMED with Java 9+")
    void badSerializable() throws Exception {
        DefaultClassInstantiator instantiator = new DefaultClassInstantiator();
        assertInstanceOf(BadlyDoneSerializableClass.class, instantiator.newInstance(BadlyDoneSerializableClass.class));
    }

    @Test
    void serializableWithUID() throws Exception {
        DefaultClassInstantiator instantiator = new DefaultClassInstantiator();
        assertInstanceOf(SerializableWithUIDClass.class, instantiator.newInstance(SerializableWithUIDClass.class));
    }

    private <T> void checkInstantiation(Class<T> clazz) {
        T mock = createMock(clazz);
        assertTrue(clazz.isAssignableFrom(mock.getClass()));
    }
}
