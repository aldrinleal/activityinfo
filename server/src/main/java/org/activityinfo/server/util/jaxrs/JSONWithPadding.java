package org.activityinfo.server.util.jaxrs;

/**
* An entity supporting JSON with Padding (JSONP).
* <p>
* If an instance is returned by a resource method and the most acceptable 
* media type is one of application/javascript, application/x-javascript,
* text/ecmascript, application/ecmascript or text/jscript then the object
* that is contained by the instance is serialized as JSON (if supported, using
* the application/json media type) and the result is wrapped around a
* JavaScript callback function, whose name by default is "callback". Otherwise,
* the object is serialized directly according to the most acceptable media type.
* This means that an instance can be used to produce the media types
* application/json, application/xml in addition to application/x-javascript.
*
* @author Jakub.Podlesak@Sun.COM
*/
public class JSONWithPadding {

   public static final String DEFAULT_CALLBACK_NAME = "callback";

   private final String callbackName;

   private final Object jsonSource;

   /**
    * Pad JSON using the default funcation name "callback".
    *
    * @param jsonSource the JSON to pad.
    */
   public JSONWithPadding(Object jsonSource) {
       this(jsonSource, DEFAULT_CALLBACK_NAME);
   }

   /**
    * Pad JSON using a declared callback funcation name.
    *
    * @param jsonSource the JSON to pad.
    * @param callbackName the callback funcation name.
    */
   public JSONWithPadding(Object jsonSource, String callbackName) {
       if (jsonSource == null)
           throw new IllegalArgumentException("JSON source MUST not be null");

       this.jsonSource = jsonSource;
       this.callbackName = (callbackName == null) ? DEFAULT_CALLBACK_NAME : callbackName;
   }

   /**
    * Get the callback function name.
    * 
    * @return the callback function name.
    */
   public String getCallbackName() {
       return callbackName;
   }

   /**
    * Get the JSON source.
    *
    * @return the JSON source.
    */
   public Object getJsonSource() {
       return jsonSource;
   }
}
