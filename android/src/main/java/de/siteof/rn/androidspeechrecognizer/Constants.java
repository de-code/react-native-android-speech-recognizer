package de.siteof.rn.androidspeechrecognizer;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Constants {
  private static String TAG = Constants.class.getSimpleName();

  public static Map<String, Object>  getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    constants.put(
      "SpeechRecognizer",
      extractConstants(android.speech.SpeechRecognizer.class)
    );
    constants.put("RecognizerIntent",
      extractConstants(android.speech.RecognizerIntent.class)
    );
    constants.put("RecognitionListener",
      getListenerEventsMap()
    );

    return constants;
  }

  private static Map<String, Object> getListenerEventsMap() {
    Map<String, Object> m = new HashMap<>();
    for (ListenerEvents listenerEvent : ListenerEvents.values()) {
      m.put(listenerEvent.name(), listenerEvent.value());
    }
    return m;
  }

  private static Map<String, Object> extractConstants(
    Class<?> c
  ) {
    Map<String, Object> m = new HashMap<>();
    for (java.lang.reflect.Field field: c.getDeclaredFields()) {
      String name = field.getName();
      Class<?> type = field.getType();
      try {
        if (
          name.toUpperCase().equals(name) &&
          (
            type.isPrimitive() ||
            Number.class.isAssignableFrom(type) ||
            type.equals(String.class)
          )
        ) {
          field.setAccessible(true);
          Object value = field.get(c);
          m.put(name, value);
        }
      } catch(Exception e) {
        Log.w(TAG, "Failed to extract constant " + name + " from " + c);
      }
    }
    return m;
  }
}
