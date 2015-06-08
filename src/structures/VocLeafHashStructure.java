/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package structures;

import helpers.VocManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import structures.PostingInfo.PostingInfo;

/**
 *
 * @author agustin
 */
public class VocLeafHashStructure extends VocLeaf{
    
    private HashMap<String,TermDataHash> termsList = new HashMap();
    
    
    public VocLeafHashStructure() {
        this.setSuffixN(0);
    }
    
    @Override
    public void appendTerm(String[] termInfo,PostingInfo postingInfo){
        String termSuffix = (termInfo[0].length() < VocManager.prefixLenght) ? termInfo[0]: termInfo[0].substring(VocManager.prefixLenght);
        if (!this.termsList.containsKey(termSuffix)){ // Se supone que el vocabulario no tiene repeticiones, pero chequeo igual
            TermDataHash termData = new TermDataHash(0,Integer.parseInt(termInfo[1]),postingInfo);
            if (termInfo[0].startsWith("liholiho")){
                 termSuffix = (termInfo[0].length() < VocManager.prefixLenght) ? termInfo[0]: termInfo[0].substring(VocManager.prefixLenght);
            } else  {
                termSuffix = (termInfo[0].length() < VocManager.prefixLenght) ? termInfo[0]: termInfo[0].substring(VocManager.prefixLenght); 
            }
            this.termsList.put(termSuffix, termData);
        }
    }
    
    
    @Override
    public TermData searchTerm(String preffix,String termSearch){
        String suffixSearch = termSearch.substring(preffix.length());
        TermDataHash termData = this.termsList.get(suffixSearch);
        return termData;
    }

    @Override
    public String getFullTerm(String preffix, TermData termData) {
        for (Entry<String, TermDataHash> entry : termsList.entrySet()) {
            if (termData.equals(entry.getValue())) { // busco clave en el hash
                return preffix + entry.getKey(); 
            }
        }
        return null;
    }

    @Override
    public void printFullTerms(String preffix) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
