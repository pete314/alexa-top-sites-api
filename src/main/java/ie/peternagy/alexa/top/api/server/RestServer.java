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
 * @description RestServer - rest server object
 * @package ie.peternagy.alexa.top.api.server
 */
package ie.peternagy.alexa.top.api.server;

import com.google.common.base.Strings;
import ie.peternagy.alexa.top.api.cli.Environment;
import ie.peternagy.alexa.top.api.service.TopSitesUpdater;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

public class RestServer {
    private UndertowJaxrsServer server;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> schedFuture;

    public RestServer() {
    }

    public void start() throws Exception {
        server = new UndertowJaxrsServer().start(buildServer());
        server.deploy(RestApplication.class, "/");
        Logger.getLogger(RestServer.class.getName()).log(Level.INFO, String.format("=============== Server started"));
        Logger.getLogger(RestServer.class.getName()).log(Level.INFO, String.format("=============== Listening on: %s ports {%d | %d}", Environment.getOrDefault("SERVER_HOST", "127.0.0.1"), Environment.getOrDefault("SERVER_PORT_SSL", 4443), Environment.getOrDefault("SERVER_PORT", 8888)));
        
        startInternalServices();
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }

        stopInternalServices();
    }

    /**
     * Start the internal processes
     *
     * @todo: This could use futures to restart on exception etc.
     */
    protected void startInternalServices() {
        //If there is a cluster, this will (liely) start the execution in different times
        int interval = ThreadLocalRandom.current().nextInt(50, 121);
        Logger.getLogger(RestServer.class.getName()).log(Level.INFO, String.format("=============== Scheduler executor started (interval %d)", interval));

        schedFuture = scheduler.scheduleAtFixedRate(new TopSitesUpdater(), 1, interval, TimeUnit.MINUTES);
    }

    /**
     * Stop internal processes
     */
    protected void stopInternalServices() {
        schedFuture.cancel(true);
        scheduler.shutdown();
    }
    
    /**
     * Check if the scheduler is running for internal services
     * 
     * @return 
     */
    public boolean isSchedulerRunning(){
        if(schedFuture != null )
            return !schedFuture.isDone() && !schedFuture.isCancelled();
        
        return false;
    }

    /**
     * Build a server based on current configuration
     *
     * @return
     * @throws Exception
     */
    protected Undertow.Builder buildServer() throws Exception {
        SSLContext sslContext = createSSLContext(
                loadKeyStore(Environment.getOrDefault("KEY_STORE_PATH", "config/selfsigned.jks")),
                 loadKeyStore(Environment.getOrDefault("THRUST_STORE_PATH", "/usr/lib/jvm/java-8-oracle/jre/lib/security/cacerts")
                ));

        return Undertow.builder()
                .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                .addHttpsListener(Environment.getOrDefault("SERVER_PORT_SSL", 4443), Environment.getOrDefault("SERVER_HOST", "127.0.0.1"), sslContext)
                .addHttpListener(Environment.getOrDefault("SERVER_PORT", 8888), Environment.getOrDefault("SERVER_HOST", "127.0.0.1"))
                .setHandler(Handlers.gracefulShutdown(Handlers.path()));
    }

    /**
     * Load key store
     *
     * @param storeLoc
     * @return
     * @throws Exception
     */
    private KeyStore loadKeyStore(String storeLoc) throws Exception {
        final InputStream stream;
        if (storeLoc == null) {
            stream = RestServer.class.getResourceAsStream(storeLoc);
        } else {
            stream = Files.newInputStream(Paths.get(storeLoc));
        }

        if (stream == null) {
            throw new RuntimeException("Could not load keystore from: " + storeLoc);
        }
        try (InputStream is = stream) {
            KeyStore loadedKeystore = KeyStore.getInstance("JKS");
            if (storeLoc != null && !storeLoc.contains("cacerts")) {
                loadedKeystore.load(is, Environment.getOrDefault("KEY_STORE_PASSWORD", "password").toCharArray());
            }

            return loadedKeystore;
        }
    }

    /**
     * Create an SSL Context
     *
     * @param keyStore
     * @param trustStore
     * @return
     * @throws Exception
     */
    private static SSLContext createSSLContext(final KeyStore keyStore, final KeyStore trustStore) throws Exception {
        KeyManager[] keyManagers;
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, Environment.getOrDefault("KEY_STORE_PASSWORD", "password").toCharArray());
        keyManagers = keyManagerFactory.getKeyManagers();

        TrustManager[] trustManagers;
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        return sslContext;
    }
}
