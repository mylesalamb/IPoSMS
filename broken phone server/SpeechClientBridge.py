import queue
import audioop
from threading import Thread
import numpy as np

class SpeechClientBridge:
    def __init__(self, on_response):
        self._on_response = on_response
        self._queue = queue.Queue()
        self._ended = False

        self.process_responses([])

    def terminate(self):
        self._ended = True

    def add_request(self, buffer):
        self._queue.put(self.process_audio(buffer))

    def process_audio(self, buffer):
        audio = audioop.ulaw2lin(buffer, 2)
        print(audio[0])

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
