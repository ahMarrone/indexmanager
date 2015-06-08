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
public abstract class TermData  implements Serializable {
    
    
    private PostingInfo postingInfo;
    
    /**
     * Collection frequency. 
     * 4 bytes
     */
    private int cf = 0;
    
    /**
     * Document frequency.
     * 4 bytes
     */
    private int df = 0;

    public PostingInfo getPostingInfo() {
        return postingInfo;
    }

    public void setPostingInfo(PostingInfo postingInfo) {
        this.postingInfo = postingInfo;
    }

    public int getCf() {
        return cf;
    }

    public void setCf(int cf) {
        this.cf = cf;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }
    
    
    
    
    
    
}
