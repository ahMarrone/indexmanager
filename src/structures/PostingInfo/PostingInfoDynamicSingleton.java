/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structures.PostingInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Agustin
 */
public class PostingInfoDynamicSingleton extends PostingInfo implements DynamicPosting {

    private int docID;

    public PostingInfoDynamicSingleton(List postingData) {
        super(postingData);
    }
    
    
    @Override
    public int[] getPostingData() {
        return new int[]{this.docID};
    }

    @Override
    public void loadData(int index, Object data) {
        if (index == 0){
            this.docID = (int) data;
        }
    }

    @Override
    public int getPostingLenght() {
        return 1;
    }

    @Override
    public void initializeProperties(List postingData) {
    }
    
    
}
