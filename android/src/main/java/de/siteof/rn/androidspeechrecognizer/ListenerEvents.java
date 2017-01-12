package de.siteof.rn.androidspeechrecognizer;

public enum ListenerEvents {
  ON_BEGINNING_OF_SPEECH("onBeginningOfSpeech"),
  ON_BUFFER_RECEIVED("onBufferReceived"),
  ON_END_OF_SPEECH("onEndOfSpeech"),
  ON_ERROR("onError"),
  ON_EVENT("onEvent"),
  ON_PARTIAL_RESULTS("onPartialResults"),
  ON_READY_FOR_SPEECH("onReadyForSpeech"),
  ON_RESULTS("onResults"),
  ON_RMS_CHANGED("onRmsChanged");

  private String value;

  ListenerEvents(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }
}
