//NOTE: The load, output-file and compare-to shouldn't be changed. They are provided here for offline simulator use.
load assembly.asm,
output-file assembly.out,
compare-to compare.cmp,
output-list RAM[1000]%D2.6.2 RAM[1001]%D2.6.2 RAM[1002]%D2.6.2 RAM[1003]%D2.6.2 RAM[1004]%D2.6.2;

set RAM[1000] 9,   // Set test arguments
set RAM[1001] 2,
set RAM[1002] 5,
set RAM[1003] 4,
set RAM[1004] 1;
repeat 1000 {
  ticktock;
}
output;
