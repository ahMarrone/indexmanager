/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package structures;

import java.io.Serializable;
import structures.PostingInfo.PostingInfo;

/**
 *
 * @author agustin
 */
public abstract class VocLeaf  implements Serializable{
    
    
    /**
     * Suffix number.
     * 4 bytes
     */
    private int suffixN;

    public int getSuffixN() {
        return suffixN;
    }

    public void setSuffixN(int suffixN) {
        this.suffixN = suffixN;
    }
    
    
    public abstract String getFullTerm(String preffix,TermData termData);
    
    public abstract TermData searchTerm(String preffix,String termSearch);
    
    public abstract void appendTerm(String[] termInfo,PostingInfo postingInfo);
    
    public abstract void printFullTerms(String preffix);
    
    
    
    
}
