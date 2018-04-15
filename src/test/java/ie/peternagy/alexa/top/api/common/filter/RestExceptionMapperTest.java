/** 
 * Copyright (C) 2018 Peter Nagy (https://peternagy.ie)
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
 * =========================================================
 * @author     Peter Nagy <pnagy@alison.com>
 * @since      03, 2018
 * @version    0.1
 * @description RestExceptionMapperTest - test cases for RestExceptionMapper
 * @package    ie.peternagy.alexa.top.api.common.filter
 * 
 */
package ie.peternagy.alexa.top.api.common.filter;

import ie.peternagy.alexa.top.api.cli.Environment;
import ie.peternagy.alexa.top.api.common.exception.RestException;
import ie.peternagy.alexa.top.api.server.RestServer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class RestExceptionMapperTest {
    private static RestServer server = new RestServer();
    
    public RestExceptionMapperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        server.start();
        Thread.sleep(100);
    }
    
    @AfterClass
    public static void tearDownClass() {
        server.stop();
    }
    
    @Before
    public void setUp() {
        System.out.println("Running tests on: " + CorsFilter.class.getName());
    }
    
    /**
     * Get a connection to the local server with 404 response 
     * 
     * @return
     * @throws MalformedURLException
     * @throws IOException 
     */
    protected static HttpURLConnection get404LocalConnection() throws MalformedURLException, IOException{
        URL url = new URL(String.format("http://%s:%d/does-not-exist", Environment.getOrDefault("SERVER_HOST", "127.0.0.1"), Environment.getOrDefault("SERVER_PORT", 8888)));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(1000);
        con.setReadTimeout(1000);
        
        return con;
    }

    /**
     * Test of toResponse method, of class RestExceptionMapper.
     */
    @Test
    public void testToResponse404() throws IOException {
        System.out.println("toResponse - NotFoundException");
        HttpURLConnection con = get404LocalConnection();
        
        
        Object body = new Object();
        Exception e = null;
        try{
            body = con.getContent();
            
        }catch(FileNotFoundException ex){
            e = ex;
            body = IOUtils.toString(con.getErrorStream(), "UTF-8");
        }
        
        assertEquals(con.getResponseCode(), 404);
        assertTrue(e != null);
        assertTrue(body.toString().contains("src"));
        assertTrue(body.toString().contains("desc"));
        assertTrue(body.toString().contains("code"));
    }
    
    /**
     * Test of toResponse method, of class RestExceptionMapper.
     */
    @Test
    public void testToResponseRestException() {
        System.out.println("toResponse - RestException");
        Throwable e = new RestException("api/test", "test", 500, 500);
        RestExceptionMapper instance = new RestExceptionMapper();
        Response result = instance.toResponse(e);
        
        Object body = result.getEntity();
        
        assertEquals(result.getStatus(), 500);
        assertTrue(body.toString().contains("src"));
        assertTrue(body.toString().contains("desc"));
        assertTrue(body.toString().contains("code"));
        
        assertTrue(body.toString().contains("api/test"));
    }
    
    
    /**
     * Test of toResponse method, of class RestExceptionMapper.
     */
    @Test
    public void testToResponseException() {
        System.out.println("toResponse - Exception");
        
        Throwable e = new Exception("api/test/exception");
        RestExceptionMapper instance = new RestExceptionMapper();
        Response result = instance.toResponse(e);
        
        Object body = result.getEntity();
        
        assertEquals(result.getStatus(), 500);
        assertTrue(body.toString().contains("src"));
        assertTrue(body.toString().contains("desc"));
        assertTrue(body.toString().contains("code"));
        
        assertTrue(body.toString().contains("Internal server error"));
    }
}
