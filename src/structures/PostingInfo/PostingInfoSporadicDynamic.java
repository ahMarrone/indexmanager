/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package structures.PostingInfo;

import java.util.List;

/**
 *
 * @author agustin
 */
public class PostingInfoSporadicDynamic extends PostingInfo implements DynamicPosting{

    private int[] postingData;
    
    
    public PostingInfoSporadicDynamic(List postingData) {
        super(postingData);
    }

    @Override
    public int[] getPostingData() {
        return this.postingData;
    }

    @Override
    public void loadData(int index, Object data) {
        this.postingData[index] = (int) data;
    }

    @Override
    public int getPostingLenght() {
        return this.postingData.length;
    }

    @Override
    public void initializeProperties(List postingData) {
        this.postingData = new int[postingData.size()];
    }

    public void setPostingData(int[] postingData) {
        this.postingData = postingData;
    }
}
