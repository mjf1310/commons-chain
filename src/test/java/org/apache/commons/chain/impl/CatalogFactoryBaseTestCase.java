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
package org.apache.commons.chain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>Test case for the <code>CatalogFactoryBase</code> class.</p>
 *
 * @author Craig R. McClanahan
 * @version $Revision$ $Date$
 */

public class CatalogFactoryBaseTestCase {


    // ---------------------------------------------------- Instance Variables


    /**
     * <p>The {@link CatalogFactory} instance under test.</p>
     */
    protected CatalogFactory factory = null;


    // -------------------------------------------------- Overall Test Methods


    /**
     * <p>Set up instance variables required by this test case.</p>
     */
    @Before
    public void setUp() {
        CatalogFactory.clear();
        factory = CatalogFactory.getInstance();
    }

    /**
     * <p>Tear down instance variables required by this test case.</p>
     */
    @After
    public void tearDown() {
        factory = null;
    }


    // ------------------------------------------------ Individual Test Methods


    /**
     * <p>Test a pristine instance of {@link CatalogFactory}.</p>
     */
    @Test
    public void testPristine() {

        assertNotNull(factory);
        assertNull(factory.getCatalog());
        assertNull(factory.getCatalog("foo"));
        assertEquals(0, getCatalogCount());

    }


    /**
     * <p>Test the default {@link Catalog} instance.</p>
     */
    @Test
    public void testDefaultCatalog() {

        Catalog catalog = new CatalogBase();
        factory.setCatalog(catalog);
        assertTrue(catalog == factory.getCatalog());
        assertEquals(0, getCatalogCount());

    }


    /**
     * <p>Test adding a specifically named {@link Catalog} instance.</p>
     */
    @Test
    public void testSpecificCatalog() {

        Catalog catalog = new CatalogBase();
        factory.setCatalog(catalog);
        catalog = new CatalogBase();
        factory.addCatalog("foo", catalog);
        assertTrue(catalog == factory.getCatalog("foo"));
        assertEquals(1, getCatalogCount());
        factory.addCatalog("foo", new CatalogBase());
        assertEquals(1, getCatalogCount());
        assertTrue(!(catalog == factory.getCatalog("foo")));
        CatalogFactory.clear();
        factory = CatalogFactory.getInstance();
        assertEquals(0, getCatalogCount());

    }


    /**
     * <p>Test <code>getCatalog()</code> method.</p>
     */
    @Test
    public void testCatalogIdentifier() {

        Catalog defaultCatalog = new CatalogBase();
        Command defaultFoo = new NonDelegatingCommand();
        defaultCatalog.addCommand("foo", defaultFoo);
        Command fallback = new NonDelegatingCommand();
        defaultCatalog.addCommand("noSuchCatalog:fallback", fallback);

        factory.setCatalog(defaultCatalog);

        Catalog specificCatalog = new CatalogBase();
        Command specificFoo = new NonDelegatingCommand();
        specificCatalog.addCommand("foo", specificFoo);
        factory.addCatalog("specific", specificCatalog);

        Command command = factory.getCommand("foo");
        assertSame(defaultFoo, command);

        command = factory.getCommand("specific:foo");
        assertSame(specificFoo, command);

        command = factory.getCommand("void");
        assertNull(command);

        command = factory.getCommand("foo:void");
        assertNull(command);

        command = factory.getCommand("specific:void");
        assertNull(command);

        command = factory.getCommand("noSuchCatalog:fallback");
        assertNull(command);

        try {
            command = factory.getCommand("multiple:delimiters:reserved");
            fail("A command ID with more than one delimiter should throw an IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            // expected behavior
        }

    }


    // ------------------------------------------------------- Support Methods


    /**
     * <p>Return the number of {@link Catalog}s defined in our
     * {@link CatalogFactory}.</p>
     */
    private int getCatalogCount() {

        Iterator names = factory.getNames();
        assertNotNull(names);
        int n = 0;
        while (names.hasNext()) {
            names.next();
            n++;
        }
        return n;

    }


}
