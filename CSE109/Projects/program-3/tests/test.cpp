#include "HashSet.hpp"

int test(const char* label, bool a, bool b) {
  printf("%s: ", label);
  if (a==b) {
    printf("Passed\n");
    return 1;
  } else {
    printf("Failed. lhs=%x, rhs= %x\n",a,b);
    exit(1);
  }
}

int main() {

	HashSet hs(10);

  int a = 0x12ab345;
  int b = 0xfeed425;
  int c = 0xabcd38;
  int d = 0x1235698;
  int e = 0x2c374;
  int f = 0xa0f8982;
  int g = 0x053eba;
  int h = 0x61c2f5e;
  int i = 0xe0295f2;
  int j = 0x5e700eaa;
  int k = 0xe75bf1;
  int l = 0x6baa9bb;
  int m = 0x8b1390a;
  int n = 0x59dd1;
  int o = 0xfc32;
  int p = 0xa9d89d8;
  int q = 0x5b2c9e;
  int r = 0x2431e3;
  int s = 0x1910a;
  int t = 0x72;

	hs.insert(a); // insert an item into the table
 	test("Test1", hs.contains(a), true); // find the item
  test("Test2", hs.insert(a), false); // can't insert the same item twice


  hs.insert(b);
  hs.insert(c);
  hs.insert(d);
  hs.insert(e);
  hs.insert(f);
  hs.insert(g);
  hs.insert(h);
  hs.insert(i);
  hs.insert(j);
  hs.insert(k);
  hs.insert(l);
  hs.insert(m);
  hs.insert(n);
  hs.insert(o);
  hs.insert(p);
  hs.insert(q);
  hs.insert(r);
  hs.insert(s);
  hs.insert(t);
  test("Test3", hs.remove(a), true); // test remove
  test("Test4", hs.contains(a), false); // the item should be gone
  test("Test5", hs.remove(a), false); // can't remove again
  test("Test6", hs.insert(a), true); // but we can add it back

  // Additional Test Case 6 - making sure contains functions in all scenarios 
  
  HashSet hq(10);
  hq.insert(t);
  test("test 6", hq.contains(t), true);
  hq.insert(t);
  test("test 6", hq.contains(t), true);
  hq.remove(t);
  test("test 6", hq.contains(t), false);
  hq.insert(t);
  test("test 6", hq.contains(t), true);

  // Additional Test Case 7 - making sure length is correct after one insertion
  HashSet hp(10);
  hp.insert(a);
  test("test 7", hp.len() == 1, true);


  // Additional Test Case 8 - making sure length is correct after insertions
  hp.insert(b); 
  hp.insert(k);
  hp.insert(o);
  test("test 8", hp.len() == 4, true);


  // Additional Test Case 9 - making sure length is correct after removal
  hp.remove(o);
  test("test 9", hp.len() == 3, true);
  

  
  // Additional Test Case 10 - making sure length is correct after removing something that isn't in the list
  hp.remove(0);
  test("test 10", hp.len() == 3, true);


  // Additonal Test case 11 - making sure length is 0 after clearing list
  hp.remove(b);
  hp.remove(k);
  hp.remove(a);
  test("test 11", hp.len() == 0, true);
  return 0;
}
