/* Summarizer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import COM.stagecast.ifc.netscape.util.Enumeration;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class Summarizer
    implements Summary, ResourceIDs.SummaryIDs, Resource.FormatCallback
{
    private static final String SECTION_HEAD = "<H2 ALIGN=CENTER>";
    private static final String SECTION_TAIL = "</H2>";
    private static final String ITEM_HEAD = "<H3>";
    private static final String ITEM_TAIL = "</H3>";
    private static final String SUBITEM_HEAD = "<H4>";
    private static final String SUBITEM_TAIL = "</H4>";
    private static final String ITAL_HEAD = "<I>";
    private static final String ITAL_TAIL = "</I>";
    private static final String BOLD_HEAD = "<B>";
    private static final String BOLD_TAIL = "</B>";
    private static final int NUMBERED_LIST = 0;
    private static final int DEFINITION_LIST = 1;
    private static final int TERM_LIST = 2;
    private static final String[] LIST_BEGIN = { "<OL>", "<DL>", "<DL>" };
    private static final String[] LIST_ITEM = { "<LI>", "<DD>", "<DT>" };
    private static final String[] LIST_END = { "</OL>", "</DL>", "</DL>" };
    private static final int EMAIL_TYPE = 0;
    private static final int FTP_TYPE = 1;
    private static final int HTTP_TYPE = 2;
    World _world;
    PrintWriter _pw;
    int _nesting;
    Vector _listType;
    Vector _allChars;
    CharacterPrototype _ruleTarget;
    GeneralizedCharacter _ruleSelf;
    
    private class CharInfo
    {
	CharacterPrototype _proto;
	Vector _userVars;
	
	CharInfo(CharacterPrototype cp) {
	    _proto = cp;
	    _userVars = Summarizer.this.selectUserVariables(cp.getVariableList
								().elements());
	}
	
	CharacterPrototype getProto() {
	    return _proto;
	}
	
	String getName() {
	    return _proto.getName();
	}
	
	Vector getVars() {
	    return _userVars;
	}
	
	boolean hasAppearances() {
	    return (_proto.numberOfAppearances() > 1
		    && !(_proto instanceof SpecialPrototype));
	}
	
	boolean hasData() {
	    return (_userVars.size() > 0 || _proto.numberOfAppearances() > 1
		    || _proto.hasRules());
	}
	
	Subroutine getRules() {
	    return _proto.getMainSubroutine();
	}
    }
    
    private class StageInfo
    {
	Stage _stage;
	Vector _userVars;
	
	StageInfo(Stage stage) {
	    _stage = stage;
	    _userVars
		= Summarizer.this
		      .selectUserVariables(stage.getVariableList().elements());
	}
	
	Stage getStage() {
	    return _stage;
	}
	
	String getName() {
	    return _stage.getName();
	}
	
	Vector getVars() {
	    return _userVars;
	}
	
	boolean hasData() {
	    return _userVars.size() > 0;
	}
    }
    
    private class LinkInfo
	implements COM.stagecast.ifc.netscape.util.Comparable
    {
	int _type;
	int _start;
	int _end;
	
	LinkInfo(int type, int start, int end) {
	    _type = type;
	    _start = start;
	    _end = end;
	}
	
	public int compareTo(Object obj) {
	    int otherStart = ((LinkInfo) obj)._start;
	    if (_start < otherStart)
		return -1;
	    if (_start == otherStart)
		return 0;
	    return 1;
	}
    }
    
    public static void summarize(World world, OutputStream os) {
	Summarizer s = new Summarizer(world);
	s.summarize(os);
    }
    
    public static String htmlize(World world, String rawText) {
	Summarizer s = new Summarizer(world);
	return s.htmlize(rawText);
    }
    
    private Summarizer(World world) {
	_world = world;
    }
    
    private void summarize(OutputStream os) {
	_pw = new PrintWriter(os, true);
	_nesting = -1;
	_listType = new Vector(5);
	_allChars = new Vector(20);
	_ruleTarget = null;
	_ruleSelf = null;
	writeHeader();
	writeBody();
	writeTrailer();
    }
    
    private void writeHeader() {
	_pw.println("<HTML>");
	_pw.println("<HEAD>");
	_pw.println
	    ("  <META NAME=\"GENERATOR\" CONTENT=\"Stagecast Creator\">");
	_pw.print("<TITLE>");
	writeText(_world.getName());
	_pw.println("</TITLE>");
	_pw.println("</HEAD>");
    }
    
    private void writeTrailer() {
	_pw.println("</HTML>");
    }
    
    private void writeBody() {
	_pw.println("<BODY BGCOLOR=\"#ffffff\">");
	writeWorld();
	_pw.println("<P>&nbsp;</P>");
	_pw.println("<P><I>");
	writeResourceText("SUM wcb");
	writeText(_world.getOriginalCreatorVersion());
	newLine();
	writeResourceText("SUM wsb");
	writeText(_world.getCreatorVersion());
	newLine();
	writeSysinfo();
	_pw.println("</I></P>");
	_pw.println("</BODY>");
    }
    
    private void newSection(String name) {
	_pw.println("<P>&nbsp;</P>");
	_pw.println("<HR>");
	writeSectionHead(name);
    }
    
    private void newLine() {
	_pw.println("<BR>");
    }
    
    private void indent() {
	for (int i = 0; i < _nesting; i++)
	    _pw.print("  ");
    }
    
    private void beginPara(String align) {
	if (align == null)
	    _pw.print("<P>");
	else
	    _pw.print("<P ALIGN=" + align + ">");
    }
    
    private void endPara() {
	_pw.print("</P>");
	_pw.println();
    }
    
    private void beginList(int type) {
	indent();
	_nesting++;
	_listType.addElement(new Integer(type));
	_pw.println(LIST_BEGIN[type]);
    }
    
    private void beginListElement() {
	beginListElement(((Integer) _listType.elementAt(_nesting)).intValue());
    }
    
    private void beginListElement(int type) {
	indent();
	_pw.print(LIST_ITEM[type]);
    }
    
    private void endListElement() {
	_pw.println();
    }
    
    private void endList() {
	int type = ((Integer) _listType.removeElementAt(_nesting)).intValue();
	_nesting--;
	indent();
	_pw.println(LIST_END[type]);
    }
    
    private void writeItalics(String text) {
	_pw.print("<I>");
	writeText(text);
	_pw.print("</I>");
    }
    
    private void writeBold(String text) {
	_pw.print("<B>");
	writeText(text);
	_pw.print("</B>");
    }
    
    private void writeSectionHead(String text) {
	_pw.print("<H2 ALIGN=CENTER>");
	writeText(text);
	_pw.println("</H2>");
    }
    
    private void writeAnchor(String anchorName) {
	_pw.print("<A NAME=\"");
	writeText(anchorName);
	_pw.println("\"></A>");
    }
    
    private void writeLink(String text, String link) {
	_pw.print("<A HREF=\"");
	writeText(link);
	_pw.print("\">");
	writeText(text);
	_pw.print("</A>");
    }
    
    private void writeLinkedText(String text, String anchorName) {
	writeLink(text, "#" + anchorName);
    }
    
    private Vector selectUserVariables(Enumeration enum) {
	Vector newVars = new Vector(5);
	while (enum.hasMoreElements()) {
	    Variable v = (Variable) enum.nextElement();
	    if (v.isUserVariable() && v.isVisible())
		newVars.addElement(v);
	}
	return newVars;
    }
    
    private void writeSysinfo() {
	writeText(PlaywriteSystem.getSystemProperty("java.vendor"));
	writeText(" Java version ");
	writeText(PlaywriteSystem.getSystemProperty("java.version"));
	writeText(", ");
	writeText(PlaywriteSystem.getSystemProperty("os.name"));
	writeText(" ");
	writeText(PlaywriteSystem.getSystemProperty("os.version"));
    }
    
    private void writeWorld() {
	collectCharInfo();
	writeAboutText();
	writeCharacters();
	writeStages();
	writeJars();
	writeGlobals();
    }
    
    private void collectCharInfo() {
	Enumeration e = _world.getPrototypes().getContents();
	while (e.hasMoreElements()) {
	    CharacterPrototype cp = (CharacterPrototype) e.nextElement();
	    if (!cp.isProxy())
		_allChars.addElement(new CharInfo(cp));
	}
	e = _world.getSpecialPrototypes().getContents();
	while (e.hasMoreElements()) {
	    CharacterPrototype cp = (CharacterPrototype) e.nextElement();
	    if (!cp.isProxy() && (_world.countRulesReferringTo(cp) > 0
				  || cp.numberOfInstances() > 0))
		_allChars.addElement(new CharInfo(cp));
	}
    }
    
    private void writeAboutText() {
	writeSectionHead(_world.getName());
	File worldSrc = _world.getSourceFile();
	Date date = (worldSrc == null ? new Date()
		     : new Date(worldSrc.lastModified()));
	beginPara("CENTER");
	writeBold(_world.getAuthor());
	newLine();
	writeText(DateFormat.getDateInstance().format(date));
	endPara();
	beginPara(null);
	_pw.print(htmlize(_world.getComment()));
	endPara();
	beginPara(null);
	int count = _allChars.size();
	writeResourceText("SUM pc", new Object[] { new Integer(count),
						   new Integer(count) });
	if (PlaywriteRoot.hasAuthoringLimits()) {
	    count = Math.max(0, 6 - _world.getNumberOfPrototypesInWorld());
	    writeResourceText("SUM pl", new Object[] { new Integer(count) });
	}
	newLine();
	count = _world.getNumberOfRulesInWorld();
	writeResourceText("SUM rc", new Object[] { new Integer(count),
						   new Integer(count) });
	count = Math.max(0, 10 - count);
	if (PlaywriteRoot.hasAuthoringLimits())
	    writeResourceText("SUM rl", new Object[] { new Integer(count) });
	newLine();
	count = _world.getNumberOfStagesInWorld();
	writeResourceText("SUM sc", new Object[] { new Integer(count),
						   new Integer(count) });
	count = Math.max(0, 2 - count);
	if (PlaywriteRoot.hasAuthoringLimits())
	    writeResourceText("SUM sl", new Object[] { new Integer(count) });
	endPara();
    }
    
    private void writeCharacters() {
	if (_allChars.size() >= 1) {
	    newSection(Resource.getText("SUM cs"));
	    beginList(1);
	    beginListElement();
	    Enumeration e = _allChars.elements();
	    while (e.hasMoreElements()) {
		CharInfo info = (CharInfo) e.nextElement();
		String name = info.getName();
		if (info.hasData())
		    writeLinkedText(name, name);
		else
		    writeText(name);
		if (e.hasMoreElements())
		    writeText(", ");
		_pw.println();
	    }
	    endListElement();
	    endList();
	    e = _allChars.elements();
	    while (e.hasMoreElements()) {
		CharInfo info = (CharInfo) e.nextElement();
		String name = info.getName();
		if (info.hasData()) {
		    writeAnchor(name);
		    _pw.print("<H3>");
		    writeText(name);
		    _pw.println("</H3>");
		    writeRules(info.getProto(), info.getRules());
		    if (info.hasAppearances())
			writeAppearances(info.getProto());
		    writeVariables(name, info.getVars(), info.getProto());
		}
	    }
	}
    }
    
    private void writeRules(CharacterPrototype cp, Subroutine sub) {
	if (sub.numberOfRules() >= 1) {
	    _ruleTarget = cp;
	    Vector rules = sub.getRules();
	    beginList(0);
	    for (int i = 0; i < rules.size(); i++) {
		RuleListItem rli = (RuleListItem) rules.elementAt(i);
		if (rli instanceof Comment)
		    beginListElement(2);
		else {
		    beginListElement();
		    if (!rli.isEnabled())
			writeBold("(disabled)  ");
		}
		if (rli instanceof Rule)
		    writeRule((Rule) rli, true);
		else if (rli instanceof Subroutine) {
		    writeSubroutine((Subroutine) rli);
		    writeRules(cp, (Subroutine) rli);
		} else if (rli instanceof Comment)
		    writeItalics(((Comment) rli).getContents());
		else
		    writeResourceText("SUM ure");
		endListElement();
	    }
	    endList();
	    _ruleTarget = null;
	}
    }
    
    private void writeRule(Rule rule, boolean includeName) {
	Appearance soloAppearance = null;
	boolean hasAppearanceTest = false;
	CharacterPrototype proto = rule.getOwner();
	GeneralizedCharacter pushSelf = _ruleSelf;
	_ruleSelf = rule.getSelf();
	String comment = rule.getComment();
	if (includeName) {
	    writeText(rule.getName());
	    if (comment != null && !comment.equals("")) {
		newLine();
		writeItalics(comment);
	    }
	}
	if (proto.numberOfAppearances() == 1) {
	    Enumeration e = proto.getAppearances();
	    soloAppearance = (Appearance) e.nextElement();
	}
	Vector tests = new Vector(10);
	Enumeration e = rule.getTests();
	while (e.hasMoreElements()) {
	    RuleTest test = (RuleTest) e.nextElement();
	    if (!(test instanceof BindTest)) {
		if (test instanceof BooleanTest
		    && test.getSelf().getPrototype() == proto
		    && test.findReferenceTo(proto.appearanceVar) != null) {
		    hasAppearanceTest = true;
		    if (soloAppearance == null
			|| test.findReferenceTo(soloAppearance) == null)
			tests.addElement(test);
		} else
		    tests.addElement(test);
	    }
	}
	if (!hasAppearanceTest && soloAppearance == null) {
	    Summarizable s = new Summarizable() {
		public void summarize(Summary s_1_) {
		    s_1_.writeText(Resource.getText("SUM aa"));
		}
	    };
	    if (tests.size() == 0)
		tests.addElement(s);
	    else
		tests.insertElementAt(s, 0);
	}
	Vector actions = new Vector(10);
	e = rule.getActions();
	while (e.hasMoreElements()) {
	    RuleAction action = (RuleAction) e.nextElement();
	    actions.addElement(action);
	}
	beginList(1);
	if (tests.size() > 0) {
	    beginListElement();
	    writeBold(Resource.getText("SUM andif"));
	    endListElement();
	}
	for (int i = 0; i < tests.size(); i++) {
	    Summarizable test = (Summarizable) tests.elementAt(i);
	    beginListElement();
	    try {
		test.summarize(this);
	    } catch (Throwable throwable) {
		writeResourceText("SUM testerr");
	    }
	    endListElement();
	}
	if (actions.size() > 0) {
	    beginListElement();
	    writeBold(Resource.getText("SUM do"));
	    endListElement();
	}
	for (int i = 0; i < actions.size(); i++) {
	    Summarizable action = (Summarizable) actions.elementAt(i);
	    beginListElement();
	    try {
		action.summarize(this);
	    } catch (Throwable throwable) {
		writeResourceText("SUM acterr");
	    }
	    endListElement();
	}
	endList();
	_ruleSelf = pushSelf;
    }
    
    private void writeSubroutine(Subroutine sub) {
	SubroutineType type = sub.getType();
	try {
	    if (sub.hasPretest()) {
		writeBold("pretest: ");
		beginList(1);
		writeRule(sub.getPretest(), false);
		endList();
	    }
	    writeText(sub.getName());
	    if (type.getClass() != NormalSubType.class) {
		writeText(" [");
		writeBold(type.getTypeName());
		writeText("]");
	    }
	} catch (Throwable throwable) {
	    writeResourceText("SUM suberr");
	}
	String comment = sub.getComment();
	if (comment != null && !comment.equals("")) {
	    newLine();
	    writeItalics(comment);
	}
    }
    
    private void writeAppearances(CharacterPrototype cp) {
	if (cp.numberOfAppearances() >= 2) {
	    _pw.print("<H4>");
	    writeResourceText("SUM ah", new Object[] { cp.getName() });
	    _pw.println("</H4>");
	    beginList(1);
	    beginListElement();
	    try {
		Enumeration e = cp.getAppearances();
		while (e.hasMoreElements()) {
		    Appearance app = (Appearance) e.nextElement();
		    writeText(app.getName());
		    if (e.hasMoreElements())
			writeText(", ");
		}
	    } catch (Throwable throwable) {
		writeResourceText("SUM apperr");
	    }
	    endListElement();
	    endList();
	}
    }
    
    private void writeVariables(String name, Vector vars,
				VariableOwner owner) {
	if (vars.size() >= 1) {
	    _pw.print("<H4>");
	    writeResourceText("SUM vh", new Object[] { name });
	    _pw.println("</H4>");
	    beginList(1);
	    for (int i = 0; i < vars.size(); i++) {
		Variable v = (Variable) vars.elementAt(i);
		beginListElement();
		try {
		    writeText(v.getName());
		    writeText(":\t");
		    writeValue(v.getValue(owner));
		} catch (Throwable throwable) {
		    writeResourceText("SUM varerr");
		}
		endListElement();
	    }
	    endList();
	}
    }
    
    private void writeStages() {
	Vector allStages = new Vector(10);
	Enumeration e = _world.getStages().getContents();
	while (e.hasMoreElements()) {
	    Stage stage = (Stage) e.nextElement();
	    if (!stage.isProxy())
		allStages.addElement(new StageInfo(stage));
	}
	newSection(Resource.getText("SUM ss"));
	beginList(1);
	beginListElement();
	try {
	    e = allStages.elements();
	    while (e.hasMoreElements()) {
		StageInfo info = (StageInfo) e.nextElement();
		String name = info.getName();
		if (info.hasData())
		    writeLinkedText(name, name);
		else
		    writeText(name);
		if (e.hasMoreElements())
		    writeText(", ");
	    }
	} catch (Throwable throwable) {
	    writeResourceText("SUM stgerr");
	}
	endListElement();
	endList();
	e = allStages.elements();
	while (e.hasMoreElements()) {
	    StageInfo info = (StageInfo) e.nextElement();
	    if (info.hasData()) {
		String name = info.getName();
		writeAnchor(name);
		writeVariables(name, info.getVars(), info.getStage());
	    }
	}
    }
    
    private void writeJars() {
	Vector allJars = new Vector(10);
	Enumeration je = _world.getJars().getContents();
	while (je.hasMoreElements()) {
	    Jar jar = (Jar) je.nextElement();
	    allJars.addElement(jar);
	}
	if (allJars.size() >= 1) {
	    newSection(Resource.getText("SUM js"));
	    beginList(2);
	    je = allJars.elements();
	    while (je.hasMoreElements()) {
		Jar jar = (Jar) je.nextElement();
		beginListElement();
		writeBold(jar.getName());
		endListElement();
		Enumeration jb = jar.getContents();
		beginList(2);
		while (jb.hasMoreElements()) {
		    Bindable b = (Bindable) jb.nextElement();
		    beginListElement();
		    writeText(b.getName());
		    endListElement();
		}
		endList();
	    }
	    endList();
	}
    }
    
    private void writeGlobals() {
	Vector userVars = selectUserVariables(_world.getVariables());
	if (_world.getMainCharacter() != null)
	    userVars.addElement(_world.getVariableList().findSystemVariable
				(World.SYS_FOLLOW_ME_VARIABLE_ID));
	if (userVars.size() >= 1) {
	    newSection(Resource.getText("SUM gs"));
	    writeVariables(Resource.getText("SUM gvar"), userVars, _world);
	}
    }
    
    public CharacterPrototype ruleTarget() {
	return _ruleTarget;
    }
    
    public GeneralizedCharacter ruleSelf() {
	return _ruleSelf;
    }
    
    public void writeText(String text) {
	StringBuffer buf = new StringBuffer(100);
	for (int i = 0; i < text.length(); i++) {
	    char ch = text.charAt(i);
	    if (ch == '<')
		buf.append("&lt;");
	    else if (ch == '>')
		buf.append("&gt;");
	    else if (ch == '&')
		buf.append("&amp;");
	    else
		buf.append(ch);
	}
	_pw.print(buf.toString());
    }
    
    public void writeResourceText(String resID) {
	writeText(Resource.getText(resID));
    }
    
    public void writeResourceText(String resID, Object[] params) {
	writeText(Resource.getTextAndFormat(resID, params));
    }
    
    public void writeValue(Object value) {
	String text;
	if (value == null)
	    text = Resource.getText("SUM null");
	else if (value instanceof Summarizable) {
	    ((Summarizable) value).summarize(this);
	    text = null;
	} else if (value instanceof Named)
	    text = ((Named) value).getName();
	else
	    text = value.toString();
	if (text != null)
	    writeItalics(text);
    }
    
    public String pushValue(Object value) {
	if (value instanceof String)
	    return (String) value;
	Summarizer nested = new Summarizer(_world);
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	nested._nesting = -1;
	nested._pw = new PrintWriter(baos);
	nested.writeValue(value);
	try {
	    nested._pw.close();
	} catch (Exception exception) {
	    /* empty */
	}
	try {
	    baos.close();
	} catch (Exception exception) {
	    /* empty */
	}
	return baos.toString();
    }
    
    public void writeFormat(String resourceID, Object[] params1,
			    Object[] params2) {
	Resource.format(this, resourceID, params1, params2);
    }
    
    public void writeFormat(ResourceBundle resourceBundle, String resourceID,
			    Object[] params1, Object[] params2) {
	Resource.format(resourceBundle, this, resourceID, params1, params2);
    }
    
    public void appendText(String string) {
	writeText(string);
    }
    
    public void embedText(String string) {
	_pw.print(string);
    }
    
    public void appendObject(Object object) {
	writeValue(object);
    }
    
    public void embedObject(Object object) {
	_pw.print(pushValue(object));
    }
    
    private String htmlize(String original) {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	BufferedReader reader = new BufferedReader(new StringReader(original));
	String result = null;
	PrintWriter previousWriter = _pw;
	try {
	    _pw = new PrintWriter(baos);
	    String nextLine;
	    do {
		nextLine = reader.readLine();
		if (nextLine != null) {
		    processLine(nextLine);
		    newLine();
		}
	    } while (nextLine != null);
	    _pw.flush();
	    result = baos.toString();
	} catch (Throwable t) {
	    Debug.print(true, t);
	} finally {
	    try {
		baos.close();
	    } catch (Throwable throwable) {
		/* empty */
	    }
	    _pw = previousWriter;
	}
	return result;
    }
    
    private void processLine(String line) {
	Vector locations = new Vector(2);
	int lastPos = 0;
	findEmail(line, locations);
	findURL(line, locations);
	locations.sort(true);
	if (locations.size() == 0)
	    writeText(line);
	else {
	    for (int i = 0; i < locations.size(); i++) {
		LinkInfo item = (LinkInfo) locations.elementAt(i);
		if (lastPos < item._start)
		    writeText(line.substring(lastPos, item._start));
		String chunk = line.substring(item._start, item._end);
		writeLink(chunk, item._type == 0 ? "mailto:" + chunk : chunk);
		lastPos = item._end;
	    }
	    if (lastPos < line.length())
		writeText(line.substring(lastPos, line.length()));
	}
    }
    
    private void findEmail(String line, Vector locations) {
	int pos = 0;
	for (pos = line.indexOf("@", pos); pos > 0;
	     pos = line.indexOf("@", pos)) {
	    int start = -1;
	    int end = pos + 1;
	    for (int i = pos - 1; i >= 0; i--) {
		char ch = line.charAt(i);
		if (!Character.isLetterOrDigit(ch) && ch != '-' && ch != '.') {
		    start = i + 1;
		    break;
		}
	    }
	    if (start != -1) {
		end = line.length();
		for (int i = pos + 1; i < line.length(); i++) {
		    char ch = line.charAt(i);
		    if (!Character.isLetterOrDigit(ch) && ch != '-'
			&& ch != '.') {
			end = i;
			break;
		    }
		}
		LinkInfo item = new LinkInfo(0, start, end);
		locations.addElement(item);
	    }
	    pos = end;
	}
    }
    
    private void findURL(String line, Vector locations) {
	int pos = 0;
	line = line.toLowerCase();
	for (pos = line.indexOf("tp://", pos); pos > 0;
	     pos = line.indexOf("tp://", pos)) {
	    int start = -1;
	    int end = line.length();
	    if (line.charAt(pos - 1) == 'f')
		start = pos - 1;
	    else if (line.charAt(pos - 1) == 't'
		     && line.charAt(pos - 2) == 'h')
		start = pos - 2;
	    else
		pos++;
	    if (start != -1) {
		for (int i = pos + 1; i < line.length(); i++) {
		    if (Character.isWhitespace(line.charAt(i))) {
			end = i;
			break;
		    }
		}
		if (line.charAt(end - 1) == '.' || line.charAt(end - 1) == ',')
		    end--;
		pos = end;
		int type = line.charAt(start) == 'f' ? 1 : 2;
		LinkInfo item = new LinkInfo(type, start, end);
		locations.addElement(item);
	    }
	}
    }
}
