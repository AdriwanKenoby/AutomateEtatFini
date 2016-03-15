/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * Representation d'un automate
 *
 * @author adriwankenoby
 */
public class AEF {

    static final String DOSSIER = "inpout-outpout-files",
            DESCR = "descr",
            DOT = "dot",
            PNG = "png",
            GIF = "gif",
            CMD = "./make_gif.sh",
            EOF = "###";

    public EnsEtat E;
    private String nom = "sansNom";
    private Set<Character> vocabulaireEntree;
    private Set<Character> vocabulaireSortie;
    private EnsEtat I;
    private EnsEtat F;

    public AEF() {
        this.E = new EnsEtat(new Etat("0", true, false));
        this.vocabulaireEntree = new TreeSet<>();
        this.vocabulaireSortie = new TreeSet<>();
        this.I = new EnsEtat(this.E.first());
        this.F = new EnsEtat();
    }

    public AEF(EnsEtat ens) {
        this.E = new EnsEtat(ens);
        this.E.fileName = ens.fileName;
        this.vocabulaireEntree = ens.alphabetIn();
        this.vocabulaireSortie = ens.alphabetOut();
        this.I = ens.initiaux();
        this.F = ens.acceptant();
    }

    public AEF(Collection<Etat> c) {
        this(new EnsEtat(c));
    }

    public AEF(SortedSet<Etat> set) {
        this(new EnsEtat(set));
    }

    /**
     * Initialise un automate a partir d'un fichier de description .descr
     *
     * @param fic
     * @throws IOException
     */
    public AEF(String fic) throws IOException {
        this();

        if (!fic.contains('.' + AEF.DESCR)) {
            throw new IllegalArgumentException("ERREUR -- format du fichier attendu .descr");
        }

        this.E.fileName = fic.substring(0, fic.length() - 6);
        this.readDescrFile(fic);
    }

    /**
     * lecture du format de fichier .decr
     *
     * @param fic fichier .descr
     * @throws IOException
     */
    private void readDescrFile(String fic) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(AEF.DOSSIER + '/' + AEF.DESCR + '/' + fic))) {

            String ligneLu;
            int numL = 0;

            while ((ligneLu = reader.readLine()) != null && !ligneLu.equals("")) {
                ++numL;
                switch (ligneLu.charAt(0)) {
                    case 'C':
                        this.nom = ligneLu.substring(3, ligneLu.length() - 1).trim();
                        break;

                    case 'M':
                        char meta = ligneLu.charAt(3);
                        if (!Transition.meta.equals(meta)) {
                            Transition.meta = meta;
                        }
                        break;

                    case 'V':
                        ligneLu = ligneLu.substring(3, ligneLu.length() - 1);

                        for (int i = 0; i < ligneLu.length(); ++i) {
                            this.vocabulaireEntree.add(ligneLu.charAt(i));
                        }
                        break;

                    case 'O':
                        ligneLu = ligneLu.substring(3, ligneLu.length() - 1);

                        for (int i = 0; i < ligneLu.length(); ++i) {
                            this.vocabulaireSortie.add(ligneLu.charAt(i));
                        }
                        break;

                    case 'E':

                        for (int i = 1; i < Integer.parseInt(ligneLu.substring(2)); ++i) {
                            this.E.add(new Etat(Integer.toString(i)));
                        }
                        break;

                    case 'N':

                        String[] tabS = ligneLu.substring(2).split(" ");

                        int i = 0;

                        for (Etat e : this.E) {
                            if (i > this.E.size()) {
                                throw new IOException("--Erreur dans le fichier .descr a la ligne " + numL
                                        + " l'etat " + tabS[i] + " vous avez defini un nomber d'etat < " + i);
                            }

                            e.setId(tabS[i]);
                            ++i;
                        }

                        break;

                    case 'I':
                        this.E.first().setInitial(false);
                        this.I.clear();
                        for (String s : ligneLu.substring(2).split(" ")) {

                            Etat e = this.E.getEtat(s);

                            if (e == null) {
                                throw new IOException("Erreur .descr ligne " + numL
                                        + " l'etat " + s + " n'est pas defini pour cet automate ");
                            }
                            e.setInitial(true);
                            this.I.add(e);
                        }
                        break;

                    case 'F':

                        for (String s : ligneLu.substring(2).split(" ")) {

                            Etat e = this.E.getEtat(s);

                            if (e == null) {
                                throw new IOException("--Erreur dans le fichier .descr a la ligne " + numL
                                        + " l'etat " + s + " n'est pas defini pour cet automate ");
                            }
                            e.setTerminal(true);
                            this.F.add(e);
                        }
                        break;

                    case 'T':

                        String[] ligneTsplit = ligneLu.substring(2).split(" ");

                        Etat init,
                         fin;
                        char entree,
                         sortie;

                        if ((init = this.E.getEtat(ligneTsplit[0])) == null) {
                            throw new IOException("--Erreur dans le fichier .descr a la ligne "
                                    + numL + " -- L'etat : " + ligneTsplit[0]
                                    + " n'est pas defini pour cet automate -- TRANSITION IMPOSSIBLE");
                        }

                        if ((fin = this.E.getEtat(ligneTsplit[2])) == null) {
                            throw new IOException("--Erreur dans le fichier .descr a la ligne "
                                    + numL + " -- L'etat : " + ligneTsplit[2]
                                    + " n'est pas defini pour cet automate -- TRANSITION IMPOSSIBLE");
                        }

                        entree = ligneTsplit[1].charAt(1);
                        sortie = Transition.meta;

                        if (ligneTsplit.length > 3) {
                            if (sortie != ligneTsplit[3].charAt(1)) {
                                sortie = ligneTsplit[3].charAt(1);
                            }
                        }
                        init.ajouterTransition(entree, fin, sortie);
                        break;
                }
            }
        }

        // Comportement par defaut si le vocabulaire d'entre est preciser mais qu'il n'existe pas de transitions 
        if (!this.vocabulaireEntree.isEmpty() && this.E.transite().isEmpty()) {
            for (Etat e : this.I) {
                e.setTerminal(true);
                for (Character c : this.vocabulaireEntree) {
                    e.ajouterTransition(c, e, Transition.meta);
                }
            }
        }
    }

    /**
     * Ecrire le fichier descr associe a l'automate
     *
     */
    private String descr() {

        String descr = "V \"";
        for (Character c : this.vocabulaireEntree) {
            descr += c.toString();
        }

        descr += "\"\nO \"";
        for (Character c : this.vocabulaireSortie) {
            descr += c.toString();
        }

        descr += "\"\nE " + Integer.toString(this.E.size())
                + "\nN";
        for (Etat e : this.E) {
            descr += ' ' + e.getId();
        }
        descr += "\nI";
        for (Etat e : this.I) {
            descr += ' ' + e.getId();
        }

        descr += "\nF";
        for (Etat e : this.F) {
            descr += ' ' + e.getId();
        }

        descr += '\n';

        for (Etat e : this.E) {
            for (Map.Entry<Character, Set<Transition>> entry : e.getTableTransitions().entrySet()) {
                for (Transition t : entry.getValue()) {
                    descr += "T " + e.getId() + " \'" + entry.getKey() + "\' " + t.getEtat().getId() + " \'" + t.getSortie() + "\' \n";
                }
            }
        }

        return descr;
    }

    public void writeDescr() throws IOException {

        try (Writer writer = new PrintWriter(AEF.DOSSIER + '/' + AEF.DESCR
                + '/' + this.E.fileName + '.' + AEF.DESCR)) {

            writer.write(this.descr());
        }
    }

    /**
     * Ecrire le fichier dot associe a un mot et a l'indice du caractere lu
     *
     * @param word le mot lu
     * @param numFile l'indice du caractere lu
     * @throws IOException
     */
    private void writeDotGif(String word, int numFile) throws IOException {

        EnsEtat current = new EnsEtat(this.I);

        current.addAll(current.lamdaFermeture());

        for (int index = 0; index < word.length(); ++index) {

            EnsEtat next = new EnsEtat();
            this.E.writeDotFile(word, numFile, index, current);
            char c = word.charAt(index);

            for (Etat e : current) {

                if (e.getTableTransitions().containsKey(c)) {
                    for (Transition t : e.getTableTransitions().get(c)) {
                        next.add(t.getEtat());
                    }
                }
            }
            current = next;
            current.addAll(current.lamdaFermeture());
        }
        this.E.writeDotFile(word, numFile, word.length(), current);
    }

    /**
     *
     * Transcription d'un mot par un automate ^_^
     *
     * @param word le mot a transcrire
     * @return une collection des sortie posssible preceder d'une chaine de
     * caractere precisant s'il la sortie est bloquante ou non
     */
    private Collection<String> transcrire(String word) {

        Set<String> setMotsTranscris = new HashSet<>();

        if (this.E.estDeterministe()) {

            String s = "";

            String mot = "";

            Etat current = new Etat(this.I.first());

            for (int i = 0; i < word.length(); ++i) {

                char c = word.charAt(i);
                s += "Etat courant : " + current.getId() + ", Entree : " + c + ' ';

                if (current.getTableTransitions().containsKey(c)) {

                    Transition transition = new Transition();

                    for (Transition t : current.getTableTransitions().get(c)) {
                        transition = t;
                    }

                    current = transition.getEtat();

                    char sortie = transition.getSortie();
                    if (sortie != Transition.meta) {
                        s += "sortie : " + sortie + ' ';
                        mot += sortie;
                    }

                    s += transition.toString() + '\n';

                } else {

                    s += "AUCUNE TRANSITION pour l'entree " + c + " -- position dans la chaine " + i + '\n';
                    break;
                }
            }

            s += (current.isTerm()) ? "Entree acceptante -> " : "Entree non-aceptante -> ";

            s += "sortie associee : " + mot + "\n--FIN de phrase--";

            setMotsTranscris.add(s);

        } else {

            Map<Etat, String> currentMap = new HashMap<>();

            EnsEtat current = new EnsEtat(this.I);

            current.addAll(current.lamdaFermeture());

            for (Etat e : current) {
                currentMap.put(e, "");
            }

            for (int index = 0; index < word.length(); ++index) {

                EnsEtat next = new EnsEtat();
                Map<Etat, String> nextMap = new HashMap<>();

                char c = word.charAt(index);

                System.out.print("Etats courant : " + current.toString() + " , Entree : " + c + '\n');

                for (Etat e : current) {

                    String s = "";

                    System.out.print("etat " + e.getId() + ' ');

                    if (e.getTableTransitions().containsKey(c)) {

                        if (currentMap.containsKey(e)) {
                            s += currentMap.get(e);
                        }

                        for (Transition t : e.getTableTransitions().get(c)) {

                            next.add(t.getEtat());

                            System.out.print(t);

                            s += (Transition.meta.equals(t.getSortie())) ? "" : t.getSortie().toString();

                            for (Etat etatNext : t.getEtat().lambdaFermeture()) {
                                nextMap.put(etatNext, s);
                            }
                        }
                    }

                    System.out.print('\n');
                }
                current = next;
                current.addAll(current.lamdaFermeture());

                currentMap.clear();
                currentMap.putAll(nextMap);
                nextMap.clear();
            }

            for (Map.Entry<Etat, String> entry : currentMap.entrySet()) {

                boolean test = !entry.getValue().isEmpty();

                if (entry.getKey().isTerm() && test) {
                    setMotsTranscris.add("Entree ACCEPTEE -> sortie associee : " + entry.getValue());
                } else if (test) {
                    setMotsTranscris.add("Entree NON ACCEPTEE -> sortie associee : " + entry.getValue());
                }
            }
        }
        return setMotsTranscris;
    }

    /**
     * Creer le fichier gif associe a la lecture d'un mot
     *
     * @param word le mot lu
     * @param i le i-eme mot lu
     * @throws IOException
     */
    private void toGif(String word, int i) throws IOException {
        this.writeDotGif(word, i);
        Runtime.getRuntime().exec(String.format(AEF.CMD + ' ' + AEF.DOSSIER + '/' + AEF.DOT + '/' + this.E.fileName + "%01d", i));
    }

    /**
     * Lecture d'un mot
     *
     * @param reader le lecteur pour lire le mot
     * @throws IOException
     */
    public void lire(BufferedReader reader) throws IOException {

        int numL = 0;
        Collection<String> motsLus = new ArrayList<>();

        System.out.println("Automate " + this.E.fileName + "\nLangage accepte : " + this.vocabulaireEntree
                + "\nVeuillez saisir les phrases à lire :\n"
                + "(Chaque phrase est terminee par ENTREE, la lecture des phrases est terminee par " + AEF.EOF);

        String mot;

        while (!(mot = reader.readLine()).equals(AEF.EOF)) {
            ++numL;
            motsLus.add(mot);
            this.toGif(mot, numL);
        }

        System.out.println("Traitement des phrases lues :");

        for (String m : motsLus) {

            Collection<String> motsTrancsris = this.transcrire(m);

            if (motsTrancsris.isEmpty()) {
                System.out.println("\nAUCUNE ROUTINE\n");
            } else {
                for (String s : motsTrancsris) {
                    System.out.println('\n' + s + '\n');
                }
            }

        }
        System.out.println("FIN du traitement\n");
    }
}
