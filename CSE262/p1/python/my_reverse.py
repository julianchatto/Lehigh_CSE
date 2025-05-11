# my_reverse: reverse a list without using the python `reverse` function

def my_reverse(l):
    # define new list to hold reversed list 
    reversed_list = []

    # iterate through l to reverse it 
    for item in l: 
        # add new item to front of list until entire list is reversed
        reversed_list.insert(0,item)
    
    # return reversed list 
    return reversed_list

# testing
def test_my_reverse():
    assert my_reverse([1, 2, 3, 4]) == [4, 3, 2, 1]
    print("Test passed!")

test_my_reverse()