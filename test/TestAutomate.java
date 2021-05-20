/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import automate.AEF;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import regex.Syntax;

/**
 *
 * @author adriwankenoby
 */
public class TestAutomate {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {

        long Ti;
        long Tf;
        String arg;
        String s;
        AEF aef = null;
        boolean lecture = false, determinise = false, complete = false, export = false;

        for (int i = 0; i < args.length; ++i) {

            switch (args[i]) {                
                case "-f":                    
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Pas de fichier après -f");

                    } else if (aef != null) {
                        throw new IllegalArgumentException("Vous avez deja creer un automate avec la commande -r\n");

                    } else {

                        Ti = System.currentTimeMillis();

                        aef = new AEF(args[i + 1]);

                        Tf = System.currentTimeMillis();

                        aef.E.toImg();

                        s = "temps pour instancier l'objet automate " + aef.E.fileName + " en memoire " + " : " + (Tf - Ti) + "ms\n"
                                + "Vous pouvez visualiser l'automate : " + AEF.DOSSIER + '/' + AEF.DOT + '/' + aef.E.fileName + "000.png\n"
                                + "Le fichier dot associer : " + AEF.DOSSIER + '/' + AEF.DOT + '/' + aef.E.fileName + "000.dot\n";
                        System.out.println(s);
                    }
                    ++i;
                    break;
                    
                case "-r":                    
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Pas d'expression reguliere preciser apres -r\n");

                    } else if (aef != null) {
                        throw new IllegalArgumentException("Vous avez deja creer un automate avec la commande -f\n");

                    } else {

                        Ti = System.currentTimeMillis();
                        
                        aef = new AEF(Syntax.analyse(args[i + 1]).build());

                        Tf = System.currentTimeMillis();

                        aef.E.toImg();

                        s = "temps pour instancier l'objet automate " + aef.E.fileName + " en memoire a partir de l'expression reguliere : " + args[i + 1] + " : " + (Tf - Ti) + "ms\n"
                                + "Vous pouvez visualiser l'automate : " + AEF.DOSSIER + '/' + AEF.DOT + '/' + aef.E.fileName + "000.png\n"
                                + "Le fichier dot associer : " + AEF.DOSSIER + '/' + AEF.DOT + '/' + aef.E.fileName + "000.dot\n";
                        System.out.println(s);
                    }
                    ++i;
                    break;
                    
                case "-l":                    
                    lecture = true;
                    break;
                    
                case "-d":
                    determinise = true;
                    break;
                    
                case "-c":
                    complete = true;
                    break;
                    
                case "-e":
                    export = true;
                    break;
                    
                default:
                    throw new IllegalArgumentException("Argument inconnu : " + args[i]);
            }
        }

        if (aef != null) {
            if (determinise) {
                Ti = System.currentTimeMillis();

                aef = new AEF(aef.E.determinise());

                Tf = System.currentTimeMillis();

                aef.E.toImg();

                s = "temps pour detereminiser l'automate : " + (Tf - Ti) + "ms\n"
                        + "Vous pouvez visualiser l'automate determiniser : " + AEF.DOSSIER + '/' + AEF.PNG + aef.E.fileName + "000.png\n"
                        + "Le fichier dot associer : " + AEF.DOSSIER + '/' + AEF.DOT + '/' + aef.E.fileName + "000.dot\n";
                System.out.println(s);
            }

            if (complete) {
                Ti = System.currentTimeMillis();

                aef = new AEF(aef.E.complete());

                Tf = System.currentTimeMillis();

                aef.E.toImg();
                s = "temps pour completer l'automate : " + (Tf - Ti) + "ms\n"
                        + "Vous pouvez visualiser l'automate complet : " + AEF.DOSSIER + '/' + AEF.PNG + '/' + aef.E.fileName + "000.png\n"
                        + "Le fichier dot associer : " + AEF.DOSSIER + '/' + AEF.DOT + '/' + aef.E.fileName + "000.dot\n";
                System.out.println(s);
            }

            if (export) {
                aef.writeDescr();
                s = "exportation au format .descr consultable : " + AEF.DOSSIER + '/' + AEF.DESCR + aef.E.fileName + ".descr\n";
                System.out.println(s);
            }

            if (lecture) {
                aef.lire(new BufferedReader(new InputStreamReader(System.in)));
                s = "parcours de l'automate lors de la lecture visualisble dans : " + AEF.DOSSIER + '/' +AEF.GIF + aef.E.fileName + "?.gif\n";
                System.out.println(s);
            }
        }
    }
}
