(START)
// wait for the a key to be pressed
@KBD
D=M
@97
D=D-A
// if D is zero, the A key is pressed so we fall through to the screen bit.
@START
D;JNE

// intialize i, which will be the address I write to
@SCREEN
D=A
@i
M=D

// while i<KBD
(LOOP)
@i
A=M
M=-1
@i
M=M+1
D=M
@KBD
D=D-A
@LOOP
D;JNE

(END)
@END
0;JMP
