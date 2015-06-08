/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package structures;

import structures.PostingInfo.PostingInfo;

/**
 *
 * @author agustin
 */
class TermDataHash extends TermData{

    public TermDataHash(int collectionFrequency, int docFrequency, PostingInfo postinginfo) {
        this.setCf(collectionFrequency);
        this.setDf(docFrequency);
        this.setPostingInfo(postinginfo);
    }
    
    
    
    
}
