# prime_divisors: compute the prime factorization of a number

def prime_divisors(n):
    # create new list to hold divisors 
    divisors = []

    # if n is <=1 no prime divisors exist so return empty list 
    if n <= 1:
         return []
    
    # while loop to add 2 to prime divisors as long as number is divisible by 2
    # n must be even to go into this loop 
    # perform integer division to decrease value of n and find potential other prime factors 
    while n % 2 == 0:
        divisors.append(2)
        n //= 2

    # if n is odd or no longer divisible by 2 
    # iterate starting at i = 3 since we already handled cases where n is 1 or even 
    # stop iterating i = square root of n since that would be biggest potential prime divisor 
    # skip by 2 since we already handled even divisors above 
    for i in range(3,int((n ** 0.5)+1),2):
            while n % i == 0:
                divisors.append(i)
                n //= i

    # if n is still greater than 2, it must be prime and therefore its prime divisor is itself 
    if n > 2:
        divisors.append(n)

    return divisors

# testing 
def test_prime_divisors():
    assert prime_divisors(60) == [2, 2, 3, 5]
    print("Test passed!")

test_prime_divisors()
