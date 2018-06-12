#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

/* Set these to your desired credentials. */
const char* ssid = "JosephControllerWiFi";
const char* password = "12347890";

String strDirection = "";
ESP8266WebServer server(80);
#define MAX 50;
int step = MAX;

void controllMotor(int direction = 0) {
  switch (direction) {
    case 1:
      digitalWrite(0, LOW);
      digitalWrite(1, HIGH);
      digitalWrite(2, LOW);
      digitalWrite(3, HIGH);
      return;
    case 2:
      digitalWrite(0, LOW);
      digitalWrite(1, HIGH);
      digitalWrite(2, HIGH);
      digitalWrite(3, LOW);
      return;
    case 3:
      digitalWrite(0, HIGH);
      digitalWrite(1, LOW );
      digitalWrite(2, HIGH);
      digitalWrite(3, LOW);
      return;
    case 4:
      digitalWrite(0, HIGH);
      digitalWrite(1, LOW );
      digitalWrite(2, LOW);
      digitalWrite(3, HIGH);
      return;
    case 0:
    default:
      digitalWrite(0, LOW);
      digitalWrite(1, LOW);
      digitalWrite(2, LOW);
      digitalWrite(3, LOW);
      return;
  }
}
/* Just a little test message.  Go to http://192.168.4.1 in a web browser
   connected to this access point to see it.
*/
void handleRoot() {
  server.send(200, "text/html", "<h1>Controller Server is online</h1>");
}
void handleControll() {
  if (server.arg("direction") != "") {
    int direction = 0;
    switch (server.arg("direction").toInt()) {
      case 1:
        direction = 1;
        strDirection = "Front";
        break;
      case 2:
        direction = 2;
        strDirection = "Right";
        break;
      case 3:
        direction = 3;
        strDirection = "Back";
        break;
      case 4:
        direction = 4;
        strDirection = "Left";
        break;
      case 0:
      default:
        direction = 0;
        strDirection = "Stop";
        break;
    }
    controllMotor(direction);
    step = MAX;
    server.send(200, "application/json", "{result:'success',direction:'" + strDirection + "'}");
  } else {
    server.send(200, "application/json", "{result:'fail',reason:'Incorrect Argument'}");
  }
}

void setup() {
  delay(1000);
  Serial.begin(115200);
  Serial.println();
  Serial.print("Configuring access point...");
  /* You can remove the password parameter if you want the AP to be open. */
  WiFi.softAP(ssid, password);

  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(myIP);
  server.on("/", handleRoot);
  server.on("/controll", handleControll);
  server.begin();
  Serial.println("HTTP server started");

  pinMode(0, OUTPUT);
  pinMode(1, OUTPUT);
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  delay(1000);
  controllMotor(0);
}

void loop() {
  server.handleClient();
  if(step--==0){
    controllMotor(0);
    step=MAX;
  }
  delay(10);
}
