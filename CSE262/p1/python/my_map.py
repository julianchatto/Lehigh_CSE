# my_map: apply a function to every element in a list, and return a list
# that holds the results.
#
# Your implementation of this function is not allowed to use the built-in
# `map` function.

def my_map(func, l):
    # empty list to hold results 
    results = []

    # iterate through each item in the input list 
    for item in l:
        # apply function to item, append result to results list 
        results.append(func(item))
    
    #return list with all applied functions
    return results  

# testing
def test_my_map():
    # squaring numbers
    assert my_map(lambda x: x**2, [1, 2, 3, 4]) == [1, 4, 9, 16]
    
    # converting to strings
    assert my_map(str, [1, 2, 3]) == ['1', '2', '3']
    
    # doubling numbers
    assert my_map(lambda x: x * 2, [0, -1, 5]) == [0, -2, 10]
    
    print("All tests passed!")

test_my_map()