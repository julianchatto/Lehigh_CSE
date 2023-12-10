.global quadratic
# a in %edi, b in %esi, c in %edx, x1 in %rcx, x2 in %r8
quadratic:
    # calculate discriminant
    imull %edi, %edx    # %edx = c * a
    sall $2, %edx       # %edx = c * a * 4
    movl %esi, %eax     # %eax = b
    imull %esi, %eax    # %eax = b * b
    subl %edx, %eax     # %eax = b * b - 4 * a * c

    # check if discriminant is negative or zero
    testl %eax, %eax
    jl .neg_discriminant
    je .zero_discriminant

    pushq %rbx          # save %rbx on the stack before using it
    movq $0, %rbx
    movl %edi, %ebx     # save the value of a in %ebx
    movl %eax, %edi     # move %eax (discriminant) to %edi before calling r_sqrt
    call r_sqrt         # result = rd in %eax

    # calculate x1 and x2
    sall $1, %ebx       # %ebx = 2 * a
    negl %esi           # %esi = -b

    # x1
    movl %esi, %edi     # save -b in %edi
    movl %eax, %r9d     # save rd in %r9d
    addl %esi, %eax     # %eax = -b + rd
    cltq                # sign extend %eax to %rax
    cqto                # sign extend %rax in %rdx for the division
    movslq %ebx, %rbx   # sign extend 2*a for the division
    idivq %rbx          # %rax = (-b + rd) / (2 * a)
    movl %eax, (%rcx)   # save x1 in *(%rcx)

    # x2
    subl %r9d, %edi     # %r9d == -b - rd
    movl %edi, %eax     # %eax = -b - rd
    cltq                # sign extend %eax to %rax
    cqto                # sign extend %rax in %rdx for the division
    idivq %rbx          # (-b - rd) / (2 * a)
    movl %eax, (%r8)    # save x2 in *(%r8)

    movq $1, %rax       # return 1
    popq %rbx           # restore the value of %rbx from the stack
    ret
.neg_discriminant:
    movq $-1, %rax      # return -1
    ret
.zero_discriminant:
    negl %esi           # %esi = -b
    movl %esi, %eax 
    sall $1, %edi       # %edi = 2 * a
    cltq                # sign extend %eax to %rax
    cqto                # sign extend %rax in %rdx for the division
    idivq %rdi          # %rax = -b / (2 * a)
    movl %eax, (%rcx)   # save x1 in *(%rcx)
    movq $0, %rax       # return 0
    ret 

r_sqrt: # should receive the argument in %edi, not %eax
    # x < 0
    testl %edi, %edi
    jl .less_than_zero 

    # x < 2
    cmp $2, %edi
    jl .retX 
    pushq %rbx          # save %rbx on the stack
    movl %edi, %ebx     # save %edi in %rbx

    sarl $2, %edi       # x >> 2 to modify the value of x 
    call r_sqrt

    sall $1, %eax       # small_cand = r_sqrt(x >> 2) << 1
    movl %eax, %edx     # %edx = small_cand 
    inc %edx            # %edx = large_cand = small_cand + 1
    movl %edx, %r9d     # save large_cand in %r9d (caller saved register)

    imull %r9d, %r9d    # %r9d = large_cand * large_cand
    cmpl %ebx, %r9d     # large_cand * large_cand > x (x stored in %ebx)

    # return small_cand 
    jg .ret_small_cand 

    # return large_cand 
    jmp .ret_large_cand

.less_than_zero:
    movq $-1, %rax    # return -1
    ret
.retX:
    movl %edi, %eax   # move x to %eax
    ret
.ret_large_cand:
    movl %edx, %eax   # %eax = large_cand
.ret_small_cand:
    popq %rbx         # restore the value of %rbx
    ret


# find the solutions of a quadratic equation in C
# int quadratic(int a, int b, int c, int *x1, int *x2){
#    int d = (b * b) - 4 * a * c; //discriminant
#    if(d < 0)
#        return -1;
#    if(d == 0){
#        *x1 = -b / (2 * a);
#        return 0;
#    }
#    int rd = r_sqrt(d);
#    *x1 = (-b - rd) / (2 * a);
#    *x2 = (-b + rd) / (2 * a);
#    return 1;
# }

# calculate recursively the square root of an int (in C) 
# int r_sqrt(int x){
#    if(x < 0)
#        return -1;
#    if (x < 2)
#        return x;
#   /* recursive call */
#    int small_cand = r_sqrt(x >> 2) << 1;
#    int large_cand = small_cand + 1;
#    if (large_cand * large_cand > x)
#        return small_cand;
#    else
#        return large_cand;
# }
