/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import indexmanager.utils.IndexConfiguration;
import indexmanager.utils.IndexFileUtils;
import indexmanager.utils.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.StringComparator;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import parsers.SporadicVoc;
import parsers.VocParser;
import structures.PostingInfo.PostingInfo;
import structures.PostingInfo.PostingInfoFactory;
import structures.PostingInfo.PostingInfoGeneric;
import structures.TermData;
import structures.VocLeaf;
import structures.VocLeafHashStructure;
import structures.VocLeafPointerStructure;

/**
 *
 * @author Agustin
 */
public class VocManager {
    
    static String DATABASE = "vocabulary";
    static String BTREE_NAME = "VocabularyBTREE";
    
    static String vocFileSuffix = "voc";
    static String postingFileSuffix = "postingbin";
    
    
    public int[] treeSizes = new int[]{1024};
    public static byte prefixLenght = 4;
    public static byte postingEntryLenght = 4; // Cada entrada de una posting ocupa 4 bytes
    
    
    private int mode = 0;  // 0: static , 1: dynamic
    private boolean removeSporadics = true;  // 0: static , 1: dynamic
    private boolean forceVocReload = false;
    private int POSTING_GENERIC_MIN = 3;  // Longitud minima que tiene que tener una posting para ser considerada generica
    private VocParser vocParser;
    private Map postingsDistrib = new HashMap<Integer,Integer>();
    private long postingAverageLenght;
    
    
    public static final int SINGLETON = 1;
    public static final int DOUBLETON = 2;
    private int vocterms;
  

    private static RecordManager recman;
    private static long          recid;
    private static Tuple         tuple = new Tuple();
    private BTree         vocBTree; // indice BTree
    private static Properties    props;
    
    
    private Properties config = null;
    
    //public static final String vocabularyFile = "./../data/dump10k.opt-vocshort.dat";
    //public static final String postingsFile = "./../data/dump10k.opt-bin.dat";

    public VocManager(int mode,boolean removeSporadics,boolean forceVocReload,VocParser parser) {
        this.mode = mode;
        this.config = new IndexConfiguration().configuration;
        this.removeSporadics = removeSporadics;
        this.forceVocReload = forceVocReload;
        this.vocParser = parser;
        this.POSTING_GENERIC_MIN = Integer.parseInt(this.config.getProperty("min_lenght_generic_posting"));
    }
    
    
    
    public void loadBTree(){
        VocManager.props = new Properties();
        try {
            for (int i = 0; i < treeSizes.length ; i++){
                long timeA = Utils.getTime();
                int treeSize = treeSizes[i];
                // open database and setup an object cache
                recman = RecordManagerFactory.createRecordManager( DATABASE, props );
                // try to reload an existing B+Tree
                recid = recman.getNamedObject( BTREE_NAME );
               if ( recid != 0  && !forceVocReload) {
                    vocBTree = BTree.load( recman, recid );
                    System.out.println( "Arbol BTree recargado con tamaño " + vocBTree.size());
                } else {
                    // create a new B+Tree data structure and use a StringComparator
                        vocBTree = BTree.createInstance( recman, new StringComparator(),null,null,treeSize);
                        recman.setNamedObject(BTREE_NAME,vocBTree.getRecid());
                        this.constructBTree();         // Construyo desde cero el BTree con los datos del archivo
                        System.out.println( "Nuevo Indice Btree para el vocabulario creado" );
                        recman.commit();
                        this.printVocabSize(treeSize);
                        //this.removeVocab();

                }
                // make the data persistent in the database

                long timeB = Utils.getTime();
                System.out.println("Tiempo: " + (Utils.substractTimes(timeB, timeA)/(float)1000) + " segundos");
            }
        } catch ( Exception except ) {
            except.printStackTrace();
        }
    }
    
    private void constructBTree() throws FileNotFoundException{
        try {
            int linesRead = 0;
            long postlenghtCounter = 0;
            BufferedReader br = new BufferedReader(new FileReader(IndexFileUtils.getInstance().getVocFileObject()));
            String line = br.readLine();
            while (line != null) {
                linesRead++;
                String[] termInfo = this.vocParser.parseLine(line);
                PostingInfo postingInfo = this.getPostingInfo(termInfo);
                // NUEVO
                // Codigo para crear un nuevo indice corto, sin singletons y doubletons
                if (postingInfo.getPostingLenght() >= POSTING_GENERIC_MIN && IndexFileUtils.getInstance().createShortPosting){
                    // Escribir informacion de la posting en el nuevo indice
                    // Obtner nuevo puntero y actualizarlo
                    long newPointer = IndexFileUtils.getInstance().getShortPostingFileObject().length();
                    this.writeShortIndex(IndexFileUtils.getInstance().getOsShortPostingFile(),postingInfo.getPostingData());
                    ((PostingInfoGeneric) postingInfo).setPostingPointer((int) newPointer);
                }
                this.updateBTree(termInfo,postingInfo);
                this.updateStats(termInfo);
                postlenghtCounter += Integer.parseInt(termInfo[1]);
                this.printStatus(linesRead);
                line = br.readLine();
            }
            //this.postingAverageLenght = (postlenghtCounter / this.vocterms);
            //System.out.println(postingAverageLenght);
            if (IndexFileUtils.getInstance().createShortPosting){
                try {
                    IndexFileUtils.getInstance().setPostingFileObject(IndexFileUtils.getInstance().getShortPostingFileObject());
                    IndexFileUtils.getInstance().getOsShortPostingFile().close();
                } catch (IOException ex) {
                    Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.printTreeKeys();
            //IndexFileUtils.getInstance().getIsPostingFileObject().close();
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void constructHashVoc(){
        
    }
    
    
    private void printTreeKeys(){
        TupleBrowser browser;
        try {
            Object objA = this.vocBTree.find("batm");
            Object objB = this.vocBTree.find("wood");
            /*browser = this.vocBTree.browse();
            while ( browser.getNext(tuple) ){
                VocLeaf leaf = (VocLeaf) tuple.getValue();
                System.out.println();
            }*/
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private PostingInfo getPostingInfo(String[] termInfo){
        int docFrequency = Integer.parseInt(termInfo[1]);
        long postingPointer = Long.parseLong(termInfo[2]);
        // Si la posting es un singleton o un doubleton voy a buscar los datos a indice para luego guardarlos en el vocabulario
        // Sino, creo una posting generica, que guarda el puntero y la longitud de la posting en la memoria, para luego ir a buscar al indice
        List postingData = new ArrayList();
        if (docFrequency < POSTING_GENERIC_MIN && this.removeSporadics){
            if (this.vocParser instanceof SporadicVoc){
                postingData = ((SporadicVoc)this.vocParser).getDocIdentifiers(termInfo[3]);
            } else {
                postingData = this.parsePosting(docFrequency, this.readPostingFiles(IndexFileUtils.getInstance().getPostingFileReader(),postingPointer, docFrequency));
            }
        } else {
            postingData.add(postingPointer);
            postingData.add(docFrequency);
        }
        int makeGenerics = Integer.parseInt(this.config.getProperty("make_generics"));
        PostingInfo postingInfo = PostingInfoFactory.createPostingInfo(makeGenerics,this.POSTING_GENERIC_MIN,mode,this.removeSporadics, docFrequency, postingData);
        return postingInfo;
    }
     
    
    /**
     * Inserto los terminos y su informacion en el arbol BTree.
     * 
     * Clave -> prefix del termino.
     * Valor -> objeto VocLeaf que contiene informacion de los terminos que comienzan con ese prefijo
     * @param tree Arbol BTree
     * @param term Informacion del término. [0]-> termino, [1]->DF, [2]-> puntero
     */
    private void updateBTree(String[] termInfo,PostingInfo postingInfo){
        try {   
                String termPrefix = (termInfo[0].length() < prefixLenght) ? "" : termInfo[0].substring(0, prefixLenght);
                VocLeaf leaf = (VocLeaf) this.vocBTree.find(termPrefix);
                // Me fijo si ya existe una hoja que alberga a los terminos con ese prefijo
                // Si existe, agrego el termino actual a esa hoja
                // Sino, creo una nueva hoja (objeto VocLeaf), e inserto en el arbol la nueva tupla clave,valor => (termPrefix,VocLeaf)
                if (leaf != null){
                    // Se deberia chequear tambien si el termino es unico.
                    leaf.appendTerm(termInfo,postingInfo);
                } else {
                    VocLeaf newLeaf = new VocLeafHashStructure();
                    newLeaf.appendTerm(termInfo,postingInfo);
                    this.vocBTree.insert(termPrefix, newLeaf,false);
                }
        } catch (IOException ex) {
                Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    private void updateHashVoc(){
        
    }
        
    
    /**
     * Imprime todos los terminos del vocabulario cargados en el arbol BTree
     * @param tree 
     */
    public void printVocabulary(){
        TupleBrowser browser;
        try {
            System.out.println("Termino \t DF");
            browser = this.vocBTree.browse();
            while ( browser.getNext(tuple) ){
                VocLeaf leaf = (VocLeaf) tuple.getValue();
                leaf.printFullTerms((String) tuple.getKey());
            }
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Recibe un termino y lo busca en el arbol. Si existe imprime los datos de su posting list.
     * Sino imprime error
     * @param termSearch
     */
    public void searchTerm(String termSearch){
        try {
            System.out.println("Buscando el termino " + termSearch + " ...");
            TermData termData = this.findTerm(termSearch);
            if (termData != null){
                System.out.println(termData.getPostingInfo().getClass());
                termData.getPostingInfo().printPostingData();
            } else {
                System.out.println("Termino no encontrado");
            }
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    private TermData findTerm(String termSearch) throws IOException{
        String preffix = (termSearch.length() < VocManager.prefixLenght) ? "" : termSearch.substring(0, VocManager.prefixLenght);
        VocLeaf leaf = (VocLeaf) this.vocBTree.find(preffix);
        if (leaf != null){
            TermData termData = leaf.searchTerm(preffix,termSearch);
            if (termData != null){
                return termData;
            }
        }
        return null;
    }
    
    
    /**
    * Metodo que recibe una posting y retrona un hash con los doc id's como clave y los 
    * respectivos impact values como valor
    * @return 
    */
    public static List parsePosting(int docFrequency,byte[] data){
        List docList = new ArrayList();
        int[] docsList = VocManager.bytesToInt(data);
        for (int doc : docsList) {
            docList.add(doc);
        }
        return docList;
    }
   
   
   
   /**
    * Recibe un array con los bytes que representan a numeros enteros en formato LITTLE ENDIAN y retorna una lista int
    * con los enteros correspondientes
    * @param data
    * @return array de int's.
    */
    public static int[] bytesToInt(byte[] data){
       int size = data.length / postingEntryLenght;
       int[] result = new int[size];
       for (int i = 1;i <=  size ;i++){
           int tmp = data[(i*4)-4] & 0xFF | (data[(i*4)-3] & 0xFF) << 8 | (data[(i*4)-2] & 0xFF) << 16 | (data[(i*4)-1] & 0xFF) << 24;
           result[i - 1] = tmp;
       }
       return result;
   }
    
    /**
     * Recibe un int 
     * @param value
     * @return 
     */
    public static byte[] intToByteArray(int value) {
    return new byte[] {
            (byte)(value),
            (byte)(value >>> 8),
            (byte)(value >>> 16),
            (byte)(value >>> 24)};
    }
    
    
    public static byte[] readPostingFiles(RandomAccessFile fileReader,long pointer,int lenght){
        try {
            int header = 0;
            int totalPostingLenght = (lenght *  postingEntryLenght) + header;
            //int totalPostingLenght = lenght *  postingEntryLenght;
            IndexFileUtils.getInstance().getByteHelper().setByteContainer(new byte[totalPostingLenght]);
            //byte[] data = new byte[totalPostingLenght];
            fileReader.seek(pointer);
            fileReader.read(IndexFileUtils.getInstance().getByteHelper().byteContainer, 0,totalPostingLenght);
            //fileReader.read(data, 0, lenght *  postingEntryLenght);
            //return data;
            return IndexFileUtils.getInstance().getByteHelper().byteContainer;
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
       
    
    /**
     *
     */
    /*public void createShortFiles(String filenamepreffix) throws FileNotFoundException{
        FileOutputStream vocFileStream = this.prepareOutputStream(filenamepreffix + vocFileSuffix + ".dat");
        FileOutputStream postingFileStream = this.prepareOutputStream(filenamepreffix + postingFileSuffix + ".dat");
        Scanner sc = new Scanner(IndexFileUtils.getInstance().getVocFileObject());
        while (sc.hasNextLine()) {
            String termLine = sc.nextLine();
            String[] termInfo = termLine.split("\t");
            String term = termInfo[0];
            int docFreq = Integer.parseInt(termInfo[1]);
            int postingPointer = Integer.parseInt(termInfo[2]);
            if (docFreq >= POSTING_GENERIC_MIN){
                List postingData = this.parsePosting(docFreq,this.readPostingFiles(IndexFileUtils.getInstance().getPostingFileReader(),postingPointer, docFreq));
                this.persistTermData(term,docFreq,postingPointer);
            }
        }
        sc.close();
    }*/
    
    /**
     * Persiste informacion de posting de un termino en un archivo binario.
     * 
     */
    private void persistTermData(String term,int docFreq,int postingPointer){
        System.out.println("Guardando datos de "  + term +  " " + docFreq + " " + postingPointer);
        
    }
    
    
    
    
    
    // METODOS PARA MANEJAR ARCHIVOS //
    
    
    private Object prepareStream(String filename,boolean read) throws FileNotFoundException{
        Object stream;
        if (read){
            stream = new FileInputStream(new File(filename));
        } else {
            stream = new FileOutputStream(new File(filename),false);
        }
        return stream;
    }
    
    
    private FileOutputStream prepareOutputStream(String filename) throws FileNotFoundException{
        return (FileOutputStream) this.prepareStream(filename, false);
    }
    
    private FileInputStream prepareInputStream(String filename) throws FileNotFoundException{
        return (FileInputStream) this.prepareStream(filename, true);
    }

    
    
    /**
     * Escribe los docs ID contenidos en posting data en el nuevo archivo indice.
     * Retorna un nuevo puntero, ubicado al inicio de escritura de estos datos
     * @param postingData
     * @return 
     */
    private void writeShortIndex(FileOutputStream indexStream,int[] postingData) {
        FileOutputStream os = null;
        try {
            for (int i = 0 ; i < postingData.length ; i++){
                byte[] intData = VocManager.intToByteArray(postingData[i]);
                    indexStream.write(intData, 0, intData.length);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void createNewVoc(String destFile) {
        this.vocParser.generateVoc(IndexFileUtils.getInstance().getVocFileObject(), new File(destFile));
    }
    
    
    private void printStatus(int linesRead) {
        if ((linesRead % 10000) == 0){
            System.out.print(linesRead + " términos leídos...\r");
        }
    }
    
    
    
    ////////////////////////////// ESTADISTICAS /////////////////////////////////////////
    
    
    
    public void printStats(){
        System.out.println("Cant. terminos:" + this.vocterms);
        Iterator entries = this.postingsDistrib.entrySet().iterator();
        while (entries.hasNext()) {
            Entry thisEntry = (Entry) entries.next();
            Object key = thisEntry.getKey();
            Object value = thisEntry.getValue();
            System.out.println(key + "  "  + value);
        // ...
        }
    }
    
    public void printStats(String filename){
        FileWriter fw = null;
        try {
            fw = new FileWriter(filename);
            fw.append("Cant. terminos:" + this.vocterms+"\n");
            Iterator entries = this.postingsDistrib.entrySet().iterator();
            // Imprimo distribucion de postings
            fw.append("Long. Postings\tFreq\tLong*Freq\tTamaño en índice (bytes)\n");
            while (entries.hasNext()) {
                Entry thisEntry = (Entry) entries.next();
                Object key = thisEntry.getKey();
                Object value = thisEntry.getValue();
                fw.append(key +"\t" + value+"\t"+  (int)key*(int)value+ "\t" + ((int)key*(int)value*8)+"\n");
            }
            fw.append("Cant. terminos:" + this.vocterms+"\n");
            fw.write("// AHORRO ESPACIO //\n\n\n");
            SimulateData simulation = new SimulateData(fw, 
                                                       IndexFileUtils.getInstance().getPostingSizeBytes(), 
                                                       vocterms, 
                                                        (int) this.postingsDistrib.get(SINGLETON), 
                                                        (int) this.postingsDistrib.get(DOUBLETON), 
                                                        this.postingsDistrib);
            simulation.simulateAveragePostings(postingAverageLenght);
            //simulation.simulateRandomSimulation();
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void printAhorroEspacio(FileWriter fw,double cantSingletons,double cantDoubletons) throws IOException{

    }

    private void updateStats(String[] termInfo) {
        int docFreq = Integer.parseInt(termInfo[1]);
        this.vocterms++;
        // Actualizo distrib postings
        if (this.postingsDistrib.containsKey(docFreq)){
            this.postingsDistrib.put(docFreq,(int)this.postingsDistrib.get(docFreq)+1); 
        } else {
            this.postingsDistrib.put(docFreq, 1);
        }
    }

    private void printVocabSize(int treeSize) {
        FileWriter fw = null;
        try {
            fw = new FileWriter("treeSizeStats.txt",true);
            //File vocDB = new File("vocabulary.db");
            File vocLG = new File("vocabulary.lg");
            //System.out.println("Tam voc db: "  + vocDB.length());
            fw.write(treeSize + "\t"  + vocLG.length()+"\n");
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void removeVocab(){
        File vocDB = new File("vocabulary.db");
        File vocLG = new File("vocabulary.lg");
        vocLG.delete();
    }
    
    
    public void createSporadicIndex(){
        try {
            int linesRead = 0;
            long postlenghtCounter = 0;
            File sporadicVocFile = new File("sporadic_" + IndexFileUtils.getInstance().getVocFileObject().getName());
            System.out.println(sporadicVocFile.getPath());
            BufferedReader br = new BufferedReader(new FileReader(IndexFileUtils.getInstance().getVocFileObject()));
            VocParser outputVocParser = new SporadicVoc();
            outputVocParser.setWriter(new FileWriter(sporadicVocFile));
            String line = br.readLine();
            while (line != null) {
                linesRead++;
                String[] termInfo = this.vocParser.parseLine(line);
                PostingInfo postingInfo = this.getPostingInfo(termInfo);
                // NUEVO
                // Codigo para crear un nuevo indice corto, sin singletons y doubletons
               if (postingInfo.getPostingLenght() >= POSTING_GENERIC_MIN){
                     //Escribir informacion de la posting en el nuevo indice
                     //Obtener nuevo puntero y actualizarlo
                    //long newPointer = IndexFileUtils.getInstance().getShortPostingFileObject().length();
                    this.writeShortIndex(IndexFileUtils.getInstance().getOsShortPostingFile(),postingInfo.getPostingData());
                    //((PostingInfoGeneric) postingInfo).setPostingPointer((int) newPointer);
                }
                //this.writeSporadicVoc(termInfo,postingInfo,outputVocParser,this.POSTING_GENERIC_MIN);
                //this.updateStats(termInfo);
                postlenghtCounter += Integer.parseInt(termInfo[1]);
                this.printStatus(linesRead);
                line = br.readLine();
            }
            if (IndexFileUtils.getInstance().createShortPosting){
                try {
                    //IndexFileUtils.getInstance().setPostingFileObject(IndexFileUtils.getInstance().getShortPostingFileObject());
                    IndexFileUtils.getInstance().getOsShortPostingFile().close();
                } catch (IOException ex) {
                    Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            outputVocParser.getWriter().close();
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void createSporadicVoc(){
        try {
            int linesRead = 0;
            long postlenghtCounter = 0;
            File sporadicVocFile = new File("sporadic_" + IndexFileUtils.getInstance().getVocFileObject().getName());
            System.out.println(sporadicVocFile.getPath());
            BufferedReader br = new BufferedReader(new FileReader(IndexFileUtils.getInstance().getVocFileObject()));
            VocParser outputVocParser = new SporadicVoc();
            outputVocParser.setWriter(new FileWriter(sporadicVocFile));
            String line = br.readLine();
            long calculatedPostingPointer = 0; // puntero al indice calculado. Se supone que cada entrada ocupa 4 bytes
            while (line != null) {
                linesRead++;
                String[] termInfo = this.vocParser.parseLine(line);
                PostingInfo postingInfo = this.getPostingInfo(termInfo);
                termInfo[2] = String.valueOf(calculatedPostingPointer);
                this.writeSporadicVoc(termInfo,postingInfo,outputVocParser,this.POSTING_GENERIC_MIN);
                //this.updateStats(termInfo);
                if (Long.parseLong(termInfo[1]) > this.POSTING_GENERIC_MIN){
                    calculatedPostingPointer += (Long.parseLong(termInfo[1]) * 4);
                } else {
                    int a = 2;
                }
                //postlenghtCounter += Integer.parseInt(termInfo[1]);
                this.printStatus(linesRead);
                line = br.readLine();
            }
            outputVocParser.getWriter().close();
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(VocManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    
    private void writeSporadicVoc(String[] termInfo,PostingInfo postingInfo,VocParser outputParser,int minGenericLenght){
        ((SporadicVoc)outputParser).writeVocLine(termInfo,postingInfo,minGenericLenght);
    }
    
    
    ///////////////// QUERY LOG's ////////////////////////////////////////////////
    
    public void cleanQueryLog(File queryFile,File destFile) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(queryFile));
        FileWriter fw = new FileWriter(destFile);
        TermData data;
        String line = br.readLine();
        int i,count;
        while (line != null) {
            String[] queryTerms = line.split(" ");
            String info = "";
            count = 0;
            for (i = 0;i<=queryTerms.length-1;i++){
                // Busco que el termino existe en el voc, sino lo elimino
                data = this.findTerm(queryTerms[i]);
                if (data != null) { //se encontro el termino
                    info += (count == 0 ) ?  queryTerms[i] : " " + queryTerms[i];
                    count++;
                }
            }
            if (!info.equals("")){
                fw.write(info);
                fw.write("\n");
            }
            line = br.readLine();
        }
        br.close();
    }
    
    
    
    public void searchQueryFile(File queryFile, File destFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(queryFile));
        FileWriter fw = new FileWriter(destFile);
        TermData data;
        String line = br.readLine();
        short i,k = 0;
        long partialTimeA,partialTimeB;
        float partialTime;
        long totalTimeA = Utils.getTime();
        while (line != null) {
            try {
                k++;
                //String[] queryTerms = line.split(" ");
                ArrayList queryTerms = customIndexOf(line," ");
                partialTimeA = Utils.getTime();
                for (i = 0;i<=queryTerms.size()-1;i++){
                    // Busco termino en el vocabulario, y obtengo info de positng list
                    String term = (String)queryTerms.get(i);
                    data = this.findTerm(term);
                    int[] postingData = data.getPostingInfo().getPostingData(); // Solamente los pido, no los imprimo    
                    postingData = null;
                    data = null;
                }
                /*if ((k % 100)== 0){ // LLamo a recolector de basura cada diez mil queries
                    System.gc();
                    System.out.println("Limpiando...");
                }*/
                partialTimeB = Utils.getTime();
                partialTime = Utils.substractTimes(partialTimeB, partialTimeA) / (float)1000000;
                fw.write(line + "\t" + Float.toString(partialTime)  + "\n");
            } catch (Exception e){
                System.out.println("Error mientras se procesaba linea: " + line);
                System.out.println(e);
            } finally {
                line = br.readLine();
            }
        }
        long totalTimeB = Utils.getTime();
        float totalTime = Utils.substractTimes(totalTimeB, totalTimeA) / (float)1000000;
        fw.write(Float.toString(totalTime));
        fw.flush();
        br.close();
    }
    
    public final ArrayList customIndexOf(String line,String separator) {
        ArrayList response = new ArrayList();
        int i = line.indexOf(separator);
        int index = 0;
        while(i >= 0) {
             //System.out.println(line.substring(0, i) + "\t");
             response.add(line.substring(0, i));
             line = line.substring(i+1);
             i = line.indexOf(' ');
             index++;
        }
        //System.out.println(line + "\n");
        response.add(line); // agrego ultimo termino
        return response;
    }  
    
    /**
     * Analiza a cantidad de terminos singletons y doubletons que se ven en los queries, teniendo
     * en cuenta el vocabulario actual.
     * Queryfile debe ser un query log adaptado a este vocabulario, no tiene terminos que no existen en el mismo.
     * @param queryFile
     * @throws IOException 
     */
    public void analizeQueryLog(File queryFile) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(queryFile));
        String line = br.readLine();
        int i,singletons = 0,doubletons = 0,tripletons = 0,quadrupletons = 0,totalTerms = 0;
        TermData data;
        System.out.println("Analizando el archivo de queries...");
        while (line != null) {
            ArrayList queryTerms = this.customIndexOf(line," ");
            for (i = 0;i<=queryTerms.size()-1;i++){
                data = this.findTerm((String)queryTerms.get(i));
                switch (data.getDf()){
                    case 1: singletons++;
                            break;
                    case 2: doubletons++;
                            break;
                    case 3: tripletons++;
                            break;
                    case 4: quadrupletons++;
                            break;
                    default: totalTerms++;
                             break;
                }
            }
            line = br.readLine();
        }
        System.out.println("Total: " + totalTerms + "\t" + "Singletons:" + singletons + "\t" + "Doubletons: " + doubletons + "Tripletons:" + tripletons + "\t"  + "Quadrupletons:" + quadrupletons + "\t" );
    }
    
    ////////////////////////// CALCULO TIEMPOS TEORICOS ////////////////////////////////
    
    
    // modo puede ser cero o uno.
    // 0-> computa tiempos como si fuera un sistema no esporadico
    // 1-> computa tiempos como si fuera un sistema esporadico (term esporadicos en vocabulario)
    public void computeTeoricTimes(int modo,File vocTimesFile,File queryLogFile,File destFile) throws FileNotFoundException, IOException{
        HashMap<String,ArrayList> vocMap = this.getVocabularyTeoricTimes(vocTimesFile);
        FileWriter fw = new FileWriter(destFile);
        BufferedReader br = new BufferedReader(new FileReader(queryLogFile));
        String line = br.readLine();
        long timeRead;
        float totalTime = 0;
        System.out.println("Calculando tiempos teóricos del query log...");
        while (line != null) {
            timeRead = 0;
            ArrayList queryTerms = this.customIndexOf(line," ");
            for (int i = 0;i<=queryTerms.size()-1;i++){
                ArrayList termInfo = vocMap.get(queryTerms.get(i)); // [0]-> DF;[1]-> tiempoTeorico
                if ((modo == 1) && Integer.parseInt((String)termInfo.get(0)) <= 4){
                    timeRead += 0;
                    System.out.println(queryTerms.get(i) + "\t" + termInfo.get(1));
                } else {
                    timeRead += Double.parseDouble((String)termInfo.get(1));
                }   
            }
            totalTime += timeRead;
            fw.write(line + "\t" + timeRead + "\n");
            line = br.readLine();
        }
        fw.write(Float.toString(totalTime));
        fw.flush();
    }
    
    private HashMap<String,ArrayList> getVocabularyTeoricTimes(File vocTimesFile) throws FileNotFoundException, IOException{
        System.out.println("Contruyendo tabla de tiempos teoricos del vocabulario...");
        HashMap<String,ArrayList> vocMap = new HashMap<String,ArrayList>();
        BufferedReader br = new BufferedReader(new FileReader(vocTimesFile));
        String line = br.readLine();
        while (line != null) {
            String[] vocLine = this.vocParser.parseLine(line);
            ArrayList termInfo = new ArrayList();
            termInfo.add(vocLine[1]);
            termInfo.add(vocLine[2]);
            vocMap.put(vocLine[0], termInfo);
            line = br.readLine();
        }
        return vocMap;
    }
    
    
    public void computeVocabularyTimes(File destFile) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(IndexFileUtils.getInstance().getVocFileObject()));
        FileWriter fw = new FileWriter(destFile);
        String line = br.readLine();
        TermData data;
        long partialTimeA,partialTimeB;
        float partialTime;
        System.out.println("Calculando tiempos de los términos del vocabulario...");
        while (line != null) {
            String[] queryTerms = this.vocParser.parseLine(line); // queryTerms[0]->termino,queryTerms[1]->df,queryTerms[2]->puntero
            partialTimeA = Utils.getTime();
            data = this.findTerm((String)queryTerms[0]);
            int[] postingData = data.getPostingInfo().getPostingData();
            partialTimeB = Utils.getTime();
            partialTime = Utils.substractTimes(partialTimeB, partialTimeA) / (float)1000000;
            fw.write(queryTerms[0] + "\t" + queryTerms[1]  + "\t"+  partialTime +  "\n");
            line = br.readLine();
        }
        fw.flush();
        //System.out.println("Total: " + totalTerms + "\t" + "Singletons:" + singletons + "\t" + "Doubletons: " + doubletons);
    }
    
    
    
    
    
    
    
    //////////////// GETTERS & SETTERS //////////////////////////////////////
    
    
    public static String getDATABASE() {
        return DATABASE;
    }

    public static void setDATABASE(String DATABASE) {
        VocManager.DATABASE = DATABASE;
    }

    public static String getBTREE_NAME() {
        return BTREE_NAME;
    }

    public static void setBTREE_NAME(String BTREE_NAME) {
        VocManager.BTREE_NAME = BTREE_NAME;
    }

    public static String getVocFileSuffix() {
        return vocFileSuffix;
    }

    public static void setVocFileSuffix(String vocFileSuffix) {
        VocManager.vocFileSuffix = vocFileSuffix;
    }

    public static String getPostingFileSuffix() {
        return postingFileSuffix;
    }

    public static void setPostingFileSuffix(String postingFileSuffix) {
        VocManager.postingFileSuffix = postingFileSuffix;
    }

    public int[] getTreeSizes() {
        return treeSizes;
    }

    public void setTreeSizes(int[] treeSizes) {
        this.treeSizes = treeSizes;
    }

    public static byte getPrefixLenght() {
        return prefixLenght;
    }

    public static void setPrefixLenght(byte prefixLenght) {
        VocManager.prefixLenght = prefixLenght;
    }

    public static byte getPostingEntryLenght() {
        return postingEntryLenght;
    }

    public static void setPostingEntryLenght(byte postingEntryLenght) {
        VocManager.postingEntryLenght = postingEntryLenght;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isRemoveSporadics() {
        return removeSporadics;
    }

    public void setRemoveSporadics(boolean removeSporadics) {
        this.removeSporadics = removeSporadics;
    }

    public boolean isForceVocReload() {
        return forceVocReload;
    }

    public void setForceVocReload(boolean forceVocReload) {
        this.forceVocReload = forceVocReload;
    }

    public VocParser getVocParser() {
        return vocParser;
    }

    public void setVocParser(VocParser vocParser) {
        this.vocParser = vocParser;
    }

    public Map getPostingsDistrib() {
        return postingsDistrib;
    }

    public void setPostingsDistrib(Map postingsDistrib) {
        this.postingsDistrib = postingsDistrib;
    }

    public long getPostingAverageLenght() {
        return postingAverageLenght;
    }

    public void setPostingAverageLenght(long postingAverageLenght) {
        this.postingAverageLenght = postingAverageLenght;
    }

    public int getVocterms() {
        return vocterms;
    }

    public void setVocterms(int vocterms) {
        this.vocterms = vocterms;
    }

    public static RecordManager getRecman() {
        return recman;
    }

    public static void setRecman(RecordManager recman) {
        VocManager.recman = recman;
    }

    public static long getRecid() {
        return recid;
    }

    public static void setRecid(long recid) {
        VocManager.recid = recid;
    }

    public static Tuple getTuple() {
        return tuple;
    }

    public static void setTuple(Tuple tuple) {
        VocManager.tuple = tuple;
    }

    public BTree getVocBTree() {
        return vocBTree;
    }

    public void setVocBTree(BTree vocBTree) {
        this.vocBTree = vocBTree;
    }

    public static Properties getProps() {
        return props;
    }

    public static void setProps(Properties props) {
        VocManager.props = props;
    }

    
    
    
    
}
