/*
    The Kwik-E-Mart Cash Register
    Copyright (C) 2020 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.ArrayList;
import java.util.List;

import stev.kwikemart.*;

/**
 * Shows a usage demo of the register.
 * @author Sylvain Hallé
 *
 */
public class RegisterDemo 
{
	/**
	 * The main method to be executed.
	 * @param args Command-line arguments (ignored)
	 */
	public static void main(String[] args)
	{
		// Get a reference to the register
		Register register = Register.getRegister();

		// Try to uncomment this line; you'll see that you cannot create
		// a new register instance
		//Register r = new Register();

		// Put the small roll of paper into the register
		register.changePaper(PaperRoll.SMALL_ROLL);

		/* Create a list of items */
		List<Item> grocery = new ArrayList<Item>();
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5));
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		// Oops, we remove the bananas
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5));
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));

		// Print receipt
		System.out.println(register.print(grocery));

		// Try to print another list of items. Since the paper roll is very small,
		// we should run out of paper...
		grocery.clear();
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5));
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75));
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 2, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		System.out.println(register.print(grocery));
		// No more paper! An exception will be thrown.
	}
}
