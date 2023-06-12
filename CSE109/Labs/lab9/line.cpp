#include <iostream>  // for cin, cout
#include <iomanip>   // for setprecision
#include <unistd.h>  // for sleep

using namespace std;

// To define DEBUG at the time of compilation, do:  mg++ -DDEBUG line.cpp
// If you don't want to run in "debug mode", do:    mg++ line.cpp
// Search for "DEBUG" to see where in this code action is taken based on 
//   how the code was compiled.

class Line {
  public:
    double getLength();       // getter/accessor: gets the length of the line
    bool setLength(double);   // setter/mutator: sets the length of the line
    Line();                   // constructor
    Line(double);             // constructor that accepts the length of the line
    ~Line();                  // destructor
  private:
    double length;
};  // semi-colon needed

int main() {
  double len;
  bool setrc;

  Line line1;   // create an instance/object
  cout << "Enter a line length: ";    // query the user
  cin >> len;
  setrc = line1.setLength(len);
  if (setrc == false)
    cout << "Length of line1 is less than 0" << endl;
  len = line1.getLength();
  cout << "Length of line1: " << len << endl;

  Line line2(1000.9999);
  len = line2.getLength();
  cout << "Length of line2 : " << setprecision(6) << len << endl;  // need iomanip
  
  sleep(5);
  return 0;
}

// functions 

// constructor
Line::Line(void) {
  cout << "\nObject is created" << endl;
}

// constructor passing initial value
Line::Line(double len) {
  length = len;
  cout << "\nObject of length " << len << " is created" << endl;
}

// destructor
Line::~Line() {
  cout << "Object is being deleted" << endl;
  #ifdef DEBUG
  std::cerr << "Value of length at time of destruction is "<< length << std::endl;
  #endif
}

// accessor
double Line::getLength(void) {
  cout << "Getting the length" << endl;
  return length;
}

// mutator
bool Line::setLength(double len) {
  length = len;
  cout << "Setting the length to " << len << endl;
  if (len < 0) 
    return false;
  return true;
}


