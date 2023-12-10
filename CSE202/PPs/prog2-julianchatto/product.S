.global product
product:
    # a in %rdi, b in %rsi, c in %rdx, size in %ecx
    movq $0, %r8 # i = 0
.check_condition:
    cmpl %ecx, %r8d # i < size
    jb .body_loop
    ret
.body_loop:
    movss (%rdi, %r8, 4), %xmm0 # xmm0 = a[i]
    mulss (%rsi, %r8, 4), %xmm0 # xmm0 = a[i] * b[i]
    movss %xmm0, (%rdx, %r8, 4) # c[i] = a[i] * b[i]
    incl %r8d # i++
    jmp .check_condition

# Product of two vectors in C
# void product(float *a, float *b, float *c, unsigned size){
#    for(unsigned i=0; i<size; i++){
#        c[i] = a[i] * b[i];
#    }
# }
