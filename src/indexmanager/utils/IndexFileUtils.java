/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexmanager.utils;

import helpers.ByteHolderHelper;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton
 * @author Agustin
 */
public class IndexFileUtils {
   private static IndexFileUtils instance = null;
   
   private static final String vocabularyFile = "data/dump10k.opt-voc.dat";
   private static final String postingsFile = "data/dump10k.opt-bin.dat";
   
   public boolean createShortPosting = true;  // crear un archivo de postings sin singletons y doubletons
   private static String shortPostingFile = "short_index_bin.dat";
   
   private File vocFileObject;
   private File postingFileObject;
   private File shortPostingFileObject;
   private long postingSizeBytes;
   private FileOutputStream osShortPostingFile = null;
   private FileInputStream isPostingFileObject = null;
   private RandomAccessFile postingFileReader = null;
   private ByteHolderHelper byteHelper = new ByteHolderHelper();
   
   
   
   protected IndexFileUtils(String vocFile,String postingFile,boolean createShortPosting){
       try {
           this.vocFileObject = new File(vocFile);
           this.postingFileObject = new File(postingFile);
           this.createPostingReader();
           this.shortPostingFileObject = new File(shortPostingFile);
           this.createShortPosting = createShortPosting;
           if (createShortPosting){
               try {
                   osShortPostingFile = new FileOutputStream(this.shortPostingFileObject);
               } catch (FileNotFoundException ex) {
                   Logger.getLogger(IndexFileUtils.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
       } catch (FileNotFoundException ex) {
           Logger.getLogger(IndexFileUtils.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
   
    private void createPostingReader() throws FileNotFoundException {
        RandomAccessFile ram = new RandomAccessFile(this.postingFileObject, "r");
        this.postingFileReader = ram;
    }

    public RandomAccessFile getPostingFileReader() {
        return postingFileReader;
    }

    public void setPostingFileReader(RandomAccessFile postingFileReader) {
        this.postingFileReader = postingFileReader;
    }
   
   
   
   public static IndexFileUtils getInstance() {
      return instance;
   }
    

   public static void initFileUtils(String vocFile,String postingFile,boolean createShortPosting){
       instance = new IndexFileUtils(vocFile,postingFile,createShortPosting);
   }
   
    public static void initFileUtils(boolean createShortPosting){
       instance = new IndexFileUtils(IndexFileUtils.vocabularyFile,IndexFileUtils.postingsFile,createShortPosting);
   }

    public File getVocFileObject() {
        return vocFileObject;
    }

    public void setVocFileObject(File vocFileObject) {
        this.vocFileObject = vocFileObject;
    }

    public File getPostingFileObject() {
        return postingFileObject;
    }

    public void setPostingFileObject(File postingFileObject) {
        this.postingFileObject = postingFileObject;
    }

    public File getShortPostingFileObject() {
        return shortPostingFileObject;
    }

    public void setShortPostingFileObject(File shortPostingFileObject) {
        this.shortPostingFileObject = shortPostingFileObject;
    }

    public FileOutputStream getOsShortPostingFile() {
        return osShortPostingFile;
    }

    public void setOsShortPostingFile(FileOutputStream osShortPostingFile) {
        this.osShortPostingFile = osShortPostingFile;
    }

    public FileInputStream getIsPostingFileObject() {
        return isPostingFileObject;
    }

    public void setIsPostingFileObject(FileInputStream isPostingFileObject) {
        this.isPostingFileObject = isPostingFileObject;
    }

    public long getPostingSizeBytes() {
        if (this.postingSizeBytes == 0){
            this.postingSizeBytes = this.postingFileObject.length();
        }
        return this.postingSizeBytes;
    }

    public ByteHolderHelper getByteHelper() {
        return byteHelper;
    }

    public void setByteHelper(ByteHolderHelper byteHelper) {
        this.byteHelper = byteHelper;
    }


    
    
    
    
    
   
    
}
