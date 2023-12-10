.global average
# a = rax b = rcx c = rdx
average:
    add %eax, %ecx # int sum = a + b;
    cmp %rax, %rcx # if(sum < a)
    jge .no_overflow
.overflow:
    movb $1, (%rdx) # *cptr = 1;
    jmp .div_by_2
.no_overflow:
    movb $0, (%rdx) # *cptr = 0;
.div_by_2:
    shr $1, %rcx # sum/2
    movq %rcx, %rax # rax = sum/2
    ret


# average of two unsigned integers
# unsigned average(unsigned a, unsigned b, char* cptr){
#    int sum = a + b;
#    if(sum < a)
#       *cptr = 1;
#    else
#       *cptr = 0;
#    return sum/2;
# }
