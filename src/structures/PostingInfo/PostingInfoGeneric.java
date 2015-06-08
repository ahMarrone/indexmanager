/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package structures.PostingInfo;

import helpers.VocManager;
import indexmanager.utils.IndexFileUtils;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Agustin
 */
public class PostingInfoGeneric extends PostingInfo{

    private long postingPointer;
    private int postingLenght;
    
    public PostingInfoGeneric(List postingData) {
        super(postingData);
    }

    

    
    @Override
    public int[] getPostingData() {
        byte[] postingData = VocManager.readPostingFiles(IndexFileUtils.getInstance().getPostingFileReader(), this.postingPointer, this.postingLenght);
        int [] intData = VocManager.bytesToInt(postingData);
        postingData = null;
        return intData;
    }

    @Override
    public void loadData(int index,Object data) {
        if (index == 0){
            this.postingPointer = (long)data;
        } else {
            this.postingLenght = (int) data;
        }
    }

    public long getPostingPointer() {
        return postingPointer;
    }

    public void setPostingPointer(int postingPointer) {
        this.postingPointer = postingPointer;
    }

    public int getPostingLenght() {
        return postingLenght;
    }

    public void setPostingLenght(int postingLenght) {
        this.postingLenght = postingLenght;
    }

    @Override
    public void initializeProperties(List postingData) {
    }
    
    
    
    


    
    
    
}
