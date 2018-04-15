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
 * @description RestException - general exception used on http resources
 * @package    ie.peternagy.alexa.top.api.common.exception
 * 
 */
package ie.peternagy.alexa.top.api.common.exception;


public class RestException extends Throwable{
    public static final String ERROR_BASE_JSON = "{\"desc\":\"%s\", \"src\":\"%s\", \"error-code\":%d}";
    
    private final String src;
    private final String desc;
    private final int code;//this is the internal code
    private final int status;
    
    public RestException(String src, String desc, int status, int code) {
        super(desc);
        this.src = src;
        this.desc = desc;
        this.status = status;
        this.code = code;
    }

    public RestException(String src, String desc, int status, int code, Throwable cause) {
        super(cause);
        this.src = src;
        this.desc = desc;
        this.status = status;
        this.code = code;
    }
    
    
    
    /**
     * Get the current public json string
     * @return 
     */
    public String toJsonString(){
        return String.format(ERROR_BASE_JSON, desc, src, code);
    }

    @Override
    public String toString() {
        return super.toString() + " RestException{" + "src=" + src + ", desc=" + desc + ", code=" + code + ", status=" + status + '}';
    }

    public String getSrc() {
        return src;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
