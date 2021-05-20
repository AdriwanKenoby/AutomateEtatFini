/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author adriwankenoby
 */
/**
 * un etat
 */
public class Etat implements Comparable {

    /**
     * identifiant de l'etat (numero)
     */
    private String id;

    /**
     * vrai si l'etat est initial
     */
    private boolean estInitial;

    /**
     * vrai si l'etat est acceptant
     */
    private boolean estAcceptant;

    /**
     * mappage des transitions de l'etat
     */
    private final Map<Character, Set<Transition>> transitions;

    /**
     * Initialise l'etat vide
     */
    public Etat() {
        this.transitions = new HashMap<>();
        this.id = "";
        this.estInitial = false;
        this.estAcceptant = false;
    }

    /**
     * Initialise l'etat selon l'id
     *
     * @param s identifiant de l'etat
     */
    public Etat(String s) {
        this();
        this.id = s;
        this.estAcceptant = (s.equalsIgnoreCase("puit"));
    }

    /**
     * Initialise l'etat selon l'id et initial/acceptant
     *
     * @param s identifiant de l'etat
     * @param init vrai si l'etat est initial
     * @param term vrai si l'etat est acceptant
     */
    public Etat(String s, boolean init, boolean term) {
        this();
        this.id = s;
        this.estInitial = init;
        this.estAcceptant = term;
    }

    public Etat(Etat e) {
        this.transitions = new HashMap<>(e.transitions);
        this.id = e.id;
        this.estInitial = e.estInitial;
        this.estAcceptant = e.estAcceptant;
    }

    public Etat(EnsEtat ens) {
        this(ens.toEtat());
    }

    public String getId() {
        return this.id;
    }

    void setId(String s) {
        this.id = s;
    }

    /**
     * Vrai si l'etat est initial, faux sinon
     *
     * @return boolean est initial
     */
    public boolean isInit() {
        return this.estInitial;
    }

    /**
     * vrai si l'etat est acceptant, faux sinon
     *
     * @return boolean est acceptant
     */
    public boolean isTerm() {
        return this.estAcceptant;
    }

    /**
     * Recupere la table des transitions qui a un carartere entre associe une ou
     * plusieurs transition
     *
     * @return HasMap<Character, Transition>
     */
    Map<Character, Set<Transition>> getTableTransitions() {
        return this.transitions;
    }

    /**
     * Defini l'etat comme etant initial ou non
     *
     * @param init un booleen
     */
    public void setInitial(boolean init) {
        this.estInitial = init;
    }

    /**
     * Defini l'etat comme etatn acceptant ou non
     *
     * @param term un boolean
     */
    public void setTerminal(boolean term) {
        this.estAcceptant = term;
    }

    /**
     * Recupere l'alphabet d'entree de l'etat
     *
     * @return ensemble des lettres pouvant etre lu
     */
    public Set<Character> alphabetIn() {
        return this.transitions.keySet();
    }

    /**
     * Recupere l'alphabet de sortie de l'etat
     *
     * @return ensemble des lettres pouvant etre ecrit
     */
    public Set<Character> alphabetOut() {

        Set<Character> alphabetSortie = new HashSet<>();

        for (Set<Transition> transitionSet : this.transitions.values()) {
            for (Transition t : transitionSet) {
                alphabetSortie.add(t.getSortie());
            }
        }
        return alphabetSortie;
    }

    /**
     * Ajoute une transition
     *
     * @param entree lettre entree
     * @param t Transition a ajouter
     * @return true si l'ensemble ne contient pas deja la transition indiquee
     */
    boolean ajouterTransition(char entree, Transition t) {

        boolean test;

        if (this.transitions.containsKey(entree)) {

            test = this.transitions.get(entree).add(t);

        } else {

            Set<Transition> st = new HashSet<>();

            st.add(t);

            this.transitions.put(entree, st);

            test = true;
        }
        return test;
    }

    /**
     * Ajoute une transition
     *
     * @param entree lettre entree
     * @param etat etat atteint par la transitioin
     * @param sortie lettre sortie
     * @return true si l'ensemble ne contient pas deja la transition indiquee
     */
    public boolean ajouterTransition(char entree, Etat etat, char sortie) {
        return this.ajouterTransition(entree, new Transition(etat, sortie));
    }
    
    public boolean ajouterTransition(char entree, Etat e) {
        return this.ajouterTransition(entree, new Transition(e));
    }

    public boolean ajouterTransition(Etat e) {
        return this.ajouterTransition(Transition.meta, new Transition(e));
    }
    
    /**
     * Recupere l'etats successeurs de la transition par la lettre en parametre
     *
     * @param c lettre de la transition
     * @return un ensemble d'etat des etats successeurs par la lettre entree
     */
    EnsEtat transite(char c) {

        EnsEtat ens = new EnsEtat();

        if (this.transitions.containsKey(c)) {

            for (Transition t : this.transitions.get(c)) {

                ens.add(t.getEtat());
            }
        }
        return ens;
    }

    /**
     * Recupere tous les successeurs de toutes les transitions
     *
     * @return un ensemble d'etat de tous les etats successeurs
     */
    EnsEtat transite() {

        EnsEtat ens = new EnsEtat();

        if (!this.transitions.isEmpty()) {

            for (Character c : this.transitions.keySet()) {

                ens.addAll(this.transite(c));
            }
        }
        return ens;
    }

    /**
     *
     * @return Ensemble des etats accessible par une lambda-transition
     */
    EnsEtat lambdaFermeture() {

        EnsEtat ens = new EnsEtat(this);

        EnsEtat res = new EnsEtat();

        while (!ens.isEmpty()) {

            Etat e = ens.pollFirst();

            if (res.add(e)) {
                ens.addAll(e.transite(Transition.meta));
            }
        }
        return res;
    }

    /**
     * Determinise un etat et ses successeurs
     *
     * @return l'etat determiniser
     */
    Etat determinise() {

        Etat etat = this.lambdaFermeture().toEtat();

        EnsEtat ens = new EnsEtat();

        for (Map.Entry<Character, Set<Transition>> entry : etat.transitions.entrySet()) {

            ens.addAll(etat.transite(entry.getKey()));

            Etat superEtatSuivant = ens.lamdaFermeture().toEtat();

            Set<Transition> st = new HashSet<>();

            st.add(new Transition(superEtatSuivant));

            entry.setValue(st);

            ens.clear();
        }

        etat.getTableTransitions().remove(Transition.meta);

        return etat;
    }

    Etat complete(Set<Character> alphabetIn, Etat puit) {

        if (!this.getTableTransitions().keySet().containsAll(alphabetIn)) {

            for (Character c : alphabetIn) {

                if (!c.equals(Transition.meta) && !this.getTableTransitions().containsKey(c)) {

                    this.ajouterTransition(c, new Transition(puit));
                }
            }
        }
        return this;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public int compareTo(Object o) {

        if (o == null) {
            throw new NullPointerException();
        }
        if (o.getClass() != this.getClass()) {
            throw new ClassCastException();
        }

        final Etat e = (Etat) o;
        return this.id.compareTo(e.id);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) ? true : (o instanceof Etat) ? this.compareTo((Etat) o) == 0 : false;
    }

    @Override
    public String toString() {

        String s = "Etat{" + "id=" + this.id;
        s += (this.estInitial) ? " est initial " : "";
        s += (this.estAcceptant) ? " est acceptant " : "";
        s += '\n';

        s += "Table des transitions :";
        for (Map.Entry<Character, Set<Transition>> entry : this.transitions.entrySet()) {

            s += "[ " + entry.getKey() + " => ( " + entry.getValue().toString() + " ) ]\n";
        }
        s += "}\n";
        return s;
    }

    String dot(boolean marked) {

        String dot = "";

        if (this.estInitial) {
            dot += "\tinit" + this.getId() + " [shape=point];\n"
                    + "\tinit" + this.getId() + " -> " + this.getId() + ";\n";
        }

        dot += "\t" + this.getId() + " [";

        dot += (this.estAcceptant) ? "shape=doublecircle" : "shape=circle";

        if (marked) {
            dot += ",style=filled";
        }
        dot += "]\n";

        for (Map.Entry<Character, Set<Transition>> entry : this.transitions.entrySet()) {
            for (Transition t : entry.getValue()) {
                dot += "\t" + this.id + " -> " + t.getEtat().id
                        + " [label=\"" + entry.getKey() + "/" + t.getSortie() + "\"]\n";
            }
        }
        return dot;
    }
}
