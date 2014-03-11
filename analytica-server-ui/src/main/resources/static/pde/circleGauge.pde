void setup() {
  noStroke();
  background(0, 0, 0, 0);
}

color graphColor = color(200,0,0);
int finalValue = -1;
int value = -1;
int maxValue = 100;

void draw() {
  if(value >= 0) {
	  noStroke();
	  background(0, 0, 0, 0);
	  int diameter = min(width,height);
	  strokeCap(SQUARE);
	  noFill();
	  
	  stroke(graphColor);
	  strokeWeight(diameter*0.015);
	  diameter -= diameter*0.02;
	  ellipse(width/2,height/2, diameter, diameter);
	  
	  strokeWeight(diameter*0.13);
	  diameter -= diameter*0.17;
	  arc(width/2,height/2, diameter, diameter, -HALF_PI, value* TWO_PI/maxValue -HALF_PI);  
  }
  if(value < finalValue) {
  		value+=2;
  } else {
  		noLoop();
  }
}

void setValue(double newValue, double newMaxValue) {
  finalValue = newValue*maxValue/newMaxValue;
  value = finalValue; //or 0
  loop();
}

void initColor(int r, int g, int b) {
  graphColor = color(r, g,b);
}

