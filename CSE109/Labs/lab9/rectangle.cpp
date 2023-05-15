#include <iostream>
using namespace std;

class Square;  // need to declare the class since it's not defined before it is referred to in Rectangle

class Rectangle {
  int width, height;   // private by default
  public:
    int area()       { return (width * height); }
    int getWidth()   { return width; }
    int getHeight()  { return height; }
    void convert(Square a);
};

class Square {
  friend class Rectangle;
  private:
    int side;
  public: 
    Square(int a) : side(a) {}  // shorthand to assign integer a to member side
};

void Rectangle::convert(Square a) {
  width  = a.side;
  height = a.side;
}

int main() {
  Rectangle rect;
  Square sqr(4);
  rect.convert(sqr);
  cout << "Area of rectangle with width " << rect.getWidth() << " and height " << rect.getHeight() << " is " << rect.area() << endl;
  return 0;
}
