/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structures.PostingInfo;

import java.util.List;

/**
 *
 * @author Agustin
 */
public class PostingInfoDynamicDoubleton extends PostingInfo implements DynamicPosting{
    
    private int docID1;
    private int docID2;

    public PostingInfoDynamicDoubleton(List postingData) {
        super(postingData);
    }

    
    
    @Override
    public int[] getPostingData() {
        return new int[]{this.docID1,this.docID2};
    }
    
       @Override
    public void loadData(int index, Object data) {
        if (index == 0){
            this.docID1 = (int) data;
        } else {
            this.docID2 = (int) data;
        }
    }

    @Override
    public int getPostingLenght() {
        return 2;
    }

    @Override
    public void initializeProperties(List postingData) {
    }

    
}
