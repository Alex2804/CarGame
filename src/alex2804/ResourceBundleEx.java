package alex2804;


import alex2804.panels.LanguageListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;


/**
* This class manages the languages. with keys there are stored words in different languages (english and german yet).
* You can get words by their keys in choosen language.
**/
public class ResourceBundleEx{
    private static ArrayList<LanguageListener> listeners = new ArrayList<LanguageListener>(); //game listeners
    public static void addListener(LanguageListener listener){
        listeners.add(listener);
    } //add game listeners

    static ResourceBundle resourceBundle = ResourceBundle.getBundle("words", Locale.ENGLISH);
    public static void setLanguage(String languageTag){
        resourceBundle = ResourceBundle.getBundle("words", Locale.forLanguageTag(languageTag));
        for(LanguageListener listener : listeners){
            listener.languageChanged();
        }
    }
    public static String getWord(String key){
        return resourceBundle.getString(key);
    }
    public static String getLanguage(){
        return resourceBundle.getBaseBundleName();
    }
}
