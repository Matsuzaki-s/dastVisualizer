package bc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockConverter {

	public static final String ENCODING_BLOCK_XML = "UTF-8";

	public static final String COLLAPSED_BLOCK_LABEL = "c//";

	// �F
	public static final String[] COLOR_NAMES = { "blue", "cyan", "green",
			"magenta", "orange", "pink", "red", "white", "yellow", "gray",
			"black", "lightGray", "darkGray" };

	public static final String[] DATA_BLOCKNAMES = { "toIntFromDouble",
			"toIntFromString", "toDoubleFromInt", "toDoubleFromString",
			"toStringFromInt", "toStringFromDouble", "toStringFromObject",
			"double-number", "number", "string", "true", "false", "pi", "e",
			"gettermember-ver-int-number", "gettermember-ver-double-number",
			"gettermember-var-string", "gettermember-var-boolean",
			"getterlocal-var-int-number", "getterlocal-var-double-number",
			"getterlocal-var-string", "getterlocal-var-boolean", "new-object", /* ! */
			"getterlocal-var-object" };

	// ���Z�q�̓R�R�ɓo�^
	public static final String[] INFIX_COMMAND_BLOCKS = { "lessthan",
			"lessthanorequalto", "greaterthan", "greaterthanorequalto",
			"equals-number", "not-equals-number", "and", "or", "sum",
			"difference", "product", "quotient", "remainder", "string-append",
			"lessthan-double", "lessthanorequalto-double",
			"greaterthan-double", "greaterthanorequalto-double",
			"equals-number-double", "not-equals-number-double", "sum-double",
			"difference-double", "product-double", "quotient-double",
			"remainder-double", "equals-boolean", "not-equals-boolean",
			"equals-string" };

	// �߂�l�̂Ȃ����\�b�h�͂����ɓo�^
	public static final String[] METHOD_CALL_BLOCKS = { "fd", "bk", "lt", "rt",
			"input", "print", "color", "up", "down", "atan", "random", "round",
			"min", "max", "pow", "abs", "sqrt", "sin", "cos", "tan", "asin",
			"acos", "log", "ln", "sleep", "hide", "update", "warp",
			"warpByTopLeft", "size", "scale", "large", "small", "wide",
			"narrow", "tall", "little", "show", "getX", "getY", "getWidth",
			"getHeight", "int", "double", "toString", /* ! */"x", "y", "width",
			"height", "setShow", "isShow", "key", "keyDown", "mouseX",
			"mouseY", "mouseClicked", "leftMouseClicked", "rightMouseClicked",
			"doubleClick", "mouseDown", "leftMouseDown", "rightMouseDown",
			"intersects", "contains", "image", "text"/* !! */, "looks"
			/* !sound! */, "file", "setFile", "play", "loop", "stop",
			"isPlaying", "getVolume", "setVolume"
			/* text */, "getText", "loadOnMemory"
			/* cui */, "cui-print", "cui-println", "cui-random", "next",
			"nextInt", "nextDouble", "hashCode"
			/* empty! */, "empty"
			/* math */, "sqrt", "sin", "cos", "tan", "log", "toRadians",
			/* list */"get", "getSize", "add", "addFirst", "addLast", "addAll",
			"moveAllTo", "removeFirst", "removeLast", "removeAll", "getCursor",
			"setCursor", "moveCursorToNext", "moveCursorToPrevious",
			"getObjectAtCursor", "addToBeforeCursor", "addToAfterCursor",
			"removeAtCursor", "shuffle", "setBgColor"
			/* card */, "getNumber"
			/* button */, "isClicked"
			/* input *//*
						 * ,"getText" , "text"
						 */, "clearText", "setActive", "isActive",
			"toJapaneseMode", "toEnglishMode", "fontsize" };

	// �߂�l�̂��郁�\�b�h�͂����ɓo�^ (��ɂ��o�^���Ȃ��ƃ_��)
	public final static String[] FUNCTION_METHODCALL_BLOCKS = { "input",
			"atan", "random", "round", "min", "max", "pow", "abs", "sqrt",
			"sin", "cos", "tan", "asin", "acos", "log", "ln", "getX", "getY",
			"getWidth", "getHeight", "int", "double", "toString", /* ! */"x",
			"y", "width", "height", "isShow", "key", "keyDown", "mouseX",
			"mouseY", "mouseClicked", "leftMouseClicked", "rightMouseClicked",
			"doubleClick", "mouseDown", "leftMouseDown", "rightMouseDown",
			"intersects", "contains", /* "image", "text", *//* !sound! */
			"isPlaying", "getVolume", "getText" /* cui */, "cui-random",
			"next", "nextInt", "nextDouble", "hashCode"/* math */, "sqrt",
			"sin", "cos", "tan", "log", "toRadians",/* list */"get", "getSize",
			"getCursor", "getObjectAtCursor", /* card */"getNumber",/* button */
			"isClicked"/* input *//* ,"getText" */, "isActive", };

	public static final String[] ALL_DATA_BLOCKNAMES;

	static {
		List<String> all = new ArrayList<String>();
		all.addAll(Arrays.asList(COLOR_NAMES));
		all.addAll(Arrays.asList(DATA_BLOCKNAMES));
		ALL_DATA_BLOCKNAMES = (String[]) all.toArray(new String[all.size()]);
	}

}
