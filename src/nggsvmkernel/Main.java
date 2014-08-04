/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nggsvmkernel;

import gr.demokritos.iit.conceptualIndex.structs.Distribution;
import gr.demokritos.iit.jinsect.documentModel.comparators.NGramCachedGraphComparator;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.structs.CategorizedFileEntry;
import gr.demokritos.iit.jinsect.structs.DocumentSet;
import gr.demokritos.iit.jinsect.utils;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ggianna
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.err.println("Syntax: " + Main.class.getCanonicalName() + " inputDir/ [outputFile.txt]");
        
        DocumentSet dsTrain = new DocumentSet(args[0], 1.0);
        Map<String,DocumentNGramGraph> cache = new
                HashMap<>();
        
        dsTrain.createSets();
        
        List<CategorizedFileEntry> lTraining = (List<CategorizedFileEntry>)
                dsTrain.getTrainingSet();
        
        // Init result buffer
        StringBuffer sb = new StringBuffer();
        // For each training file
        int iCnt = 1;
        
        Distribution<Double> dIntraClass = new Distribution<>();
        Distribution<Double> dInterClass = new Distribution<>();
        
        for (final CategorizedFileEntry cfe : lTraining) {
            // Get label (int) from category
            String sLabel = String.valueOf(cfe.getCategory().hashCode());
            sb.append(sLabel);
            
            // Append ID
            sb.append(" 0:" + iCnt++ + " ");
            
            // Create graph
            DocumentNGramGraph dgCur;
            if (cache.containsKey(cfe.getFileName()))
                dgCur = cache.get(cfe.getFileName());
            else {
                dgCur = new DocumentNGramGraph();
                dgCur.setDataString(utils.loadFileToStringWithNewlines(cfe.getFileName()));
                synchronized (cache) {
                    cache.put(cfe.getFileName(), dgCur);
                }
            }
            
            NGramCachedGraphComparator ngc = new NGramCachedGraphComparator();
            
            // For each training file
            int iCntOther = 1;
            for (CategorizedFileEntry cfeOther : (List<CategorizedFileEntry>)dsTrain.getTrainingSet()) {
                // Do not compare with self
                if (iCnt == iCntOther++)
                    continue;
                
                // Create graph
                DocumentNGramGraph dgCurOther;
                if (cache.containsKey(cfeOther.getFileName()))
                    dgCurOther = cache.get(cfeOther.getFileName());
                else {
                    dgCurOther = new DocumentNGramGraph();
                    dgCurOther.setDataString(utils.loadFileToStringWithNewlines(
                            cfeOther.getFileName()));
                    synchronized (cache) {
                        cache.put(cfeOther.getFileName(), dgCurOther);
                    }
                }
                
                
                double dSim =  ngc.getSimilarityBetween(dgCur, 
                        dgCurOther).ValueSimilarity / Math.sqrt(dsTrain.getTrainingSet().size());
                
                if (cfe.getCategory().equalsIgnoreCase(cfeOther.getCategory()))
                    dIntraClass.increaseValue(dSim, 1.0);
                else
                    dInterClass.increaseValue(dSim, 1.0);
                
                sb.append("" + iCntOther + ":" + dSim + " ");
                // DEBUG INFO
                if (iCntOther % 10 == 0)
                    System.err.print(".");
                if (iCntOther % 100 == 0)
                    System.err.println();
            }
            // Next instance
            sb.append("\n");
                        
            // DEBUG INFO
            System.err.println(iCnt + "*");
        }
        
        
        // If no output file has been declared
        if (args.length < 2)            
            // Output result file to stdout
            System.out.println(sb.toString());
        else
        {
            try {
                // Open file
                FileWriter fw = new FileWriter(args[1]);
                // Write
                fw.append(sb.toString());
                // Close file
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, 
                        "Could not write output file. Trying standard output.", ex);
                // Output result file to stdout
                System.out.println(sb.toString());
            }
        }
        
        double dGamma = dIntraClass.average(false) - dInterClass.average(false);
        System.err.println("Gamma: " + dGamma);
        System.err.println("Gamma: " + dGamma);
    }
    
}
