/*
 * @author Antoine Bouabana (BOUA25119908)
 * @author Jules Emery (EMEJ05119405)
 * @author Léo Monteiro (MONL29079907)
*/

/* ==================================================================================  
 * ======================== 	    Classes D'�quivalences 		 ==================== 
 * ================================================================================== 
 * 
 * =======================      	    ITEM LIST  		      ======================= 
 * v.1 : Number items between [1;10] (Range)|	i.1 : number items < 1
 * 											|	i.2 : number items > 10
 * 
 * =======================       	   PAPER ROLL        	  ======================= 
 * v.2 : Paper roll available				|	i.3 : No paper inside the register
 * 											|	i.4 : No paper left on the roll
 * 
 * =======================          	   CUP                 ======================= 
 * v.3 : Normal item						|	i.5 : Not integer quantity (weight item) when first CPU number != 2
 * v.4 : Item with weight	(Specific)		|	i.6 : Withdrawal without registering first
 * v.5 : Coupon 			(Specific)		| 	i.7 : Same CUP twice for the same ticket (not Withdrawal)
 * v.6 : Withdrawal item	(Specific)		|   i.8 : length of CUP != 12
 * 											|	i.9 : Wrong computation of the 12th CUP number
 * 											|	i.10: Non integer information inside the CUP
 * 											| 	i.11: Price coupon > total
 * 											| 	1.12: Quantity coupon for same CUP != 1
 * 
 * =======================          	Unit Price                 ==================== 
 * v.7 : Price between [0, 35] (Range)		|	i.13 : Price > 35
 * 											|	i.14 : Price < 0
 * 
 * =======================            Price Reduction              ==================== 
 * v.8 : 5 Different Items					|	i.15 : Different items < 5 
 * 											|	i.14 : Different items >= 5 but withdrawal make it < 5
*/


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.util.ArrayList;
import java.util.List;

import stev.kwikemart.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegisterTest_Leo {
	
	Register register = Register.getRegister();
	List<Item> grocery;
	
	@BeforeEach
	void setUp() throws Exception {
		register.changePaper(PaperRoll.SMALL_ROLL);
		if(PaperRoll.SMALL_ROLL.getLinesLeft() == 0) {
			register.changePaper(PaperRoll.LARGE_ROLL);
		}
		grocery = new ArrayList<Item>();
	}

	@AfterEach
	void tearDown() throws Exception {
		grocery.clear();
	}
	
    @Test
    @Order(1)
    @DisplayName("invalid : register s'arrête si il n'y à plus de papier")
    public void iOutOfPaper() {
    	System.out.println("prems");
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		register.print(grocery);
		grocery.clear();

		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		register.print(grocery);
		grocery.clear();
		
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
    	assertThrows(PaperRollException.OutOfPaperException.class, () -> { register.print(grocery); });
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
    	checksum = checksum % 10;
    	assertEquals(checksum, Upc.getCheckDigit(String.valueOf(UPC)));
    }

	@Test 
    @DisplayName("invalid : mauvais check digit")
    public void iInvalidCheckDigit() {
    	grocery.add(new Item("123456789015", "Something", 1, 5));
    	assertThrows(InvalidUpcException.InvalidCheckDigitException.class, () -> { register.print(grocery); });
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
    public void irNegativeAmount() {	
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, -1));
    	assertThrows(AmountException.NegativeAmountException.class, () -> { register.print(grocery); });
    }
    
    @Test
    @DisplayName("invalid & range : prix >35")
    public void irAmountTooLarge() {	
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
    
    @Test
    @DisplayName("valid & unique : Coupon = CUP commence par 5")
    public void vuCoupon() {	
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		grocery.add(new Item("543234323434", "Doritos Club", 1, 0.5)); // Coupon
		assertDoesNotThrow(() -> register.print(grocery));
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
		assertDoesNotThrow(() -> System.out.println(register.print(grocery)));
    }
}
