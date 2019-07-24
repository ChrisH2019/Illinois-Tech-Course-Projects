#ifndef __LC3_H__ // REMINDER: this is a trick to make sure this file does not get doubly included
#define __LC3_H__

#include <stdint.h>
#include <inttypes.h>


// CPU Declarations -- a CPU is a structure with fields for the
// different parts of the CPU.
typedef short int word_t;          // type that represents a word of LC-3 memory (a 2-byte or 16-bit signed integer)
typedef unsigned short int address_t;   // type that represents an LC-3 address
typedef unsigned char cc_t;        // condition code: <, =, or > 0
typedef unsigned char opcode_t;    // opcodes 0-15
typedef unsigned char flag_t;      // boolean flag
typedef unsigned char reg_t;       // register number 0-7

#define CC_POS 1 // these are powers of 2, this should tell you that they indicate bit positions! (bit 0)
#define CC_ZER 2 // (bit 1)
#define CC_NEG 4 // (bit 2)

// These are preprocessor macros (typically called "pound defines" by C programmers) 
// They are substituted by their values *before* the compiler actually runs.
// This is done by a program called the preprocessor
#define MEMLEN 65536 // there are 65K memory locations in LC-3
#define NREG   8 // there are 8 GPRs (R0-R7)

// For I/O instructions
#define IO_BUF_LEN 256

#define MULTI_INSTR_LIMIT 100

/* BEGIN STRUCTURE AND TYPE DEFINITIONS */

// You have probably never seen the below structure syntax before. This is,
// however, a pattern that is used quite often in systems programming, enough
// that it's worth learning about early on. The
// idea is to use a C struct to represent a series of ordered bits which may
// have different meanings (for example
// a hardware register, a file header, or an instruction. 
// There are four things worth explaining here:
//
// 1) The union. Note that unions are ways to treat a data value differently
//    depending on the name. In the case below, we can treat an instruction as
//    a series of 16 raw bits (indicated by val) or we can treat them as
//    a struct that contains different fields corresponding to different bits we
//    care about. If we want to operate on the whole instruction, we can do
//    something like:
//    instr_t i;
//    int x = (i.val >> 15) & 1;
//
//    We can also operate on individual parts:
//    instr_t i;
//    i.opcode = 2;
//    i.rest = 0;
//
//    This operates on the instruction in a piecewise manner. We could
//    accomplish the same thing using a 16-bit integer and modify it
//    with shifts and adds, but this is cumbersome and easy to get wrong. 
//
// 2) C integer types: it gets annoying to type out long type names like:
//      unsigned short int and unsigned char over and over again. The C
//      standard library provides us with short hands that take less time to
//      type:
//      uint16_t == unsigned short int
//      uint8_t  == unsigned char
//      uint32_t == unsigned int
//      uint64_t == unsigned long
//      int16_t  == short int
//      and so on.
//
// 3) C bit fields: We can specify to the C compiler that when we lay out
//    a structure, we want each member to take up a certain number of bits. 
//    This is done with the colon (:) below. The number that follows the colon
//    should be the number of bits the member requires. The type that you
//    give that member should be at least large enough to hold that number of
//    bits. The smallest type greater than 12 bits is a short int (16 bits). The
//    smallest type greater than 4 bits is an unsigned char. The types can be
//    bigger, but by convention we use the smallest one possible to avoid
//    confusion.
//
// 4) __attribute__((packed)). This is a directive to the C compiler that it
//    should not put any extra padding in our struct when it lays it out in 
//    memory (which might affect our bit fields!). This is mostly paranoid,
//    but good practice when you need your structure to line up exactly with
//    some specification (in this case the instruction formats of the LC-3 ISA).
//    
//
//
//    The below structure definitions are taken straight out of the LC-3 ISA 
//    reference. You'll see similar idioms in other low-level C code that
//    is modeling hardware. Instructions with the same format share a structure
//    definition for brevity. 
//
typedef struct {
    union {
        uint16_t val;
        struct {
            uint16_t rest  : 12;
            uint8_t opcode : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) instr_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            uint8_t sr2    : 3;
            uint8_t rsvd   : 2;
            uint8_t flag   : 1;
            uint8_t sr1    : 3;
            uint8_t dr     : 3;
            uint8_t opcode : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) add_and_reg_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            int8_t  imm5   : 5;
            uint8_t flag   : 1;
            uint8_t sr1    : 3;
            uint8_t dr     : 3;
            uint8_t opcode : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) add_and_imm_t;


typedef struct {
    union {
        uint16_t val;
        struct { 
            int16_t pcoffset9 : 9;
            uint8_t p         : 1;
            uint8_t z         : 1;
            uint8_t n         : 1;
            uint8_t opcode    : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) br_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            uint8_t rsvd1  : 6;
            uint8_t baser  : 3;
            uint8_t rsvd   : 3;
            uint8_t opcode : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) jmp_ret_t;


typedef struct {
    union {
        uint16_t val;
        struct {
            int16_t pcoffset11   : 11;
            uint8_t flag         : 1;
            uint8_t opcode       : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) jsr_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            uint8_t rsvd1  : 6;
            uint8_t baser  : 3;
            uint8_t rsvd   : 2;
            uint8_t flag   : 1;
            uint8_t opcode : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) jsrr_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            int16_t pcoffset9  : 9;
            uint8_t dr         : 3;
            uint8_t opcode     : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) ld_ldi_lea_t;


typedef struct {
    union {
        uint16_t val;
        struct {
            int16_t offset6    : 6;
            uint8_t baser      : 3;
            uint8_t dr         : 3;
            uint8_t opcode     : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) ldr_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            uint16_t rsvd      : 6;
            uint8_t sr         : 3;
            uint8_t dr         : 3;
            uint8_t opcode     : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) not_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            uint16_t rsvd       : 12;
            uint8_t opcode     : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) rti_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            int16_t pcoffset9  : 9;
            uint8_t sr         : 3;
            uint8_t opcode     : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) st_sti_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            int16_t offset6    : 6;
            uint8_t baser      : 3;
            uint8_t sr         : 3;
            uint8_t opcode     : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) str_t;

typedef struct {
    union {
        uint16_t val;
        struct {
            uint8_t trapvect8  : 8;
            uint8_t rsvd       : 4;
            uint8_t opcode     : 4;
        } __attribute__((packed));
    } __attribute__((packed));
} __attribute__((packed)) trap_t;


// Definition of a CPU
typedef struct {
    word_t mem[MEMLEN];  
    word_t reg[NREG];   // Note: "register" is a reserved keyword in C
    address_t pc;       // Program counter
    cc_t cc;            // negative, zero, or positive
    flag_t running;     // running = 1 iff CPU is executing instructions
    word_t ir;          // Instruction register
} cpu_t;


/* END STRUCTURE AND TYPE DEFINITIONS */

/* BEGIN FUNCTION PROTOTYPES */

void init_cpu (cpu_t *cpu);
void init_memory (int argc, char *argv[], cpu_t *cpu);
FILE *get_datafile (int argc, char *argv[]);
void dump_cpu (cpu_t *cpu);
void dump_memory (cpu_t *cpu, address_t from, address_t to);
void dump_gprs (cpu_t *cpu);
void print_instr (instr_t *instr);

/* END FUNCTION PROTOTYPES */


#endif
