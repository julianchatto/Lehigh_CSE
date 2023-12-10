.global min
# list in %rdi, n in %esi
min:
	movl $0, %ecx # unsigned i = 0;
	movw (%rdi, %rcx, 2), %ax # short min = list[i];
.check_while_condition:
	cmpl %esi, %ecx # i < n
	jb .while_body
	ret
.while_body:
	cmpw %ax, (%rdi, %rcx, 2)  # list[i] < min
	jge .continue
	movw (%rdi, %rcx, 2), %ax # min = list[i];
.continue:
	inc %rcx # i++;
  	jmp .check_while_condition
# short min(short *list, unsigned n){
#     unsigned i = 0;
#     short min = list[i];
#     while (i < n){
#         if(list[i] < min)
#             min = list[i];
#         i++;
#     }
#     return min;
# }

