import processing.serial.*;

import cc.arduino.*;

float r_p = 0; // ラケット位置
float r_d;    // ラケット変位
float r_s = 1.2;//ラケット速度
Arduino arduino;

color off = color(4, 79, 111);
color on = color(84, 145, 158);

void setup() {
  size(600, 600);// ウィンドウサイズ設定
  background(255, 255, 255);  //初期背景白
  r_d = 1.2; //ラケット初期速度
  arduino = new Arduino(this, Arduino.list()[1], 57600);

  for (int i = 0; i <= 13; i++)
    arduino.pinMode(i, Arduino.INPUT);
}

//初期値
float x = 0; //ボール位置ｘ
float y = 100; //ボール位置ｙ
float dx = 1; //ボール変位ｘ
float dy = 2; //ボール変位ｙ
float sx = 1; 
float sy = 2;

//初期値 カウント
float count = 0;     //衝突回数
float score; //スコア
float parameter = 0; //ボーナススコア
int ball_count = 3; //ボールライフ
int command; //画面遷移の画面番号
int s_c = 1; //ポーズ判定
int d; // 難易度値


int hit[] = { 
  5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5
}; // ブロックダメージ値

float r_w = 50.0; // ラケット幅
float b_w = 40.0; // ブロック幅
float b_h = 40.0; // ブロック高さ
float a_r = 15.0; // ボール直径 旧a_w
// ボール高さ 旧a_h
float a_d = 0;        // ボールの動作の変位   

float a_d_score = abs(a_d); // スピードボーナス

//パッドへのあたり判定関数
boolean checkHit(float x, float y) { 
  //ボールがデッドラインより内側か否か
  if (y + a_r <= d * 65) return false; //false=セーフ
  //ボールがパッドから外したか否か
  if (x + a_r >= r_p && x <= r_p + r_w && y + r_w > d * 50 - 10) {
    return true; //true＝アウトー
  } else {
    return false; //false=セーフ
  }
}

//同心円エフェクト関数
void effect(float ex, float ey) {   //effect x座標、effect y座標
  float diameter, i;

  //初期条件の設定
  //  noStroke();
  fill(random(100, 255), random(100, 255), random(100, 255), 10); //色はランダム
  diameter = 300; //円の直径を代入

  //同心円を描く
  for (i=0; i<32; i++) { //i<○の○部分を変更で円の密度変更
    ellipse(ex, ey, diameter, diameter); //同心円描写
    diameter = diameter - 20;
  }
}

//スコア換算
void sumscore() {
  //スコア変動要因部品の確認出力
  parameter = ball_count * (10 - d) * abs(a_d) * 10 + count; //加算スコア計算（ボール残機＊レベル＊スピード加速値＊１０＋パッド衝突回数)
  score = score + parameter; //  スコア計算=累計スコア＋加算スコア（parameter）
}

//ブロック描写関数
void show_block(int n) {
  rect(b_w * n, 40, b_w, b_h);
}

//ブロックへの当たり判定関数
int checkHitBlock(int n, float x, float y) {
  float left   = b_w * n;    //左判定
  float right  = b_w * (n + 1); //右判定
  float top    = 40 + 8;     //上端判定
  float bottom = 40 + b_h + 8;   //下端判定
  float cx = left + b_w / 2;
  float cy = top + b_h / 2;
  float y1, y2;

  if ((x + a_r <= left) ||
    (x >= right) ||
    (y + a_r <= top) ||
    (y >= bottom)) {
    return 0;
  }

  x = x + a_r / 2; //ボール中心を移動の中心に設定
  y = y + a_r / 2; //ボール中心を移動の中心に設定

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

// 難易度変更関数
void difficulty() {
  if (keyPressed == true) { //数字キー入力を受け付けたとき
    switch(key) {
      case('1'): //１キーの時、高さ定数９に設定
      d = 9;
      break;
      case('2'):
      //２キーの時、高さ定数８に設定
      d = 8;
      break;
      case('3'):
      //３キーの時、高さ定数７に設定
      d = 7;
      break;
      case('4'):
      //４キーの時、高さ定数６に設定
      d = 6;
      break;
      case('5'):
      //５キーの時、高さ定数５に設定
      d = 5;
      break;
      case('6'):
      //６キーの時、高さ定数４に設定
      d = 4;
      break;
      case('7'):
      //７キーの時、高さ定数３に設定
      d = 3;
      break;
    default:
      //規定以外のキーの時、高さ定数０に設定、後に弾かれる
      d = 0;
      break;
    }
  }
}


// キー入力関数
void keyPressed() {
  if (keyCode == RIGHT) { //→キー受付
    a_d = a_d + r_s * 0.3; //右方向加速値代入
  } else if (keyCode == LEFT) { //左キー受付
    a_d = a_d - r_s * 0.3; //左方向加速値代入
  } else if (keyCode == ENTER) { //Enterキー受付
    command = 1; //ゲーム開始
  } else if (keyCode == SHIFT) { //シフトキー受付
    s_c++; //ポーズ判定値１＋
  } else if (key =='c') { //['o']ｶﾁｶﾁｶﾁｶﾁ
    command = 3; //クリアー画面表示
  } else if (key =='d') { //['o']ｶﾁｶﾁｶﾁｶﾁ
    ; //ブロック1個だけ
    for (int i = 0; i < hit.length - 1; i++) {
      hit[i] = 0;
    }
    hit[hit.length - 1] = 1;
  } else if (keyCode == ALT) { //ALTキー入力
    for (int i = 0; i < hit.length; i++) {
      hit[i] = 5;
    }
    count = 0;
    score = 0;
    parameter = 0;
    ball_count = 3;
    dx = 1; //ボール変位ｘ初期化
    dy = 2; //ボール変位ｙ初期化
    sx = 1; 
    sy = 2;
    r_p = 0; // ラケット位置
    r_s = 1.2;//ラケット速度
    r_d = 1.2; //ラケット初期速度
    x = 0; //ボール位置ｘ
    y = 100; //ボール位置ｙ
    count = 0;     //衝突回数
    ball_count = 3; //ボールライフ
    s_c = 1; //ポーズ判定
    // ボール高さ 旧a_h
    a_d = 0;        // ボールの動作の変位   

    loop();
    command = 0; //メニューに戻る
  }
}

void draw() {
  background(off);
  stroke(on);

  for (int i = 0; i <= 13; i++) {
    if (arduino.digitalRead(i) == Arduino.HIGH)
      fill(on);
    else
      fill(off);

    rect(420 - i * 30, 30, 20, 20);
  }

  for (int i = 0; i <= 2; i++) {
    int in = (arduino.analogRead(i) - 512) / 4;
    //rect(200, 150 + i * 30, in * 2, 20);
    println(in);
    if (i==0) {
      if (in>0) {
        r_d=1;
      } else {
        r_d=-1;
      }
    } 
    /* if (i==0) {
     if (in>0) {
     a_d -= r_s * 0.3;
     } else {
     a_d += r_s * 0.3;
     }
     }*/
  }
  if (d != 9 && d != 8 && d != 7 && d != 6 && d != 5 && d != 4 && d != 3) {
    command = 0;
  }
  switch(command) {

  case 1: // キー入力あったとき＝ゲーム開始
    smooth();
    fill(0, 0, 0, 20);
    rect(0, 0, width, height);
    fill(255); //ブロック消えた後の表示色
    rect(0, 40, width, 40); //ブロック消えた後の表示ブロック
    int ref = 0;

    // ボールの動き
    x = x + dx + a_d; 
    y = y + dy;

    // 反射判定
    if (x + a_r >= 610) { //画面左端
      a_d = 0;
      dx = -1;
      effect(x + a_r, y);
    } else if (x < 0) {   //画面右端
      a_d = 0;
      dx = 1;
      effect(x - a_r, y);
    }

    if (y > d * 65) {     //デッドライン超えたとき
      x = 0;
      y = 100;
      dx = 1;
      dy = 2;
      count = 0;
      ball_count--;
      if (ball_count == 0) { //ゲームオーバーか否か
        command = 2;
      }
    } else if (y < 0) {
      dy = 2;
      effect(x + a_r, y + a_r);
    }

    //ゲームクリア判定
    int sum = 0; //hitの値とか足した値
    for (int i = 0; i < hit.length; i++) {
      sum = sum + hit[i];
    }
    if (sum == 0) {
      command = 3;
    }

    // ラケット動作制御
    r_p = r_d + r_p;
    if (r_p + 20 > 600 - r_w / 2) { // ラケット方向切り替え右側判定
      r_d = - r_s;
    } else if (r_p < 0) {            // ラケット方向切り替え左側判定
      r_d = r_s;
    }


    strokeWeight(5);
    stroke(200, 50, 50);
    line(0, d * 65, width, d * 65);  // デッドライン
    line(603, 0, 603, height);  // サイドデッドライン
    noStroke();
    fill(255, 255, 255); //ボール・ラケットの白塗りつぶし
    ellipse(x, y, a_r, a_r);   // ボール
    rect(r_p, d * 65 - 10, r_w, 3); // ラケット

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
        show_block(i); //ブロック描写
        ref = checkHitBlock(i, x, y); //ブロック衝突回数
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
    fill(255); //ライフゲージ色
    rect(0, 0, (width / 3) * ball_count, 10); //ライフゲージ

    if (checkHit(x, y)) { 
      dy = -2;
      count = count + 1 ; //衝突回数
      sumscore();
    }
    break;

  case 2:
    noLoop();
    dx = 0; //ボール停止
    dy = 0;
    background(0); //背景真っ黒！
    effect(width / 2 + 20, height / 2); //なんかかっこいい何か
    fill(255); //テキスト白塗りつぶし
    textSize(50); //テキストサイズ50
    text("GAME OVER", width/2, height/2 + 11); //げーむおーばー
    text("Press ALT", width / 2 - 235, height / 2 + 250);
    fill(250, 100, 100);
    text(score, width/2, height/2 + 110); //スコア表示
    text("YourScore", width/2 - 10, height/2 + 70); //SCORE文字表示
    break;

  case 3:
    noLoop();
    dx = 0; //ボール停止
    dy = 0;
    background(255); //背景真っ白！
    effect(width / 2 + 20, height / 2); //なんかかっこいい何か
    fill(0); //テキスト黒塗りつぶし
    textSize(50); //テキストサイズ50
    text("GAME CLEAR", width/2 - 10, height/2 + 11); //げーむくりあー
    text("Press ALT", width / 2 - 235, height / 2 + 250);
    fill(50, 100, 200);
    text(score, width/2, height/2 + 110); //スコア表示
    text("YourScore", width/2 - 10, height/2 + 70); //SCORE文字表示

    break;




  default: // キー入力なしの時＝スタート画面
    background(255);
    fill(0);  //テキスト白色塗りつぶし
    textSize(60);  //テキストサイズ60
    text("Breakout feat.Reversi", width / 2 - 300, 200); //タイトル文字
    textSize(40); //テキストサイズ40
    difficulty();  //難易度変更関数呼び出し
    textSize(50); //テキストサイズ50
    fill(255 - d * 20, 0, 0); //テキスト塗りつぶし、色は難易度が高くなるにつれて赤
    text(key, width / 2 + 150, 300); //キー入力受付確認テキスト
    fill(0); //文字白塗りつぶし
    textSize(40); //テキストサイズ40
    text("difficulty setting 1-7", width / 2 - 270, 300); //難易度選択
    textSize(50);
    fill(0);
    text("Press Enter to start", width / 2 - 235, height / 2 + 250);
    if (keyPressed == true && key != '1' && key != '2' && key != '3' && key != '4' && key != '5' && key != '6' && key != '7') {
      d = 0;
    }
    break;
  }
}
