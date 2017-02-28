"use strict";


// *** CONSTANTS ***

// constant for Show_Message()
var NOT_SUPPORTED = "This type is not supported by this emulator!";

//constants for palntsc
var PAL = Packages.jgamebase.Const.FORDISPLAY_PALNTSC[0] + "";
var PALNTSC = Packages.jgamebase.Const.FORDISPLAY_PALNTSC[1] + "";
var NTSC = Packages.jgamebase.Const.FORDISPLAY_PALNTSC[2] + "";
var PALntsc = Packages.jgamebase.Const.FORDISPLAY_PALNTSC[3] + "";

// constants for control
var JOYPORT2 = Packages.jgamebase.Const.FORDISPLAY_CONTROL[0] + "";
var JOYPORT1 = Packages.jgamebase.Const.FORDISPLAY_CONTROL[1] + "";
var KEYBOARD = Packages.jgamebase.Const.FORDISPLAY_CONTROL[2] + "";
var PADDLEPORT2 = Packages.jgamebase.Const.FORDISPLAY_CONTROL[3] + "";
var PADDLEPORT1 = Packages.jgamebase.Const.FORDISPLAY_CONTROL[4] + "";
var MOUSE = Packages.jgamebase.Const.FORDISPLAY_CONTROL[5] + "";
var LIGHTPEN = Packages.jgamebase.Const.FORDISPLAY_CONTROL[6] + "";
var KOALAPAD = Packages.jgamebase.Const.FORDISPLAY_CONTROL[7] + "";
var LIGHTGUN = Packages.jgamebase.Const.FORDISPLAY_CONTROL[8] + "";


// *** VARIABLES ***
var global = (function () { return this; }).call(null);

var command = "";
var commandline = [];

// convert variables from java String to javascript string
dbPath = dbPath + "";
emulatorPath = emulatorPath + "";
emulatorConfigFile = emulatorConfigFile + "";
workPath = workPath + "";
itemName = itemName + "";
itemComment = itemComment + "";
itemVersionComment = itemVersionComment + "";
itemControl = itemControl + "";
itemPalNtsc = itemPalNtsc + "";
itemPlayersMin = itemPlayersMin + "";
itemPlayersMax = itemPlayersMax + "";
//isRunsOnPal, isRunsOnNtsc, isRunsOnTrueDrive, useHardwareJoystick are boolean, no need to convert
fileToRun = fileToRun + "";
imageIndex = imageIndex + "";
imageName = imageName + "";
imageNameNative = imageNameNative + "";
itemPathAndFile = itemPathAndFile + "";
itemPath = itemPath + "";
itemFile = itemFile + "";
itemFileExt = itemFileExt + "";
itemType = itemType + "";
itemFileNoExt = itemFileNoExt + "";

for (var i = 0; i < itemPathsAndFiles.length; i++) {
	itemPathsAndFiles[i] = itemPathsAndFiles[i] + "";
	itemPaths[i] = itemPaths[i] + "";
	itemFiles[i] = itemFiles[i] + "";
	itemFilesExt[i] = itemFilesExt[i] + "";
	itemFilesNoExt[i] = itemFilesNoExt[i] + "";
}

//*** OBJECTS ***

function File() {
	if (arguments.length === 1) {
		if ((typeof arguments[0] !== "string") && (typeof arguments[0] !== "object")) {
			throw "TypeError: Call to constructor 'File(<string||object>)' with wrong type of argument!";
		}
		return new Packages.jgamebase.model.script.JSFile(arguments[0]);
	}

	if (arguments.length === 2) {
		if ((typeof arguments[0] !== "string") && (typeof arguments[0] !== "object") && (typeof arguments[1] !== "string")) {
			throw "TypeError: Call to constructor 'File(<string||object>, <string>)' with wrong type of argument!";
		}
		return new Packages.jgamebase.model.script.JSFile(arguments[0], arguments[1]);
	}

	throw "ArgumentCountError: Call to constructor 'File()' with wrong number of arguments!";
}	

// *** METHODS ***

//add startsWith() method to String
String.prototype.startsWith = function (needle) {
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

//add endsWith() method to String
String.prototype.endsWith = function (needle) {
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

//add trim() method to String
String.prototype.trim = function () {
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

//add contains() method to String
String.prototype.contains = function () {
	if (arguments.length === 0) {
		throw "ArgumentCountError: Call to method 'String.contains()' with wrong number of arguments!";
	}

	var found = false, haystack = this.toLowerCase();
	
	for (var i = 0; i < arguments.length; i++) {

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

//add replaceAll() method to String
String.prototype.replaceAll = function (SearchFor, ReplaceText) {
	var temp = this.toString();
	
	while (temp.indexOf(SearchFor) !== -1)  {
		temp = temp.replace(SearchFor, ReplaceText);
	}	

	return temp;
};

//add contains to (string) array
Array.prototype.contains = function (needle) {
	for (var i = 0; i < this.length; i++) {
		// make object a string
		if ((this[i] + "").contains(needle)) {
			return true;
		}
	}
	return false;
};

//add indexOfContains to (string) array
Array.prototype.indexOfContains = function (needle) {
	for (var i = 0; i < this.length; i++) {
		// make object a string
		if ((this[i] + "").contains(needle)) {
			return i;
		}
	}
	return -1;
};

// *** FUNCTIONS ***

function Add_CLP() {
	if (arguments.length === 0) {
		throw "ArgumentCountError: Call to function 'Add_CLP()' with wrong number of arguments!";
	}
	
	for (var i = 0; i < arguments.length; i++) {
		commandline.push((arguments[i]+"").trim());
	}
}

function Run_GameEmulator() {
	if (command === "") {
		throw "NoEmulatorSetError: Before calling 'Run_Emulator()' set the emulator command to run (e.g. 'command = \"x64\"').";
	}
	Packages.jgamebase.model.Emulators.executeGame(commandline);
}

function Run_MusicEmulator() {
	if (command === "") {
		throw "NoEmulatorSetError: Before calling 'Run_Emulator()' set the emulator command to run (e.g. 'command = \"x64\"').";
	}
	Packages.jgamebase.model.Emulators.executeMusic(commandline);
}

function Run_Emulator() {
	Run_GameEmulator();
}

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
			
			if ((lowerLine.startsWith(lowerKey + delimiter)) || (lowerLine.startsWith(lowerKey + " " + delimiter)) || (lowerLine.startsWith(lowerKey + "\t" + delimiter))) {
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

function Set_CFG_Item(filename, key, delimiter, value) {
	if (arguments.length !== 4) {
		throw "ArgumentCountError: Call to function 'Set_CFG_Item()' with wrong number of arguments!";
	}
	if ((typeof arguments[0] !== "string") && (typeof arguments[0] !== "object")) {
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

function Set_CFG(filename, key) {
	if (arguments.length !== 2) {
		throw "ArgumentCountError: Call to function 'Set_CFG()' with wrong number of arguments!";
	}
	if ((typeof arguments[0] !== "string") && (typeof arguments[0] !== "object")) {
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

function Set_CFG_Value(filename, key, value) {
	if (arguments.length !== 3) {
		throw "ArgumentCountError: Call to function 'Set_CFG_Value()' with wrong number of arguments!";
	}
	if ((typeof arguments[0] !== "string") && (typeof arguments[0] !== "object")) {
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

function Set_INI_Value(filename, section, key, value) {
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

	var iniFileManager = Packages.jgamebase.tools.IniFileManager(filename);
	iniFileManager.set(section, key, value);
	iniFileManager.save();
}

function Show_Message(message) {
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to function 'Show_Message()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to function 'Show_Message()' with wrong type of argument (first argument must be string)!";
	}
	
	javax.swing.JOptionPane.showMessageDialog(null, message, "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE);
}

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

function Clear_CLP() {
	if (arguments.length !== 0) {
		throw "ArgumentCountError: Call to function 'Clear_CLP()' with wrong number of arguments!";
	}

	commandline = [];
}

function Value(key) {
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

function findInPath(needle) {
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to function 'findInPath()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to function 'findInPath()' with wrong type of argument (first argument 'needle' must be string)!";
	}

    return Packages.jgamebase.tools.FileTools.findInPath(needle);
}

function existsInPath(needle) {
	if (arguments.length !== 1) {
		throw "ArgumentCountError: Call to function 'existsInPath()' with wrong number of arguments!";
	}
	if (typeof arguments[0] !== "string") {
		throw "TypeError: Call to function 'existsInPath()' with wrong type of argument (first argument 'needle' must be string)!";
	}

    return Packages.jgamebase.tools.FileTools.existsInPath(needle);
}

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
	
	for (var i = 0; i < itemPathsAndFiles.length; i++) {
		println("itemPathsAndFiles[" + i + "]='" + itemPathsAndFiles[i] + "'");
		println("itemPaths[" + i + "]='" + itemPaths[i] + "'");
		println("itemFiles[" + i + "]='" + itemFiles[i] + "'");
		println("itemFilesExt[" + i + "]='" + itemFilesExt[i] + "'");
		println("itemFilesNoExt[" + i + "]='" + itemFilesNoExt[i] + "'");
		println("");
	}

	println("Keys and Values (get via function 'Value(key)':");
	//keyAndValue_
	for (var prop in global) {
		if (typeof prop === "string" && prop.startsWith("keyAndValue_")) {
			var key = prop.substring(12, prop.length);
			println(key + "='" + Value(key) + "'");
		}
	}
	println("*** END VARIABLE DUMP ***\n");
}
