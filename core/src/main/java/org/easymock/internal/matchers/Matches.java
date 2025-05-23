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
package org.easymock.internal.matchers;

import org.easymock.IArgumentMatcher;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Matches if the argument is a string matching a given regex.
 *
 * @author OFFIS, Tammo Freese
 */
public class Matches implements IArgumentMatcher, Serializable {

    private static final long serialVersionUID = -6657694947057597484L;

    private final Pattern regex;

    public Matches(String regex) {
        this.regex = Pattern.compile(regex);
    }

    @Override
    public boolean matches(Object actual) {
        return (actual instanceof String) && regex.matcher((String) actual).matches();
    }

    @Override
    public void appendTo(StringBuffer buffer) {
        buffer.append("matches(\"").append(regex.pattern().replaceAll("\\\\", "\\\\\\\\")).append("\")");
    }
}
