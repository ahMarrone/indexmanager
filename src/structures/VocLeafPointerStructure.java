/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structures;

import helpers.VocManager;
import java.io.Serializable;
import java.util.ArrayList;
import structures.PostingInfo.PostingInfo;

/**
 *
 * @author Agustin
 */
public class VocLeafPointerStructure extends VocLeaf{
    
    
    /**
     * Lista de objetos TermDataPointer. Cada uno de ellos contiene informacion de un termino especifico.
     * (Todos los terminos comparten el mismo prefijo).
     */
    private ArrayList<TermDataPointer> termsDataList;
    
    /**
     * String que contiene los suffix de los terminos
     */
    private String termsList = "";

    public VocLeafPointerStructure() {
        this.setSuffixN(0);
        this.termsDataList = new ArrayList<>();
    }
    
    
    
    
    
    public void appendTerm(String[] termInfo,PostingInfo postingInfo){
        // Creo objeto que contiene la informacion de cada termino
        // notar que el estoy pasando como impact value el df (todavia no estoy trabajando con impact values)
        TermDataPointer termData = new TermDataPointer(this.termsList.length(), 0, Integer.parseInt(termInfo[1]),postingInfo);
        this.termsDataList.add(termData);
        if (termInfo[0].length() < VocManager.prefixLenght){
            this.termsList = this.termsList + termInfo[0];
        } else {
            this.termsList = this.termsList + termInfo[0].substring(VocManager.prefixLenght, termInfo[0].length());
        }
        this.setSuffixN(this.getSuffixN()+1);
    }
    
    
    @Override
    public void printFullTerms(String preffix){
        short index = 0;
        for (TermDataPointer termData : this.getTermsDataList()){
            String fullTerm = this.getFullTerm(preffix, termData);
            System.out.println(fullTerm + " \t "  +  termData.getDf());
            index ++;
        }
    }
    
    
        
    @Override
    public String getFullTerm(String preffix,TermData termData){
        return this.getFullTerm(preffix, this.termsDataList.indexOf(termData));
    }
    
    
    
    private String getFullTerm(String preffix,int termDataIndex){
        String suffix;
        if (termDataIndex < this.getTermsDataList().size() - 1){
            suffix = this.termsList.substring(this.termsDataList.get(termDataIndex).getStringPointer(),this.termsDataList.get(termDataIndex+1).getStringPointer());
        } else { // ultimo elemento de la lista. ultimo termino que contiene ese prefijo
            suffix = this.termsList.substring(this.termsDataList.get(termDataIndex).getStringPointer());
        }
        String fullTerm = preffix + suffix;
        return fullTerm;
    }

    
    
    @Override
    public TermData searchTerm(String preffix,String termSearch){
        for (TermDataPointer termData : this.getTermsDataList()){
            String fullTerm = this.getFullTerm(preffix, termData);
            if (fullTerm.compareTo(termSearch) == 0){
                return termData;
            }
        } 
        return null;
    }
    
    
    public ArrayList<TermDataPointer> getTermsDataList() {
        return termsDataList;
    }

    public void setTermsDataList(ArrayList<TermDataPointer> termsDataList) {
        this.termsDataList = termsDataList;
    }

    public String getTermsString() {
        return termsList;
    }

    public void setTermsString(String termsList) {
        this.termsList = termsList;
    }

    
    
    
    
}
