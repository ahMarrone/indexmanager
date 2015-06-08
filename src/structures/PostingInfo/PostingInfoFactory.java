/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structures.PostingInfo;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Agustin
 */
public class PostingInfoFactory {
    
    
    /**
     * Si storeSporadics es false, entonces todas las postings son genericas, no se almacenan
     * los terminos esporadicos en el vocabulario.
     * Si el mismo es true, los singletons y doubletons van al voc (objetivo del trabajo)
     * @param mode
     * @param storeSporadics
     * @param docFrequency
     * @param postingData
     * @return 
     */
    public static PostingInfo createPostingInfo(int makeGenerics,int minGenericLenght,int mode,boolean storeSporadics,int docFrequency,List postingData){
        if (!storeSporadics){
            return PostingInfoFactory.getGenericPostingInfo(postingData);
        } else {
            if (makeGenerics == 1){
                return (mode == 0) ? PostingInfoFactory.createGenericsStaticPosting(minGenericLenght,docFrequency,postingData)
                                   : PostingInfoFactory.createGenericsDynamicPosting(minGenericLenght,docFrequency,postingData); 
            } else {
                return (mode == 0)? PostingInfoFactory.createStaticPosting(docFrequency,postingData)
                              : PostingInfoFactory.createDynamicPosting(docFrequency,postingData);   
            }
        }
    }
    
    private static PostingInfo createGenericsStaticPosting(int minGenericLenght,int docFrequency,List postingData){
        PostingInfo postingInfo;
        if (docFrequency < minGenericLenght ) {
            postingInfo = new PostingInfoSporadic(postingData);
        } else {
            postingInfo = PostingInfoFactory.getGenericPostingInfo(postingData);
        }
        return postingInfo;
    }
    
       
    private static PostingInfo createGenericsDynamicPosting(int minGenericLenght,int docFrequency,List postingData){
        PostingInfo postingInfo;
        if (docFrequency < minGenericLenght ) {
            postingInfo = new PostingInfoSporadicDynamic(postingData);
        } else {
            postingInfo = PostingInfoFactory.getGenericPostingInfo(postingData);
        }
        return postingInfo;
    }
    
    
    
    
    private static PostingInfo createStaticPosting(int docFrequency,List postingData){
        PostingInfo postingInfo;
        switch (docFrequency){
            case 1:  postingInfo = new PostingInfoSingleton(postingData);
                     break;
            case 2:  postingInfo = new PostingInfoDoubleton(postingData);
                    break;
            default: postingInfo = PostingInfoFactory.getGenericPostingInfo(postingData);
                     break;
        }
        return postingInfo;
    }
 
    
    private static PostingInfo createDynamicPosting(int docFrequency,List postingData){
        PostingInfo postingInfo;
        switch (docFrequency){
            case 1:  postingInfo = new PostingInfoDynamicSingleton(postingData);
                     break;
            case 2:  postingInfo = new PostingInfoDynamicDoubleton(postingData);
                    break;
            default: postingInfo = PostingInfoFactory.getGenericPostingInfo(postingData);
                     break;
        }
        return postingInfo;
    }
    
    
    private static PostingInfo getGenericPostingInfo(List postingData){
        return new PostingInfoGeneric(postingData);
    }
    
    
}
