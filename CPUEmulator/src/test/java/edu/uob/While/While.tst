load assembly.asm,
output-file assembly.out,
compare-to compare.cmp,
output-list RAM[0]%D2.6.2 RAM[1]%D2.6.2 time%D2.6.2;

set RAM[0] 0,   // Set test arguments
set RAM[1] 0,
set RAM[2] 0;  // Test that program initialized product to 0
set RAM[24576] 23;
while RAM[2] <> 8 {
  ticktock;
}
output;