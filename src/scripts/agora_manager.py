import sys
import json
import threading
import os
import sys

# Ajouter le chemin du SDK Agora local au PYTHONPATH
sys.path.append(os.path.join(os.path.dirname(__file__), "Agora-Python-SDK"))

from agora_token_builder import RtcTokenBuilder
from agora.rtc import RtcEngine, RtcEngineEventHandler

# Configuration Agora (remplacez par vos credentials)
APP_ID = "51528228261f450c83a6c74cde283b9d"
APP_CERTIFICATE = "0c72f1774760417c81273a9f93838921"

class AgoraEventHandler(RtcEngineEventHandler):
    def __init__(self, on_message_received):
        super().__init__()
        self.on_message_received = on_message_received

    def on_join_channel_success(self, channel, uid, elapsed):
        print(f"JOIN_SUCCESS:{channel}:{uid}")

    def on_user_joined(self, uid, elapsed):
        print(f"USER_JOINED:{uid}")

    def on_user_offline(self, uid, reason):
        print(f"USER_OFFLINE:{uid}")

    def on_message_received(self, message, uid):
        self.on_message_received(message, uid)

class AgoraManager:
    def __init__(self):
        self.rtc_engine = None
        self.event_handler = None

    def start_call(self, channel_name, uid):
        token = RtcTokenBuilder.build_token_with_uid(
            APP_ID, APP_CERTIFICATE, channel_name, uid, 1, 0
        )

        # Initialiser le moteur Agora
        self.rtc_engine = RtcEngine.create(APP_ID)
        self.event_handler = AgoraEventHandler(self.handle_message)
        self.rtc_engine.set_event_handler(self.event_handler)

        # Configuration vidéo de base
        self.rtc_engine.enable_video()
        self.rtc_engine.set_channel_profile(1)  # COMMUNICATION mode

        # Rejoindre le canal
        self.rtc_engine.join_channel(token, channel_name, "", uid)
        print(f"CALL_STARTED:{channel_name}")

    def send_message(self, channel_name, message):
        if self.rtc_engine:
            self.rtc_engine.send_channel_message(channel_name, message)
            print(f"MESSAGE_SENT:{message}")

    def handle_message(self, message, uid):
        print(f"MESSAGE_RECEIVED:{uid}:{message}")

manager = AgoraManager()

def handle_command(command):
    try:
        data = json.loads(command)
        action = data.get("action")

        if action == "start_call":
            manager.start_call(data["channel_name"], data["uid"])
        elif action == "send_message":
            manager.send_message(data["channel_name"], data["message"])
    except Exception as e:
        print(f"ERROR:{str(e)}")

if __name__ == "__main__":
    for line in sys.stdin:
        threading.Thread(target=handle_command, args=(line.strip(),)).start()