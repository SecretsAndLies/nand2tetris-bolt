// if kbd -97 != 0 goto end
@KBD
D=M
@97
D=D-A
@END
D;JNE

// otherwise set 25 to 7
@7
D=A
@25
M=D

(END)
@END
0;JMP