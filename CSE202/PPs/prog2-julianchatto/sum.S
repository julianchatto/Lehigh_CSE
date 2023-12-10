.global sum
sum: 
    # pointer to list in %rdi
    movq $0, %rax # long total = 0;
    movq %rdi, %rcx # struct ll *ptr = list;
    jmp .check_condition
.loop:
    addq 8(%rcx), %rax # total += ptr->value
    movq 16(%rcx), %rcx # ptr = ptr->next
.check_condition:
    testq %rcx, %rcx # while(ptr)
    jne .loop # jmp ptr & ptr != 0 goTo .loop
    ret    

# sum of the elements of a linkedlist in C
# long sum(struct ll *list){
#    long total = 0;
#    struct ll *ptr = list;
#    while(ptr){
#        total += ptr->value;
#        ptr = ptr->next;
#    }
#    return total;
# }
