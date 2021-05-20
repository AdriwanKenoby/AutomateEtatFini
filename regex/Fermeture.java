/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;

import automate.EnsEtat;
import automate.Etat;

/**
 *
 * @author adriwankenoby
 */
class Fermeture extends RegEx {

    private final RegEx expression;

    Fermeture(RegEx reg) {
        this.expression = reg;
    }

    @Override
    public String toString() {
        return '(' + this.expression.toString() + ")*";
    }

    @Override
    public EnsEtat build() {

        EnsEtat ens = this.expression.build();

        Etat eInit = new Etat(Integer.toString(Syntax.inc()), true, false);
        Etat eFinal = new Etat(Integer.toString(Syntax.inc()), false, true);

        for (Etat eF : ens.acceptant()) {

            eF.setTerminal(false);
            eF.ajouterTransition(eFinal);
            
            for ( Etat eI : ens.initiaux() ) {
                
                eI.setInitial(false);
                eInit.ajouterTransition(eI);
                eF.ajouterTransition(eI);
            }
        }
        
        eInit.ajouterTransition(eFinal);
        ens.add(eInit);
        ens.add(eFinal);

        return ens;
    }

}
