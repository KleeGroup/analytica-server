void setup() {
  noStroke();
  fill(255);
  rect(0,0,width,height);
}

color graphColor = color(200,0,0);
int finalValue = -1;
int value = -1;
int maxValue = 100;

void draw() {
if(value >= 0) {
  noStroke();
  fill(255, 255);
  rect(0,0,width,height);
  int diameter = min(width,height);
  strokeCap(SQUARE);
  noFill();
  stroke(210);
  strokeWeight(4);
  diameter -= 4;
  //ellipse(width/2,height/2,diameter, diameter);
  noStroke();
  fill(255);
  diameter -= 2;
  ellipse(width/2,height/2,diameter,diameter);
  noFill();
  
  stroke(graphColor);
  strokeWeight(2);
  diameter -= 2;
  ellipse(width/2,height/2, diameter, diameter);
  
  strokeWeight(14);
  diameter -= 20;
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
  //maxValue = newMaxValue;
  loop();
}

void initColor(int r, int g, int b) {
  graphColor = color(r, g,b);
}

