/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parsers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author agustin
 */
public class ScaleVocParser extends VocParser{
    
    private int scale;

    public ScaleVocParser() {
        this.scale = 1;
    }
    
    

    @Override
    public String[] parseLine(String line) {
        return null;
    }
    
    
    @Override
    public String[] parseBaseLine(String line) {
        System.out.println(line + "\n");
        String[] resp = new String[4];
        String[] info = line.split("\t");
        resp[0] = info[0];
        resp[1] = info[1];
        long pointer = Long.parseLong(info[2]);
        if (pointer > Integer.MAX_VALUE){
            resp[2] = String.valueOf(Long.parseLong(info[2]) % Integer.MAX_VALUE);
        } else {
            resp[2] = info[2];
        }
        resp[3] = String.valueOf(pointer / Integer.MAX_VALUE);
        //System.out.println("Result: " + resp[0] + "\t"+  resp[1] + "\t" + resp[2]+"\t"+resp[3]+"\n");
        return resp;
    }

 
    @Override
    public void writeVocLine(String input) {
        try {
            String[] info = this.parseBaseLine(input);
            this.writer.append(info[0] + "\t"+  info[1] + "\t" + info[2]+"\t"+info[3]+"\n");
        } catch (IOException ex) {
           Logger.getLogger(ScaleVocParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
