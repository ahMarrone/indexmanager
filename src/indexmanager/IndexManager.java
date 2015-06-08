/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexmanager;


import asg.cliche.ShellFactory;
import helpers.VocManager;
import indexmanager.utils.IndexFileUtils;
import indexmanager.utils.Utils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import parsers.RelativeVocParser;
import parsers.ScaleVocParser;
import parsers.SimpleVocParser;
import parsers.SporadicVoc;
import parsers.UKParser;
import parsers.VocParser;

/**
 *
 * @author Agustin
 */
public class IndexManager {

    private static VocManager vocManager = null;
    
    
    public static void main( String[] args ) {
        CommandLine cmd = IndexManager.parseArgs(args);
        if (cmd.hasOption("action")) {
            String vocFileOpt = cmd.getOptionValue("voc");
            String postingFileOpt = cmd.getOptionValue("post");
            VocParser vocParser = IndexManager.getVocParser(cmd.getOptionValue("voctype"));
            int mode = (cmd.hasOption("d")) ? 1 : 0;
            boolean removeSporadics = (cmd.hasOption("r"));
            boolean createShort = cmd.hasOption("c") || cmd.getOptionValue("action").equals("makevoc");
            if (cmd.hasOption("voc") && cmd.hasOption("post")){
                IndexFileUtils.initFileUtils(vocFileOpt, postingFileOpt,createShort);
            } else {
                IndexFileUtils.initFileUtils(createShort);
            }
            // Si elijo crear un nuevo archivo corto de postings, obligo a que se regenere el arbol-B
            boolean forceReload = cmd.hasOption("f");
            vocManager = new VocManager(mode,removeSporadics,forceReload,vocParser);
            doAction(cmd.getOptionValue("action"));
        } else {
            System.out.println("Por favor, especifique acción a realizar (parámetro -action)");
        }
    }
    
    
    private static void doAction(String action){
        if (action.equals("maketree")){
            vocManager.loadBTree();
            String infoModo = (vocManager.getMode() == 0) ? "estatico" : "dinamico";
            System.out.println("IndexManager creado en modo " + infoModo);
            presentCLI(vocManager);
        } else if (action.equals("makesporadicvoc")){
            vocManager.createSporadicVoc();
        } else if (action.equals("makesporadicindex")){
            vocManager.createSporadicIndex();
        }
    }
    
    
    private static void presentCLI(VocManager vocManager){
        try {
            ShellFactory.createConsoleShell("indexmanagerCLI", "", new CLIManager(vocManager)).commandLoop();
        } catch (IOException ex) {
            System.out.println("Comando inválido");
        }
    }
    
    
    /**
     * 
     */
    private static CommandLine parseArgs(String[] args){
        CommandLine cmd = null;
        try {
            Options options = new Options();
            Option force = new Option( "f", "Forzar recarga del arbol B-tree (lo genera nuevamente aunque ya este guardado en disco)" );
            Option removeSporadics = new Option( "r", "Remover terminos esporadicos del indice y almacenarlos en el vocabulario" );
            Option createShort = new Option("c", "Crear nuevo archivo corto de postings, y trabajar con el mismo");
            Option dynamic = new Option("d", "Crear vocabulario en modo dinamico");
            options.addOption(force);
            options.addOption(removeSporadics);
            options.addOption(createShort);
            options.addOption(dynamic);
            options.addOption("newvoc", true, "Si se envia como argumento, se creara un nuevo tipo de voc (de tipo {voctype}), a partir de {voc}");
            options.addOption("action", true, "Accion a realizar. [maketree,makevoc]");
            options.addOption("voctype", true, "Tipo de vocabulario a utilizar. n = normal, s = scalevoc, r = relativevoc");
            //options.addOption("m", true, "Modo del vocabulario. 0 = estatico, 1= dinamico");
            options.addOption("voc", true, "Ruta al archivo vocabulario");
            options.addOption("post", true, "Ruta al archivo de postings");
            CommandLineParser parser = new BasicParser();
            cmd = parser.parse(options,args);
        } catch (ParseException ex) {
            Logger.getLogger(IndexManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cmd;
    }

    private static VocParser getVocParser(String optionValue) {
        if (optionValue.equals("s")){
            return new ScaleVocParser(); 
        } else if(optionValue.equals("r")){
            return new RelativeVocParser();
        } else if(optionValue.equals("uk")){
            return new UKParser();
        } else if(optionValue.equals("sporadic")){
            return new SporadicVoc();
        } else {
            return new SimpleVocParser();
        }
    }
    
}
