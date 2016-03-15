/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package automate;

/**
 *
 * une Transition
 * associe un etat et un caractere de sortie S
 * 
 * @author adriwankenoby
 */
class Transition implements Comparable {
    
    public static Character meta = '#';
    
    private final Etat etat ;
    private final Character sortie ;

    /**
     * Initialise une transition vers l'etat "vide", pas de caractere de sortie associee
     */
    Transition()
    {
        this.etat = new Etat() ;
        this.sortie = Transition.meta ;
    }
    
    /**
     * Initialise une transition vers un etat, pas de caractere de sortie
     * @param e 
     */
    Transition( Etat e )
    {
        this.etat = e ;
        this.sortie = Transition.meta ;
    }
    
    /**
     * Initialise une transition
     * @param e etat associe apres transition
     * @param s caractere associe a une transition
     */
    Transition( Etat e , char s )
    {
        this.etat = e ;
        this.sortie = s ;
    }
    
    /**
     * Recupere l'etat de sortie associe a une transition
     * @return l'etat de sortie apres transition
     */
    Etat getEtat() {
        return this.etat;
    }

    /**
     * Recupere le caractere de sortie associe a une transition
     * @return caractere de sortie
     */
    Character getSortie() {
        return this.sortie;
    }

    @Override
    public int hashCode() {
        int hash = this.etat.hashCode() * this.etat.hashCode();
        hash += this.sortie.hashCode() * this.sortie.hashCode();
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (o.getClass() != this.getClass()) {
            throw new ClassCastException();
        }

        final Transition t = (Transition) o;
        return this.hashCode() - t.hashCode();
    }
    
    @Override
    public boolean equals(Object o ) {
        return ( o == this ) ? true : ( o instanceof Transition ) ? this.compareTo((Transition)o) == 0 : false ;
    }

    @Override
    public String toString() {
        return "Transition{ " + "etat=" + this.etat.getId() + ", sortie=" + sortie + " }";
    }   
}
