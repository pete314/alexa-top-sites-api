/**
 * Copyright (C) 2018 Peter Nagy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ======================================================================
 *
 * @author Peter Nagy - https://peternagy.ie
 * @since March 2018
 * @version 0.1
 * @description PaginatedRequestTest - test cases on PaginatedRequest
 * @package ie.peternagy.alexa.top.api.common.request
 */
package ie.peternagy.alexa.top.api.common.request;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class PaginatedRequestTest {

    private static final MultivaluedMap<String, String> correctParams = new MultivaluedHashMap<>();
    private static final MultivaluedMap<String, String> inCorrectParams = new MultivaluedHashMap<>();

    public PaginatedRequestTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        correctParams.addAll("size", Arrays.asList("31", "32"));
        correctParams.addAll("page", Arrays.asList("1", "2"));

        inCorrectParams.addAll("quasda", Arrays.asList("this is a test", "this is a test 2"));
        inCorrectParams.addAll("size", Arrays.asList("-1", "-2"));
        inCorrectParams.addAll("page", Arrays.asList("NoWay", "IsThisANumber"));
    }


    @Before
    public void setUp() {
        System.out.println("Running tests on: " + PaginatedRequestTest.class.getName());
    }

    /**
     * Test of addError method, of class PaginatedRequest.
     */
    @Test
    public void testAddError() {
        System.out.println("addError");

        String param = "test";
        String error = "test";
        PaginatedRequest instance = new PaginatedRequest(null, inCorrectParams) {
        };

        assertTrue(!instance.getErrors().isEmpty());
    }

    /**
     * Test of isValid method, of class PaginatedRequest.
     */
    @Test
    public void testIsValid() {
        System.out.println("isValid");

        PaginatedRequest instance = new PaginatedRequest(null, correctParams) {};

        boolean expResult = true;
        boolean result = instance.isValid();
        assertEquals(expResult, result);
        
        //Inverse test
        instance = new PaginatedRequest(null, inCorrectParams) {};
        
        assertEquals(false, instance.isValid());
    }

    /**
     * Test of getErrors method, of class PaginatedRequest.
     */
    @Test
    public void testGetErrors() {
        System.out.println("getErrors");
        
        PaginatedRequest instance = new PaginatedRequest(null, inCorrectParams) {};
        
        assertTrue(!instance.getErrors().isEmpty());
    }

    /**
     * Test of getALLOWED_FIELDS method, of class PaginatedRequest.
     */
    @Test
    public void testGetALLOWED_FIELDS() {
        System.out.println("getALLOWED_FIELDS");
        
        PaginatedRequest instance = new PaginatedRequest(null, correctParams) {};
        Set<String> expResult = new HashSet<>(Arrays.asList("size", "page"));
        
        Set<String> result = instance.getALLOWED_FIELDS();
        
        assertTrue(null != result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSize method, of class PaginatedRequest.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        
        PaginatedRequest instance = new PaginatedRequest(null, correctParams) {};
        int expResult = 31;
        int result = instance.getSize();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getPage method, of class PaginatedRequest.
     */
    @Test
    public void testGetPage() {
        System.out.println("getPage");
        
        PaginatedRequest instance = new PaginatedRequest(null, correctParams) {};
        int expResult = 1;
        int result = instance.getPage();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class PaginatedRequest.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        
        PaginatedRequest instance1 = new PaginatedRequest(null, correctParams) {};
        PaginatedRequest instance2 = new PaginatedRequest(null, correctParams) {};
        
        int hashCode1 = instance1.hashCode();
        int hashCode2 = instance2.hashCode();
        
        assertEquals(hashCode1, hashCode2);
        
        //Inverse test
        instance1 = new PaginatedRequest(null, inCorrectParams) {};
        hashCode1 = instance1.hashCode();
        
        assertNotEquals(hashCode1, hashCode2);
    }

    /**
     * Test of equals method, of class PaginatedRequest.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        PaginatedRequest instance1 = new PaginatedRequestImpl();
        PaginatedRequest instance2 = new PaginatedRequestImpl();
        
        assertEquals(instance1, instance2);
        
        //Inverse test
        instance1 = new PaginatedRequest(null, inCorrectParams) {};
        
        assertNotEquals(instance2, instance1);
    }

    public class PaginatedRequestImpl extends PaginatedRequest {

        
        public PaginatedRequestImpl() {
            super(null, correctParams);
        }
    }

}
