/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parsers;

import indexmanager.utils.IndexFileUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author agustin
 */
public abstract class VocParser {
    
    
    public FileWriter writer;
    
    /**
     * Retorna String[] => [0]: termino, [1]:longitudPosting,[2]:postingPointer,[N]: ...
     * (Los primeros tres elementos son iguales para todos)
     * @param line
     * @return 
     */
    public abstract String[] parseLine(String line);
    
    /**
     * Metodo que parsea informacion del vocabulario simple (term df pointer)
     * @param line
     * @return 
     */
    public abstract String[] parseBaseLine(String line);
    
    
    /**
     * 
     * @param info String[] [0] term,[1] df,[2] pointer
     */
    public abstract void writeVocLine(String info);
    
    
    public void generateVoc(File sourceFile,File destFile){
        Scanner sc = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(sourceFile));
            this.writer = new FileWriter(destFile);
            //VocParser baseParser = new SimpleVocParser();
            String line = br.readLine();
            while (line != null) {
               this.writeVocLine(line);
               line = br.readLine();
            }
            br.close();
            this.writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VocParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VocParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
    }

    public FileWriter getWriter() {
        return writer;
    }

    public void setWriter(FileWriter writer) {
        this.writer = writer;
    }
    
    
    
    
    
}
