/*
 * @author Antoine Bouabana (BOUA25119908)
 * @author Jules Emery (EMEJ05119405)
 * @author LÃ©o Monteiro (MONL29079907)
*/

/* ==================================================================================  
 * ======================== 	    Classes D'équivalences 		 ==================== 
 * ================================================================================== 
 * 
 * Notes :  + Using heuristics Range and Specific
 * 			+ JUnit Version 5.7.1
 * 			+ JDK 11
 * 
 * =======================      	    ITEM LIST  		      ======================= 
 * v.1 : Number items between [1;10] (Range)|	i.1 : number items < 1
 * 											|	i.2 : number items > 10
 * 
 * =======================       	   PAPER ROLL        	  ======================= 
 * v.2 : Paper roll available				|	i.3 : No paper roll inside the register
 * 											|	i.4 : No paper left on the roll
 * 
 * =======================          	   CUP                 ======================= 
 * v.3 : Normal item						|	i.5 : Not integer quantity (weight item) when first CPU number != 2
 * v.4 : Item with weight	(Specific)		|	i.6 : Withdrawal without registering first (Quantity < 0)
 * v.5 : Coupon 			(Specific)		| 	i.7 : Same CUP twice for the same ticket (not Withdrawal)
 * v.6 : Withdrawal item	(Specific)		|   i.8 : length of CUP < 12
 * 											|	i.9	: length of CUP > 12
 * 											|	i.10: Wrong computation of the 12th CUP number
 * 											|	i.11: Non integer information inside the CUP
 * 											| 	i.12: Price coupon > total
 * 											| 	1.13: Quantity coupon for same CUP > 1 
 * 											|	i.14: Quantity item = 0
 * 											|	i.19: length of CUP = 0
 * 
 * =======================          	Unit Price                 ==================== 
 * v.7 : Price between [0, 35] (Range)		|	i.15: Price > 35
 * 											|	i.16: Price < 0
 * 
 * =======================            Price Reduction              ==================== 
 * v.8 : 5 Different Items					|	i.17: Different items >= 5 but withdrawal make it < 5 and reduction applies
 * v.9 : No reduction						|	i.18: 5 different items but price <2
 * 
 * 
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
import stev.kwikemart.Register.NoPaperRollException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegisterTest_Leo {
	
	Register register = Register.getRegister();
	List<Item> grocery;	
	
	@BeforeEach
	void setUp() throws Exception {
		register.changePaper(PaperRoll.LARGE_ROLL);
		grocery = new ArrayList<Item>();
	}

	@AfterEach
	void tearDown() throws Exception {
		grocery.clear();
	}
	
	
	/**
	 * casualScenario() represent the casual scenario, without coupon or price reduction. No items are withdrawal.
	 * It represents the situation : 
	 * v.1 and v.2 and (v.3 OR v.4) and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and (v.3 OR v.4) and v.7 and v.9 -> casual scenario (No exception expected)")
    public void casualScenario() {
    	float price = (float) (Math.random() * ( Register.MAX_AMOUNT - 0 ));
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, price));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Chicken", 1, 3));
		if (register.print(grocery).contains("Rebate for 5 items")) {
			fail();
    	} else {
    		assertDoesNotThrow(() -> register.print(grocery));
    	} 	
    }
    
    /**
	 * couponScenario() represent the scenario where we applies a coupon, without price reduction. No items are withdrawal.
	 * It represents the situation : 
	 * v.1 and v.2 and (v.3 OR v.4) and v.5 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and (v.3 OR v.4) and v.5 and v.7 and v.9 -> Coupon scenario (No exception expected)")
    public void couponScenario() {
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Chicken", 1, 3));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Chicken Free For All", 1, 3));	
		if (!register.print(grocery).contains("Coupon:")) {
			fail();
    	} else {
    		assertDoesNotThrow(() -> register.print(grocery));
    	}
    }
    
    /**
	 * ReductionScenario() represent the scenario where we applies a withdrawal, without  coupon or reduction.
	 * It represents the situation : 
	 * v.1 and v.2 and (v.3 OR v.4) and v.6 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and (v.3 OR v.4) and v.6 and v.7 and v.9 -> Withdrawal scenario (No exception expected)")
    public void withdrawalScenario() {
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 3));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 3));
		assertDoesNotThrow(() -> register.print(grocery));
    }
    
    /**
	 * reductionScenario() represent the scenario where we applies a reduction, without coupon. No items are withdrawal.
	 * It represents the situation : 
	 * v.1 and v.2 and (v.3 OR v.4) and v.7 and v.8
	 */
    @Test
    @DisplayName("v.1 and v.2 and (v.3 OR v.4) and v.7 and v.8 -> price reduction scenario (No exception expected)")
    public void reductionScenario() {
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Chicken", 1, 3));
		grocery.add(new Item(Upc.generateCode("45612378973"), "Bananas", 3, 2));
		grocery.add(new Item(Upc.generateCode("12345678789"), "Pastas", 4, 2));
		grocery.add(new Item(Upc.generateCode("11111111111"), "French fries", 2, 1));
		assertDoesNotThrow(() -> register.print(grocery));
    }
    
    
    /**
	 * emptyGroceriesScenario() represent the scenario where we the list item is emtpy
	 * It represents the situation : 
	 * i.1
	 */
    @Test
    @DisplayName("i.1 -> empty groceries scenario (EmptyGroceryListException expected)")
    public void emptyGroceriesScenario() {
    	grocery.clear();
    	assertThrows(RegisterException.EmptyGroceryListException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * tooBigGroceriesScenario() represent the scenario where we the list item is >10
	 * It represents the situation : 
	 * i.2 and v.2 and (v.3 OR v.4) and v.7 and v.9
	 */
    @Test
    @DisplayName("i.2 and v.2 and (v.3 OR v.4) and v.7 and v.9 -> groceries lenght > 10 (TooManyItemsException expected)")
    public void tooBigGroceriesScenario() {
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Chicken", 1, 3));
		grocery.add(new Item(Upc.generateCode("45612378973"), "Bananas", 3, 2));
		grocery.add(new Item(Upc.generateCode("12345678789"), "Pastas", 4, 2));
		grocery.add(new Item(Upc.generateCode("11111111111"), "French fries", 2, 1));
		grocery.add(new Item(Upc.generateCode("22222222222"), "Soup", 2, 24));
		grocery.add(new Item(Upc.generateCode("32132132122"), "Jewlery", 1, 20));
		grocery.add(new Item(Upc.generateCode("77777777777"), "Loto ticket", 1, 2));
		grocery.add(new Item(Upc.generateCode("73170573170"), "Books", 1, 13));
		grocery.add(new Item(Upc.generateCode("31313131313"), "Wisdom", 1, 28));
		grocery.add(new Item(Upc.generateCode("11223344556"), "Water", 1, 1));
		assertThrows(RegisterException.TooManyItemsException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * noPaperScenario() represent the scenario where we don't put any paper in the register
	 * It represents the situation : 
	 * v.1 and i.3 and (v.3 OR v.4) and v.7 and v.9
	 */
    @Test
    @DisplayName("i.3 and v.2 and (v.3 OR v.4) and v.7 and v.9 -> no paper scenario (NoPaperRollException expected)")
    public void noPaperScenario() {
    	register.changePaper(null);
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Chicken", 1, 3));
		assertThrows(NoPaperRollException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * noMorePaperScenario() represent the scenario where we use too much paper
	 * It represents the situation : 
	 * v.1 and i.4 and (v.3 OR v.4) and v.7 and v.9
	 */
    @Test
    @DisplayName("i.4 and v.2 and (v.3 OR v.4) and v.7 and v.9 -> no more paper scenario (OutOfPaperException expected)")
    public void noMorePaperScenario() {
    	register.changePaper(PaperRoll.SMALL_ROLL);
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Chicken", 1, 3));
		register.print(grocery);
		grocery.clear();
		
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Chicken", 1, 3));
		register.print(grocery);
		grocery.clear();
		
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("12345678901"), "Chicken", 1, 3));		
		assertThrows(PaperRollException.OutOfPaperException.class, () -> { register.print(grocery); });
    }
    
    
    /**
	 * notIntegerQuantityScenario() represent the scenario where we don't use 2 as the first CUP number for a fractional quantity
	 * It represents the situation : 
	 * v.1 and v.2 and i.5 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.5 and v.7 and v.9 -> fractionnal quantity when first CUP number != 2 (InvalidQuantityForCategoryException expected)")
    public void notIntegerQuantityScenario() {
    	int randomFirstDigit = 2;
    	while(randomFirstDigit == 2) { // Random first digit except 2
    		randomFirstDigit = (int) (Math.random() * ( 10 - 0 ));
    	}
		grocery.add(new Item(Upc.generateCode(randomFirstDigit+"2804918500"), "Doritos", 0.5, 3));
    	assertThrows(InvalidQuantityException.InvalidQuantityForCategoryException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * withdrawalNoRegisteredScenario() represent the scenario where we withdrawal without registering an item
	 * It represents the situation : 
	 * v.1 and v.2 and i.6 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.6 and v.7 and v.9 -> Withdrawal without registering first (NoSuchItemException expected)")
    public void withdrawalNoRegisteredScenario() {
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 3));
		grocery.add(new Item(Upc.generateCode("11111111111"), "WrongWithdrawal", -1, 1));
		assertThrows(Register.NoSuchItemException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * sameCUPScenario() represent the scenario where we use the same CUP for 2 items
	 * It represents the situation : 
	 * v.1 and v.2 and i.7 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.7 and v.7 and v.9 -> Same CUP for 2 items (no withdrawal) (DuplicateItemException expected)")
    public void sameCUPScenario() {
    	grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 3));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 3));
    	assertThrows(Register.DuplicateItemException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * tooShortCUPScenario() represent the scenario where the CUP length is lower than 12
	 * It represents the situation : 
	 * v.1 and v.2 and i.8 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.8 and v.7 and v.9 -> CUP length < 12 (UpcTooShortException expected)")
    public void tooShortCUPScenario() {    	
    	grocery.add(new Item("123456789", "tooShortCUP", 1, 1));
    	assertThrows(InvalidUpcException.UpcTooShortException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * tooLongCUPScenario() represent the scenario where the CUP length is higher than 12
	 * It represents the situation : 
	 * v.1 and v.2 and i.9 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.9 and v.7 and v.9 -> CUP length > 12 (UpcTooLongException expected)")
    public void tooLongCUPScenario() {    	
    	grocery.add(new Item("123465789123456789", "tooLongCUP", 1, 3));
    	assertThrows(InvalidUpcException.UpcTooLongException.class, () -> { register.print(grocery); });
    }
    
    
    /**
	 * emptyCUPScenario() represent the scenario where the CUP length is equal to 0
	 * It represents the situation : 
	 * v.1 and v.2 and i.19 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.9 and v.7 and v.9 -> CUP length > 12 (NoUpcException expected)")
    public void emptyCUPScenario() {    	
    	grocery.add(new Item("", "empty CUP", 1, 3));
    	assertThrows(InvalidUpcException.NoUpcException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * wrongComputationCPUScenario() represent the scenario where the CUP length is higher than 12
	 * It represents the situation : 
	 * v.1 and v.2 and i.10 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.10 and v.7 and v.9 -> Wrong Computation CUP (InvalidCheckDigitException expected)")
    public void wrongComputationCPUScenario() {    	
    	grocery.add(new Item("123456789019", "wrongCUP", 1, 3));
    	assertThrows(InvalidUpcException.InvalidCheckDigitException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * notIntegerCUPScenario() represent the scenario where the CUP contains non integer information
	 * It represents the situation : 
	 * v.1 and v.2 and i.11 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.11 and v.7 and v.9 -> Non integer information for the CUP (InvalidUpcException expected)")
    public void notIntegerCUPScenario() {    	;
    	grocery.add(new Item("abc123def123", "wrongCUP", 1, 3));
    	assertThrows(InvalidUpcException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * couponTooHighScenario() represent the scenario where the coupon > total
	 * It represents the situation : 
	 * v.1 and v.2 and i.12 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.12 and v.7 and v.9 -> coupon value > total (no exception expected)")
    public void couponTooHighScenario() {    	
    	grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 3));
    	grocery.add(new Item(Upc.generateCode("51212121212"), "Doritos for free", 1, 10));
    	assertDoesNotThrow(() -> register.print(grocery));
    }
    
    /**
	 * multipleCouponScenario() represent the scenario where we applies a quantity != 1 for a coupon
	 * It represents the situation : 
	 * v.1 and v.2 and i.13 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.13 and v.7 and v.9 -> coupon quantity > 1 (invalidCouponQuantityException expected)")
    public void multipleCouponScenario() {    	
    	grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 30));
    	grocery.add(new Item(Upc.generateCode("51212121212"), "Doritos for free", 2, 2));
    	assertThrows(CouponException.InvalidCouponQuantityException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * emptyQuantityScenario() represent the scenario where an item has 0 quantity
	 * It represents the situation : 
	 * v.1 and v.2 and i.14 and v.7 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and i.14 and v.7 and v.9 -> Item quantity = 0 (InvalidQuantityException expected)")
    public void emptyQuantityScenario() {    	
    	grocery.add(new Item(Upc.generateCode("12312312312"), "noStuff", 0, 2));
    	assertThrows(InvalidQuantityException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * priceTooHighScenario() represent the scenario where an item's price > 35
	 * It represents the situation : 
	 * v.1 and v.2 and (v.3 OR v.4) and i.15 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and (v.3 OR v.4) and i.15 and v.9 -> Item price > 35 (AmountTooLargeException expected)")
    public void priceTooHighScenario() {   
    	int maxInt = Integer.MAX_VALUE;
    	int randomPrice = (int) (Math.random() * (maxInt-Register.MAX_AMOUNT+1) + Register.MAX_AMOUNT); 
    	grocery.add(new Item(Upc.generateCode("12312312312"), "tooExpensiveItem", 1, randomPrice));
    	assertThrows(AmountException.AmountTooLargeException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * priceTooLowScenario() represent the scenario where an item's price < 0
	 * It represents the situation : 
	 * v.1 and v.2 and (v.3 OR v.4) and i.16 and v.9
	 */
    @Test
    @DisplayName("v.1 and v.2 and (v.3 OR v.4) and i.16 and v.9 -> Item price < 0 (NegativeAmountException expected)")
    public void priceTooLowScenario() {   
    	int lowestInt = Integer.MIN_VALUE;
    	int randomPrice = (int) (Math.random() * (0-lowestInt+1) + lowestInt); 
    	grocery.add(new Item(Upc.generateCode("12312312312"), "PriceTooLowItem", 1, randomPrice));
    	assertThrows(AmountException.NegativeAmountException.class, () -> { register.print(grocery); });
    }
    
    /**
	 * noReductionWithdrawalScenario() represent the scenario list item > 5 but withdrawal make it <5 so there is no reduction
	 * It represents the situation : 
	 * v.1 and v.2 and (v.3 OR v.4) and v.7 and i.17
	 */
    @Test
    @DisplayName("v.1 and v.2 and (v.3 OR v.4) and v.7 and i.17 -> No reduction expected, item list <5 after withdrawal (no exception expected)")
    public void noReductionWithdrawalScenario() {   
    	grocery.add(new Item(Upc.generateCode("12312312312"), "Bananas", 1, 5));
    	grocery.add(new Item(Upc.generateCode("12345612312"), "Cherry", 2, 5));
    	grocery.add(new Item(Upc.generateCode("12347612312"), "Strawberry", 2, 5));
    	grocery.add(new Item(Upc.generateCode("12345781111"), "Apple", 2, 15));
    	grocery.add(new Item(Upc.generateCode("11223344556"), "Pineapple", 1, 8));
    	grocery.add(new Item(Upc.generateCode("11223344556"), "Pineapple", -1, 8));
    	grocery.add(new Item(Upc.generateCode("12345781111"), "Apple", -2, 15));    	
    	if (register.print(grocery).contains("Rebate for 5 items")) {
    		fail();
    	} else {
    		assertDoesNotThrow(() -> register.print(grocery));
    	}
    }
    
    /**
	 * noReductionPriceTooLowScenario() represent the scenario list item > 5 but price <2
	 * It represents the situation : 
	 * v.1 and v.2 and (v.3 OR v.4) and v.7 and i.18
	 */
    @Test
    @DisplayName("v.1 and v.2 and (v.3 OR v.4) and v.7 and i.18 -> No reduction expected, item list >5 but price <2 (no exception expected)")
    public void noReductionPriceTooLowScenario() {   
    	grocery.add(new Item(Upc.generateCode("12312312312"), "Bananas", 1, 0.2));
    	grocery.add(new Item(Upc.generateCode("12345612312"), "Cherry", 2, 0.2));
    	grocery.add(new Item(Upc.generateCode("12347612312"), "Strawberry", 2, 0.2));
    	grocery.add(new Item(Upc.generateCode("12345781111"), "Apple", 2, 0.2));
    	grocery.add(new Item(Upc.generateCode("11223344556"), "Pineapple", 1, 0.2));    	
    	if (register.print(grocery).contains("Rebate for 5 items")) {
    		fail();
    	} else {
    		assertDoesNotThrow(() -> register.print(grocery));
    	} 	
    }
}
