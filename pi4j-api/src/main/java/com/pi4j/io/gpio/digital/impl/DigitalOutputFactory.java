package com.pi4j.io.gpio.digital.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (API)
 * FILENAME      :  DigitalOutputFactory.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
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
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.exception.NotInitializedException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputBuilder;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProvider;
import com.pi4j.provider.exception.ProviderException;
import com.pi4j.registry.exception.RegistryException;

/**
 * Analog Output Factory - it returns instances of {@link DigitalOutput} interface.
 *
 * @author Robert Savage (<a
 *         href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 */
public class DigitalOutputFactory {

    // private constructor
    private DigitalOutputFactory() {
        // forbid object construction
    }

    public static boolean exists(Context context, String id) throws ProviderException, NotInitializedException {
        return context.registry().exists(id, DigitalOutput.class);
    }

    public static <T extends DigitalOutput> T get(Context context, String id) throws ProviderException, NotInitializedException, RegistryException {
        return (T)context.registry().get(id, DigitalOutput.class);
    }

    public static DigitalOutputBuilder builder() throws ProviderException {
        return new DefaultDigitalOutputBuilder();
    }

    public static <T extends DigitalOutput> T  create(Context context, DigitalOutputConfig config) throws NotInitializedException, ProviderException, RegistryException {
        return (T)context.registry().create(config, DigitalOutput.class);
    }

    public static <T extends DigitalOutput> T  create(Context context, DigitalOutputProvider provider, DigitalOutputConfig config) throws NotInitializedException, ProviderException, RegistryException {
        return (T)context.registry().create(provider, config, DigitalOutput.class);
    }

    public static  <T extends DigitalOutput> T create(Context context, String providerId, DigitalOutputConfig config) throws ProviderException, NotInitializedException, RegistryException {
        return (T)context.registry().create(providerId, config, DigitalOutput.class);
    }

    public static <T extends DigitalOutput> T create(Context context, DigitalOutputConfig config, Class<T> clazz) throws ProviderException, NotInitializedException, RegistryException {
        return (T)context.registry().create(config, clazz);
    }

    public static <T extends DigitalOutput> T create(Context context, String providerId, DigitalOutputConfig config, Class<T> clazz) throws ProviderException, NotInitializedException, RegistryException {
        return (T)context.registry().create(providerId, config, clazz);
    }

    public static <T extends DigitalOutput> T create(Context context, DigitalOutputProvider provider, DigitalOutputConfig config, Class<T> clazz) throws ProviderException, NotInitializedException, RegistryException {
        return (T)context.registry().create(provider, config, clazz);
    }
}
