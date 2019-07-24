CC:=gcc
CFLAGS:=-Wall -Wno-unused-function -Wno-unused-variable -Wno-unused-but-set-variable -Wno-packed-bitfield-compat -Wno-format
LIBS=-lm

all: lc3

lc3: lc3.c lc3.h
	$(CC) $(CFLAGS) $(LIBS) -o $@ $<

test: lc3
	@./test_harness lc3 tests lc3-soln

handin:
	@echo "Turning in the following files..."
	@tar cvzf `whoami`-final.tgz lc3.c lc3.h Makefile
	@cp `whoami`-final.tgz /home/khale/HANDIN/final
	@echo "You can run 'ls /home/khale/HANDIN/final/<yourid>-final.tgz' to make sure your file made it to the HANDIN dir"

clean:
	rm -f lc3

.PHONY: clean test handin
