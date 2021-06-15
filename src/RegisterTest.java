/*
 * @author Antoine Bouabana
 * @author Jules Emery
 * @author Léo Monteiro
*/

/* Classes d'équivalences
 * 
 * valid : CUP = 12ème chiffre déterminé par les 11 précédents
 * valid : CUP = 12 chiffres
 * invalid : CUP < 12 chiffres
 * invalid : CUP > 12 chiffres
 * invalid : CUP == 0 chiffres
 * valid : Item(CUP, description, quantité, prix)
 * valid & range : prix 0-35
 * invalid & range : prix <0
 * invalid & range : prix >35
 * valid & unique : quantité fractionnaire => CUP commence par 2
 * invalid & specific : quantité fractionnaire AND CUP ne commence pas par 2
 * invalid & specific : 2 items équivalents
 * valid & unique : 2 items dont l'un à une quantité négative
 * invalid & unique : items quantité négative seul
 * valid & range : grocery list range 1-10
 * invalid & range : grocery list <=0
 * invalid & range : grocery list >10
 * valid & unique : 5 items distincts AND total (hors taxes) >=2 => rabais 1$
 * valid & unique : Coupon = CUP commence par 5
 * valid & range : Coupon >0
 * invalid & range : Coupon <0
 * invalid & specific : total<Coupon 
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
		switch(PaperRoll.LARGE_ROLL.getLinesLeft()) {
		case 1000:
			register.changePaper(PaperRoll.LARGE_ROLL);
			break;
		case 0:
			register.changePaper(PaperRoll.SMALL_ROLL);
			break;
		default:
			System.out.println(PaperRoll.LARGE_ROLL.getLinesLeft());
		}
		grocery = new ArrayList<Item>();
	}

	@AfterEach
	void tearDown() throws Exception {
		grocery.clear();
	}

    @Test
    @DisplayName("valid : CUP = 12ème chiffre déterminé par les 11 précédents")
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
    @DisplayName("invalid : CUP < 12 chiffres")
    public void iUpcTooShort() {
    	grocery.add(new Item("228850", "Beef", 0.5, 5.75));
    	assertThrows(InvalidUpcException.UpcTooShortException.class, () -> { register.print(grocery); });
    }
    
    @Test
    @DisplayName("invalid : CUP > 12 chiffres")
    public void iUpcTooLong() {
    	grocery.add(new Item("7960301149779", "Something", 1, 5));
    	assertThrows(InvalidUpcException.UpcTooLongException.class, () -> { register.print(grocery); });
    }
    
    @Test
    @DisplayName("invalid : CUP == 0 chiffres")
    public void iNoUpc() {
    	grocery.add(new Item("", "Something", 1, 5));
    	assertThrows(InvalidUpcException.NoUpcException.class, () -> { register.print(grocery); });
    }
    
    @Test
    @DisplayName("valid : Item(CUP, description, quantité, prix)")
    public void vItem() {
    	Item item = new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5);
        assertAll("item", 
        		() -> assertEquals("123456789012", item.getUpc()), // NOTE: Hardcoded checksum since we already verified how it's works in vGetCheckDigit
                () -> assertEquals("Bananas", item.getDescription()),
        		() -> assertEquals(1, item.getQuantity()),
        		() -> assertEquals(1.5, item.getRetailPrice())
        		);
    }

    @Test
    @DisplayName("valid & range : prix 0-35")
    public void vrRetailPrice() {	
    	int randomRangedRetailPrice = (int) (Math.random() * ( 35 - 0 ));
    	Item item = new Item(Upc.generateCode("61519314159"), "Doritos", 1, randomRangedRetailPrice);
        grocery.add(item);
        assertDoesNotThrow(() -> register.print(grocery));
    }

    @Test
    @DisplayName("invalid & range : prix <0")
    public void irRetailPriceUnderZero() {	
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, -1));
    	assertThrows(AmountException.NegativeAmountException.class, () -> { register.print(grocery); });
    }
    
    @Test
    @DisplayName("invalid & range : prix >35")
    public void irRetailPriceOverThirtyFive() {	
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 36));
    	assertThrows(AmountException.AmountTooLargeException.class, () -> { register.print(grocery); });
    }	

    @Test
    @DisplayName("valid & unique : quantité fractionnaire => CUP commence par 2")
    public void vuFractionalQuantityItemsStartWith2() {	
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		assertDoesNotThrow(() -> register.print(grocery));
    }
    
    @Test
    @DisplayName("invalid & specific : quantité fractionnaire & CUP ne commence pas par 2")
    public void isInvalidQuantityForCategory() {	
    	int randomFirstDigit = 2;
    	while(randomFirstDigit == 2) { // Random first digit except 2
    		randomFirstDigit = (int) (Math.random() * ( 9 - 0 ));
    	}
		grocery.add(new Item(Upc.generateCode(randomFirstDigit+"2804918500"), "Doritos", 0.5, 3));
    	assertThrows(InvalidQuantityException.InvalidQuantityForCategoryException.class, () -> { register.print(grocery); });
    }

    @Test
    @DisplayName("invalid & specific : 2 items équivalents")
    public void isDuplicateItem() {	
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 3));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 3));
    	assertThrows(Register.DuplicateItemException.class, () -> { register.print(grocery); });
    }
    
    @Test
    @DisplayName("valid & unique : 2 items dont l'un à une quantité négative")
    public void vuDuplicateItemButOneIsNegative() {	
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 3)); // Add one
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", -1, 3)); // Remove one
		assertDoesNotThrow(() -> register.print(grocery));
    }
    
    @Test
    @DisplayName("invalid & unique : items quantité négative seul")
    public void iuNoSuchItem() {	
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", -1, 3));
    	assertThrows(Register.NoSuchItemException.class, () -> { register.print(grocery); });
    }
    
    @Test
    @DisplayName("valid & range : grocery list range 1-10")
    public void vuGroceryListRange1To10() {	
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); //Remove bananas
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		assertDoesNotThrow(() -> register.print(grocery));
    }
    
    @Test
    @DisplayName("invalid & range : grocery list <=0")
    public void irEmptyGroceryList() {	
    	assertThrows(RegisterException.EmptyGroceryListException.class, () -> { register.print(grocery); });
    }
    
    @Test
    @DisplayName("invalid & range : grocery list >10")
    public void irTooManyItems() {	
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); //Remove bananas
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5)); // Remove Doritos Club
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", -1, 1.25)); // Remove Doritos
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", -1, 1.44)); // Remove 1 Nerds
    	assertThrows(RegisterException.TooManyItemsException.class, () -> { register.print(grocery); });
    }
    		
    @Test // TODO : How to check the TOTAL for the rebate if there are no methods?
    @DisplayName("valid & unique : 5 items distincts AND total (hors taxes) >=2 => rabais 1$")
    public void vu() {	
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); //Remove bananas
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		System.out.println(register.print(grocery));
		assertDoesNotThrow(() -> register.print(grocery));
    }
}
