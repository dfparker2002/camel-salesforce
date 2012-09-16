/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.camel.component.salesforce.api;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PicklistEnumConverter implements Converter {
    private static final Logger LOG = LoggerFactory.getLogger(PicklistEnumConverter.class);
    private static final String FACTORY_METHOD = "fromValue";

    @SuppressWarnings("unchecked")
    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        Class<?> aClass = o.getClass();
        try {
            Method getterMethod = aClass.getMethod("value");
            writer.setValue((String) getterMethod.invoke(o));
        } catch (NoSuchMethodException e) {
            String msg = String.format("Exception writing pick list value %s of type %s: %s",
                o, o.getClass().getName(), e.getMessage());
            LOG.error (msg, e);
            throw new RuntimeException(msg, e);
        } catch (InvocationTargetException e) {
            String msg = String.format("Exception writing pick list value %s of type %s: %s",
                o, o.getClass().getName(), e.getMessage());
            LOG.error (msg, e);
            throw new RuntimeException(msg, e);
        } catch (IllegalAccessException e) {
            String msg = String.format("Exception writing pick list value %s of type %s: %s",
                o, o.getClass().getName(), e.getMessage());
            LOG.error (msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String value = reader.getValue();
        Class<?> requiredType = context.getRequiredType();
        try {
            Method factoryMethod = requiredType.getMethod(FACTORY_METHOD, String.class);
            // use factory method to create object
            return factoryMethod.invoke(null, value);
        } catch (Exception e) {
            String msg = String.format(
                "Exception reading pick list value %s of type %s: %s",
                value, context.getRequiredType().getName(), e.getMessage());
            LOG.error (msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class aClass) {
        try {
            return Enum.class.isAssignableFrom(aClass) &&
                aClass.getMethod(FACTORY_METHOD, String.class) != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

}
