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
 * @description Site - Site model
 * @package ie.peternagy.alexa.top.api.sites.model
 */
package ie.peternagy.alexa.top.api.resources.sites.model;

import com.google.common.base.Strings;
import java.util.Objects;
import org.apache.commons.codec.digest.DigestUtils;

public class Site {
    private int rank;
    private String site;
    private String tld;

    public Site() {
    }

    public Site(int rank, String site) {
        this.rank = rank;
        this.site = site;
        setTld();
    }

    public int getRank() {
        return rank;
    }

    public String getSite() {
        return site;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setSite(String site) {
        this.site = site;
        setTld();
    }

    private void setTld(){
        if(!Strings.isNullOrEmpty(site)){
            tld = site.substring(site.indexOf(".")+1, site.length());
        }
    }
    
    public String getTld(){
        return tld;
    }
    
    /**
     * Hash strings with md5 
     * 
     * Note: this is used for cache key generation
     * 
     * @param str
     * @return 
     */
    public static String hash(String str){
        return DigestUtils.md5Hex(str);
    }
}
