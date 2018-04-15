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
 * @description CorsFilterTest - test cases for CorsFilter
 * @package    ie.peternagy.alexa.top.api.common.filter
 * 
 */
package ie.peternagy.alexa.top.api.common.filter;

import com.google.common.base.Strings;
import ie.peternagy.alexa.top.api.cli.Environment;
import ie.peternagy.alexa.top.api.server.RestServer;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.jboss.resteasy.spi.CorsHeaders;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class CorsFilterTest{
    private static RestServer server = new RestServer();
    
    public CorsFilterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        server.start();
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
     * Get a default OPTIONS HttpURLConnection
     * @return
     * @throws MalformedURLException
     * @throws IOException 
     */
    protected static HttpURLConnection getOptionsHTTPConn() throws MalformedURLException, IOException{
        URL url = new URL(String.format("http://%s:%d/does-not-exist", Environment.getOrDefault("SERVER_HOST", "127.0.0.1"), Environment.getOrDefault("SERVER_PORT", 8888)));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("OPTIONS");
        con.setConnectTimeout(1000);
        con.setReadTimeout(1000);
        
        return con;
    }

    /**
     * Test of filter method, of class CorsFilter.
     */
    @Test
    public void testFilter() throws Exception {
        System.out.println("filter");
        
        HttpURLConnection con  = getOptionsHTTPConn();
        
        con.setRequestProperty(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization");
        con.setRequestProperty("Access-Control-Request-Method", "GET");
        
        con.setUseCaches(false);
        
        assertTrue(con.getResponseCode() == 200);
        StringBuilder sb = new StringBuilder();
        for(String h : con.getHeaderFields().keySet()){
            sb.append(h).append(',');
        }
        
        assertTrue(sb.toString().contains(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertTrue(con.getHeaderField(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS).contains("GET"));
        assertTrue(con.getHeaderField(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS).contains("authorization"));
    }
    
    /**
     * Test of filter method, of class CorsFilter.
     */
    @Test
    public void testFilterMissionHeader() throws Exception {
        System.out.println("filter - missing headers");
        
        URL url = new URL(String.format("http://%s:%d/does-not-exist", Environment.getOrDefault("SERVER_HOST", "127.0.0.1"), Environment.getOrDefault("SERVER_PORT", 8888)));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("OPTIONS");
        con.setConnectTimeout(1000);
        con.setReadTimeout(1000);
        
        assertTrue(con.getResponseCode() == 200);
        StringBuilder sb = new StringBuilder();
        for(String h : con.getHeaderFields().keySet()){
            sb.append(h).append(',');
        }
        
        assertTrue(sb.toString().contains("Access-Control-Allow-Origin"));
        assertTrue(!Strings.isNullOrEmpty(con.getHeaderField("Access-Control-Max-Age")));
        assertTrue(!Strings.isNullOrEmpty(con.getHeaderField("Access-Control-Allow-Headers")));
    }
    
}
