/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * un ensemble d'etats
 *
 * @author adriwankenoby
 */
public class EnsEtat extends TreeSet<Etat> implements Comparable {

    public String fileName = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());

    public EnsEtat() {
        super();
    }

    public EnsEtat(Collection<Etat> c) {
        super(c);
    }

    public EnsEtat(SortedSet<Etat> set) {
        super(set);
    }

    public EnsEtat(AEF A) {
        super(A.E);
    }

    public EnsEtat(Etat e) {
        this();
        this.add(e);
    }

    /**
     * Recupere l'etat en fonction de son id
     *
     * @param id id de l'etat rechercher
     * @return l'etat rechercher ou null si il n'est pas present
     */
    Etat getEtat(String id) {
        Etat e = new Etat(id);
        e = this.ceiling(e);
        return (id.equals(e.getId())) ? e : null;
    }

    /**
     *
     * @return true si l'ensemble contient un etat acceptant false sinon
     */
    boolean containsTerm() {

        for (Etat e : this) {
            if (e.isTerm()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return vraie si l'ensemble contient un etat inital faux sinon
     */
    boolean containsInit() {

        for (Etat e : this) {
            if (e.isInit()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return l'ensemble des etas initiaux
     */
    public EnsEtat initiaux() {

        EnsEtat ens = new EnsEtat();

        for (Etat e : this) {
            if (e.isInit()) {
                ens.add(e);
            }
        }
        return ens;
    }

    public EnsEtat acceptant() {

        EnsEtat ens = new EnsEtat();

        for (Etat e : this) {
            if (e.isTerm()) {
                ens.add(e);
            }
        }
        return ens;
    }

    /**
     * Ensemble des etats successeur de par la lettre en parametre
     *
     * @param c lettre de la transition
     * @return l'ensemble d'etat atteignable de par la lettre
     */
    EnsEtat transite(char c) {

        EnsEtat ens = new EnsEtat();

        for (Etat e : this) {
            ens.addAll(e.transite(c));
        }
        return ens;
    }

    /**
     * Ensemble d'etat successeur sur tout l'alphabet
     *
     * @return l'ensemble d'etat atteignable sur tout l'alphabet
     */
    EnsEtat transite() {

        EnsEtat ens = new EnsEtat();

        for (Etat e : this) {
            ens.addAll(e.transite());
        }
        return ens;
    }

    /**
     * vocabulaire entrant
     *
     * @return ensemble des lettres acceptees par cet ensemble
     */
    Set<Character> alphabetIn() {

        Set<Character> alphabetIn = new TreeSet<>();

        for (Etat e : this) {
            alphabetIn.addAll(e.alphabetIn());
        }
        return alphabetIn;
    }

    /**
     * vocabulaire de sortie
     *
     * @return ensemble des lettres possibles en sortie
     */
    Set<Character> alphabetOut() {

        Set<Character> alphabetOut = new TreeSet<>();

        for (Etat e : this) {
            alphabetOut.addAll(e.alphabetOut());
        }
        return alphabetOut;
    }

    /**
     * dot(null,0,null)
     *
     * @param word pour annimation gif si un mot est lu
     * @param i l'indice du caractere lu
     * @param marked etats courants pendant la lecture
     * @return chaine de carateres .dot
     */
    private String dot(String word, int i, Set<Etat> marked) {

        String dot = "digraph G {\n"
                + "\trankdir=LR;\n"
                + "\tlabel=\"";
        if (word != null) {
            for (int j = 0; j < word.length(); ++j) {
                if (i == j) {
                    dot += '>';
                } else {
                    dot += ' ';
                }
                dot += word.charAt(j);
            }
        } else {
            dot += this.fileName;
        }

        dot += "\"\n";

        if (marked != null) {

            for (Etat e : this) {
                dot += (marked.contains(e)) ? e.dot(true) : e.dot(false);
            }

        } else {

            for (Etat e : this) {
                dot += e.dot(false);
            }
        }

        dot += "}\n";

        return dot;
    }

    /**
     * writeDotFile(null,0,null)
     *
     * @param word le mot lu
     * @param numFile indice de fichier pour animation
     * @param index indice du caracter lu
     * @param marked etats courants pendant la lecture
     * @throws FileNotFoundException
     * @throws IOException
     */
    void writeDotFile(String word, int numFile, int index, Set<Etat> marked) throws FileNotFoundException, IOException {

        try (Writer writer = new PrintWriter(String.format(AEF.DOSSIER + '/' + AEF.DOT
                + '/' + this.fileName + "%01d%02d." + AEF.DOT, numFile, index), "UTF-8")) {

            writer.write(this.dot(word, index, marked));
        }
    }

    /**
     *
     * @param numFile
     * @param index
     * @throws IOException
     */
    private void toImg(int numFile, int index) throws IOException {

        this.writeDotFile(null, index, 0, null);

        Runtime.getRuntime().exec(String.format("dot " + AEF.DOSSIER + '/' + AEF.DOT + '/' + this.fileName + "%01d%02d." + AEF.DOT
                + " -T" + AEF.PNG + " -o "
                + AEF.DOSSIER + '/' + AEF.PNG + '/' + this.fileName + "%01d%02d." + AEF.PNG,
                numFile, index, numFile, index));

        //Decommenter la ligne si vous ne souhaitez pas conserver le fichier.dot
        //Runtime.getRuntime().exec(String.format("rm " + TestAutomate.DOSSIER + '/' + TestAutomate.DOT + '/' + this.fileName + "%01d%02d." + TestAutomate.DOT, numFile, index));
    }

    public void toImg() throws IOException {
        this.toImg(0, 0);
    }

    /**
     *
     * @return l'ensemble des etat accessibles par une lambda transition
     */
    EnsEtat lamdaFermeture() {

        EnsEtat ens = new EnsEtat();

        for (Etat e : this) {
            ens.addAll(e.lambdaFermeture());
        }

        return ens;
    }

    /**
     * un "super etat"
     *
     * @return un etat contenant toutes les transitions de tout les etats de cet
     * ensemble
     */
    Etat toEtat() {

        String id = "";
        Etat superEtat = new Etat();

        for (Etat e : this) {

            id += e.getId();

            for (Map.Entry<Character, Set<Transition>> entry : e.getTableTransitions().entrySet()) {

                for (Transition t : entry.getValue()) {

                    if (this.contains(t.getEtat())) {
                        superEtat.ajouterTransition(entry.getKey(), superEtat, t.getSortie());
                    } else {
                        superEtat.ajouterTransition(entry.getKey(), t.getEtat(), t.getSortie());
                    }
                }
            }
        }

        if (this.containsTerm()) {
            superEtat.setTerminal(true);
        }

        superEtat.setId(id);

        return superEtat;
    }

    public EnsEtat determinise() {

        EnsEtat P = new EnsEtat(this.initiaux().toEtat());

        EnsEtat ens = new EnsEtat();

        int i = 0;

        while (!P.isEmpty()) {

            Etat e = P.pollFirst();

            if (!ens.contains(e)) {

                Etat superEtat = e.determinise();

                if (i == 0) {
                    superEtat.setInitial(true);
                }

                ens.add(superEtat);

                for (Character c : superEtat.alphabetIn()) {
                    P.add(superEtat.transite(c).toEtat());
                }
            }
            ++i;
        }

        ens.fileName = this.fileName + "_D";

        return ens;
    }

    public boolean estDeterministe() {

        if (this.isEmpty()) {
            return true;
        }

        if (this.initiaux().size() > 1) {
            return false;
        }

        for (Etat e : this) {
            for (Map.Entry<Character, Set<Transition>> entry : e.getTableTransitions().entrySet()) {
                if (entry.getValue().size() > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private EnsEtat complete(String etat) {

        Etat puit = new Etat(etat);

        EnsEtat ens = new EnsEtat(puit);

        for (Etat e : this) {
            ens.add(e.complete(this.alphabetIn(), puit));
        }

        ens.fileName = this.fileName + "_C";
        return ens;
    }

    public EnsEtat complete() {
        return this.complete("puit");
    }

    /**
     * Test si l'ensemble d'etat accepte le sous mot demarrant a la position i
     *
     * @param mot mot a tester
     * @param i position de depart
     * @return resultat du test
     */
    public boolean accept(String mot, int i) {
        return (i == mot.length())
                ? (this.containsTerm())
                : (!this.transite(mot.charAt(i)).isEmpty())
                ? (this.transite(mot.charAt(i)).accept(mot, ++i))
                : false;
    }

    @Override
    public int compareTo(Object o) {

        if (o == null) {
            throw new NullPointerException();
        }

        if (o.getClass() != this.getClass()) {
            throw new ClassCastException();
        }

        final EnsEtat ens = (EnsEtat) o;

        // this inclus dans ens
        if (!this.containsAll(ens) && ens.containsAll(this)) {
            return -1;
        }

        // ens inclus dans this
        if (!ens.containsAll(this) && this.containsAll(ens)) {
            return 1;
        }

        // ens == this
        if (this.containsAll(ens) && ens.containsAll(this)) {
            return 0;
        }

        // ens != this
        return 2;
    }

    /**
     *
     * @param o ensemble d'etat a comparer
     * @return boolean vraie si les deux ensembles contiennent les memes etats
     */
    @Override
    public boolean equals(Object o) {
        return (o == this) ? true : (o instanceof EnsEtat) ? (this.compareTo((EnsEtat) o) == 0) : false;
    }

    @Override
    public String toString() {
        String s = "EnsEtat{";
        for (Etat e : this) {
            s += " " + e.getId();
        }
        s += " }";

        return s;
    }
}
