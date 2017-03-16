import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class game1014167 extends PApplet {

//1014167 \u6a0b\u53e3\u9244\u6717

float r_p = 0; // \u30e9\u30b1\u30c3\u30c8\u4f4d\u7f6e
float r_d;    // \u30e9\u30b1\u30c3\u30c8\u5909\u4f4d
float r_s = 1.2f;//\u30e9\u30b1\u30c3\u30c8\u901f\u5ea6

public void setup() {//\u521d\u671f\u8a2d\u5b9a
  size(600, 600);// \u30a6\u30a3\u30f3\u30c9\u30a6\u30b5\u30a4\u30ba\u8a2d\u5b9a
  background(255, 255, 255);  //\u521d\u671f\u80cc\u666f\u767d
  r_d = 1.2f; //\u30e9\u30b1\u30c3\u30c8\u521d\u671f\u901f\u5ea6
}
//\u521d\u671f\u5024
float x = 0; //\u30dc\u30fc\u30eb\u4f4d\u7f6e\uff58
float y = 100; //\u30dc\u30fc\u30eb\u4f4d\u7f6e\uff59
float dx = 1; //\u30dc\u30fc\u30eb\u5909\u4f4d\uff58
float dy = 2; //\u30dc\u30fc\u30eb\u5909\u4f4d\uff59
float sx = 1; 
float sy = 2;

//\u521d\u671f\u5024 \u30ab\u30a6\u30f3\u30c8
float count = 0;     //\u885d\u7a81\u56de\u6570
float score; //\u30b9\u30b3\u30a2
float parameter = 0; //\u30dc\u30fc\u30ca\u30b9\u30b9\u30b3\u30a2
int ball_count = 3; //\u30dc\u30fc\u30eb\u30e9\u30a4\u30d5
int command; //\u753b\u9762\u9077\u79fb\u306e\u753b\u9762\u756a\u53f7
int s_c = 1; //\u30dd\u30fc\u30ba\u5224\u5b9a
int d; // \u96e3\u6613\u5ea6\u5024


int hit[] = { 
  5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5
}; // \u30d6\u30ed\u30c3\u30af\u30c0\u30e1\u30fc\u30b8\u5024

float r_w = 50.0f; // \u30e9\u30b1\u30c3\u30c8\u5e45
float b_w = 40.0f; // \u30d6\u30ed\u30c3\u30af\u5e45
float b_h = 40.0f; // \u30d6\u30ed\u30c3\u30af\u9ad8\u3055
float a_r = 15.0f; // \u30dc\u30fc\u30eb\u76f4\u5f84 \u65e7a_w
// \u30dc\u30fc\u30eb\u9ad8\u3055 \u65e7a_h
float a_d = 0;        // \u30dc\u30fc\u30eb\u306e\u52d5\u4f5c\u306e\u5909\u4f4d   

float a_d_score = abs(a_d); // \u30b9\u30d4\u30fc\u30c9\u30dc\u30fc\u30ca\u30b9

//\u30d1\u30c3\u30c9\u3078\u306e\u3042\u305f\u308a\u5224\u5b9a\u95a2\u6570
public boolean checkHit(float x, float y) { 
  //\u30dc\u30fc\u30eb\u304c\u30c7\u30c3\u30c9\u30e9\u30a4\u30f3\u3088\u308a\u5185\u5074\u304b\u5426\u304b
  if (y + a_r <= d * 65) return false; //false=\u30bb\u30fc\u30d5
  //\u30dc\u30fc\u30eb\u304c\u30d1\u30c3\u30c9\u304b\u3089\u5916\u3057\u305f\u304b\u5426\u304b
  if (x + a_r >= r_p && x <= r_p + r_w && y + r_w > d * 50 - 10) {
    return true; //true\uff1d\u30a2\u30a6\u30c8\u30fc
  } else {
    return false; //false=\u30bb\u30fc\u30d5
  }
}

//\u540c\u5fc3\u5186\u30a8\u30d5\u30a7\u30af\u30c8\u95a2\u6570
public void effect(float ex, float ey) {   //effect x\u5ea7\u6a19\u3001effect y\u5ea7\u6a19
  float diameter, i;

  //\u521d\u671f\u6761\u4ef6\u306e\u8a2d\u5b9a
  //  noStroke();
  fill(random(100, 255), random(100, 255), random(100, 255), 10); //\u8272\u306f\u30e9\u30f3\u30c0\u30e0
  diameter = 300; //\u5186\u306e\u76f4\u5f84\u3092\u4ee3\u5165

  //\u540c\u5fc3\u5186\u3092\u63cf\u304f
  for (i=0; i<32; i++) { //i<\u25cb\u306e\u25cb\u90e8\u5206\u3092\u5909\u66f4\u3067\u5186\u306e\u5bc6\u5ea6\u5909\u66f4
    ellipse(ex, ey, diameter, diameter); //\u540c\u5fc3\u5186\u63cf\u5199
    diameter = diameter - 20;
  }
}

//\u30b9\u30b3\u30a2\u63db\u7b97
public void sumscore() {
  //\u30b9\u30b3\u30a2\u5909\u52d5\u8981\u56e0\u90e8\u54c1\u306e\u78ba\u8a8d\u51fa\u529b
  parameter = ball_count * (10 - d) * abs(a_d) * 10 + count; //\u52a0\u7b97\u30b9\u30b3\u30a2\u8a08\u7b97\uff08\u30dc\u30fc\u30eb\u6b8b\u6a5f\uff0a\u30ec\u30d9\u30eb\uff0a\u30b9\u30d4\u30fc\u30c9\u52a0\u901f\u5024\uff0a\uff11\uff10\uff0b\u30d1\u30c3\u30c9\u885d\u7a81\u56de\u6570)
  score = score + parameter; //  \u30b9\u30b3\u30a2\u8a08\u7b97=\u7d2f\u8a08\u30b9\u30b3\u30a2\uff0b\u52a0\u7b97\u30b9\u30b3\u30a2\uff08parameter\uff09
}

//\u30d6\u30ed\u30c3\u30af\u63cf\u5199\u95a2\u6570
public void show_block(int n) {
  rect(b_w * n, 40, b_w, b_h);
}

//\u30d6\u30ed\u30c3\u30af\u3078\u306e\u5f53\u305f\u308a\u5224\u5b9a\u95a2\u6570
public int checkHitBlock(int n, float x, float y) {
  float left   = b_w * n;    //\u5de6\u5224\u5b9a
  float right  = b_w * (n + 1); //\u53f3\u5224\u5b9a
  float top    = 40 + 8;     //\u4e0a\u7aef\u5224\u5b9a
  float bottom = 40 + b_h + 8;   //\u4e0b\u7aef\u5224\u5b9a
  float cx = left + b_w / 2;
  float cy = top + b_h / 2;
  float y1, y2;

  if ((x + a_r <= left) ||
    (x >= right) ||
    (y + a_r <= top) ||
    (y >= bottom)) {
    return 0;
  }

  x = x + a_r / 2; //\u30dc\u30fc\u30eb\u4e2d\u5fc3\u3092\u79fb\u52d5\u306e\u4e2d\u5fc3\u306b\u8a2d\u5b9a
  y = y + a_r / 2; //\u30dc\u30fc\u30eb\u4e2d\u5fc3\u3092\u79fb\u52d5\u306e\u4e2d\u5fc3\u306b\u8a2d\u5b9a

  y1 = y - (-(x - cx)* b_h / b_w + cy);
  y2 = y - ( (x - cx)* b_h / b_w + cy);

  if (y1 > 0) {
    if (y2 > 0) {
      return 1;
    } else if (y2 == 0) {
      return 2;
    } else {
      return 3;
    }
  } else if (y1 < 0) {
    if (y2 > 0) {
      return 7;
    } else if (y2 == 0) {
      return 6;
    } else {
      return 5;
    }
  } else {
    if (y2 > 0) {
      return 8;
    } else if (y2 == 0) {
      return -1;
    } else {
      return 4;
    }
  }
}

// \u96e3\u6613\u5ea6\u5909\u66f4\u95a2\u6570
public void difficulty() {
  if (keyPressed == true) { //\u6570\u5b57\u30ad\u30fc\u5165\u529b\u3092\u53d7\u3051\u4ed8\u3051\u305f\u3068\u304d
    switch(key) {
      case('1'): //\uff11\u30ad\u30fc\u306e\u6642\u3001\u9ad8\u3055\u5b9a\u6570\uff19\u306b\u8a2d\u5b9a
      d = 9;
      break;
      case('2'):
      //\uff12\u30ad\u30fc\u306e\u6642\u3001\u9ad8\u3055\u5b9a\u6570\uff18\u306b\u8a2d\u5b9a
      d = 8;
      break;
      case('3'):
      //\uff13\u30ad\u30fc\u306e\u6642\u3001\u9ad8\u3055\u5b9a\u6570\uff17\u306b\u8a2d\u5b9a
      d = 7;
      break;
      case('4'):
      //\uff14\u30ad\u30fc\u306e\u6642\u3001\u9ad8\u3055\u5b9a\u6570\uff16\u306b\u8a2d\u5b9a
      d = 6;
      break;
      case('5'):
      //\uff15\u30ad\u30fc\u306e\u6642\u3001\u9ad8\u3055\u5b9a\u6570\uff15\u306b\u8a2d\u5b9a
      d = 5;
      break;
      case('6'):
      //\uff16\u30ad\u30fc\u306e\u6642\u3001\u9ad8\u3055\u5b9a\u6570\uff14\u306b\u8a2d\u5b9a
      d = 4;
      break;
      case('7'):
      //\uff17\u30ad\u30fc\u306e\u6642\u3001\u9ad8\u3055\u5b9a\u6570\uff13\u306b\u8a2d\u5b9a
      d = 3;
      break;
    default:
      //\u898f\u5b9a\u4ee5\u5916\u306e\u30ad\u30fc\u306e\u6642\u3001\u9ad8\u3055\u5b9a\u6570\uff10\u306b\u8a2d\u5b9a\u3001\u5f8c\u306b\u5f3e\u304b\u308c\u308b
      d = 0;
      break;
    }
  }
}


// \u30ad\u30fc\u5165\u529b\u95a2\u6570
public void keyPressed() {
  if (keyCode == RIGHT) { //\u2192\u30ad\u30fc\u53d7\u4ed8
    a_d = a_d + r_s * 0.3f; //\u53f3\u65b9\u5411\u52a0\u901f\u5024\u4ee3\u5165
  } else if (keyCode == LEFT) { //\u5de6\u30ad\u30fc\u53d7\u4ed8
    a_d = a_d - r_s * 0.3f; //\u5de6\u65b9\u5411\u52a0\u901f\u5024\u4ee3\u5165
  } else if (keyCode == ENTER) { //Enter\u30ad\u30fc\u53d7\u4ed8
    command = 1; //\u30b2\u30fc\u30e0\u958b\u59cb
  } else if (keyCode == SHIFT) { //\u30b7\u30d5\u30c8\u30ad\u30fc\u53d7\u4ed8
    s_c++; //\u30dd\u30fc\u30ba\u5224\u5b9a\u5024\uff11\uff0b
  } else if (key =='c') { //['o']\uff76\uff81\uff76\uff81\uff76\uff81\uff76\uff81
    command = 3; //\u30af\u30ea\u30a2\u30fc\u753b\u9762\u8868\u793a
  } else if (key =='d') { //['o']\uff76\uff81\uff76\uff81\uff76\uff81\uff76\uff81
    ; //\u30d6\u30ed\u30c3\u30af1\u500b\u3060\u3051
    for (int i = 0; i < hit.length - 1; i++) {
      hit[i] = 0;
    }
    hit[hit.length - 1] = 1;
  } else if (keyCode == ALT) { //ALT\u30ad\u30fc\u5165\u529b
    for (int i = 0; i < hit.length; i++) {
      hit[i] = 5;
    }
    count = 0;
    score = 0;
    parameter = 0;
    ball_count = 3;
    dx = 1; //\u30dc\u30fc\u30eb\u5909\u4f4d\uff58\u521d\u671f\u5316
    dy = 2; //\u30dc\u30fc\u30eb\u5909\u4f4d\uff59\u521d\u671f\u5316
    sx = 1; 
    sy = 2;
    r_p = 0; // \u30e9\u30b1\u30c3\u30c8\u4f4d\u7f6e
    r_s = 1.2f;//\u30e9\u30b1\u30c3\u30c8\u901f\u5ea6
    r_d = 1.2f; //\u30e9\u30b1\u30c3\u30c8\u521d\u671f\u901f\u5ea6
    x = 0; //\u30dc\u30fc\u30eb\u4f4d\u7f6e\uff58
    y = 100; //\u30dc\u30fc\u30eb\u4f4d\u7f6e\uff59
    count = 0;     //\u885d\u7a81\u56de\u6570
    ball_count = 3; //\u30dc\u30fc\u30eb\u30e9\u30a4\u30d5
    s_c = 1; //\u30dd\u30fc\u30ba\u5224\u5b9a
    // \u30dc\u30fc\u30eb\u9ad8\u3055 \u65e7a_h
    a_d = 0;        // \u30dc\u30fc\u30eb\u306e\u52d5\u4f5c\u306e\u5909\u4f4d   

    loop();
    command = 0; //\u30e1\u30cb\u30e5\u30fc\u306b\u623b\u308b
  }
}

public void draw() {
  if (d != 9 && d != 8 && d != 7 && d != 6 && d != 5 && d != 4 && d != 3) {
    command = 0;
  }
  switch(command) {

  case 1: // \u30ad\u30fc\u5165\u529b\u3042\u3063\u305f\u3068\u304d\uff1d\u30b2\u30fc\u30e0\u958b\u59cb
    smooth();
    fill(0, 0, 0, 20);
    rect(0, 0, width, height);
    fill(255); //\u30d6\u30ed\u30c3\u30af\u6d88\u3048\u305f\u5f8c\u306e\u8868\u793a\u8272
    rect(0, 40, width, 40); //\u30d6\u30ed\u30c3\u30af\u6d88\u3048\u305f\u5f8c\u306e\u8868\u793a\u30d6\u30ed\u30c3\u30af
    int ref = 0;

    // \u30dc\u30fc\u30eb\u306e\u52d5\u304d
    x = x + dx + a_d; 
    y = y + dy;

    // \u53cd\u5c04\u5224\u5b9a
    if (x + a_r >= 610) { //\u753b\u9762\u5de6\u7aef
      a_d = 0;
      dx = -1;
      effect(x + a_r, y);
    } else if (x < 0) {   //\u753b\u9762\u53f3\u7aef
      a_d = 0;
      dx = 1;
      effect(x - a_r, y);
    }

    if (y > d * 65) {     //\u30c7\u30c3\u30c9\u30e9\u30a4\u30f3\u8d85\u3048\u305f\u3068\u304d
      x = 0;
      y = 100;
      dx = 1;
      dy = 2;
      count = 0;
      ball_count--;
      if (ball_count == 0) { //\u30b2\u30fc\u30e0\u30aa\u30fc\u30d0\u30fc\u304b\u5426\u304b
        command = 2;
      }
    } else if (y < 0) {
      dy = 2;
      effect(x + a_r, y + a_r);
    }

    //\u30b2\u30fc\u30e0\u30af\u30ea\u30a2\u5224\u5b9a
    int sum = 0; //hit\u306e\u5024\u3068\u304b\u8db3\u3057\u305f\u5024
    for (int i = 0; i < hit.length; i++) {
      sum = sum + hit[i];
    }
    if (sum == 0) {
      command = 3;
    }

    // \u30e9\u30b1\u30c3\u30c8\u52d5\u4f5c\u5236\u5fa1
    r_p = r_d + r_p;
    if (r_p + 20 > 600 - r_w / 2) { // \u30e9\u30b1\u30c3\u30c8\u65b9\u5411\u5207\u308a\u66ff\u3048\u53f3\u5074\u5224\u5b9a
      r_d = - r_s;
    } else if (r_p < 0) {            // \u30e9\u30b1\u30c3\u30c8\u65b9\u5411\u5207\u308a\u66ff\u3048\u5de6\u5074\u5224\u5b9a
      r_d = r_s;
    }


    strokeWeight(5);
    stroke(200, 50, 50);
    line(0, d * 65, width, d * 65);  // \u30c7\u30c3\u30c9\u30e9\u30a4\u30f3
    line(603, 0, 603, height);  // \u30b5\u30a4\u30c9\u30c7\u30c3\u30c9\u30e9\u30a4\u30f3
    noStroke();
    fill(255, 255, 255); //\u30dc\u30fc\u30eb\u30fb\u30e9\u30b1\u30c3\u30c8\u306e\u767d\u5857\u308a\u3064\u3076\u3057
    ellipse(x, y, a_r, a_r);   // \u30dc\u30fc\u30eb
    rect(r_p, d * 65 - 10, r_w, 3); // \u30e9\u30b1\u30c3\u30c8

    for (int i = 0; i<hit.length; i++) {
      switch(hit[i]) {
      case 0:
        fill(255, 255, 255);
        break;
      case 1:
        fill(200, 200, 200);
        break;
      case 2:
        fill(150, 150, 150);
        break;
      case 3:
        fill(100, 100, 100);
        break;
      case 4:
        fill(50, 50, 50);
        break;
      case 5:
        fill(0, 0, 0);
        break;
      default:
        fill(255, 255, 255);
        break;
      }
      if (hit[i] > 0) {
        show_block(i); //\u30d6\u30ed\u30c3\u30af\u63cf\u5199
        ref = checkHitBlock(i, x, y); //\u30d6\u30ed\u30c3\u30af\u885d\u7a81\u56de\u6570
        switch (ref) {
        case 1:
          hit[i]--;
          effect(x + a_r, y + a_r);
        case 2:
        case 8:
          dy = sy;
          break;
        case 5:
          hit[i]--;
          effect(x + a_r, y + a_r);
        case 4:
        case 6:
          dy = -sy;
          break;
        }
        switch (ref) {
        case 2:
        case 3:
        case 4:
          dx = sx;
          effect(x + a_r, y + a_r);
          break;
        case 6:
        case 7:
        case 8:
          dx = -sx;
          effect(x + a_r, y + a_r);
          break;
        }
      }
    }
    if (s_c % 2 == 0) {
      noLoop();
    } else {
      loop();
    }
    fill(255, 255, 255);
    textSize(20);
    text("Life", 0, 30);
    text(score, 10, 280); 
    text("Score", 10, 260);
    fill(255); //\u30e9\u30a4\u30d5\u30b2\u30fc\u30b8\u8272
    rect(0, 0, (width / 3) * ball_count, 10); //\u30e9\u30a4\u30d5\u30b2\u30fc\u30b8

    if (checkHit(x, y)) { 
      dy = -2;
      count = count + 1 ; //\u885d\u7a81\u56de\u6570
      sumscore();
    }
    break;

  case 2:
    noLoop();
    dx = 0; //\u30dc\u30fc\u30eb\u505c\u6b62
    dy = 0;
    background(0); //\u80cc\u666f\u771f\u3063\u9ed2\uff01
    effect(width / 2 + 20, height / 2); //\u306a\u3093\u304b\u304b\u3063\u3053\u3044\u3044\u4f55\u304b
    fill(255); //\u30c6\u30ad\u30b9\u30c8\u767d\u5857\u308a\u3064\u3076\u3057
    textSize(50); //\u30c6\u30ad\u30b9\u30c8\u30b5\u30a4\u30ba50
    text("GAME OVER", width/2, height/2 + 11); //\u3052\u30fc\u3080\u304a\u30fc\u3070\u30fc
    text("Press ALT", width / 2 - 235, height / 2 + 250);
    fill(250, 100, 100);
    text(score, width/2, height/2 + 110); //\u30b9\u30b3\u30a2\u8868\u793a
    text("YourScore", width/2 - 10, height/2 + 70); //SCORE\u6587\u5b57\u8868\u793a
    break;

  case 3:
    noLoop();
    dx = 0; //\u30dc\u30fc\u30eb\u505c\u6b62
    dy = 0;
    background(255); //\u80cc\u666f\u771f\u3063\u767d\uff01
    effect(width / 2 + 20, height / 2); //\u306a\u3093\u304b\u304b\u3063\u3053\u3044\u3044\u4f55\u304b
    fill(0); //\u30c6\u30ad\u30b9\u30c8\u9ed2\u5857\u308a\u3064\u3076\u3057
    textSize(50); //\u30c6\u30ad\u30b9\u30c8\u30b5\u30a4\u30ba50
    text("GAME CLEAR", width/2 - 10, height/2 + 11); //\u3052\u30fc\u3080\u304f\u308a\u3042\u30fc
    text("Press ALT", width / 2 - 235, height / 2 + 250);
    fill(50, 100, 200);
    text(score, width/2, height/2 + 110); //\u30b9\u30b3\u30a2\u8868\u793a
    text("YourScore", width/2 - 10, height/2 + 70); //SCORE\u6587\u5b57\u8868\u793a

    break;




  default: // \u30ad\u30fc\u5165\u529b\u306a\u3057\u306e\u6642\uff1d\u30b9\u30bf\u30fc\u30c8\u753b\u9762
    background(255);
    fill(0);  //\u30c6\u30ad\u30b9\u30c8\u767d\u8272\u5857\u308a\u3064\u3076\u3057
    textSize(60);  //\u30c6\u30ad\u30b9\u30c8\u30b5\u30a4\u30ba60
    text("Breakout feat.Reversi", width / 2 - 300, 200); //\u30bf\u30a4\u30c8\u30eb\u6587\u5b57
    textSize(40); //\u30c6\u30ad\u30b9\u30c8\u30b5\u30a4\u30ba40
    difficulty();  //\u96e3\u6613\u5ea6\u5909\u66f4\u95a2\u6570\u547c\u3073\u51fa\u3057
    textSize(50); //\u30c6\u30ad\u30b9\u30c8\u30b5\u30a4\u30ba50
    fill(255 - d * 20, 0, 0); //\u30c6\u30ad\u30b9\u30c8\u5857\u308a\u3064\u3076\u3057\u3001\u8272\u306f\u96e3\u6613\u5ea6\u304c\u9ad8\u304f\u306a\u308b\u306b\u3064\u308c\u3066\u8d64
    text(key, width / 2 + 150, 300); //\u30ad\u30fc\u5165\u529b\u53d7\u4ed8\u78ba\u8a8d\u30c6\u30ad\u30b9\u30c8
    fill(0); //\u6587\u5b57\u767d\u5857\u308a\u3064\u3076\u3057
    textSize(40); //\u30c6\u30ad\u30b9\u30c8\u30b5\u30a4\u30ba40
    text("difficulty setting 1-7", width / 2 - 270, 300); //\u96e3\u6613\u5ea6\u9078\u629e
    textSize(50);
    fill(0);
    text("Press Enter to start", width / 2 - 235, height / 2 + 250);
    if (keyPressed == true && key != '1' && key != '2' && key != '3' && key != '4' && key != '5' && key != '6' && key != '7') {
      d = 0;
    }
    break;
  }
}



//\u30dd\u30fc\u30ba\u6a5f\u80fd\u5b9f\u88c5\u3067\u304d\u305a(\u00b4\u30fb\u03c9\u30fb\uff40)

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "game1014167" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
