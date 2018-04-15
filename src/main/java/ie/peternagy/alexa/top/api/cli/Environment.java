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
 * @description Environment - wrapper for environment variables
 * @package ie.peternagy.alexa.top.api.cli
 */
package ie.peternagy.alexa.top.api.cli;

import com.google.common.base.Strings;
import java.util.Map;

public class Environment {
    private static final Map<String, String> ENV = System.getenv();
    
    /**
     * Get all environment variables
     * 
     * @return 
     */
    public static Map<String, String> getAll(){
        return ENV;
    }
    
    /**
     * Get an environment variable
     * 
     * @param key the variable name
     * @return 
     */
    public static String get(String key){
        return ENV.get(key);
    }
    
    /**
     * Get a string environment variable
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public static String getOrDefault(String key, String defaultValue){
        return ENV.getOrDefault(key, defaultValue);
    }
    
    /**
     * Get an integer environment variable
     * 
     * @param key the variable name
     * @param defaultValue used if can't be formatted or not found
     * @return 
     */
    public static Integer getOrDefault(String key, int defaultValue){
        try{
            String value = get(key);
            if(Strings.isNullOrEmpty(value)){
                return defaultValue;
            }
            
            return Integer.parseInt(value);
            
        }catch(NumberFormatException ex){
            
        }
        
        return defaultValue;
    }
}
