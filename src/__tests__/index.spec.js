import test from 'tape';
import sinon from 'sinon';

import { NativeModules, NativeAppEventEmitter } from 'react-native';

const { RNAndroidSpeechRecognizer } = NativeModules;

import { SpeechRecognizer } from '../index';

const DEFAULT_EVENT_PREFIX = 'RNAndroidSpeechRecognizer_';
const EVENT_NAME = 'onResults';
const OTHER_EVENT_NAME = 'onError';

const createObject = (keys, f) => keys.reduce((o, key) => {
  o[key] = f(key);
  return o;
}, {});

const createSpies = keys => createObject(keys, () => sinon.spy());

const createSubscription = () => createSpies(['remove']);

const sinonAssert = t => createObject(Object.keys(sinon.assert), key => (...args) => {
  try {
    sinon.assert[key].apply(sinon.assert, args);
    t.ok(true);
  } catch (e) {
    sinon.fail(e);
  }
});

const resetAllSpies = () => {
  RNAndroidSpeechRecognizer.enableEvents.reset();
  NativeAppEventEmitter.addListener.reset();
};

test('index', g => {
  g.test('SpeechRecognizer defined', t => {
    t.equal(typeof SpeechRecognizer, "object");
    t.end();
  });

  g.test('SpeechRecognizer.isRecognitionAvailable is reference to RNAndroidSpeechRecognizer.isRecognitionAvailable', t => {
    t.equal(SpeechRecognizer.isRecognitionAvailable, RNAndroidSpeechRecognizer.isRecognitionAvailable);
    t.end();
  });

  g.test('setEventPrefix called with default prefix', t => {
    sinonAssert(t).calledWith(
      RNAndroidSpeechRecognizer.setEventPrefix,
      DEFAULT_EVENT_PREFIX
    );
    t.end();
  });

  const testWithSpeechRecognizer = f => t => {
    resetAllSpies();
    const subscription = createSubscription();
    NativeAppEventEmitter.addListener.returns(subscription);
    RNAndroidSpeechRecognizer.createSpeechRecognizer.returns(Promise.resolve());
    SpeechRecognizer.createSpeechRecognizer().then(speechRecognizer =>
      f(t, {speechRecognizer, subscription})
    ).catch(err => {
      if (err.stack) {
        t.comment(err.stack);
      }
      t.fail(err);
      t.end();
    });
  };

  g.test('createSpeechRecognizer returns object', testWithSpeechRecognizer((t, {speechRecognizer}) => {
    t.equal(typeof speechRecognizer, "object");
    t.end();
  }));

  g.test('setRecognitionListener should call enableEvents with keys',
    testWithSpeechRecognizer((t, {speechRecognizer}) => {

    const listeners = createSpies([EVENT_NAME]);
    speechRecognizer.setRecognitionListener(listeners);
    sinonAssert(t).calledWith(
      RNAndroidSpeechRecognizer.enableEvents,
      [EVENT_NAME]
    );
    t.end();
  }));

  g.test('setRecognitionListener should call addListener for each listener',
    testWithSpeechRecognizer((t, {speechRecognizer}) => {
    
    const listeners = createSpies([EVENT_NAME]);
    speechRecognizer.setRecognitionListener(listeners);
    sinonAssert(t).calledWith(
      NativeAppEventEmitter.addListener,
      DEFAULT_EVENT_PREFIX + EVENT_NAME,
      listeners[EVENT_NAME]
    );
    t.end();
  }));

  g.test('setRecognitionListener called twice should remove old listeners',
    testWithSpeechRecognizer((t, {speechRecognizer}) => {
    
    const subscription = createSubscription();
    const listeners = createSpies([EVENT_NAME]);
    NativeAppEventEmitter.addListener.returns(subscription);
    speechRecognizer.setRecognitionListener(listeners);
    speechRecognizer.setRecognitionListener({});
    sinonAssert(t).calledWith(subscription.remove);
    t.end();
  }));

  g.test('setRecognitionListener called twice should add new listeners',
    testWithSpeechRecognizer((t, {speechRecognizer}) => {
    
    const listeners = createSpies([EVENT_NAME]);
    const newListeners = createSpies([OTHER_EVENT_NAME]);
    NativeAppEventEmitter.addListener.returns(createSubscription());
    speechRecognizer.setRecognitionListener(listeners);
    speechRecognizer.setRecognitionListener(newListeners);
    sinonAssert(t).calledWith(
      NativeAppEventEmitter.addListener,
      DEFAULT_EVENT_PREFIX + OTHER_EVENT_NAME,
      newListeners[OTHER_EVENT_NAME]
    );
    t.end();
  }));

  g.test('setRecognitionListener called twice should call enableEvents with new event names',
    testWithSpeechRecognizer((t, {speechRecognizer}) => {
    
    const listeners = createSpies([EVENT_NAME]);
    const newListeners = createSpies([OTHER_EVENT_NAME]);
    NativeAppEventEmitter.addListener.returns(createSubscription());
    speechRecognizer.setRecognitionListener(listeners);
    speechRecognizer.setRecognitionListener(newListeners);
    sinonAssert(t).calledWith(
      RNAndroidSpeechRecognizer.enableEvents,
      [OTHER_EVENT_NAME]
    );
    t.end();
  }));

  g.end();
});
