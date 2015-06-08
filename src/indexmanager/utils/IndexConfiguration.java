/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexmanager.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author agustin
 */
public class IndexConfiguration {
    
    
    public Properties configuration = new Properties();
    InputStream input = null;

    public IndexConfiguration() {
        this.loadConfiguration();
    }
    
    public void loadConfiguration(){
        try {
            System.out.println("Cargando configuración del sistema...");
            input = new FileInputStream("config/configuration.properties");
            configuration.load(input);
 
	} catch (IOException ex) {
		ex.printStackTrace();
        }
    }
    
}
