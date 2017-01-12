
import { NativeModules, NativeAppEventEmitter } from 'react-native';

const { RNAndroidSpeechRecognizer } = NativeModules;

const {
  SpeechRecognizer,
  RecognizerIntent,
  RecognitionListener,
  isRecognitionAvailable,
  cancel,
  destroy,
  startListening,
  stopListening
} = RNAndroidSpeechRecognizer || {};

const eventPrefix = 'RNAndroidSpeechRecognizer_';
RNAndroidSpeechRecognizer.setEventPrefix(eventPrefix);

const setRecognitionListener = listener => {
  const keys = Object.keys(listener);
  RNAndroidSpeechRecognizer.enableEvents(keys);
  keys.forEach(key => {
    NativeAppEventEmitter.addListener(eventPrefix + key, listener[key]);
  });
 }

const createSpeechRecognizer = (...args) => 
  RNAndroidSpeechRecognizer.createSpeechRecognizer(...args).then(() => ({
    cancel,
    destroy,
    setRecognitionListener,
    startListening,
    stopListening
  }));

SpeechRecognizer.isRecognitionAvailable = isRecognitionAvailable;
SpeechRecognizer.createSpeechRecognizer = createSpeechRecognizer;

export {
   SpeechRecognizer,
   RecognizerIntent,
   RecognitionListener,
   isRecognitionAvailable,
   createSpeechRecognizer
 }

export default RNAndroidSpeechRecognizer;
