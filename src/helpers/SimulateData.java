/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package helpers;

import indexmanager.utils.IndexFileUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author agustin
 */
public class SimulateData {
    private final long totalIndexSize;
    private final int cantTerminos;
    private final int initialSingletons;
    private final int initialDoubletons;
    private final Map postingsDistrib;
    private final FileWriter fw;
    
      
    // Matriz de porcentajes
    private int[] singletonsPercentages = new int[]{40,50};
    private int[] doubletonsPercentages = new int[]{10,30};
    
    private int singletonPostingSize = 25;
    private int doubletonPostingSize = 29;
    
    private int[] averagePostingPercentages = new int[]{-10,10,20,30,50};

    public SimulateData(FileWriter fw,long totalIndexSize,int cantTerminos,int initialSingletons,int initialDoubletons,Map postingsDistrib) {
        this.fw = fw;
        this.totalIndexSize = totalIndexSize;
        this.cantTerminos = cantTerminos;
        this.initialSingletons = initialSingletons;
        this.initialDoubletons = initialDoubletons;
        this.postingsDistrib = postingsDistrib;
    }
    
    
    public void simulateAveragePostings(long averagePostingLenght) throws IOException{
        // Simulo con los datos originales
        this.performAverageSimulation(averagePostingLenght, initialSingletons, initialDoubletons);
        // Comienzo simulacion con alteracion de parametros
        for (int k = 0; k < this.averagePostingPercentages.length;k++){
            long averagePosting = averagePostingLenght + (this.averagePostingPercentages[k] * averagePostingLenght) /100;
            for (int i = 0; i < this.singletonsPercentages.length;i++){
                int porcentajeSingletons = this.singletonsPercentages[i];
                for (int j = 0; j< this.doubletonsPercentages.length;j++){
                    int porcentajeDoubletons = this.doubletonsPercentages[j];
                    double cantSingletons = (double)porcentajeSingletons * this.cantTerminos / 100;
                    double cantDoubletons = (double)porcentajeDoubletons * this.cantTerminos / 100;
                    this.performAverageSimulation(averagePosting, cantSingletons,cantDoubletons); 
                }
            }
        }
    }
    
    private void performAverageSimulation(long avgPostingLenght,double cantSingletons,double cantDoubletons) throws IOException{
        int excesoPostings = (int) ((cantSingletons - this.initialSingletons) + (cantDoubletons - this.initialDoubletons));
        fw.write("SIMULACION -  TAMAÑO PROMEDIO POSTINGS (Cant.de docs ID's que contiene):" + avgPostingLenght + "\n"); 
        fw.write("Tamaño posting singleton: " + singletonPostingSize  +   " (bytes), Tamaño posting doubleton: " +  doubletonPostingSize   +" (bytes)\n"); 
        fw.write("Cant. singletons: " + cantSingletons + "  (%" +((cantSingletons / (double)this.cantTerminos) * 100) + ")\n");
        fw.write("Cant. doubletons: " + cantDoubletons + "(%" + ((cantDoubletons / (double)this.cantTerminos) * 100) + ")\n");
        fw.write("(Exceso singletons y doubletons: " + excesoPostings  + "\n");
        double saveSingletons =  cantSingletons * singletonPostingSize;
        double saveDoubletons =  cantDoubletons * doubletonPostingSize;
        fw.write("Ahorro en singletons: " + saveSingletons + " bytes\n");
        fw.write("Ahorro en doubletons: " + saveDoubletons + " bytes\n");
        fw.write("Total ahorro: " + (saveSingletons + saveDoubletons) + " bytes\n");
        long totalIndexSize = IndexFileUtils.getInstance().getPostingSizeBytes() - (avgPostingLenght*4)*excesoPostings;
        fw.write("Tamaño índice (simulado): " + totalIndexSize + " bytes\n");
        fw.write("% ahorro en índice: " + (((saveSingletons + saveDoubletons) * 100) / totalIndexSize) + "\n");
        fw.write("\n\n\n");
    }
    
    
    
    
    
    
    
    //#############################//////////////////////////////////
    // RANDOM /////////////////////////////////////////////////////
    
    
    
    public void simulateRandomSimulation() throws IOException{
        // Simulo con los datos originales
        this.performRandomSimulation(initialSingletons, initialDoubletons);
        // Comienzo simulacion con alteracion de parametros
            for (int i = 0; i < this.singletonsPercentages.length;i++){
                int porcentajeSingletons = this.singletonsPercentages[i];
                for (int j = 0; j< this.doubletonsPercentages.length;j++){
                    int porcentajeDoubletons = this.doubletonsPercentages[j];
                    double cantSingletons = (double)porcentajeSingletons * this.cantTerminos / 100;
                    double cantDoubletons = (double)porcentajeDoubletons * this.cantTerminos / 100;
                    this.performRandomSimulation(cantSingletons,cantDoubletons); 
                }
            }
    }
    
    
    private void performRandomSimulation(double cantSingletons,double cantDoubletons) throws IOException{
        int excesoPostings = (int) ((cantSingletons - this.initialSingletons) + (cantDoubletons - this.initialDoubletons));
        System.out.println("Simulando...");
        fw.write("SIMULACION -  RANDOM  \n"); 
        fw.write("Cant. singletons: " + cantSingletons + "  (%" +((cantSingletons / (double)this.cantTerminos) * 100) + ")\n");
        fw.write("Cant. doubletons: " + cantDoubletons + "(%" + ((cantDoubletons / (double)this.cantTerminos) * 100) + ")\n");
        fw.write("(Exceso singletons y doubletons: " + excesoPostings  + "\n");
        double saveSingletons =  cantSingletons * 19;
        double saveDoubletons =  cantDoubletons * 24;
        long totalIndexSize = IndexFileUtils.getInstance().getPostingSizeBytes();
        // Elimino {excesoPostings} entradas. Resto la cantidad de bytes que voy eliminando
        // al tamaño total del índice
        ArrayList<Integer> longitudesPostings = this.crearListaLongitudes();
        Random random = new Random();
        int max = this.postingsDistrib.size() - 1;
        int min = 0;
        List<Integer> keys = new ArrayList<Integer>(this.postingsDistrib.keySet());
        for (int k = 0; k < excesoPostings;k++){
            boolean removed = false;
            while (!removed){
                // Obtengo la longitud de la posting random en  {randomkey}
                int randomPosition = random.nextInt(longitudesPostings.size());
                int postingLenght = longitudesPostings.get(randomPosition);
                if (postingLenght >= 3){
                    totalIndexSize -= postingLenght * 4;
                    longitudesPostings.remove(randomPosition);
                    removed = true;
                }
            }
        }
        fw.write("Ahorro en singletons: " + saveSingletons + " bytes\n");
        fw.write("Ahorro en doubletons: " + saveDoubletons + " bytes\n");
        fw.write("Total ahorro: " + (saveSingletons + saveDoubletons) + " bytes\n");
        fw.write("Tamaño índice (simulado): " + totalIndexSize + " bytes\n");
        fw.write("% ahorro en índice: " + (((saveSingletons + saveDoubletons) * 100) / totalIndexSize) + "\n");
        fw.write("\n\n\n");
    }
    
    
    private  ArrayList<Integer> crearListaLongitudes(){
        ArrayList<Integer> longitudes = new ArrayList<>();
        Iterator entries = this.postingsDistrib.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            int key = (int)thisEntry.getKey();
            int value = (int)thisEntry.getValue();
            for (int i = 0; i < (int) value;i++){
                longitudes.add(key);
            }
    }
        return longitudes;
    }
    
    
    
    // ########### CALCULOS DE AHORRO  /#############################//
    
    
    public void calcularAhorros(){
        for (int i = 0; i < this.singletonsPercentages.length;i++){
                for (int j = 0; j< this.doubletonsPercentages.length;j++){
                    
                }
        }
    }
    
}
