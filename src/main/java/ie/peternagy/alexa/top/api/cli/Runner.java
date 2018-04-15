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
 * @description Runner - the cli entry point for app
 * @package ie.peternagy.alexa.top.api.cli
 */
package ie.peternagy.alexa.top.api.cli;

import ie.peternagy.alexa.top.api.server.RestServer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Runner {
    private static RestServer server;

    public static void main(String[] args) throws Exception {
        server = new RestServer();
        server.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Logger.getLogger(Runner.class.getName()).log(Level.INFO, String.format("=============== Shutting down..."));
                server.stop();
                server = null;
                Logger.getLogger(Runner.class.getName()).log(Level.INFO, String.format("=============== Server shutdown complete..."));
            }
        });
    }
}
