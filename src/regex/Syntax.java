/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package regex;

/**
 *
 * @author adriwankenoby
 */
public class Syntax {

    private static int nb_etat = 0;

    static int inc() {
        return Syntax.nb_etat++;
    }

    private static RegEx exp;
    private static int index;
    private static String reg;

    public static RegEx analyse(String s) throws Exception {

        Syntax.nb_etat = 0;

        Syntax.index = 0;
        Syntax.reg = s;
        Syntax.exp = Syntax.expression();
        return Syntax.exp;
    }

    private static RegEx expression() throws Exception {

        RegEx r = Syntax.produit();
        if (Syntax.index < Syntax.reg.length()) {
            switch (Syntax.reg.charAt(Syntax.index)) {
                case '|':
                    Syntax.index++;
                    return new Alternative(r, Syntax.expression());
                case '.':
                    Syntax.index++;
                    return new Concatenation(r, Syntax.expression());
            }
        }
        return r;
    }

    private static RegEx produit() throws Exception {
        RegEx r = Syntax.facteur();

        if (Syntax.index < Syntax.reg.length() && Syntax.reg.charAt(Syntax.index) == '*') {
            Syntax.index++;
            return new Fermeture(r);
        }
        return r;
    }

    private static RegEx facteur() throws Exception {
        RegEx r = new Extension('#');

        if (Syntax.index < Syntax.reg.charAt(Syntax.index)) {

            char c = Syntax.reg.charAt(Syntax.index);

            if (Character.isLetterOrDigit(c)) {
                r = new Extension(c);

            } else if (c == '(') {

                Syntax.index++;

                r = Syntax.expression();

                if (Syntax.reg.charAt(Syntax.index) != ')') {
                    throw new Exception(") manquante a l'indice " + Syntax.index);
                }
                
            } else {
                throw new Exception("ERREUR de syntaxe a l'indice " + Syntax.index);
            }
        }
        Syntax.index++;
        return r;
    }
}
