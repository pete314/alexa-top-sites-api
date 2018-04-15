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
 * @description RestApplication - Application resource loader
 * @package    ie.peternagy.alexa.top.api.server
 * 
 */
package ie.peternagy.alexa.top.api.server;

import ie.peternagy.alexa.top.api.common.filter.CorsFilter;
import ie.peternagy.alexa.top.api.common.filter.RestExceptionMapper;
import ie.peternagy.alexa.top.api.resources.sites.resource.SitesResource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.jboss.resteasy.plugins.cache.server.ServerCacheFeature;

@ApplicationPath("")
public class RestApplication extends Application{
    
    @Override
    public Set<Class<?>> getClasses() {
        return Collections.emptySet();
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();
        
        singletons.add(new CorsFilter());
        singletons.add(new RestExceptionMapper());
        singletons.add(new ServerCacheFeature());
        singletons.add(new SitesResource());
        
        return singletons;
    }
}
