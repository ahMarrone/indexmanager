/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexmanager;

import asg.cliche.Command;
import helpers.VocManager;
import indexmanager.utils.IndexFileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Agustin
 */
public class CLIManager {
    
    private VocManager vocManager;

    public CLIManager(VocManager vocManager) {
        this.vocManager = vocManager;
    }
    
    
    @Command(description="Busca el termino y retorna informacion de su posting list", abbrev="s")
    public void search(String term) {
        this.vocManager.searchTerm(term);
    }
    
    @Command(description="Recorre el fichero y busca cada query presente en el (busca información de posting). Escribe"
            + "en archivo destino información de tiempos de búsqueda por cada línea, y al final el tiempo total del proceso", abbrev="qf")
    public void queryfile(String filepath,String filedest) {
        try {
            File queryFile = new File(filepath);
            File destFile = new File(filedest);
            this.vocManager.searchQueryFile(queryFile,destFile);
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    @Command(description="Recorre el query log y obtiene estadisticas: cantidad total de términos,"
            + " singletons y doubletons", abbrev="aq")
    public void analizequery(String filepath) {
        try {
            File queryFile = new File(filepath);
            this.vocManager.analizeQueryLog(queryFile);
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    
    @Command(description="Termina el programa", abbrev="q")
    public void quit() {
        System.exit(0);
    }
    
    @Command(description="A partir de los archivos del vocabulario y de la posting que se pasaron como argumentos al inicio"
            + ", crea dos nuevos archivos (voc y posting). El archivo vocabulario se adaptará a un nuevo archivo de postings, el"
            + "cual no contendrá los singletons y doubletons")
    public void createshortfiles(String filenamepreffix){
    /*    try {
            this.vocManager.createShortFiles(filenamepreffix);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CLIManager.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    @Command(description= "Imprime todas las entradas del vocabulario")
    public void printvoc(){
        this.vocManager.printVocabulary();
    }
    
    @Command(description = "Imprime el archivo de posting")
    public void printpostingfile(){
        System.out.println(IndexFileUtils.getInstance().getPostingFileObject().getPath());
    }
    
    @Command(description = "Imprime estadisticas", abbrev="st")
    public void stats(){
        this.vocManager.printStats();
    }
    
    @Command(description = "Imprime estadisticas", abbrev="st")
    public void stats(String filename){
        this.vocManager.printStats(filename);
    }
    
    
    @Command(description = "Genera un nuevo archivo de queries, el cual contiene solo aquellos queries"
            + "que tengan los términos presentes en el vocabulario actual", abbrev="cq")
    public void cleanquery(String queryFileString,String destFileString){
        try {
            File queryFile = new File(queryFileString);
            File destFile = new File(destFileString);
            this.vocManager.cleanQueryLog(queryFile, destFile);
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    @Command(description = "Busca cada termino del vocabulario en el indice e imprime tiempo de búsqueda"
            + "en el archivo destino", abbrev="cq")
    public void computeVocabularyTimes(String destFileString){
        try {
            File destFile = new File(destFileString);
            this.vocManager.computeVocabularyTimes(destFile);
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    @Command(description = "A partir de un archivo que contiene los tiempos de búsqueda para cada termino del vocabulario de forma individual"
            + ", calcula cual seria el tiempo teorico para responder a un query log", abbrev="cq")
    public void computeTeoricTimes(String modo,String vocFileString,String queryLogFileString,String destFileString){
        try {
            File vocTimesFile = new File(vocFileString);
            File queryLogFile = new File(queryLogFileString);
            File destFile = new File(destFileString);
            int mode = Integer.parseInt(modo);
            this.vocManager.computeTeoricTimes(mode,vocTimesFile,queryLogFile,destFile);
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    
}
