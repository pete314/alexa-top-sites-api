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
 * @description TopSiteUpdater - update service for Alexa top 1 million site
 * data
 * @package ie.peternagy.alexa.top.api.service
 */
package ie.peternagy.alexa.top.api.service;

import com.google.common.base.Strings;
import ie.peternagy.alexa.top.api.cli.Environment;
import ie.peternagy.alexa.top.api.common.client.JedisFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class TopSitesUpdater implements Runnable {

    private final SimpleDateFormat HEADER_DATE_FORMAT;
    protected final String CACHE_KEY_UPDATE;
    protected final String CACHE_KEY_UPDATE_LAST_UPDATE;
    protected final String TMP_SITES_FILE_PATH;

    public TopSitesUpdater() {
        HEADER_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        CACHE_KEY_UPDATE = String.format("%s:update", Environment.getOrDefault("CACHE_KEY_BASE", "alexa:top:api"));
        CACHE_KEY_UPDATE_LAST_UPDATE = String.format("%s:last-update", Environment.getOrDefault("CACHE_KEY_BASE", "alexa:top:api"));
        TMP_SITES_FILE_PATH = String.format("%s/top-1m.csv.zip", System.getProperty("user.home"));

    }

    /**
     * Check the Last-Modified on the external data source
     *
     * @return
     */
    protected Date getExternalUpdateTime() {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpHead headRequest = new HttpHead(Environment.getOrDefault("TOP_LIST_FILE_URI", "http://s3.amazonaws.com/alexa-static/top-1m.csv.zip"));
            CloseableHttpResponse response = client.execute(headRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                Header header = response.getFirstHeader("Last-Modified");
                if (header != null) {
                    String lastModified = header.getValue();

                    return HEADER_DATE_FORMAT.parse(lastModified);
                }
            }
        } catch (ParseException | IOException ex) {
            Logger.getLogger(TopSitesUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Return the EPOC, nothing to avoid triggering the update
        return new Date(1);
    }

    /**
     * Get the internal update time
     *
     * @return
     */
    protected Date getInternalUpdateTime() {
        try (Jedis jedis = JedisFactory.getInstance().newClient()) {
            if (jedis.exists(CACHE_KEY_UPDATE_LAST_UPDATE)) {
                String lastModified = jedis.get(CACHE_KEY_UPDATE_LAST_UPDATE);

                return HEADER_DATE_FORMAT.parse(lastModified);
            }
        } catch (JedisConnectionException | ParseException ex) {
            Logger.getLogger(TopSitesUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new Date(1);
    }

    /**
     * Set the internal update time
     *
     * @param updated
     * @return true on success
     */
    protected boolean setInternalUpdateTime(Date updated) {
        try (Jedis jedis = JedisFactory.getInstance().newClient()) {
            String lastModified = HEADER_DATE_FORMAT.format(updated);
            String response = jedis.set(CACHE_KEY_UPDATE_LAST_UPDATE, lastModified);

            return !Strings.isNullOrEmpty(response) && response.equalsIgnoreCase("ok");

        } catch (JedisConnectionException ex) {
            Logger.getLogger(TopSitesUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Download the external content to local disk
     *
     * @param localFilePath the full file path to save the file into
     * @return true on success
     */
    protected boolean downloadExternalData(String localFilePath) {
        try {
            URL url = new URL(Environment.getOrDefault("TOP_LIST_FILE_URI", "http://s3.amazonaws.com/alexa-static/top-1m.csv.zip"));
            try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    FileOutputStream fos = new FileOutputStream(localFilePath)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }

            return true;
        } catch (IOException ex) {
            Logger.getLogger(TopSitesUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    /**
     * Check if update is needed
     *
     * @return
     */
    protected boolean needsUpdate() {
        Date external = getExternalUpdateTime();
        Date internal = getInternalUpdateTime();

        return internal.before(external);
    }

    @Override
    public void run() {
        Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, "======= Sites data updater started");
        Date sourceUpdate = getExternalUpdateTime();
        Date internalUpdate = getInternalUpdateTime();
        boolean runUpdate = false;
        
        if(internalUpdate.equals(new Date(1))){
            Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, "======= Initializing cache");
            runUpdate = true;
        }else if(internalUpdate.before(sourceUpdate)){
            Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, "======= Internal data update started");
            runUpdate = true;
        }else{
            Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, "======= Data up to date");
        }
        
        if(runUpdate){
            if(downloadExternalData(TMP_SITES_FILE_PATH)){
                try {
                    SitesRedisExtractor extractor = new SitesRedisExtractor(TMP_SITES_FILE_PATH);
                    Thread t = new Thread(extractor);
                    t.start();
                    t.join();
                    
                    if(extractor.isComplete() && extractor.isSuccessfulUpdate()){
                        setInternalUpdateTime(sourceUpdate);
                        Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, "======= Update success");
                        return;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(TopSitesUpdater.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, "======= Update failed");
        }
    }

}
