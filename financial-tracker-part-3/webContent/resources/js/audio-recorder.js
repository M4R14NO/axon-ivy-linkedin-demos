let mediaRecorder;
let audioChunks = [];

function startRecording() {
    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(stream => {
            mediaRecorder = new MediaRecorder(stream);
            mediaRecorder.start();

            audioChunks = [];

            mediaRecorder.addEventListener("dataavailable", event => {
                audioChunks.push(event.data);
            });

            mediaRecorder.addEventListener("stop", () => {
                const audioBlob = new Blob(audioChunks, { type: 'audio/webm' });
                const reader = new FileReader();
                reader.readAsDataURL(audioBlob);
				reader.onloadend = () => {
				    const base64AudioMessage = reader.result;
				    document.getElementById('form:audioData').value = base64AudioMessage;
				    console.log("Base64 length:", base64AudioMessage.length);

				    // Optional: Vorspielen
				    const audio = document.getElementById("audioPlayback");
				    audio.src = base64AudioMessage;
				};

//                reader.onloadend = () => {
//                    const base64AudioMessage = reader.result;
//					document.getElementById('form:audioData').value = base64AudioMessage;
//
//                    // Optional: Vorspielen
//                    const audio = document.getElementById("audioPlayback");
//                    audio.src = base64AudioMessage;
//                };
            });
        });
}

function stopRecording() {
    if (mediaRecorder) {
        mediaRecorder.stop();
    }
}

function prepareSubmit() {
  const audioData = document.getElementById('form:audioData').value;
  if (!audioData || audioData.trim() === "") {
    alert("Bitte zuerst eine Aufnahme starten und stoppen, bevor du speicherst!");
    return false; // verhindere Submit
  }
  return true;
}

