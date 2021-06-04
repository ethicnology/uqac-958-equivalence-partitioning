/*
 * @author Antoine Bouabana
 * @author Jules Emery
 * @author Léo Monteiro
*/

/* Classes d'équivalences
 * 
 * valide : CUP = 12ème chiffre déterminé par les 11 précédents
 * valide : CUP = 12 chiffres
 * invalide : CUP =/= 12 chiffres
 * valide : Item(CUP, description, quantité, prix)
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
 */


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stev.kwikemart.*;

class RegisterTest {
	
	Register register = Register.getRegister();
	List<Item> grocery;
	
	@BeforeEach
	void setUp() throws Exception {
		register.changePaper(PaperRoll.SMALL_ROLL);
		grocery = new ArrayList<Item>();
	}

	@AfterEach
	void tearDown() throws Exception {
		grocery.clear();
	}

    @Test
    @DisplayName("valide : CUP = 12ème chiffre déterminé par les 11 précédents")
    public void vGetCheckDigit() {
    	// Generate random UPC
    	char[] UPC = new char[11];
        for (int i = 0; i < 11; i++) {
        	int random = (int) (Math.random() * ( 9 - 0 ));
        	UPC[i] = Character.forDigit(random, 10);
        }
        // Proceed to checksum computation based on : https://fr.wikipedia.org/wiki/Code_universel_des_produits#Construction
    	int checksum = 0;
    	for(int i = 0; i < UPC.length; i+=2) {
    		if(i % 2 == 0) {
    			checksum += UPC[i] - '0';	
    		}
    	}
    	checksum = checksum * 3;
    	for(int i = 0; i < UPC.length; i++) {
    		if(i % 2 != 0) {
    			checksum += UPC[i] - '0';	
    		}
    	}
    	int substract = checksum + (10 - checksum % 10);
    	checksum = Math.abs(checksum - substract);
    	assertEquals(checksum, Upc.getCheckDigit(String.valueOf(UPC)));
    }

    @Test
    @DisplayName("invalide : CUP =/= 12 chiffres")
    public void iUPC() {	
        assertAll("invalide UPC", 
            	() -> assertThrows(InvalidUpcException.UpcTooShortException.class, () -> { Upc.generateCode("123"); }),
            	() -> assertThrows(InvalidUpcException.UpcTooLongException.class, () -> { Upc.generateCode("123456789012345"); })
        );
    }
    
    @Test
    @DisplayName("valide : Item(CUP, description, quantité, prix)")
    public void vItem() {
    	Item item = new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5);
        assertAll("item", 
        		() -> assertEquals("123456789012", item.getUpc()),
                () -> assertEquals("Bananas", item.getDescription()),
        		() -> assertEquals(1, item.getQuantity()),
        		() -> assertEquals(1.5, item.getRetailPrice())
        		);
    }
}
