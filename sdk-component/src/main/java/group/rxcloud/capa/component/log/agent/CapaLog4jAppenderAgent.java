/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package group.rxcloud.capa.component.log.agent;

/**
 * @Author gpeng
 * @Date 2021/11/17 14:16
 */

import group.rxcloud.capa.component.log.CapaLog4jAppender;
import group.rxcloud.capa.infrastructure.config.CapaProperties;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * The abstract log4j appender. Extend this and provide your specific impl.
 */
@Plugin(name = "CapaLog4jAppender", elementType = Appender.ELEMENT_TYPE, category = "Core")
public class CapaLog4jAppenderAgent extends AbstractAppender {

    /**
     * The log component type.
     */
    private static final String LOG_COMPONENT_TYPE = "log";
    /**
     * Capa log4j appender instance.
     */
    private static final CapaLog4jAppender logAppender;

    /**
     * Init the logbackAppender impl.
     */
    static {
        logAppender = buildCapaLog4jAppender();
    }

    /**
     * Instantiates a new Capa log4j appender.
     *
     * @param name             The name of the appender.
     * @param filter           The filter of the appender.
     * @param layout           The layout of the appender.
     * @param ignoreExceptions Whether to ignore exceptions.
     */
    public CapaLog4jAppenderAgent(String name,
                                  Filter filter,
                                  Layout<? extends Serializable> layout,
                                  boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    /**
     * Create a appender instance.
     *
     * @param name             The name of the appender.
     * @param filter           The filter of the appender.
     * @param layout           The layout of the appender.
     * @param ignoreExceptions Whether to ignore exceptions.
     * @return CapaLog4jAppender instance.
     */
    @PluginFactory
    public static CapaLog4jAppenderAgent createAppender(@PluginAttribute("name") String name,
                                                        @PluginElement("Filter") final Filter filter,
                                                        @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                        @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {
        return new CapaLog4jAppenderAgent(name, filter, layout, ignoreExceptions);
    }

    /**
     * Build a appender instance.
     *
     * @return CapaLog4jAppender instance.
     */
    public static CapaLog4jAppender buildCapaLog4jAppender() {
        // load spi capa Log4j appender impl
        try {
            Properties properties = CapaProperties.COMPONENT_PROPERTIES_SUPPLIER.apply(LOG_COMPONENT_TYPE);
            String capaLogAppenderClassPath = properties.getProperty(CapaLog4jAppender.class.getName());
            Class<? extends CapaLog4jAppender> aClass = (Class<? extends CapaLog4jAppender>) Class.forName(capaLogAppenderClassPath);
            Constructor<? extends CapaLog4jAppender> constructor = aClass.getConstructor();
            Object newInstance = constructor.newInstance();
            return (CapaLog4jAppender) newInstance;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("No Capa log4j appender supported.");
        }
    }

    @Override
    public void append(LogEvent event) {
        logAppender.appendLog(event);
    }


}
