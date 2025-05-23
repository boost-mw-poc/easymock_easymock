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

import static org.easymock.EasyMock.*;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author OFFIS, Tammo Freese
 */
class RecordStateInvalidDefaultThrowableTest {

    private IMethods mock;

    private class CheckedException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    @BeforeEach
    void setup() {
        mock = createMock(IMethods.class);
    }

    @Test
    void throwNull() {
        try {
            expect(mock.throwsNothing(false)).andStubThrow(null);
            Assertions.fail("NullPointerException expected");
        } catch (NullPointerException expected) {
            Assertions.assertEquals("null cannot be thrown", expected.getMessage());
        }

    }

    @Test
    void throwCheckedExceptionWhereNoCheckedExceptionIsThrown() {
        try {
            expect(mock.throwsNothing(false)).andStubThrow(new CheckedException());
            Assertions.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            Assertions.assertEquals("last method called on mock cannot throw " + this.getClass().getName()
                    + "$CheckedException", expected.getMessage());
        }
    }

    @Test
    void throwWrongCheckedException() throws IOException {
        try {
            expect(mock.throwsIOException(0)).andStubThrow(new CheckedException());
            Assertions.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            Assertions.assertEquals("last method called on mock cannot throw " + this.getClass().getName()
                    + "$CheckedException", expected.getMessage());
        }
    }
}
