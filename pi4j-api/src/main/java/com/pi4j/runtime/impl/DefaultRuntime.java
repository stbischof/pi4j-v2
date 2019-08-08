package com.pi4j.runtime.impl;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (API)
 * FILENAME      :  DefaultRuntime.java
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

import com.pi4j.annotation.AnnotationEngine;
import com.pi4j.annotation.exception.AnnotationException;
import com.pi4j.annotation.impl.DefaultAnnotationEngine;
import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.Pi4JException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.extension.impl.DefaultPluginService;
import com.pi4j.extension.impl.PluginStore;
import com.pi4j.platform.Platform;
import com.pi4j.platform.impl.DefaultRuntimePlatforms;
import com.pi4j.platform.impl.RuntimePlatforms;
import com.pi4j.provider.Provider;
import com.pi4j.provider.impl.DefaultRuntimeProviders;
import com.pi4j.provider.impl.RuntimeProviders;
import com.pi4j.registry.impl.DefaultRuntimeRegistry;
import com.pi4j.registry.impl.RuntimeRegistry;
import com.pi4j.runtime.Runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public class DefaultRuntime implements Runtime {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Context context = null;
    private AnnotationEngine annotationEngine = null;
    private RuntimeRegistry registry = null;
    private RuntimeProviders providers = null;
    private RuntimePlatforms platforms = null;

    public static Runtime newInstance(Context context) throws Pi4JException {
        return new DefaultRuntime(context);
    }

    // private constructor
    private DefaultRuntime(Context context) throws Pi4JException {

        // set local references
        this.context = context;
        this.annotationEngine = DefaultAnnotationEngine.newInstance(context);
        this.registry = DefaultRuntimeRegistry.newInstance(this);
        this.providers = DefaultRuntimeProviders.newInstance(this);
        this.platforms = DefaultRuntimePlatforms.newInstance(this);

        logger.debug("Pi4J runtime context successfully created & initialized.'");
    }

    @Override
    public Context context() { return this.context; }

    @Override
    public RuntimeRegistry registry() { return this.registry; }

    @Override
    public RuntimeProviders providers() {
        return this.providers;
    }

    @Override
    public RuntimePlatforms platforms() {
        return this.platforms;
    }

    @Override
    public Runtime inject(Object... objects) throws AnnotationException {
        annotationEngine.inject(objects);
        return this;
    }

    @Override
    public Runtime shutdown() throws ShutdownException {
        logger.trace("invoked 'shutdown();'");
        try {
             // shutdown all providers
            this.providers.shutdown();

            // shutdown platforms
            this.platforms.shutdown();

            // remove all I/O instances
            this.registry.shutdown();

        } catch (Exception e) {
            logger.error("failed to 'shutdown(); '", e);
            throw new ShutdownException(e);
        }

        logger.debug("Pi4J context/runtime successfully shutdown.'");

        return this;
    }

    @Override
    public Runtime initialize() throws InitializeException {
        logger.trace("invoked 'initialize();'");
        try {
            // container sets for providers and platforms to load
            Set<Provider> providers = Collections.synchronizedSet(new HashSet<>());
            Set<Platform> platforms = Collections.synchronizedSet(new HashSet<>());

            // copy all configured platforms and providers defined in the context configuration
            providers.addAll(context().config().getProviders());
            platforms.addAll(context().config().getPlatforms());

            // only attempt to load platforms and providers from the classpath if an auto detect option is enabled
            if(context.config().autoDetectPlatforms() || context.config().autoDetectProviders()) {

                // detect available Pi4J Plugins by scanning the classpath looking for plugin instances
                var plugins = ServiceLoader.load(Plugin.class);
                for (var plugin : plugins) {
                    if (plugin != null) {
                        logger.trace("detected plugin: [{}] in classpath; calling 'initialize()'",
                                plugin.getClass().getName());
                        try {
                            PluginStore store = new PluginStore();
                            plugin.initialize(DefaultPluginService.newInstance(this.context(), store));

                            // if auto-detect providers is enabled,
                            // then add any detected providers to the collection to load
                            if(context.config().autoDetectProviders())
                                providers.addAll(store.providers);

                            // if auto-detect platforms is enabled,
                            // then add any detected platforms to the collection to load
                            if(context.config().autoDetectPlatforms())
                                platforms.addAll(store.platforms);

                        } catch (Exception ex) {
                            // unable to initialize this provider instance
                            logger.error("unable to 'initialize()' plugin: [{}]; {}",
                                    plugin.getClass().getName(), ex.getMessage());
                            continue;
                        }
                    }
                }
            }

            // initialize I/O registry
            this.registry.initialize();

            // initialize all providers
            this.providers.initialize(providers);

            // initialize all platforms
            this.platforms.initialize(platforms);

        } catch (Exception e) {
            logger.error("failed to 'initialize(); '", e);
            throw new InitializeException(e);
        }

        logger.debug("Pi4J context/runtime successfully initialized.'");

        return this;
    }
}