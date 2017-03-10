"use strict";

// *** WORKAROUND FOR MISSING println ***
if (typeof println === "undefined") {
	this.println = print;
}

// *** CONSTANTS ***

/**
 * The String "This type is not supported by this emulator!" for use with Show_Message().
 * @constant
 * @type String
 */
var NOT_SUPPORTED = "This type is not supported by this emulator!";

//constants for palntsc
/**
 * The String "PAL" for comparison with variable itemPalNtsc.
 * @constant
 * @type String
 */
var PAL = String(Packages.jgamebase.Const.FORDISPLAY_PALNTSC[0]);

/**
 * The String "PAL+NTSC" for comparison with variable itemPalNtsc.
 * @constant
 * @type String
 */
var PALNTSC = String(Packages.jgamebase.Const.FORDISPLAY_PALNTSC[1]);

/**
 * The String "NTSC" for comparison with variable itemPalNtsc.
 * @constant
 * @type String
 */
var NTSC = String(Packages.jgamebase.Const.FORDISPLAY_PALNTSC[2]);

/**
 * The String "PAL(+NTSC?)" for comparison with variable itemPalNtsc.
 * @constant
 * @type String
 */
var PALntsc = String(Packages.jgamebase.Const.FORDISPLAY_PALNTSC[3]);

// constants for control
/**
 * The String "Joystick Port 2" for comparison with variable itemControl.
 * @constant
 * @type String
 */
var JOYPORT2 = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[0]);

/**
 * The String "Joystick Port 1" for comparison with variable itemControl.
 * @constant
 * @type String
 */
var JOYPORT1 = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[1]);

/**
 * The String "Keyboard" for comparison with variable itemControl.
 * @constant
 * @type String
 */
var KEYBOARD = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[2]);

/**
 * The String "Paddle Port 2" for comparison with variable itemControl.
 * @constant
 * @type String
 */
var PADDLEPORT2 = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[3]);

/**
 * The String "Paddle Port 1" for comparison with variable itemControl.
 * @constant
 * @type String
 */
var PADDLEPORT1 = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[4]);

/**
 * The String "Mouse" for comparison with variable itemControl.
 * @constant
 * @type String */
var MOUSE = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[5]);

/**
 * The String "Light Pen" for comparison with variable itemControl.
 * @constant
 * @type String
 */
var LIGHTPEN = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[6]);

/**
 * The String "Koala Pad" for comparison with variable itemControl.
 * @constant
 * @type String
 */
var KOALAPAD = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[7]);

/**
 * The String "Light Gun" for comparison with variable itemControl.
 * @constant
 * @type String
 */
var LIGHTGUN = String(Packages.jgamebase.Const.FORDISPLAY_CONTROL[8]);

// *** VARIABLES ***
/** @ignore */
var global = (function() {
	return this;
}).call(null);

/**
 * The command to execute (eg "x64"). Must be set in the script,
 * otherwise calls to Run_Emulator() will fail.
 * @type String
 */
var command = "";
var commandline = [];

// Java Objects
/**
 * Reference to the current database.
 *  @name Database
 *  @type jgamebase.model.Database
 */

/**
 * Reference to the current emulator.
 *  @name Emulator
 *  @type jgamebase.model.Emulator
 */

/**
 * Reference to the current item.
 *  @name Item
 *  @type jgamebase.db.model.Item
 */

/**
 * Reference to the current game.
 *  @name Game
 *  @type jgamebase.db.model.Game
 */

/**
 * Reference to the current music.
 *  @name Music
 *  @type jgamebase.db.model.Music
 */

// convert variables from java String to javascript string
/**
 * The name of the operating system as read from System.getProperty("os.name").
 *  @type String
 */
osName = String(osName);

/**
 * The line separator as read from System.getProperty("line.separator").
 *  @type String
 */
lineSeparator = String(lineSeparator);

/**
 * The system-dependent default name-separator character. On UNIX
 * systems the value of this field is <code>'/'</code>; on Microsoft
 * Windows systems it is <code>'\'</code>.
 *  @type String
 */
separator = String(separator);

/**
 * The path to the database directory (eg "/home/USER/.jgamebase/C64lite").
 *  @type String
 */
dbPath = String(dbPath);

/**
 * The path to the emulator script or executable (eg "/home/USER/.jgamebase/C64lite/Scripts").
 *  @type String
 */
emulatorPath = String(emulatorPath);

/**
 * The configuration file of the emulator (eg "/home/USER/.jgamebase/C64lite/Scripts/vice.ini").
 *  @type String
 */
emulatorConfigFile = String(emulatorConfigFile);

/**
 * The path to the work directory where GameBase extracts and runs files (eg "/tmp/jgamebase").
 *  @type String
 */
workPath = String(workPath);

/**
 * The name of the item (eg "3-D Glooper").
 *  @type String
 */
itemName = String(itemName);

/**
 * The comment of the item (if any).
 *  @type String
 */
itemComment = String(itemComment);

/**
 * The version comment of the item (eg "load manually", or "not working").
 *  @type String
 */
itemVersionComment = String(itemVersionComment);

/**
 * How the item is controlled (eg "Keyboard", or "Joystick Port 2").
 *  @type String
 */
itemControl = String(itemControl);

/**
 * If the item needs NTSC or PAL (eg "PAL", or "PAL+NTSC").
 *  @type String
 */
itemPalNtsc = String(itemPalNtsc);

// itemPlayersMin, itemPlayersMax are int, no need to convert
/**
 * The minimum number of players that can play the item simultanously.
 *
 * @name itemPlayersMin
 * @type int
 */

/**
 * The maximum number of players that can play the item simultanously.
 *  @name itemPlayersMax
 *  @type int
 */

// isGame, isOsWindows, isRunsOnPal, isRunsOnNtsc, isRunsOnTrueDrive,
// useHardwareJoystick are boolean, no need to convert
/**
 * <code>true</code> if the item is a Game, <code>false</code> if the item is a piece of Music.
 * @name isGame
 * @type boolean
 */

/**
 * <code>true</code> if the operating system is Microsoft
 * Windows, <code>false</code> otherwise.
 * @name isOsWindows
 * @type boolean
 */

/**
 * Whether the item works on a PAL system.
 * @name isRunsOnPal
 * @type boolean
 */

/**
 * Whether the item works on a NTSC system.
 *  @name isRunsOnNtsc
 *  @type boolean
 */

/**
 * Whether the item needs true drive emulation (no fast loader).
 *  @name isRunsOnTrueDrive
 *  @type boolean
 */

/**
 * Whether real joysticks are to be used (instead of eg keyboard).
 *  @name useHardwareJoystick
 *  @type boolean
 */

/**
 * The file to be run (eg "/tmp/jgamebase/3DGLOOPR.T64").
 *  @type String
 */
fileToRun = String(fileToRun);

// imageIndex is int, no need to convert
/**
 * The index of the file to be started on the supplied image (eg used to start the second file in the image).
 *  @name imageIndex
 *  @type int
 */

/**
 * The name of the image.
 *  @type String
 */
imageName = String(imageName);

/**
 * The name of the image in the native character set.
 *  @type String
 */
imageNameNative = String(imageNameNative);

//isBootable is boolean, no need to convert
/**
 * <code>true</code> if the first image can be booted, <code>false</code> otherwise.
 * @name isBootable
 * @type boolean
 */

/**
 * Path and filename of the item (eg "/tmp/jgamebase/3DGLOOPR.T64").
 *  @type String
 */
itemPathAndFile = String(itemPathAndFile);

/**
 * The path (no filename) of the item (eg "/tmp/jgamebase").
 *  @type String
 */
itemPath = String(itemPath);

/**
 * The filename (no path) of the item (eg "3DGLOOPR.T64").
 *  @type String
 */
itemFile = String(itemFile);

/**
 * The file extension (no filename) of the item (eg "t64").
 *  @type String
 */
itemFileExt = String(itemFileExt);

/**
 * The type (equals the file extension) of the item (eg "t64").
 *  @type String
 */
itemType = String(itemType);

/** The filename (no file extension) of the item (eg "3DGLOOPR").
 *  @type String
 */
itemFileNoExt = String(itemFileNoExt);

for (var i = 0; i < itemPathsAndFiles.length; i++) {
	/**
	 * Paths and filenames of the items.
	 *  @name itemPathsAndFiles
	 *  @type Array of String
	 */
	itemPathsAndFiles[i] = String(itemPathsAndFiles[i]);

	/**
	 * The paths (no filenames) of the items.
	 *  @name itemPaths
	 *  @type Array of String
	 */
	itemPaths[i] = String(itemPaths[i]);

	/**
	 * The filenames (no paths) of the items.
	 *  @name itemFiles
	 *  @type Array of String
	 */
	itemFiles[i] = String(itemFiles[i]);

	/**
	 * The file extensions (no filenames) of the items.
	 *  @name itemFilesExt
	 *  @type Array of String
	 */
	itemFilesExt[i] = String(itemFilesExt[i]);

	/**
	 * The filenames (no file extensions) of the items.
	 *  @name itemFilesNoExt
	 *  @type Array of String
	 */
	itemFilesNoExt[i] = String(itemFilesNoExt[i]);
}

//*** OBJECTS ***
/**
 * @ignore
 * Transparent wrapper around Java File.
 */
function File() {
	if (arguments.length === 1) {
		if ((typeof arguments[0] !== "string")
				&& (typeof arguments[0] !== "object")) {
			throw "TypeError: Call to constructor 'File(<string||object>)' with wrong type of argument!";
		}
		return new Packages.jgamebase.model.script.JSFile(arguments[0]);
	}

	if (arguments.length === 2) {
		if ((typeof arguments[0] !== "string")
				&& (typeof arguments[0] !== "object")
				&& (typeof arguments[1] !== "string")) {
			throw "TypeError: Call to constructor 'File(<string||object>, <string>)' with wrong type of argument!";
		}
		return new Packages.jgamebase.model.script.JSFile(arguments[0],
				arguments[1]);
	}

	throw "ArgumentCountError: Call to constructor 'File()' with wrong number of arguments!";
}

// *** METHODS ***

/**
 * Tests if this string starts with the specified prefix.
 * @returns <code>true</code> if the character sequence represented by the
 *          argument is a prefix of the character sequence represented by
 *          this string; <code>false</code> otherwise.
 */
String.prototype.startsWith = function(/**String*/ needle) /**boolean*/ {
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to method 'String.startsWith()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to method 'String.startsWith()' with wrong type of argument (parameter 'needle' must be of type string)!";
	}

	if (needle.length > this.length) {
		return false;
	}
	return (this.indexOf(needle) === 0);
};

/**
 * Tests if this string ends with the specified suffix.
 * @returns <code>true</code> if the character sequence represented by the
 *          argument is a suffix of the character sequence represented by
 *          this object; <code>false</code> otherwise.
 */
String.prototype.endsWith = function(/**String*/ needle) /**boolean*/ {
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to method 'String.endsWith()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to method 'String.endsWith()' with wrong type of argument (parameter 'needle' must be of type string)!";
	}

	if (needle.length > this.length) {
		return false;
	}
	return (this.lastIndexOf(needle) === (this.length - needle.length));
};

/**
 * Returns a copy of the string, with leading and trailing space omitted.
 * @return  A copy of this string with leading and trailing space removed,
 *          or this string if it has no leading or trailing space.
 */
String.prototype.trim = function() /**String*/ {
	if (arguments.length !== 0) {
		throw "ArgumentCountError: Call to method 'String.trim()' with wrong number of arguments!";
	}

	var temp = this.toString();

	while (temp.startsWith(" ")) {
		temp = temp.substring(1, temp.length);
	}

	while (temp.endsWith(" ")) {
		temp = temp.substring(0, temp.length - 1);
	}

	return temp;
};

/**
 * Returns <code>true</code> if and only if this string contains one or more of the specified
 * values. <p>
 * For boolean values of <code>true</code> the String is compared with "yes", "on",
 * "true", and "1"; For boolean values of <code>false</code> with "no", "off",
 * "false", and "0".<p>
 * For String values the following wildcards are supported:<ul>
 * <li>"*"</li> The String is not empty
 * <li>"value"</li> The String must match the value
 * <li>"value*"</li> The String must start with the value
 * <li>"*value"</li> The String must end with the value
 * <li>"*value*"</li> The String must contain the value
 * </ul><p>
 * E.g. to check if the value of the variable "command" is set<p><pre>
 * if (command.contains("*")) {
 * &nbsp;&nbsp;...
 * }</pre>
 *
 * @param {boolean or String} ... the values to search for
 * @return <code>true</code> if this string contains one or more of the specified
 *         values, <code>false</code> otherwise
 */
String.prototype.contains = function() /**boolean*/ {
	if (arguments.length === 0) {
		throw "ArgumentCountError: Call to method 'String.contains()' with wrong number of arguments!";
	}

	var found = false, haystack = this.toLowerCase();

	for ( var i = 0; i < arguments.length; i++) {

		if (typeof arguments[i] === "boolean") {
			if (arguments[i]) {
				return this.contains("yes", "on", "true", "1");
			} else {
				return this.contains("no", "off", "false", "0");
			}
		}

		if (typeof arguments[i] !== "string") {
			throw "TypeError: Call to method 'String.contains()' with wrong type of argument (parameters must be of type string or boolean)!";
		}

		var needle = arguments[i].toLowerCase();

		if (needle === "*") {
			return (haystack !== "");
		}

		var firstIsStar = needle.startsWith("*");
		var lastIsStar = needle.endsWith("*");

		if ((firstIsStar) && (lastIsStar) && (!found)) {
			// *needle*
			needle = needle.substring(1, needle.length - 1);
			found = (haystack.indexOf(needle) !== -1);
		} else if ((!firstIsStar) && (lastIsStar) && (!found)) {
			// needle*
			needle = needle.substring(0, needle.length - 1);
			found = haystack.startsWith(needle);
		} else if ((firstIsStar) && (!lastIsStar) && (!found)) {
			// *needle
			needle = needle.substring(1, needle.length);
			found = haystack.endsWith(needle);
		} else if ((!firstIsStar) && (!lastIsStar) && (!found)) {
			// needle
			found = (haystack === needle);
		}
	}

	return found;
};

/**
 * Replaces each substring of this String that matches the given
 * substring with the given replacement.
 * @return  The resulting <code>String</code>
 */
String.prototype.replaceAll = function(/**String*/ SearchFor, /**String*/ ReplaceText) /**String*/ {
	var temp = this.toString();

	while (temp.indexOf(SearchFor) !== -1) {
		temp = temp.replace(SearchFor, ReplaceText);
	}

	return temp;
};

/**
 * Tests if an array element contains the specified String by iterating
 * over each array element and calling String.contains(needle).
 * @return <code>true</code> if an array element contains the specified
 *         String value, <code>false</code> otherwise.
 */
Array.prototype.contains = function(/**String*/ needle) /**boolean*/ {
	for ( var i = 0; i < this.length; i++) {
		// make object a string
		if ((String(this[i])).contains(needle)) {
			return true;
		}
	}
	return false;
};

/**
 * Finds which array element contains the specified String by iterating
 * over each array element and calling String.contains(needle).
 * @return The index of the first matching array element or "-1" if none
 * matches.
 */
Array.prototype.indexOfContains = function(/**String*/ needle) /**int*/ {
	for ( var i = 0; i < this.length; i++) {
		// make object a string
		if ((String(this[i])).contains(needle)) {
			return i;
		}
	}
	return -1;
};

// *** FUNCTIONS ***

/**
 * Add parameters to the command line (CLP = Command Line Parameters).
 * @param ... the values to add
 */
function Add_CLP() {
	if (arguments.length === 0) {
		throw "ArgumentCountError: Call to function 'Add_CLP()' with wrong number of arguments!";
	}

	for ( var i = 0; i < arguments.length; i++) {
		commandline.push((arguments[i] + "").trim());
	}
}

/**
 * Run the game emulator (stored in the variable "<code>command</code>")
 * with the parameters (stored in the array "<code>commandline</code>")
 * by calling Java "<code>Emulators.executeGame(commandline)</code>".
 */
function Run_GameEmulator() {
	if (arguments.length !== 0) {
		throw "ArgumentCountError: Call to method 'Run_GameEmulator()' with wrong number of arguments!";
	}
	if (command === "") {
		throw "NoEmulatorSetError: Before calling 'Run_Emulator()' set the emulator command to run (e.g. 'command = \"x64\"').";
	}
	Packages.jgamebase.model.Emulators.executeGame(commandline);
}

/**
 * Run the music emulator (stored in the variable "<code>command</code>")
 * with the parameters (stored in the array "<code>commandline</code>")
 * by calling Java "<code>Emulators.executeMusic(commandline)</code>".
 */
function Run_MusicEmulator() {
	if (arguments.length !== 0) {
		throw "ArgumentCountError: Call to method 'Run_MusicEmulator()' with wrong number of arguments!";
	}
	if (command === "") {
		throw "NoEmulatorSetError: Before calling 'Run_Emulator()' set the emulator command to run (e.g. 'command = \"x64\"').";
	}
	Packages.jgamebase.model.Emulators.executeMusic(commandline);
}

/**
 * An alias for <code>Run_GameEmulator()</code>.
 */
function Run_Emulator() {
	Run_GameEmulator();
}

/**
 * @ignore
 * Internal implementation, should NOT be called directly.
 */
function _Set_CFG_Item(filename, key, delimiter, value) {
	var reader;
	var writer;

	var found = false;
	var newLine = key + delimiter + value;

	try {
		var inFile = new File(filename);
		var outFile = new File(filename + "_new");

		reader = new java.io.BufferedReader(new java.io.FileReader(inFile));
		writer = new java.io.BufferedWriter(new java.io.FileWriter(outFile));

		// ... Loop as long as there are input lines.
		var line = null;
		while ((line = reader.readLine()) !== null) {
			var lowerLine = line.toLowerCase();
			var lowerKey = key.toLowerCase();

			if ((lowerLine.startsWith(lowerKey + delimiter))
					|| (lowerLine.startsWith(lowerKey + " " + delimiter))
					|| (lowerLine.startsWith(lowerKey + "\t" + delimiter))) {
				writer.write(newLine); // write new line
				found = true;
			} else {
				writer.write(line); // copy line
			}
			writer.newLine(); // Write system dependent end of line.
		}

		if (!found) { // add line at end of file
			writer.write(newLine); // write new line
			writer.newLine(); // Write system dependent end of line.
		}

		inFile.remove();
		outFile.renameTo(inFile);

	} catch (error) {
		println(error.name + ": " + error.message);
	} finally {
		if (reader !== null) {
			try {
				reader.close();
			} catch (ignore1) {
			}
		}
		if (writer !== null) {
			try {
				writer.close();
			} catch (ignore2) {
			}
		}
	}
}

/**
 * Sets a value in a configuration file. The file will be searched for a line
 * starting with key and this line will be replaced by key + delimiter + value.
 * (eg key = "useJoystick", delimiter = "=", value = "true": will lead to the
 * line "useJoystick=true")
 * @param {String or File} filename The filename or File that describes the configuration file.
 * @param key The parameter to set.
 * @param delimiter The String between key and value.
 * @param value The value the parameter will be set to.
 */
function Set_CFG_Item(filename, /**String*/
key, /**String*/
delimiter, value) {
	if (arguments.length !== 4) {
		throw "ArgumentCountError: Call to function 'Set_CFG_Item()' with wrong number of arguments!";
	}
	if ((typeof arguments[0] !== "string")
			&& (typeof arguments[0] !== "object")) {
		throw "TypeError: Call to function 'Set_CFG_Item()' with wrong type of argument (first argument 'filename' must be string or File)!";
	}
	if (typeof arguments[1] !== "string") {
		throw "TypeError: Call to function 'Set_CFG_Item()' with wrong type of argument (second argument 'key' must be string)!";
	}
	if (typeof arguments[2] !== "string") {
		throw "TypeError: Call to function 'Set_CFG_Item()' with wrong type of argument (third argument 'delimeter' must be string)!";
	}
	// fourth argument may be of any type

	if (typeof arguments[0] === "object") {
		_Set_CFG_Item(filename.getAbsolutePath(), key, delimiter, value);
	} else {
		_Set_CFG_Item(filename, key, delimiter, value);
	}

}

/**
 * Clears a value in a configuration file.<p>
 * Shortcut for Set_CFG_Item(filename, key, "", "").
 * @param {String or File} filename The filename or File that describes the configuration file.
 * @param key The parameter to set.
 */
function Set_CFG(filename, /**String*/
key) {
	if (arguments.length !== 2) {
		throw "ArgumentCountError: Call to function 'Set_CFG()' with wrong number of arguments!";
	}
	if ((typeof arguments[0] !== "string")
			&& (typeof arguments[0] !== "object")) {
		throw "TypeError: Call to function 'Set_CFG()' with wrong type of argument (first argument 'filename' must be string or File)!";
	}
	if (typeof arguments[1] !== "string") {
		throw "TypeError: Call to function 'Set_CFG()' with wrong type of argument (second argument 'key' must be string)!";
	}

	if (typeof arguments[0] === "object") {
		_Set_CFG_Item(filename.getAbsolutePath(), key, "", "");
	} else {
		_Set_CFG_Item(filename, key, "", "");
	}
}

/**
 * Sets a value in a configuration file.<p>
 * Shortcut for Set_CFG_Item(filename, key, "=", value).
 * @param {String or File} filename The filename or File that describes the configuration file.
 * @param key The parameter to set.
 * @param value The value the parameter will be set to.
 */
function Set_CFG_Value(filename, /**String*/
key, value) {
	if (arguments.length !== 3) {
		throw "ArgumentCountError: Call to function 'Set_CFG_Value()' with wrong number of arguments!";
	}
	if ((typeof arguments[0] !== "string")
			&& (typeof arguments[0] !== "object")) {
		throw "TypeError: Call to function 'Set_CFG_Value()' with wrong type of argument (first argument 'filename' must be string or File)!";
	}
	if (typeof arguments[1] !== "string") {
		throw "TypeError: Call to function 'Set_CFG_Value()' with wrong type of argument (second argument 'key' must be string)!";
	}
	// third argument may be of any type

	if (typeof arguments[0] === "object") {
		_Set_CFG_Item(filename.getAbsolutePath(), key, "=", value);
	} else {
		_Set_CFG_Item(filename, key, "=", value);
	}
}

/**
 * Sets a value in an windows style ".ini" configuration file. The file will be searched for the section and
 * the section will be searched for a line starting with key. This line will be replaced by key + "=" + value.
 * @param {String or File} filename The filename or File that describes the configuration file.
 * @param section The name of the section in the ini file (started through a "[section]" line).
 * @param key The parameter to set.
 * @param value The value the parameter will be set to.
 */
function Set_INI_Value(/**String*/
filename, /**String*/
section, /**String*/
key, value) {
	if (arguments.length !== 4) {
		throw "ArgumentCountError: Call to function 'Set_INI_Value()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to function 'Set_INI_Value()' with wrong type of argument (first argument 'filename' must be string)!";
	}
	if (typeof arguments[1] !== "string") {
		throw "TypeError: Call to function 'Set_INI_Value()' with wrong type of argument (second argument 'section' must be string)!";
	}
	if (typeof arguments[2] !== "string") {
		throw "TypeError: Call to function 'Set_INI_Value()' with wrong type of argument (third argument 'key' must be string)!";
	}
	// fourth argument may be of any type

	var iniFileManager = new Packages.jgamebase.tools.IniFileManager(filename);
	if (value === "") {
	  // remove empty values
	  iniFileManager.remove(section, key);
	} else {
	  // set value
	  iniFileManager.set(section, key, value);
	}
    iniFileManager.save();
}

/**
 * Displays a dialog with a message for the user.
 * @param message The message to display.
 */
function Show_Message(/**String*/
message) {
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to function 'Show_Message()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to function 'Show_Message()' with wrong type of argument (first argument must be string)!";
	}

	javax.swing.JOptionPane.showMessageDialog(null, message, "Info",
			javax.swing.JOptionPane.INFORMATION_MESSAGE);
}

// DID NOT WORK AS EXPECTED :-(
//function Edit_CLP(prompt) {
//	if (arguments.length > 1) {
//		throw "ArgumentCountError: Call to function 'Edit_CLP()' with wrong number of arguments!";
//	}
//	if ((arguments.length === 1) && (typeof arguments[0] != "string")) {
//		throw "TypeError: Call to function 'Edit_CLP()' with wrong type of argument (first argument must be string)!";
//	}
//
//	if (prompt === undefined) {
//		prompt = "Adjust the command line parameters as needed";
//	}
//	temp = javax.swing.JOptionPane.showInputDialog(null, prompt + ":", commandline);
//	if (temp !== null) {
//		commandline = temp;
//	}
//}

/**
 * Clear all Command Line Parameters.
 */
function Clear_CLP() {
	if (arguments.length !== 0) {
		throw "ArgumentCountError: Call to function 'Clear_CLP()' with wrong number of arguments!";
	}

	commandline = [];
}

/**
 * Get the value of an user defined "KEY=VALUE" pair set in the database.<p>
 * E.g. to display the value of the user defined variable "msg" only
 * if it is set:<p><pre>
 * if (Value("msg").contains("*")) {
 * &nbsp;&nbsp;Show_Message(&nbsp;Value("msg")&nbsp;);
 * }</pre>
 *
 * @param key The key to get the value of.
 * @returns {String} The value of the given key, or "" (empty String) if not set.
 */
function Value(/**String*/
key) /**String*/
{
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to function 'Value()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to function 'Value()' with wrong type of argument (first argument 'key' must be string)!";
	}

	var value = "";

	try {
		eval(" value = keyAndValue_" + key.toLowerCase() + " + \"\" ;");
	} catch (ignore) {
	}

	return value;
}

/**
 * Search command in the operating system search path and (last)
 * in the database script directory.
 * @return The absolute path to the command, or "" (empty String)
 * otherwise.
 */
function findInPath(/**String*/
needle) /**String*/
{
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to function 'findInPath()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to function 'findInPath()' with wrong type of argument (first argument 'needle' must be string)!";
	}

	return Packages.jgamebase.tools.FileTools.findInPath(needle);
}

/**
 * Search command in the operating system search path and (last)
 * in the database script directory.
 * @return <code>true</code> if the command is found,
 *         <code>false</code> otherwise.
 */
function existsInPath(/**String*/
needle) /**boolean*/
{
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to function 'existsInPath()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to function 'existsInPath()' with wrong type of argument (first argument 'needle' must be string)!";
	}

	return Packages.jgamebase.tools.FileTools.existsInPath(needle);
}

/**
 * Print all variables (for debug purposes).
 */
function Dump() {
	println("\n*** START VARIABLE DUMP ***");
	println("command='" + command + "'");
	println("commandline='" + commandline + "'");
	println("");

	println("dbPath='" + dbPath + "'");
	println("emulatorPath='" + emulatorPath + "'");
	println("emulatorConfigFile='" + emulatorConfigFile + "'");
	println("workPath='" + workPath + "'");
	println("");

	println("itemName='" + itemName + "'");
	println("itemComment='" + itemComment + "'");
	println("itemVersionComment='" + itemVersionComment + "'");
	println("itemControl='" + itemControl + "'");
	println("itemPalNtsc='" + itemPalNtsc + "'");
	println("itemPlayersMin='" + itemPlayersMin + "'");
	println("itemPlayersMax='" + itemPlayersMax + "'");
	println("isRunsOnPal='" + isRunsOnPal + "'");
	println("isRunsOnNtsc='" + isRunsOnNtsc + "'");
	println("isRunsOnTrueDrive='" + isRunsOnTrueDrive + "'");
	println("");

	println("useHardwareJoystick='" + useHardwareJoystick + "'");
	println("");

	println("fileToRun='" + fileToRun + "'");
	println("imageIndex='" + imageIndex + "'");
	println("imageName='" + imageName + "'");
	println("imageNameNative='" + imageNameNative + "'");
	println("");

	println("itemPathAndFile='" + itemPathAndFile + "'");
	println("itemPath='" + itemPath + "'");
	println("itemFile='" + itemFile + "'");
	println("itemFileExt='" + itemFileExt + "'");
	println("itemType='" + itemType + "'");
	println("itemFileNoExt='" + itemFileNoExt + "'");
	println("");

	println("itemPathsAndFiles.length=" + itemPathsAndFiles.length);
	println("");

	for ( var i = 0; i < itemPathsAndFiles.length; i++) {
		println("itemPathsAndFiles[" + i + "]='" + itemPathsAndFiles[i] + "'");
		println("itemPaths[" + i + "]='" + itemPaths[i] + "'");
		println("itemFiles[" + i + "]='" + itemFiles[i] + "'");
		println("itemFilesExt[" + i + "]='" + itemFilesExt[i] + "'");
		println("itemFilesNoExt[" + i + "]='" + itemFilesNoExt[i] + "'");
		println("");
	}

	println("Keys and Values (get via function 'Value(key)':");
	//keyAndValue_
	for ( var prop in global) {
		if (typeof prop === "string" && prop.startsWith("keyAndValue_")) {
			var key = prop.substring(12, prop.length);
			println(key + "='" + Value(key) + "'");
		}
	}
	println("*** END VARIABLE DUMP ***\n");
}
