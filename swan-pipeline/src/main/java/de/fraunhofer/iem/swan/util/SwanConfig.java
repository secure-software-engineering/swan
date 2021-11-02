package de.fraunhofer.iem.swan.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Oshando Johnson on 2019-10-11
 */
public class SwanConfig {

    private Properties config;

    public SwanConfig(){

        config = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream("swan_core_config.properties");

        try {
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Properties getConfig() {
        return config;
    }
}
