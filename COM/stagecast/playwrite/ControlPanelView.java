/* ControlPanelView - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Application;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.GridLayout;
import COM.stagecast.ifc.netscape.application.Image;
import COM.stagecast.ifc.netscape.application.PackConstraints;
import COM.stagecast.ifc.netscape.application.PackLayout;
import COM.stagecast.ifc.netscape.application.Target;
import COM.stagecast.ifc.netscape.application.View;
import COM.stagecast.ifc.netscape.util.Vector;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

class ControlPanelView extends WideView
    implements Debug.Constants, ResourceIDs.CommandIDs,
	       ResourceIDs.ControlPanelIDs, Target
{
    static final int DEFAULT_HEIGHT = 44;
    static final int DEFAULT_WIDTH = 400;
    static final String SPEED_SLOW_NAME = "Turtle";
    static final String SPEED_MEDIUM_NAME = "SpeedM";
    static final String SPEED_FAST_NAME = "SpeedF";
    static final String SPEED_FULL_NAME = "Bunny";
    public static final String RESET_PANELS = "smokin";
    private World _world;
    private StateWatcher worldWatcher;
    private Watcher historyWatcher;
    private PlaywriteButton playerReset;
    private PlaywriteButton authoringSplat;
    private PlaywriteButton authoringPaint;
    private PlaywriteButton authoringRule;
    private PlaywriteButton playBackward;
    private PlaywriteButton playStepBack;
    private PlaywriteButton playStop;
    private PlaywriteButton playStepForward;
    private PlaywriteButton playForward;
    private PlaywriteButton speedSlow;
    private PlaywriteButton speedMedium;
    private PlaywriteButton speedFast;
    private PlaywriteButton speedFull;
    private PlaywriteButton copyButton;
    private PlaywriteButton deleteButton;
    private ClockView clockView;
    private boolean _deferredResetWorld = false;
    private boolean _worldWasStepping = false;
    private boolean constructed = false;
    private boolean _worldHasClosed = false;
    
    ControlPanelView(World world) {
	super(0, 0, 400, 44);
	_world = world;
	PackConstraints defaultPC = new PackConstraints();
	defaultPC.setSide(2);
	defaultPC.setPadX(3);
	PackLayout lm = new PackLayout();
	lm.setDefaultConstraints(defaultPC);
	this.setLayoutManager(lm);
	populate();
	this.layoutView(0, 0);
	worldWatcher = new StateWatcher() {
	    public void stateChanged(Object target, Object oldState,
				     Object transition, Object newState) {
		synchronized (this) {
		    if (newState == World.STOPPED)
			_world.setForceStopFlag(false);
		}
		if (_deferredResetWorld && newState == World.STOPPED) {
		    _deferredResetWorld = false;
		    PlaywriteRoot.app().performCommandAndWait
			(ControlPanelView.this, "CP reset", null);
		    if (!_worldWasStepping)
			performCommand("CP Play", null);
		}
		if (newState != World.CLOSING)
		    Application.application().performCommandLater
			(ControlPanelView.this, "smokin", null);
		else
		    _worldHasClosed = true;
	    }
	};
	_world.addStateWatcher(worldWatcher);
	historyWatcher = new EventThreadWatcher(new Watcher() {
	    public void update(Object item, Object data) {
		ControlPanelView.this.resetPlayPanel();
	    }
	});
	_world.addHistoryWatcher(historyWatcher);
	Variable.systemVariable(World.SYS_SPEED_VARIABLE_ID, _world)
	    .addValueWatcher(_world, new Watcher() {
	    public void update(Object var, Object data) {
		ControlPanelView.this.resetSpeedPanel();
	    }
	});
	constructed = true;
    }
    
    final World getWorld() {
	return _world;
    }
    
    public boolean isTransparent() {
	return false;
    }
    
    public void drawView(Graphics g) {
	g.setColor(getWorld().getColor());
	g.fillRect(this.localBounds());
    }
    
    public void discard() {
	super.discard();
	_world.removeStateWatcher(worldWatcher);
	_world.removeHistoryWatcher(historyWatcher);
	_world = null;
	playerReset = null;
	authoringSplat = null;
	authoringPaint = null;
	authoringRule = null;
	playBackward = null;
	playStepBack = null;
	playStop = null;
	playStepForward = null;
	playForward = null;
	speedSlow = null;
	speedMedium = null;
	speedFast = null;
	speedFull = null;
	clockView = null;
    }
    
    void populate() {
	PackLayout mgr = (PackLayout) this.layoutManager();
	PackConstraints constraints = mgr.defaultConstraints();
	if (PlaywriteRoot.isAuthoring()) {
	    authoringSplat = Tool.newCharacterTool.makeButton();
	    authoringPaint = Tool.editAppearanceTool.makeButton();
	    authoringRule = Tool.newRuleTool.makeButton();
	    this.addSubview(authoringSplat);
	    this.addSubview(authoringPaint);
	    this.addSubview(authoringRule);
	    BitmapManager
		.checkOutBitmap(Tool.newCharacterTool.getCursorBitmap());
	    BitmapManager.checkOutBitmap((Bitmap) authoringSplat.altImage());
	    BitmapManager
		.checkOutBitmap(Tool.editAppearanceTool.getCursorBitmap());
	    BitmapManager.checkOutBitmap((Bitmap) authoringPaint.altImage());
	    BitmapManager.checkOutBitmap(Tool.newRuleTool.getCursorBitmap());
	    BitmapManager.checkOutBitmap((Bitmap) authoringRule.altImage());
	} else if (PlaywriteRoot.isPlayer()) {
	    playerReset = PlaywriteButton.createFromResource("CP reset",
							     "CP reset", this);
	    this.addSubview(playerReset);
	}
	PlaywriteView separator = Tool.makeSeparator();
	separator.sizeTo(separator.bounds.width, 44);
	this.addSubview(separator);
	playBackward = PlaywriteButton.createFromResource("CP rewind",
							  "CP rewind", this);
	playBackward.setType(2);
	playStepBack
	    = PlaywriteButton.createFromResource("CP step back",
						 "CP step back", this);
	playStepBack.setType(2);
	playStop
	    = PlaywriteButton.createFromResource("CP stop", "CP stop", this);
	playStop.setType(2);
	BitmapManager.checkOutBitmap((Bitmap) playStop.altImage());
	playStepForward
	    = PlaywriteButton.createFromResource("CP step forward",
						 "CP step forward", this);
	playStepForward.setType(2);
	playForward
	    = PlaywriteButton.createFromResource("CP Play", "CP Play", this);
	playForward.setType(2);
	PlaywriteView play
	    = new PlaywriteView(0, 0, playStop.width() * 5, playStop.height());
	play.setBackgroundColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	play.setTransparent(false);
	play.setLayoutManager(new GridLayout(1, 0, 0, 0, 0));
	play.addSubview(playBackward);
	play.addSubview(playStepBack);
	play.addSubview(playStop);
	play.addSubview(playStepForward);
	play.addSubview(playForward);
	play.layoutView(0, 0);
	this.addSubview(play);
	clockView = new ClockView(0, 0, _world);
	this.addSubview(clockView);
	Bitmap speedButtonUp = Resource.getImage("CP speedup");
	Bitmap speedButtonDown = Resource.getImage("CP speeddown");
	speedSlow = makeSpeedButton(speedButtonUp, speedButtonDown, 1,
				    "Turtle", "CP SLOW");
	speedSlow.moveTo(6, 17);
	speedMedium = makeSpeedButton(speedButtonUp, speedButtonDown, 1,
				      "SpeedM", "CP MEDIUM");
	speedMedium.moveTo(20, 17);
	speedFast = makeSpeedButton(speedButtonUp, speedButtonDown, 1,
				    "SpeedF", "CP FAST");
	speedFast.moveTo(34, 17);
	speedFull = makeSpeedButton(speedButtonUp, speedButtonDown, 1, "Bunny",
				    "CP MAX");
	speedFull.moveTo(48, 17);
	PlaywriteView speed = new PlaywriteView(Resource.getImage("CP frame"));
	speed.setBackgroundColor(PlaywriteWindow.DEFAULT_BACKGROUND_COLOR);
	speed.addSubview(speedSlow);
	speed.addSubview(speedMedium);
	speed.addSubview(speedFast);
	speed.addSubview(speedFull);
	speed.layoutView(0, 0);
	this.addSubview(speed);
	resetSpeedPanel();
	constraints.setAnchor(8);
	constraints.setPadY(0);
	separator = Tool.makeSeparator();
	separator.sizeTo(separator.bounds.width, 44);
	this.addSubview(separator);
	copyButton = Tool.copyLoadTool.makeButton();
	this.addSubview(copyButton);
	BitmapManager.checkOutBitmap(Tool.copyLoadTool.getCursorBitmap());
	BitmapManager.checkOutBitmap((Bitmap) copyButton.altImage());
	deleteButton = Tool.deleteTool.makeButton();
	this.addSubview(deleteButton);
	BitmapManager.checkOutBitmap(Tool.deleteTool.getCursorBitmap());
	BitmapManager.checkOutBitmap((Bitmap) deleteButton.altImage());
	Vector subviews = this.subviews();
	constraints = mgr.constraintsFor((View) subviews.elementAt(0));
	constraints.setExpand(true);
	constraints.setAnchor(2);
	constraints
	    = mgr.constraintsFor((View)
				 subviews.elementAt(subviews.size() - 1));
	constraints.setExpand(true);
	constraints.setAnchor(6);
	resetAuthoringPanel();
	resetPlayPanel();
    }
    
    Tool.NewCharacterButton getNewCharButton() {
	return (Tool.NewCharacterButton) authoringSplat;
    }
    
    private PlaywriteButton makeSpeedButton
	(Image up, Image down, int type, String command, String toolTipResID) {
	PlaywriteButton button = new PlaywriteButton(up, down);
	button.setTarget(this);
	button.setTransparent(true);
	button.setType(type);
	button.setCommand(command);
	button.setToolTipText(Resource.getToolTip(toolTipResID));
	return button;
    }
    
    private void resetAuthoringPanel() {
	Object state = _world.getState();
	if (PlaywriteRoot.isAuthoring()) {
	    authoringSplat.setEnabled(state == World.STOPPED);
	    authoringPaint.setEnabled(state != World.RUNNING);
	    authoringRule.setEnabled(state == World.STOPPED);
	} else if (PlaywriteRoot.isPlayer())
	    playerReset.setEnabled(state == World.STOPPED);
	copyButton.setEnabled(state != World.RUNNING);
	deleteButton.setEnabled(state != World.RUNNING);
    }
    
    private void throwInWorldThread() {
	if (_world != null && constructed && _world.inWorldThread())
	    throw new PlaywriteInternalError
		      ("You may not call this method from the world thread");
    }
    
    private void resetPlayPanel() {
	throwInWorldThread();
	RuleEditor ruleEditor = _world.getRuleEditor();
	boolean suspended = _world.isSuspendedForDebug();
	boolean running = _world.isRunning();
	boolean forward = _world.timeIsForward();
	boolean backward = _world.timeIsBackward();
	boolean stepping = _world.isStepping();
	boolean hasHistory = _world.historyListIsEmpty() ^ true;
	boolean inrules
	    = ruleEditor == null ? false : RuleEditor.isRecordingOrEditing();
	playBackward.setState(running && backward && !stepping);
	playStepBack.setState(running && backward && stepping);
	playStop.setState(!running && !suspended && !inrules);
	playStepForward.setState(running && forward && stepping && !suspended);
	playForward.setState(running && forward && !stepping && !suspended);
	boolean enabled = !suspended && !inrules;
	playBackward
	    .setEnabled(enabled && !playBackward.state() && hasHistory);
	playStepBack
	    .setEnabled(enabled && !playStepBack.state() && hasHistory);
	playStop.setEnabled(enabled || inrules);
	playStepForward
	    .setEnabled((enabled || suspended) && !playStepForward.state());
	playForward.setEnabled((enabled || suspended) && !playForward.state());
    }
    
    private void resetSpeedPanel() {
	throwInWorldThread();
	speedSlow.setState(_world.getSpeed() == 0);
	speedMedium.setState(_world.getSpeed() == 1);
	speedFast.setState(_world.getSpeed() == 2);
	speedFull.setState(_world.getSpeed() == 3);
    }
    
    public void performCommand(String command, Object info) {
	if (!_worldHasClosed) {
	    Debug.print("debug.control.panel", command);
	    if (command == "CP step back")
		_world.stepBackward();
	    else if (command == "CP rewind")
		_world.runBackward();
	    else if (command == "CP stop") {
		if (_world.getState() == World.RECORDING
		    || _world.getState() == World.EDITING)
		    getWorld().doManualAction(new StopAction());
		else {
		    synchronized (this) {
			if (_world.getState() == World.RUNNING) {
			    if (_world.wantsToRun())
				_world.stopWorld();
			    else
				_world.setForceStopFlag(true);
			}
		    }
		}
	    } else if (command == "CP step forward") {
		CharacterWindow.hideStepRuleButton();
		_world.stepForward();
	    } else if (command == "CP Play") {
		if (Debug.lookup("debug.fatalerroronrun") == true)
		    throw new Error
			      ("*********** This is a user-requested fatal error ***********");
		CharacterWindow.hideStepRuleButton();
		_world.runForward();
	    } else if (command == "Turtle")
		_world.setSpeed(0);
	    else if (command == "SpeedM")
		_world.setSpeed(1);
	    else if (command == "SpeedF")
		_world.setSpeed(2);
	    else if (command == "Bunny")
		_world.setSpeed(3);
	    else if (command == "CP reset") {
		if (_world.getState() == World.RUNNING)
		    _world.stopAndReset();
		else if (_world.getState() == World.RECORDING
			 || _world.getState() == World.EDITING)
		    getWorld().doManualAction(new ResetAction());
		else {
		    _world.loadState();
		    resetSpeedPanel();
		}
	    } else if (command == "RESET_IN_A_NEW_THREAD")
		_world.loadState();
	    else if (command == "smokin") {
		resetAuthoringPanel();
		resetPlayPanel();
	    } else
		Debug.print(true, "Unknown command in Control Panel: ",
			    command);
	}
    }
    
    void disable() {
	speedSlow.setEnabled(false);
	speedMedium.setEnabled(false);
	speedFast.setEnabled(false);
	speedFull.setEnabled(false);
	clockView.disable();
    }
    
    void enable() {
	speedSlow.setEnabled(true);
	speedMedium.setEnabled(true);
	speedFast.setEnabled(true);
	speedFull.setEnabled(true);
	clockView.enable();
    }
}
