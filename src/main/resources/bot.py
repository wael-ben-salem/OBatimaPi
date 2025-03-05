import sounddevice as sd
from scipy.io.wavfile import write
import os
import google.generativeai as genai
import mysql.connector
import threading
import pygame
import time
from gtts import gTTS
from io import BytesIO
import tkinter as tk
from tkinter import messagebox

# Configuration
SAMPLE_RATE = 44100
GEMINI_API_KEY = "AIzaSyCHumKm4DMrHQK6rjnjQzLSn2Z8bY-2y0c"
DB_CONFIG = {
    'host': 'localhost',
    'database': 'OurBatimapi',
    'user': 'root',
    'password': ''
}

# Initialize APIs
genai.configure(api_key=GEMINI_API_KEY)

# Initialize Pygame mixer
pygame.mixer.init(frequency=44100, buffer=1024)

# Global variables
is_recording = False
recorded_audio = None

# Enhanced system instruction
def fetch_table_contents():
    """Fetch the content of all tables and format it into a string"""
    conn = None
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor(dictionary=True)

        tables = ["Stock", "Fournisseur", "Article", "etapeprojet"]
        table_contents = {}

        for table in tables:
            cursor.execute(f"SELECT * FROM {table}")
            table_contents[table] = cursor.fetchall()

        formatted_content = ""
        for table, rows in table_contents.items():
            formatted_content += f"\nTABLE {table}:\n"
            for row in rows:
                formatted_content += f"{row}\n"

        return formatted_content.strip()
    except mysql.connector.Error as e:
        print(f"Error fetching table data: {e}")
        return ""
    finally:
        if conn and conn.is_connected():
            cursor.close()
            conn.close()

table_data = fetch_table_contents()
system_instruction = f"""You are an advanced construction management AI assistant , your name is Bati , and the name of the 
desktop app is Our batima. Handle these tables:

TABLE Stock (id, nom, emplacement, dateCreation)
TABLE Fournisseur (id, nom, prenom, email, numero_de_telephone, adresse)
TABLE Article (id, nom, description, prix_unitaire, photo, stock_id, fournisseur_id, etapeprojet_id)
TABLE etapeprojet (Id_etapeProjet, Id_projet, nomEtape, description, dateDebut, dateFin, statut, montant, Id_rapport)

The current content of these tables is as follows:
{table_data}

The user needs to provide the whole information to complete the addition to the database.

Article has 3 foreign keys:
- stock_id (references Stock.id)
- fournisseur_id (references Fournisseur.id)
- etapeprojet_id (references etapeprojet.Id_etapeProjet)

When adding an article:
1. Use existing stock_id and fournisseur_id from their tables
2. for the photo use always the same link: "C:\\xest.png"

Respond STRICTLY in this format:
<VALID SQL QUERY or empty if not needed>;
###
<Spoken response>

Rules:
1. Always check ID existence before operations
2. Use CURRENT_TIMESTAMP for dates
3. For prices use DECIMAL(10,2)
4. Handle Arabic/Latin translations
5. If no SQL needed, leave query part empty
6. Always use fresh table data from database
7. Be professional and chatty in responses
8. For greetings or non-database queries, respond appropriately without SQL 
9. For read/search operations, no SQL is needed unless explicitly requested , like if the user asked you how many articles are in the database
or to show the content of a table or to search for a specific article by name or id or to show the content of a specific stock or fournisseur
you have already the data in the tables so you can respond directly without SQL

10. Example for adding an article:
INSERT INTO Article (nom, description, prix_unitaire, photo, stock_id, fournisseur_id, etapeprojet_id) 
VALUES ('Cement', 'High-quality cement', 25.50, 'C:\\xest.png', 1, 1, 1);
###
Article 'Cement' has been added successfully.

11. in the delete you can delete only the article by it just name unless there is a specific request to delete a specific article by id or there's 2 articles with the same name
same for the other tables
12. in the update you can update only the article by it just name unless there is a specific request to update a specific article by id or there's 2 articles with the same name
same for the other tables

13. for example if the user asked you the articel B in which stock is it you can respond directly without SQL(because you already knowthe if of the stock and you have the data of the table stock)
14. when adding the article and the user didn't mention the stock id and the fournisseur id and the etape de projet id just assign it to random ones that we have in the tables
15. when adding an articles the user can provide you of the fournisseur name or the stock name or the etape de projet name and you can search for the id of the fournisseur or the stock or the etape de projet by the name and assign it to the article(yo ualready have them in the tables)
16. try to extract the maximum information from the user and try to be as helpful as possible(the user voice can be a little bit noisy but try to extract the maximum information from it)

"""

model = genai.GenerativeModel(
    model_name="gemini-2.0-flash",
    generation_config={
        "temperature": 0.5,
        "max_output_tokens": 2048,
    },
    system_instruction=system_instruction
)

def text_to_speech(text):
    """Convert text to speech using gTTS with multilingual support"""
    try:
        # Detect language for TTS
        lang = 'ar' if any('\u0600' <= c <= '\u06FF' for c in text) else 'en'
        
        # Create temporary in-memory file
        tts = gTTS(text=text, lang=lang, slow=False)
        fp = BytesIO()
        tts.write_to_fp(fp)
        fp.seek(0)
        
        # Load and play audio
        pygame.mixer.music.load(fp)
        pygame.mixer.music.play()
        
        # Wait for playback to finish
        while pygame.mixer.music.get_busy():
            time.sleep(0.1)

    except Exception as e:
        print(f"Error generating speech: {str(e)}")

def start_recording():
    """Start audio recording"""
    global is_recording, recorded_audio
    if is_recording:
        return
    is_recording = True
    print("\033[93mRecording started. Click the button to stop.\033[0m")
    recorded_audio = sd.rec(int(10 * SAMPLE_RATE), samplerate=SAMPLE_RATE, channels=1)

def stop_recording():
    """Stop audio recording and save the file"""
    global is_recording, recorded_audio
    if not is_recording:
        return
    is_recording = False
    print("\033[93mRecording stopped.\033[0m")
    sd.stop()
    output_path = "command.wav"
    write(output_path, SAMPLE_RATE, recorded_audio)
    return output_path

def execute_sql(sql):
    """Safe SQL execution with error handling"""
    if not sql:
        return False
    
    conn = None
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        cursor.execute(sql)
        
        if sql.strip().upper().startswith('SELECT'):
            results = cursor.fetchall()
            columns = [desc[0] for desc in cursor.description]
            return {"columns": columns, "data": results}
        else:
            conn.commit()
            return {"affected_rows": cursor.rowcount}
            
    except mysql.connector.Error as err:
        print(f"\033[91mSQL Error: {err}\033[0m")
        if conn:
            conn.rollback()
        return {"error": str(err)}
    finally:
        if conn and conn.is_connected():
            cursor.close()
            conn.close()

def format_results(query_result):
    """Convert SQL results to natural language"""
    if 'error' in query_result:
        return f"Database error: {query_result['error']}"
    
    if 'affected_rows' in query_result:
        return f"Operation completed. Affected {query_result['affected_rows']} records."
    
    if 'data' in query_result:
        summary = []
        for row in query_result['data'][:5]:
            summary.append(", ".join([f"{col}: {val}" for col, val in zip(query_result['columns'], row)]))
        return "Here are the results: " + ". ".join(summary)
    
    return "Operation successful"

def process_command(audio_path):
    """Full command processing pipeline"""
    try:
        # Get fresh table data for each request
        current_table_data = fetch_table_contents()
        
        # Update model with fresh data
        dynamic_model = genai.GenerativeModel(
            model_name="gemini-2.0-flash",
            generation_config={
                "temperature": 0.5,
                "max_output_tokens": 2048,
            },
            system_instruction=system_instruction.replace(
                "CURRENT TABLE DATA PLACEHOLDER",
                current_table_data
            )
        )
        
        audio_file = genai.upload_file(audio_path)
        response = dynamic_model.generate_content(
            [f"Current database state:\n{current_table_data}\nProcess this command:", audio_file]
        )
        
        # Handle response format flexibly
        if '###' in response.text:
            sql_part, message_part = response.text.split('###', 1)
            sql = sql_part.strip()
            message = message_part.strip()
        else:
            sql = ""
            message = response.text.strip()

        # Execute SQL only if valid and needed
        result = None
        if sql and sql != ";":
            valid_commands = ["SELECT", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP", "ALTER", "TRUNCATE"]
            first_word = sql.split()[0].upper() if sql else ""
            
            if first_word in valid_commands:
                result = execute_sql(sql)
                if result and 'error' not in result:
                    formatted_result = format_results(result)
                    full_response = f"{message}\n{formatted_result}"
                else:
                    full_response = f"{message}\nError executing SQL: {result.get('error', 'Unknown error')}"
            else:
                full_response = f"{message}\nNote: SQL command was not executed as it was invalid."
        else:
            full_response = message

        print(f"\033[94mASSISTANT:\033[0m {full_response}")
        threading.Thread(target=text_to_speech, args=(full_response,)).start()
        
    except Exception as e:
        error_msg = f"Error processing command: {str(e)}"
        print(f"\033[91mERROR:\033[0m {error_msg}")
        threading.Thread(target=text_to_speech, args=(error_msg,)).start()

def on_button_click():
    """Handle GUI button click to start/stop recording"""
    global is_recording
    if is_recording:
        audio_path = stop_recording()
        process_command(audio_path)
    else:
        start_recording()

def main():
    """Main interaction loop with GUI"""
    print("\033[1;36mConstruction Voice Assistant Ready\033[0m")
    text_to_speech("Ready for construction site commands")
    
    # Create GUI
    root = tk.Tk()
    root.title("Construction Voice Assistant")
    root.geometry("300x150")
    root.configure(bg="black")  # Set background to black
    
    button = tk.Button(root, text="Start Recording", font=("Arial", 14), bg="yellow", fg="black", command=on_button_click)
    button.pack(expand=True, fill="both", padx=20, pady=20)
    
    def update_button_text():
        if is_recording:
            button.config(text="Stop Recording", bg="lightcoral", fg="black")
        else:
            button.config(text="Start Recording", bg="yellow", fg="black")
        root.after(100, update_button_text)
    
    update_button_text()
    root.mainloop()

if __name__ == "__main__":
    main()