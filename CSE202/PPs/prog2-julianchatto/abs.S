.global abs
.abs:
    # x in %edi
    movl %edi, %eax # a < 0 
    testl %eax, %eax # a < 0 
    jge .end # a < 0 
    negl %eax # a = -a
.end:
    ret

# absolute value of a number in C
# int abs(int a){
#    if(a < 0)
#        a = -a;
#    return a;
# }