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
public class PostingInfoSporadic extends PostingInfoSporadicDynamic{

    
    private byte[] garbageBytes;
    
    public PostingInfoSporadic(List postingData) {
        super(postingData);
    }
    
    
    @Override
    public void initializeProperties(List postingData) {
        this.setPostingData(new int[postingData.size()]);
        int garbage = 12 - ((postingData.size() * 4)); // 12? Es la longitud que necesito para guardar en memoria un puntero a una posting y su longitud (8+4);
        if (garbage > 0){
            this.garbageBytes = new byte[garbage];
        }
    }
    
    
    
    
}
