/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structures.PostingInfo;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Agustin
 */
public abstract class PostingInfo implements Serializable{
    
    /**
     * Debera retornar un Hash, con los doc ID's como clave, y el impact value como valor de cada uno
     * @return 
     */
    public abstract int[] getPostingData();
    
    /**
     * Debera retornar un Hash, con los doc ID's como clave, y el impact value como valor de cada uno
     * @return 
     */
    public abstract void initializeProperties(List postingData);
    

    public abstract void loadData(int index,Object data);
    
    
    public abstract int getPostingLenght();
    
    
    /**
     * Imprime informacion de la posting correspondiente
     */
    public void printPostingData(){
        if (this instanceof PostingInfoGeneric){
            System.out.println("Obteniendo datos de la posting desde el indice en disco");
        } else {
            System.out.println("Obteniendo datos de la posting desde el vocabulario");
        }
        System.out.println("Frecuencia del término en el vocabulario:" + this.getPostingLenght());
        int[] postingData = this.getPostingData();
        for (int i = 0 ; i < postingData.length ; i++){
            System.out.println("Doc ID: \t" + postingData[i]);
        }
    }
    
    
    public PostingInfo(List postingData){
        this.initializeProperties(postingData);
        for (int i = 0 ; i < postingData.size() ; i++){
            this.loadData(i,postingData.get(i));
        }
    }
    
    
    
    
}
