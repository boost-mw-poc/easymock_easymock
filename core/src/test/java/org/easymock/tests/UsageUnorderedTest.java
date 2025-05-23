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

import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author OFFIS, Tammo Freese
 */
class UsageUnorderedTest {

    public interface Interface {
        void method(int number);
    }

    @Test
    void message() {
        Interface mock = createMock(Interface.class);

        mock.method(anyInt());
        expectLastCall().once();
        mock.method(42);
        mock.method(anyInt());
        expectLastCall().times(2);

        replay(mock);

        mock.method(6);
        mock.method(7);
        mock.method(1);
        mock.method(42);

        AssertionError expected = assertThrows(AssertionError.class, () -> mock.method(42));
        assertEquals(
            "\n  Unexpected method call EasyMock for interface org.easymock.tests.UsageUnorderedTest$Interface -> Interface.method(42 (int)). Possible matches are marked with (+1):"
                    + "\n    EasyMock for interface org.easymock.tests.UsageUnorderedTest$Interface -> Interface.method(<any>): expected: 3, actual: 3 (+1)"
                    + "\n    EasyMock for interface org.easymock.tests.UsageUnorderedTest$Interface -> Interface.method(42 (int)): expected: 1, actual: 1 (+1)",
            expected.getMessage());
    }
}
