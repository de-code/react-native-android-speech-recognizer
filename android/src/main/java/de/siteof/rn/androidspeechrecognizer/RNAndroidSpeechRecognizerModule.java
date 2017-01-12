package de.siteof.rn.androidspeechrecognizer;

import android.content.Intent;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RNAndroidSpeechRecognizerModule extends ReactContextBaseJavaModule {

  private static final String TAG = RNAndroidSpeechRecognizerModule.class.getSimpleName();

  private final ReactApplicationContext reactContext;
  private SpeechRecognizer speechRecognizer;
  private String eventPrefix = "";
  private final Map<String, Callback> listenerMap = new HashMap<>();
  private final Map<String, Boolean> enabledEventsMap = new ConcurrentHashMap<>();
  private final Set<String> enabledEvents = Collections.newSetFromMap(
    enabledEventsMap
  );
  private final Handler mainHandler;

  public RNAndroidSpeechRecognizerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.mainHandler = new Handler(reactContext.getMainLooper());
  }

  @Override
  public String getName() {
    return "RNAndroidSpeechRecognizer";
  }

  @Override
  public Map<String, Object> getConstants() {
    return Constants.getConstants();
  }

  // config methods

  @ReactMethod
  public void setEventPrefix(String eventPrefix) {
    Log.d(TAG, "setEventPrefix: " + eventPrefix);
    this.eventPrefix = eventPrefix;
  }

  @ReactMethod
  public void enableEvents(ReadableArray events) {
    Log.d(TAG, "enableEvents: " + events);
    for (String event: ArgumentsConverter.toStringList(events)) {
      this.enableEvent(event);
    }
  }

  @ReactMethod
  public void enableEvent(String event) {
    Log.d(TAG, "enableEvent: " + event);
    this.enabledEventsMap.put(event, Boolean.TRUE);
  }


  // static methods

  @ReactMethod
  public void isRecognitionAvailable(final Promise promise) {
    boolean available = SpeechRecognizer.isRecognitionAvailable(this.reactContext);
    Log.d(TAG, "isRecognitionAvailable: " + available);
    promise.resolve(available);
  }

  @ReactMethod
  public void createSpeechRecognizer(final Promise promise) {
    this.enabledEventsMap.clear();
    Log.d(TAG, "createSpeechRecognizer, posting to main thread");
    this.mainHandler.post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "createSpeechRecognizer (main thread)");
        doCreateSpeechRecognizer(promise);
      }
    });
  }

  private void doCreateSpeechRecognizer(final Promise promise) {
    this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.reactContext);
    this.speechRecognizer.setRecognitionListener(new ListenerMapRecognitionListener(
      this.enabledEvents,
      this.reactContext.getJSModule(RCTNativeAppEventEmitter.class),
      this.eventPrefix
    ));
    promise.resolve(null);
  }

  // instance methods

  @ReactMethod
  public void cancel() {
    Log.d(TAG, "cancel, posting to main thread");
    this.mainHandler.post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "cancel (main thread)");
        speechRecognizer.cancel();
      }
    });
  }

  @ReactMethod
  public void destroy() {
    Log.d(TAG, "destroy, posting to main thread");
    this.mainHandler.post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "destroy (main thread)");
        speechRecognizer.destroy();
        speechRecognizer = null;
      }
    });
  }

  private Intent createIntent(String action, ReadableMap extra) {
    final Intent intent = new Intent(action);
    for (
      ReadableMapKeySetIterator it = extra.keySetIterator();
      it.hasNextKey();
    ) {
      String key = it.nextKey();
      ReadableType type = extra.getType(key);
      switch(type) {
        case Null:
          break;
        case Boolean:
          intent.putExtra(key, extra.getBoolean(key));
          break;
        case Number:
          intent.putExtra(key, extra.getInt(key));
          break;
        case String:
          intent.putExtra(key, extra.getString(key));
          break;
        default:
          throw new IllegalArgumentException("Unsupported type " + type);
      }
    }
    return intent;
  }

  @ReactMethod
  public void startListening(String action, ReadableMap recognizerIntentParameters) {
    final Intent intent = createIntent(action, recognizerIntentParameters);
    Log.d(TAG, "startListening, posting to main thread");
    this.mainHandler.post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "startListening (main thread)");
        speechRecognizer.startListening(intent);
      }
    });
  }

  @ReactMethod
  public void stopListening() {
    Log.d(TAG, "stopListening, posting to main thread");
    this.mainHandler.post(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "stopListening (main thread)");
        speechRecognizer.stopListening();
      }
    });
  }
}
