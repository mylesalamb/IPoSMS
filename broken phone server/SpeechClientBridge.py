import queue
import audioop
from threading import Thread
from chirpsdk import ChirpSDK, CallbackSet, ChirpSDKError
import numpy as np
# from google.cloud import speech
# from google.cloud.speech import types
class Callbacks(CallbackSet):
    def on_receiving(self, channel):
        print('Receiving data [ch{ch}]'.format(ch=channel))
    def on_received(self, payload, channel):
        if payload is not None:
            identifier = payload.decode('utf-8')
            print('Received: ' + identifier)
        else:
            print('Decode failed')

class SpeechClientBridge:
    def __init__(self, on_response):
        self._on_response = on_response
        self._queue = queue.Queue()
        self._ended = False

        self.chirp = ChirpSDK(debug=True)
        print(str(self.chirp))
        print('Protocol: {protocol} [v{version}]'.format( protocol=self.chirp.protocol_name, version=self.chirp.protocol_version))
        # self.chirp.audio = None

        self.chirp.set_callbacks(Callbacks())
        self.chirp.start(send=False, receive=True)


        # client = speech.SpeechClient()
        # responses = client.streaming_recognize(
            # streaming_config,
            # self.get_requests()
        # )
        self.process_responses([])

    def terminate(self):
        self.chirp.stop()
        self._ended = True

    def add_request(self, buffer):
        self._queue.put(self.process_audio(buffer))

    def process_audio(self, buffer):
        audio = audioop.ulaw2lin(buffer, 2)
        freq_array = []
        for i in range(0, len(audio)):
            # freq_array.append(float(audio[i]) / float(255))
            freq_array.append(audio[i] / 255)

        freq_array = np.array(freq_array)
        try:
            self.chirp.process_input(freq_array.tobytes())
        except ChirpSDKError as e:
            print("oh no"  + e)

    def get_requests(self):
        while not self._ended:
            yield self._queue.get()

    def process_responses(self, responses):
        thread = Thread(target=self.process_responses_loop, args=[responses])
        thread.start()

    def process_responses_loop(self, responses):
        for response in responses:
            self._on_response(response)

            if self._ended:
              break;
