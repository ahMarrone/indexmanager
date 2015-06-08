/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parsers;

import java.io.File;

/**
 *
 * @author agustin
 */
public class SimpleVocParser extends VocParser{

    @Override
    public String[] parseLine(String line) {
        return this.parseBaseLine(line);
    }

    @Override
    public void generateVoc(File sourceFile, File destFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String[] parseBaseLine(String line) {
        return line.split("\t");
    }

    @Override
    public void writeVocLine(String info) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    
}
