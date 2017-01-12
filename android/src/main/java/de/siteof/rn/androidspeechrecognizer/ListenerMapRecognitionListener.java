package de.siteof.rn.androidspeechrecognizer;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ListenerMapRecognitionListener implements RecognitionListener {

  private static final String TAG = ListenerMapRecognitionListener.class.getSimpleName();

  private final Set<String> enabledEvents;
  private final RCTNativeAppEventEmitter emitter;
  private final String eventPrefix;

  public ListenerMapRecognitionListener(
    Set<String> enabledEvents,
    RCTNativeAppEventEmitter emitter,
    String eventPrefix
  ) {
    this.enabledEvents = enabledEvents;
    this.emitter = emitter;
    this.eventPrefix = eventPrefix;
  }

  private boolean isEnabled(ListenerEvents event) {
    return enabledEvents.contains(event.value());
  }

  private void emit(ListenerEvents event, Object data) {
    this.emitter.emit(this.eventPrefix + event.value(), data);
  }

  private void emit(ListenerEvents event) {
    this.emit(event, null);
  }

  private void emitIfEnabled(ListenerEvents event) {
    if (isEnabled(event)) {
      this.emit(event, null);
    }
  }

  @Override
  public void onBeginningOfSpeech() {
    Log.d(TAG, "onBeginningOfSpeech");
    this.emitIfEnabled(ListenerEvents.ON_BEGINNING_OF_SPEECH);
  }

  @Override
  public void onBufferReceived(byte[] buffer) {
    Log.d(TAG, "onBufferReceived");
    if (isEnabled(ListenerEvents.ON_BUFFER_RECEIVED)) {
      WritableMap data = Arguments.createMap();
      data.putArray("buffer", ArgumentsConverter.fromArray(buffer));
      emit(ListenerEvents.ON_BUFFER_RECEIVED, data);
    }
  }

  @Override
  public void onEndOfSpeech() {
    Log.d(TAG, "onEndOfSpeech");
    this.emitIfEnabled(ListenerEvents.ON_END_OF_SPEECH);
  }

  @Override
  public void onError(int error) {
    Log.i(TAG, "onError: " + error);
    if (isEnabled(ListenerEvents.ON_ERROR)) {
      WritableMap data = Arguments.createMap();
      data.putInt("error", error);
      emit(ListenerEvents.ON_ERROR, data);
    }
  }

  @Override
  public void onEvent(int eventType, Bundle params) {
    Log.d(TAG, "onEvent: " + eventType);
    if (isEnabled(ListenerEvents.ON_EVENT)) {
      WritableMap data = Arguments.createMap();
      data.putInt("eventType", eventType);
      data.putMap("params", ArgumentsConverter.fromBundle(params));
      emit(ListenerEvents.ON_EVENT, data);
    }
  }

  @Override
  public void onPartialResults(Bundle partialResults) {
    Log.d(TAG, "onPartialResults: " + partialResults);
    if (isEnabled(ListenerEvents.ON_PARTIAL_RESULTS)) {
      WritableMap data = Arguments.createMap();
      data.putMap("partialResults", ArgumentsConverter.fromBundle(partialResults));
      emit(ListenerEvents.ON_PARTIAL_RESULTS, data);
    }
  }

  @Override
  public void onReadyForSpeech(Bundle params) {
    Log.d(TAG, "onReadyForSpeech: " + params);
    if (isEnabled(ListenerEvents.ON_READY_FOR_SPEECH)) {
      WritableMap data = Arguments.createMap();
      data.putMap("params", ArgumentsConverter.fromBundle(params));
      emit(ListenerEvents.ON_READY_FOR_SPEECH, data);
    }
  }

  @Override
  public void onResults(Bundle results) {
    Log.d(TAG, "onResults: " + results);
    if (isEnabled(ListenerEvents.ON_RESULTS)) {
      WritableMap data = Arguments.createMap();
      data.putMap("results", ArgumentsConverter.fromBundle(results));
      emit(ListenerEvents.ON_RESULTS, data);
    }
  }

  @Override
  public void onRmsChanged(float rmsdB) {
    Log.d(TAG, "onRmsChanged: " + rmsdB);
    if (isEnabled(ListenerEvents.ON_RMS_CHANGED)) {
      WritableMap data = Arguments.createMap();
      data.putDouble("rmsdB", rmsdB);
      emit(ListenerEvents.ON_RMS_CHANGED, data);
    }
  }
}
