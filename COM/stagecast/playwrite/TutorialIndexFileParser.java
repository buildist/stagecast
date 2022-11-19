/* TutorialIndexFileParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;

import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.util.Vector;

class TutorialIndexFileParser implements Debug.Constants
{
    public static final String KEY_WORD_NAME = "name";
    public static final String KEY_WORD_BUTTON1 = "button1";
    public static final String KEY_WORD_BUTTON2 = "button2";
    public static final String KEY_WORD_TITLE = "titleImage";
    public static final String KEY_WORD_HEADER = "header";
    public static final String KEY_WORD_PAGE = "nextpage";
    public static final String KEY_WORD_QUICKSTART = "quickstart";
    private String _nextPage = null;
    private PlaywriteView _header = null;
    private TutorialLessonLabel _qsLabel = null;
    
    public void setNextPage(String nextPageName) {
	_nextPage = nextPageName;
    }
    
    public void setHeader(PlaywriteView header) {
	_header = header;
    }
    
    public void setQuickStartLabel(TutorialLessonLabel label) {
	_qsLabel = label;
    }
    
    public String getNextPage() {
	return _nextPage;
    }
    
    public PlaywriteView getHeader() {
	return _header;
    }
    
    public TutorialLessonLabel getQuickStartLabel() {
	return _qsLabel;
    }
    
    public Vector parseInputStream(InputStream is) throws IOException {
	Vector result = new Vector(12);
	if (is == null) {
	    Debug.print("debug.tutorial", "input stream is null");
	    return null;
	}
	InputStreamReader reader = new InputStreamReader(is);
	BufferedReader bufferedReader = new BufferedReader(reader);
	String line = bufferedReader.readLine();
	int i = 1;
	while (line != null) {
	    TutorialLessonLabel label = parseLine(line, i);
	    if (label != null)
		result.addElement(label);
	    line = bufferedReader.readLine();
	    i++;
	}
	return result;
    }
    
    private TutorialLessonLabel parseLine(String line, int lineNumber)
	throws IOException {
	TutorialLessonLabel result = null;
	StringReader reader = new StringReader(line);
	StreamTokenizer tokenizer = new StreamTokenizer(reader);
	int tokenType = tokenizer.nextToken();
	Image titleImage = null;
	Image buttonImage1 = null;
	Image buttonImage2 = null;
	String fileName = null;
	boolean isQuickStart = false;
	for (/**/; tokenType != -1; tokenType = tokenizer.nextToken()) {
	    if (tokenType == -3) {
		if ("titleImage".equalsIgnoreCase(tokenizer.sval)) {
		    tokenType = tokenizer.nextToken();
		    tokenType = tokenizer.nextToken();
		    titleImage = getImage(tokenizer.sval);
		    if (titleImage == null)
			Debug.print
			    (true,
			     ("error in titleImage in tutorial.index: line "
			      + lineNumber));
		} else if ("button1".equalsIgnoreCase(tokenizer.sval)) {
		    tokenType = tokenizer.nextToken();
		    tokenType = tokenizer.nextToken();
		    buttonImage1 = getImage(tokenizer.sval);
		    if (buttonImage1 == null)
			Debug.print
			    (true, ("error in button1 in tutorial.index: line "
				    + lineNumber));
		} else if ("button2".equalsIgnoreCase(tokenizer.sval)) {
		    tokenType = tokenizer.nextToken();
		    tokenType = tokenizer.nextToken();
		    buttonImage2 = getImage(tokenizer.sval);
		    if (buttonImage2 == null)
			Debug.print
			    (true, ("error in button2 in tutorial.index: line "
				    + lineNumber));
		} else if ("name".equalsIgnoreCase(tokenizer.sval)) {
		    tokenType = tokenizer.nextToken();
		    tokenType = tokenizer.nextToken();
		    fileName = tokenizer.sval;
		    if (fileName == null)
			Debug.print(true,
				    ("error in name in tutorial.index: line "
				     + lineNumber));
		} else {
		    if ("nextpage".equalsIgnoreCase(tokenizer.sval)) {
			tokenType = tokenizer.nextToken();
			tokenType = tokenizer.nextToken();
			if (tokenizer.sval == null)
			    Debug.print
				(true,
				 ("error in nextPage in tutorial.index: line "
				  + lineNumber));
			else
			    _nextPage = tokenizer.sval;
			break;
		    }
		    if ("header".equalsIgnoreCase(tokenizer.sval)) {
			tokenType = tokenizer.nextToken();
			tokenType = tokenizer.nextToken();
			Image image = getImage(tokenizer.sval);
			if (image == null)
			    Debug.print
				(true,
				 ("error in header in tutorial.index: line "
				  + lineNumber));
			else
			    _header = new PlaywriteView(image);
			break;
		    }
		    if ("quickstart".equalsIgnoreCase(tokenizer.sval)) {
			if (_qsLabel != null)
			    Debug.print
				(true,
				 ("you cannot have more than one quick start file per page "
				  + lineNumber));
			else
			    isQuickStart = true;
		    }
		}
		if (fileName != null && titleImage != null
		    && buttonImage1 != null && buttonImage2 != null) {
		    PlaywriteButton button
			= new RolloverButton(buttonImage1, buttonImage2, null,
					     null);
		    if (isQuickStart)
			_qsLabel = (new TutorialLessonLabel
				    (button, new PlaywriteView(titleImage),
				     fileName));
		    else {
			verifyFileName(Tutorial.TUTORIAL_WORLD_DIRECTORY,
				       fileName);
			result = (new TutorialLessonLabel
				  (button, new PlaywriteView(titleImage),
				   fileName));
		    }
		    break;
		}
	    }
	}
	return result;
    }
    
    private Image getImage(String fileName) {
	if (fileName != null)
	    return (BitmapManager.createNativeBitmapManager
		    (new ResourceStreamProducer(this.getClass(),
						"/tutorial/" + fileName)));
	return null;
    }
    
    public static boolean verifyFileName(String applicationPath,
					 String fileName) {
	boolean result = true;
	File test = new File(applicationPath + fileName);
	if (test.exists()) {
	    try {
		fileName = fileName.replace('/', File.separator.charAt(0));
		String canonicalPath = test.getCanonicalPath();
		if (test.isDirectory() && !fileName.endsWith(File.separator))
		    fileName += File.separator;
		if (!canonicalPath.endsWith(fileName)) {
		    String realFileName
			= canonicalPath.substring(canonicalPath.length()
						  - fileName.length());
		    Debug.print(true, "file name is not correct: ");
		    Debug.print(true, "want: " + fileName);
		    Debug.print(true, "have: " + realFileName);
		    String diff = new String();
		    for (int i = 0; i < realFileName.length(); i++) {
			if (realFileName.charAt(i) != fileName.charAt(i))
			    diff += "^";
			else
			    diff += " ";
		    }
		    Debug.print(true, "      " + diff);
		    PlaywriteSystem.beep();
		    result = false;
		}
	    } catch (IOException e) {
		throw new PlaywriteInternalError
			  ("there is something terribly wrong with " + test
			   + "\n" + e);
	    }
	} else {
	    Debug.print(true, "file " + fileName + " not found");
	    result = false;
	}
	return result;
    }
}
