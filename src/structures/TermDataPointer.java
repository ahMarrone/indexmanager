/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structures;

import java.io.Serializable;
import structures.PostingInfo.PostingInfo;

/**
 *
 * @author Agustin
 */
public class TermDataPointer extends TermData{
    
    
    /**
     * Puntero de la posicion del termino dentro del atributo termsString de VocLeaf.
     * 4 bytes
     */
    private int stringPointer; 
    
    
    /**
     * Constructor de TermData.
     * Dependiendo el DF recibido, se deben crear distintas instancias de PostingInfo
     * @param stringPointer
     * @param collectionFrequency
     * @param docFrequency
     * @param postingPointer
     * @param impactValue 
     */
    public TermDataPointer(int stringPointer,int collectionFrequency, int docFrequency, PostingInfo postinginfo) {
        this.stringPointer = stringPointer;
        this.setCf(collectionFrequency);
        this.setDf(docFrequency);
        this.setPostingInfo(postinginfo);
    }
    
    
    public int getStringPointer() {
        return stringPointer;
    }

    public void setStringPointer(int stringPointer) {
        this.stringPointer = stringPointer;
    }
    
}
