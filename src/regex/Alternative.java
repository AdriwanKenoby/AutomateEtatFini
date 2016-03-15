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
class Alternative extends RegEx {

    private final RegEx expG;
    private final RegEx expD;

    Alternative(RegEx eG, RegEx eD) {
        this.expG = eG;
        this.expD = eD;
    }

    @Override
    public String toString() {
        return '(' + this.expG.toString() + '+' + this.expD.toString() + ')';
    }

    @Override
    public EnsEtat build() {

        EnsEtat ensG = this.expG.build();
        EnsEtat ensD = this.expD.build();

        EnsEtat ens = new EnsEtat();
        ens.addAll(ensG);
        ens.addAll(ensD);

        Etat eI = new Etat(Integer.toString(Syntax.inc()), true, false);
        Etat eF = new Etat(Integer.toString(Syntax.inc()), false, true);

        for (Etat e : ens.initiaux()) {
            e.setInitial(false);
            eI.ajouterTransition(e);
        }

        for (Etat e : ens.acceptant()) {
            e.setTerminal(false);
            e.ajouterTransition(eF);
        }

        ens.add(eI);
        ens.add(eF);

        return ens;
    }

}
