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
public class RelativeVocParser extends VocParser{

    @Override
    public String[] parseBaseLine(String line) {
        String[] info = line.split("\t");
        return info;
    }


    @Override
    public String[] parseLine(String line) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeVocLine(String info) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
    
    
    
}
