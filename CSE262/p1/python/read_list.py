# read_list: Read from the keyboard and put the results into a list.  The code
# should keep reading until EOF (control-d) is input by the user.
#
# The order of elements in the list returned by read_list should the reverse of
# the order in which they were entered.

def read_list():
    # create list for input results 
    result_list = []

    while True:
        try:
            # read input from the user, new lines of input considered new elements in the list 
            line = input()
            # append the line to the front of list so the list is returned in reverse order
            result_list.insert(0, line)
        except EOFError:
        # reached the end of input, return the result
            return result_list

# main method to test 
def main():
    print("Enter values (press Ctrl+D on a new line to stop):")
    result = read_list()
    print("List:", result)

if __name__ == "__main__":
    main()