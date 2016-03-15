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
class Concatenation extends RegEx {

    private final RegEx expG;
    private final RegEx expD;
    
    Concatenation( RegEx eG , RegEx eD ) {
        this.expG = eG;
        this.expD = eD;
    }
    
    @Override
    public String toString() {
        return '('+this.expG.toString()+'.'+this.expD.toString()+')';
    }
    
    @Override
    public EnsEtat build() {
        EnsEtat ensG = this.expG.build();
        EnsEtat ensD = this.expD.build();
        
        for ( Etat eF : ensG.acceptant() ) {
            
            eF.setTerminal(false);
            
            for ( Etat eI : ensD.initiaux() ) {
                
                eI.setInitial(false);
                eF.ajouterTransition(eI);
            }
        }
        ensD.addAll(ensG);
        return ensD ;
    }
    
}
