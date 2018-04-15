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
 * @description RestServerTest - test cases for RestServer
 * @package    ie.peternagy.alexa.top.api.server
 * 
 */
package ie.peternagy.alexa.top.api.server;

import ie.peternagy.alexa.top.api.cli.Environment;
import io.undertow.Undertow;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RestServerTest {
    
    public RestServerTest() {
    }
    
    @Before
    public void setUp() {
        System.out.println("Running tests on: " + RestServer.class.getName());
    }
    
    /**
     * Get a connection to the local server with 404 response 
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
     * Test of start method, of class RestServer.
     */
    @Test
    public void testStart() throws Exception {
        System.out.println("start");
        
        RestServer instance = new RestServer();
        Exception t = null;
        
        try{
            instance.start();
        }catch(Exception tt){
            t = tt;
        }
        
        assertTrue(t == null);
        
        Thread.sleep(1000);
        
        HttpURLConnection con = get404LocalConnection();
        
        assertTrue(404 == con.getResponseCode());
        
        instance.stop();
    }

    /**
     * Test of stop method, of class RestServer.
     */
    @Test
    public void testStop() throws InterruptedException, IOException {
        System.out.println("stop");
        RestServer instance = new RestServer();
        Exception t = null;
        
        try{
            instance.start();
        }catch(Exception tt){
            System.out.println(tt.getMessage());
            t = tt;
        }
        
        assertTrue(t == null);
        
        instance.stop();
        
        Thread.sleep(1000);
        
        Exception e = null;
        try{
            HttpURLConnection con = get404LocalConnection();
            int code = con.getResponseCode();
        }catch(ConnectException ex){
            e = ex;
        }
        
        assertTrue(e != null && e instanceof ConnectException);
    }

    /**
     * Test of startInternalServices method, of class RestServer.
     */
    @Test
    public void testStartInternalServices() throws InterruptedException {
        System.out.println("startInternalServices");
        RestServer instance = new RestServer();
        instance.startInternalServices();
        
        Thread.sleep(1000);
        
        assertTrue(instance.isSchedulerRunning());
        
        instance.stop();
    }

    /**
     * Test of stopInternalServices method, of class RestServer.
     */
    @Test
    public void testStopInternalServices() throws InterruptedException {
        System.out.println("stopInternalServices");
        RestServer instance = new RestServer();
        assertFalse(instance.isSchedulerRunning());
        
        instance.startInternalServices();
        Thread.sleep(1000);
        
        assertTrue(instance.isSchedulerRunning());
        
        instance.stopInternalServices();
        Thread.sleep(100);
        
        assertFalse(instance.isSchedulerRunning());
    }

    /**
     * Test of buildServer method, of class RestServer.
     */
    @Test
    public void testBuildServer() throws Exception {
        System.out.println("buildServer");
        RestServer instance = new RestServer();
        Exception e = null;
        Undertow.Builder result = null;
        try{
            result = instance.buildServer();
        }catch(Exception ex){
            e = ex;
            Logger.getLogger(RestServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertTrue(null != result);
        assertTrue(null == e);
    }
    
}
