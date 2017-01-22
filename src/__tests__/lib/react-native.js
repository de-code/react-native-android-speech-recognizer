import sinon from 'sinon';

// mocks for react-native and module (here due to limitations of rewire)
export const NativeModules = {
  RNAndroidSpeechRecognizer: {
    SpeechRecognizer: {},
    setEventPrefix: sinon.spy(),
    enableEvents: sinon.spy(),
    isRecognitionAvailable: sinon.stub(),
    createSpeechRecognizer: sinon.stub(),
  }
};

export const NativeAppEventEmitter = {
  addListener: sinon.stub()
};
