/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cglib.proxy;

import org.springframework.cglib.core.ClassGenerator;
import org.springframework.cglib.core.GeneratorStrategy;

/**
 * Intercepts type creation of {@link org.springframework.cglib.proxy.Enhancer}.
 *
 * @author Shu Tadaka
 */
@SuppressWarnings("rawtypes")
public class InterceptableEnhancer extends Enhancer {

    private Class superClass = null;

    private Class[] interfaces = null;

    private byte[] generatedClassBytes = null;

    private static InterceptableEnhancerCallback CALLBACK = null;

    public static void setCallback(InterceptableEnhancerCallback callback) {
        CALLBACK = callback;
    }

    @Override
    public void setSuperclass(Class superclass) {
        super.setSuperclass(superclass);
        this.superClass = superclass;
    }

    @Override
    public void setInterfaces(Class[] interfaces) {
        super.setInterfaces(interfaces);
        this.interfaces = interfaces;
    }

    @Override
    public void setStrategy(GeneratorStrategy strategy) {
        super.setStrategy(new GeneratorStrategyProxy(strategy));
    }

    @Override
    public Object create() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public Object create(Class[] argumentTypes, Object[] arguments) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public synchronized Class createClass() {
        //
        if (!(getStrategy() instanceof GeneratorStrategyProxy)) {
            setStrategy(new GeneratorStrategyProxy(getStrategy()));
        }

        //
        Class result = super.createClass();
        if (CALLBACK != null) {
            CALLBACK.onClassCreated(this.superClass, this.interfaces, result, this.generatedClassBytes);
        }

        return result;
    }

    private class GeneratorStrategyProxy implements GeneratorStrategy {

        public GeneratorStrategyProxy(GeneratorStrategy actualGeneratorStrategy) {
            this.actualGeneratorStrategy = actualGeneratorStrategy;
        }

        @Override
        public byte[] generate(ClassGenerator classGenerator) throws Exception {
            byte[] result = this.actualGeneratorStrategy.generate(classGenerator);
            InterceptableEnhancer.this.generatedClassBytes = result;
            return result;
        }

        private final GeneratorStrategy actualGeneratorStrategy;

    }

}
