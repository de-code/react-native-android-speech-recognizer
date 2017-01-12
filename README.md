
# react-native-android-speech-recognizer

The purpose of this module is to provide access to
[Android's SpeechRecognizer API](https://developer.android.com/reference/android/speech/SpeechRecognizer.html)
for React Native apps.

This module isn't meant to abstract the API. Higher level modules could be written to do that.

The SpeechRecognizer can be used to integrate voice recognition into your app rather than using the default UI.

## Getting started

`$ npm install react-native-android-speech-recognizer --save`

### Mostly automatic installation

`$ react-native link react-native-android-speech-recognizer`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import de.siteof.rn.androidspeechrecognizer.RNAndroidSpeechRecognizerPackage;` to the imports at the top of the file
  - Add `new RNAndroidSpeechRecognizerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  ```
  include ':react-native-android-speech-recognizer'
  project(':react-native-android-speech-recognizer').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-android-speech-recognizer/android')
  ```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  ```
    compile project(':react-native-android-speech-recognizer')
  ```

## Permissions

To use this library you will need the RECORD_AUDIO permission.

Insert the following in `android/app/src/AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

## Usage

The API follows Android's
[SpeechRecognizer](https://developer.android.com/reference/android/speech/SpeechRecognizer.html)
and
[RecognizerIntent](https://developer.android.com/reference/android/speech/RecognizerIntent.html)

Some changes are necessary due to the way React Native works:

* Methods can only return promises (or nothing) due to being asynchronous
* Types are restricted by the what React Native supports
* Callbacks can't be called multiple times (events can)

```javascript
import {
  SpeechRecognizer,
  RecognizerIntent,
  RecognitionListener
} from 'react-native-android-speech-recognizer';

const recognise = options => new Promise(async (resolve, reject) => {
  const available = await SpeechRecognizer.isRecognitionAvailable();
  if (!available) {
    reject("not available");
  }
  const recognizer = await SpeechRecognizer.createSpeechRecognizer();
  recognizer.setRecognitionListener({
    onError: event => reject("Failed with error code: " + event.error),
    onResults: event => {
      const recognition = event.results[SpeechRecognizer.RESULTS_RECOGNITION];
      const bestRecognition = recognition[0];
      resolve(bestRecognition);
    }
  });
  recognizer.startListening(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, {});
});

recognise().then(bestRecognition => {
  console.log("recognised:", resultTextToEvent(bestRecognition));
}).catch(error => {
  console.log("error:", error);
});
```

You could also request partial results like so:

```javascript
recognizer.setRecognitionListener({
  // ...
  onPartialResults: event => {
    const recognition = event.partialResults[SpeechRecognizer.RESULTS_RECOGNITION];
    const bestRecognition = recognition[0];
    console.log("best recognition so far:", bestRecognition);
  }
});

recognizer.startListening(
  RecognizerIntent.ACTION_RECOGNIZE_SPEECH, {
    [RecognizerIntent.EXTRA_PARTIAL_RESULTS]: true
  }
);
```
