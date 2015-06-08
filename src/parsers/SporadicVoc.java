/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parsers;

import helpers.VocManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import structures.PostingInfo.PostingInfo;

/**
 *
 * @author agustin
 */
public class SporadicVoc extends VocParser{

    @Override
    public String[] parseLine(String line) {
        return line.split("\t");
        
    }

    @Override
    public String[] parseBaseLine(String line) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeVocLine(String info) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public void writeVocLine(String[] termInfo,PostingInfo postingInfo,int minGenericLenght) {
        try {
            int df = Integer.parseInt(termInfo[1]);
            short index = 1;
            String stringDocList = "";
            if (df  < minGenericLenght){
                for (int docID : postingInfo.getPostingData()){
                    stringDocList += (index == postingInfo.getPostingLenght()) ? docID : docID + ";";
                    index++;
                }
                this.writer.append(termInfo[0] + "\t"+  termInfo[1] + "\t" + termInfo[2]+"\t"+stringDocList+"\n");
            } else {
                this.writer.append(termInfo[0] + "\t"+  termInfo[1] + "\t" + termInfo[2]+"\n");
            }
        } catch (IOException ex) {
           Logger.getLogger(ScaleVocParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public ArrayList getDocIdentifiers(String lineDocsIdentifiers){
        ArrayList response = new ArrayList();
        String[] docIDs = lineDocsIdentifiers.split(";");
        for (String id : docIDs){
            response.add(Integer.parseInt(id));
        }
        return response;
    }
    
}
