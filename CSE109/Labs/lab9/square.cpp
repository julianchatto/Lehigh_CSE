#include <iostream>
using namespace std;

class Square {
  private: 
    double side;
    double area;
  public:
    friend istream & operator >>(istream &, Square &);  // extraction
    friend ostream & operator <<(ostream &, Square &);  // insertion
};

istream &operator >>(istream &din, Square &r) {
  cout << endl;
  cout << "Enter length of side: ";
  din >> r.side;
  return (din);
}

ostream &operator <<(ostream &dout, Square &r) {
  dout << endl;
  dout << "Square side:  " << r.side << endl;
  r.area = r.side * r.side;
  dout << "The area of the Square is: " << r.area << endl << endl;
  return (dout);
};

int main() {
  Square r1;  // create an object r1 of class Square
  cin >> r1;  // use overloaded >> operator to get values into r1
  cout << r1; // use overloaded << operator to print r1 values and area
  return 0;
}
