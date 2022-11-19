/* AppletResources - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.io.InputStream;
import java.net.URL;

import COM.stagecast.ifc.netscape.util.Vector;

class AppletResources
{
    Application _applet;
    URL _baseURL;
    String _subdirectory = "";
    static final String CONTENTS = ".contents";
    static final String LANGUAGES = "Languages";
    static final String INTERFACES_DIRECTORY = "interfaces";
    static final String IMAGE_DIRECTORY = "images";
    static final String FONT_DIRECTORY = "fonts";
    static final String SOUND_DIRECTORY = "sounds";
    
    public AppletResources(Application application, URL url) {
	_applet = application;
	_baseURL = url;
    }
    
    public void setBaseURL(URL url) {
	_baseURL = url;
    }
    
    public URL baseURL() {
	return _baseURL;
    }
    
    public void setSubdirectory(String string) {
	_subdirectory = string;
	if (_subdirectory == null)
	    _subdirectory = "";
    }
    
    public String subdirectory() {
	return _subdirectory;
    }
    
    public Vector availableLanguages() {
	return null;
    }
    
    private URL _urlFromBaseAndPath(URL url, String string) {
	URL url_0_;
	try {
	    url_0_ = new URL(url, string);
	} catch (Exception exception) {
	    System.err.println("appletResources._urlFromBaseAndPath() - "
			       + exception);
	    url_0_ = null;
	}
	return url_0_;
    }
    
    public Vector URLsForResource(String string) {
	Vector vector = new Vector();
	String string_1_ = _subdirectory;
	if (string_1_.length() > 0
	    && string_1_.charAt(string_1_.length() - 1) != '/')
	    string_1_ += "/";
	Vector vector_2_ = _applet.languagePreferences();
	Vector vector_3_ = availableLanguages();
	if (vector_3_ == null) {
	    int i = vector_2_.count();
	    for (int i_4_ = 0; i_4_ < i; i_4_++)
		vector.addElement(_urlFromBaseAndPath(_baseURL,
						      (((String)
							vector_2_
							    .elementAt(i_4_))
						       + ".pkg/" + string_1_
						       + string)));
	    vector.addElement(_urlFromBaseAndPath(_baseURL,
						  string_1_ + string));
	    return vector;
	}
	if (vector_3_.isEmpty()) {
	    vector.addElement(_urlFromBaseAndPath(_baseURL,
						  string_1_ + string));
	    return vector;
	}
	if (vector_2_.isEmpty()) {
	    int i = vector_3_.count();
	    for (int i_5_ = 0; i_5_ < i; i_5_++)
		vector.addElement(_urlFromBaseAndPath(_baseURL,
						      (((String)
							vector_3_
							    .elementAt(i_5_))
						       + ".pkg/" + string_1_
						       + string)));
	    vector.addElement(_urlFromBaseAndPath(_baseURL,
						  string_1_ + string));
	    return vector;
	}
	int i = vector_2_.count();
	for (int i_6_ = 0; i_6_ < i; i_6_++) {
	    if (vector_3_.contains(vector_2_.elementAt(i_6_)))
		vector.addElement(_urlFromBaseAndPath(_baseURL,
						      (((String)
							vector_2_
							    .elementAt(i_6_))
						       + ".pkg/" + string_1_
						       + string)));
	}
	i = vector_3_.count();
	for (int i_7_ = 0; i_7_ < i; i_7_++) {
	    if (!vector_2_.contains(vector_3_.elementAt(i_7_)))
		vector.addElement(_urlFromBaseAndPath(_baseURL,
						      (((String)
							vector_3_
							    .elementAt(i_7_))
						       + ".pkg/" + string_1_
						       + string)));
	}
	vector.addElement(_urlFromBaseAndPath(_baseURL, string_1_ + string));
	return vector;
    }
    
    public Vector URLsForInterface(String string) {
	return URLsForResource("interfaces/" + string);
    }
    
    public Vector URLsForImage(String string) {
	return URLsForResource("images/" + string);
    }
    
    public Vector URLsForFont(String string) {
	return URLsForResource("fonts/" + string);
    }
    
    public Vector URLsForSound(String string) {
	return URLsForResource("sounds/" + string);
    }
    
    public Vector URLsForResourceOfType(String string, String string_8_) {
	return URLsForResource(string_8_ + "/" + string);
    }
    
    public InputStream streamForURLs(Vector vector) {
	if (vector == null || vector.isEmpty())
	    return null;
	int i = vector.count();
	int i_9_ = 0;
	while (i_9_ < i) {
	    try {
		URL url = (URL) vector.elementAt(i_9_);
		InputStream inputstream = url.openStream();
		return inputstream;
	    } catch (Exception exception) {
		i_9_++;
	    }
	}
	return null;
    }
    
    public InputStream streamForInterface(String string) {
	return streamForURLs(URLsForInterface(string));
    }
    
    URL urlForBitmapNamed(String string) {
	Vector vector = URLsForImage(string);
	if (vector.count() > 0)
	    return (URL) vector.elementAt(0);
	return null;
    }
    
    URL urlForSoundNamed(String string) {
	Vector vector = URLsForSound(string);
	if (vector.count() > 0)
	    return (URL) vector.elementAt(0);
	return null;
    }
    
    URL urlForFontNamed(String string) {
	Vector vector = URLsForFont(string);
	if (vector.count() > 0)
	    return (URL) vector.elementAt(0);
	return null;
    }
    
    public InputStream streamForResourceOfType(String string,
					       String string_10_) {
	return streamForURLs(URLsForResourceOfType(string, string_10_));
    }
}
