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
public class PostingInfoSingleton extends PostingInfoDynamicSingleton implements StaticPosting{
    
    
    // Se inicializa con el valor por defecto del tipo primitivo byte de java (cero).
    private byte[] tmpSpace = new byte[7];

    public PostingInfoSingleton(List docsList) {
        super(docsList);
    }    
    
}
