/* @pjs font="../../resources/elements/thermometer/STENCIL.TTF"; */


int arcColor;
float temperatureMin;
float temperatureMax;
float temperature;
float temp;
float unitLabel;

void setup() {
  background(255);
  PFont fontA = loadFont("Verdana.TTF");
  textAlign(LEFT);
  textFont(fontA, 12);
  
  arcColor = 0;
  smooth();
}


void setData(float data, String _unitLabel) {
	temperatureMin = data.min;
    temperatureMax = data.max;
    temperature = data.value;
	temp = temperature;
	unitLabel = _unitLabel;
}

void draw() {
  background(255, 0);
  noStroke();

  /* Fond noir du thermomètre */
  fill(15);
  arc((2*width*54/125+width*17/125)/2, height/5, width*17/125, height*17/125, -PI, 0); // Arc supérieur
  rect(width*54/125, height/5, width*17/125, height*3/5); // Corps
  ellipse(width/2, height*4/5, width*6/25, height*6/25); // Base

/* Si la température est comprise dans l'intervalle défini
   on colore le corps du thermomètre en rouge 
   à partir de la température définie (rapportée à un pourcentage de la taille du thermomètre)
   jusqu'à 0°C */
  {
  	arcColor = 0;
    temp = (height*5/25)+(temperatureMax-temperature)*(height*12/25)/(temperatureMax-temperatureMin);
    if (temperature <= temperatureMin) temp = height*17/25;
    if (temperature >= temperatureMax) {
    	temp = height*24/125;
    	arcColor = 1;
    }
  }
  
  
  /* Fond blanc du thermomètre */
  fill(255);
  /* Arc supérieur 
   rempli en rouge si 100°C */
  if (arcColor == 1) fill(255, 0, 0);
  arc((2*width*54/125+width*17/125)/2, height/5, width*17/250, height*17/250, -PI, 0);
  rect(width*233/500, height/5, width*17/250, height*3/5); // Corps

  /* Dessin de la flèche indiquant le max 
   et texte indiquant la valeur*/
  fill(0);
  beginShape();
  vertex(width*30/50, height*4/25);
  vertex(width*32/50, height*5/25);
  vertex(width*31/50, height*4/25);
  vertex(width*32/50, height*3/25);
  vertex(width*30/50, height*4/25);
  endShape();
  text(temperatureMax + unitLabel, width*33/50, height*4.5/25);

  /* Dessin de la flèche indiquant le min
   et texte indiquant la valeur*/
  beginShape();
  vertex(width*30/50, height*17/25);
  vertex(width*32/50, height*18/25);
  vertex(width*31/50, height*17/25);
  vertex(width*32/50, height*16/25);
  vertex(width*30/50, height*17/25);
  endShape();
  text(temperatureMin + unitLabel, width*33/50, height*17.5/25);

  /* Dessin de la base rouge du thermomètre indiquant le min */
  fill(255, 0, 0);
  ellipse(width/2, height*4/5, width*19/125, height*19/125);  
  rect(width*233/500, height*17/25, width*17/250, height/10);

  /*Effet reflet blanc sur la base du thermomètre */
  fill(255);
  ellipse(width*67/125, height*197/250, width*6/125, height*29/500 );
    
  fill(255, 0, 0);
  rect(width*233/500, temp, width*17/250, height*17/25-temp);     


  /* Si on pointe le curseur sur le thermomètre
   on affiche une flèche avec la valeur de la température alignés à la hauteur du rectangle rouge dans son corps.
   Si la température est supérieure à 100°C ou inférieure à 0°C, on affiche par défaut 0°C */
  if (mouseX >= width*54/125 && 
    mouseX <=  width*54/125+width*17/125 && 
    mouseY >= height/5 && 
    mouseY <= height/5+height*3/5 ||
    dansCercle(width/2, height*4/5, max(width*6/50, height*6/50), mouseX, mouseY) ||
    dansCercle((2*width*54/125+width*17/125)/2, height/5, max(width*17/250, height*17/250), mouseX, mouseY))
  {
    fill(204,0,0);
	rect(width*30/50,temp-11, textWidth(temperature + unitLabel)+20, 22 );
	stroke(204,0,0);
	line(width*30/50-30,temp, width*30/50,temp);
	fill(255);
    text(temperature + unitLabel, width*30/50+10, temp+5);   
  }
}

boolean dansCercle( float cercleX, float cercleY, float rayon, float x, float y) {
  float distance = dist( cercleX, cercleY, x, y);
  boolean result = distance < rayon;
  return result;
}

