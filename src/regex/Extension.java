/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;

import automate.Etat;
import automate.EnsEtat;

/**
 *
 * @author adriwankenoby
 */
class Extension extends RegEx{

    private final char c;
    
    Extension(char e){
        this.c = e ;
    }
    
    @Override
    public String toString(){
        return Character.toString(c);
    }
    
    @Override
    public EnsEtat build() {
        Etat eI = new Etat(Integer.toString(Syntax.inc()) , true , false);
        Etat eF = new Etat(Integer.toString(Syntax.inc()) , false , true);
        
        eI.ajouterTransition(c, eF);
        
        EnsEtat ens = new EnsEtat();
        ens.add(eI);
        ens.add(eF);
        
        return ens;
    }
    
}
