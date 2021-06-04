/*
 * @author Léo Monteiro
 * @author Jules Emery
 * @author Antoine Bouabana
*/

/* Classes d'équivalences
 * 
 * valide : Item(CUP, description, quantité, prix)
 * invalide : Item sans arguments
 * valide : CUP = 12 chiffres
 * valide : CUP = 12ème chiffre déterminé par les 11 précédents
 * invalide : CUP =/= 12 chiffres
 * valide & intervalle : prix 0-35
 * invalide & intervalle : prix <0
 * invalide & intervalle : prix >35
 * invalide & spécifique : prix >35 => Register cesse de fonctionner
 * valide & unique : quantité fractionnaire => CUP commence par 2
 * invalide & spécifique : quantité fractionnaire AND CUP ne commence pas par 2
 * invalide & spécifique : 2 items équivalents
 * valide & unique : 2 items dont l'un à une quantité négative
 * valide & intervalle : List<Item> 1-10
 * invalide & intervalle : List<Item> <=0
 * invalide & intervalle : List<Item> >10
 * valide & unique : 5 items distincts AND total (hors taxes) >=2 => rabais 1$
 * valide & unique : Coupon = CUP commence par 5
 * valide & intervalle : Coupon >0
 * invalide & intervalle : Coupon <0
 * invalide & spécifique : total<Coupon
 * 
 */

import stev.kwikemart.*;