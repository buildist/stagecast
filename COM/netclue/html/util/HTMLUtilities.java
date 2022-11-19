/* HTMLUtilities - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.netclue.html.util;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;

import com.netclue.html.HTMLCharTable;

public class HTMLUtilities
{
    private static boolean eventQueueTested;
    private static boolean canAccessEventQueue;
    static HTMLCharTable specTable = new HTMLCharTable();
    static String[] rome1 = { "i", "x", "c", "m" };
    static String[] rome5 = { "v", "l", "d", " " };
    
    static class ScheduleComponent extends Component
    {
	static final int EventID = 10000;
	Runnable runnable;
	Object lock;
	Exception exception;
	
	ScheduleComponent(Runnable runnable) {
	    this.enableEvents(10000L);
	    this.runnable = runnable;
	    lock = new Object();
	}
	
	protected void processEvent(AWTEvent awtevent) {
	    synchronized (runnable) {
		try {
		    runnable.run();
		} catch (Exception exception) {
		    this.exception = exception;
		}
	    }
	}
    }
    
    static class ScheduleEvent extends AWTEvent
    {
	ScheduleEvent(Component component) {
	    super(component, 10000);
	}
    }
    
    public static int stringToInt(String string) {
	int i = 0;
	try {
	    i = Integer.parseInt(string);
	} catch (Exception exception) {
	    /* empty */
	}
	return i;
    }
    
    public static int stringToRatioInt(String string) {
	int i = string.indexOf('%');
	if (i != -1)
	    string = "-" + string.substring(0, i);
	int i_0_ = 0;
	try {
	    i_0_ = Integer.parseInt(string);
	} catch (Exception exception) {
	    /* empty */
	}
	return i_0_;
    }
    
    public static int stringToFontSize(String string) {
	int i = 0;
	try {
	    char c = string.charAt(0);
	    if (c == '+' || c == '-')
		string = string.substring(1);
	    i = Integer.parseInt(string);
	    if (c == '+' || c == '-')
		i = c == '+' ? 1000 + i : 1000 - i;
	} catch (Exception exception) {
	    /* empty */
	}
	return i;
    }
    
    public static int halignToInt(String string) {
	int i = 0;
	if (string.equalsIgnoreCase("center"))
	    i = 1;
	else if (string.equalsIgnoreCase("right"))
	    i = 2;
	return i;
    }
    
    public static String getRomeSequence(int i) {
	if (i >= 4400)
	    return "";
	String string = "";
	int i_1_ = 0;
	while (i_1_ < 4) {
	    int i_2_ = i % 10;
	    switch (i_2_) {
	    case 1:
		string = rome1[i_1_] + string;
		break;
	    case 2:
		string = rome1[i_1_] + rome1[i_1_] + string;
		break;
	    case 3:
		string = rome1[i_1_] + rome1[i_1_] + rome1[i_1_] + string;
		break;
	    case 4:
		string = rome1[i_1_] + rome5[i_1_] + string;
		break;
	    case 5:
		string = rome5[i_1_] + string;
		break;
	    case 6:
		string = rome5[i_1_] + rome1[i_1_] + string;
		break;
	    case 7:
		string = rome5[i_1_] + rome1[i_1_] + rome1[i_1_] + string;
		break;
	    case 8:
		string = (rome5[i_1_] + rome1[i_1_] + rome1[i_1_] + rome1[i_1_]
			  + string);
		break;
	    case 9:
		string = rome1[i_1_] + rome1[i_1_ + 1] + string;
		break;
	    }
	    i_1_++;
	    i /= 10;
	}
	return string;
    }
    
    public static String colorToHex(Color color) {
	String string = new String("#");
	String string_3_ = Integer.toHexString(color.getRed());
	if (string_3_.length() > 2)
	    throw new Error("invalid red value");
	if (string_3_.length() < 2)
	    string += "0" + (String) string_3_;
	else
	    string += (String) string_3_;
	string_3_ = Integer.toHexString(color.getGreen());
	if (string_3_.length() > 2)
	    throw new Error("invalid green value");
	if (string_3_.length() < 2)
	    string += "0" + (String) string_3_;
	else
	    string += (String) string_3_;
	string_3_ = Integer.toHexString(color.getBlue());
	if (string_3_.length() > 2)
	    throw new Error("invalid green value");
	if (string_3_.length() < 2)
	    string += "0" + (String) string_3_;
	else
	    string += (String) string_3_;
	return string;
    }
    
    public static final Color hexToColor(String string) {
	if (string.length() != 7) {
	    string = string.trim();
	    if (string.length() == 6)
		string = string.concat("0");
	    else if (string.length() != 7)
		return Color.white;
	}
	if (string.startsWith("#")) {
	    String string_4_ = "0x" + string.substring(1, 7);
	    Color color;
	    try {
		color = Color.decode(string_4_);
	    } catch (Exception exception) {
		color = null;
	    }
	    return color;
	}
	return null;
    }
    
    static final int stringToHex(String string) {
	if (string.length() != 2)
	    throw new Error("invalid hex string" + string);
	int i = Character.digit(string.charAt(0), 16) * 16;
	int i_5_ = Character.digit(string.charAt(1), 16);
	return i_5_ + i;
    }
    
    public static final Color stringToColor(String string) {
	Color color = null;
	try {
	    if (string.charAt(0) == '#')
		color = hexToColor(string);
	    else if (string.equalsIgnoreCase("Black"))
		color = hexToColor("#000000");
	    else if (string.equalsIgnoreCase("Silver"))
		color = hexToColor("#C0C0C0");
	    else if (string.equalsIgnoreCase("Gray"))
		color = hexToColor("#808080");
	    else if (string.equalsIgnoreCase("White"))
		color = hexToColor("#FFFFFF");
	    else if (string.equalsIgnoreCase("Maroon"))
		color = hexToColor("#800000");
	    else if (string.equalsIgnoreCase("Red"))
		color = hexToColor("#FF0000");
	    else if (string.equalsIgnoreCase("Purple"))
		color = hexToColor("#800080");
	    else if (string.equalsIgnoreCase("Fuchsia"))
		color = hexToColor("#FF00FF");
	    else if (string.equalsIgnoreCase("Green"))
		color = hexToColor("#008000");
	    else if (string.equalsIgnoreCase("Lime"))
		color = hexToColor("#00FF00");
	    else if (string.equalsIgnoreCase("Olive"))
		color = hexToColor("#808000");
	    else if (string.equalsIgnoreCase("Yellow"))
		color = hexToColor("#FFFF00");
	    else if (string.equalsIgnoreCase("Navy"))
		color = hexToColor("#000080");
	    else if (string.equalsIgnoreCase("Blue"))
		color = hexToColor("#0000FF");
	    else if (string.equalsIgnoreCase("Teal"))
		color = hexToColor("#008080");
	    else if (string.equalsIgnoreCase("Aqua"))
		color = hexToColor("#00FFFF");
	    else if (string.length() == 6)
		color = Color.decode("#" + string);
	} catch (Exception exception) {
	    /* empty */
	}
	return color;
    }
    
    private static String replaceSpecialChar(String string, int i, int i_6_,
					     char c) {
	String string_7_ = string.substring(0, i) + c;
	if (i_6_ < string.length() - 1)
	    string_7_ += string.substring(i_6_ + 1, string.length());
	return string_7_;
    }
    
    public static String xlateSpecialChars(String string) {
	boolean bool = false;
	for (int i = string.indexOf('&'); i < string.length() - 1 && i >= 0;
	     i = string.indexOf('&', i + 1)) {
	    boolean bool_8_ = false;
	    int i_9_ = string.indexOf(';', i);
	    if (string.charAt(i + 1) == '#') {
		String string_10_;
		if (i_9_ < 0) {
		    i_9_ = string.indexOf(' ', i);
		    if (i_9_ == -1)
			i_9_ = i + 5;
		    string_10_ = string.substring(i + 2, i_9_--);
		} else
		    string_10_ = string.substring(i + 2, i_9_);
		try {
		    int i_11_ = Integer.valueOf(string_10_).intValue();
		    if (i_11_ == 149)
			string = replaceSpecialChar(string, i, i_9_, '.');
		    else if (i_11_ == 145)
			string = replaceSpecialChar(string, i, i_9_, '`');
		    else if (i_11_ == 146)
			string = replaceSpecialChar(string, i, i_9_, '\'');
		    else if (i_11_ >= 147 && i_11_ <= 148)
			string = replaceSpecialChar(string, i, i_9_, '\"');
		    else if (i_11_ == 151 || i_11_ == 150)
			string = replaceSpecialChar(string, i, i_9_, '-');
		    else
			string = replaceSpecialChar(string, i, i_9_,
						    (char) i_11_);
		} catch (Exception exception) {
		    break;
		}
	    } else {
		if (i_9_ < 0)
		    i_9_ = string.indexOf(' ', i);
		if (i_9_ > 0 || string.length() <= 6) {
		    if (i_9_ < 0)
			i_9_ = string.length();
		    String string_12_ = string.substring(i + 1, i_9_);
		    char c = specTable.getSymbol(string_12_);
		    if (c != 0)
			string = replaceSpecialChar(string, i, i_9_, c);
		}
	    }
	}
	return string;
    }
    
    static synchronized EventQueue getEventQueue() {
	if (!eventQueueTested) {
	    try {
		EventQueue eventqueue
		    = Toolkit.getDefaultToolkit().getSystemEventQueue();
		canAccessEventQueue = true;
	    } catch (Exception exception) {
		canAccessEventQueue = false;
	    }
	    eventQueueTested = true;
	}
	if (eventQueueTested && canAccessEventQueue)
	    return Toolkit.getDefaultToolkit().getSystemEventQueue();
	return null;
    }
    
    public static void scheduleIt(Runnable runnable) {
	EventQueue eventqueue = getEventQueue();
	if (eventqueue != null) {
	    ScheduleComponent schedulecomponent
		= new ScheduleComponent(runnable);
	    ScheduleEvent scheduleevent = new ScheduleEvent(schedulecomponent);
	    eventqueue.postEvent(scheduleevent);
	}
    }
}
