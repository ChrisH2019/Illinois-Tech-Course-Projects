/* Author: Weicong Hong
 * Dynamic Memory Allocator
 * Segregated free lists
 */
 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <unistd.h>

#include "mm.h"
#include "memlib.h"

/* Constants and Macros */
#define WSIZE           4       /* Word and header/footer size (bytes) */ 
#define DSIZE           8       
#define MIN_BLK_SIZE    16      
#define CHUNKSIZE       (1 << 14)  /* Magic numbers */

#define MAX(x, y) ((x) > (y)? (x) : (y))  

/* single word (4) or double word (8) alignment */
#define ALIGNMENT 8

/* rounds up to the nearest multiple of ALIGNMENT */
#define ALIGN(size) (((size_t)(size) + (ALIGNMENT - 1)) & ~0x7)

/* Pack a size and allocated bit into a word */
#define PACK(size, alloc)  ((size) | (alloc)) 

/* Read and write a word at address p */
#define GET(p)       (*(unsigned int *)(p))            
#define SET(p, val)  (*(unsigned int *)(p) = (val))  

/* Read the size and allocated fields from address p */
#define GET_SIZE(p)  (GET(p) & ~0x7)                   
#define GET_ALLOC(p) (GET(p) & 0x1)                  

/* Given block ptr bp, compute address of its header and footer */
#define HDR(bp)       ((char *)(bp) - WSIZE)                      
#define FTR(bp)       ((char *)(bp) + GET_SIZE(HDR(bp)) - DSIZE)

/* Given block ptr bp, write a word at the header/footer */
#define SET_HDR(bp, val) (SET((HDR(bp)), (val)))
#define SET_FTR(bp, val) (SET((FTR(bp)), (val)))

/* Given block ptr bp, compute address of next and previous blocks */
#define NEXT_BLK(bp)    ((char *)(bp) + GET_SIZE(((char *)(bp) - WSIZE)))
#define PREV_BLK(bp)    ((char *)(bp) - GET_SIZE(((char *)(bp) - DSIZE)))

/* Given block ptr bp, compute the size */
#define PREV_SIZE(bp)   (GET_SIZE((char *)(bp) - DSIZE))
#define SIZE(bp)        (GET_SIZE(HDR(bp)))
#define NEXT_SIZE(bp)   (GET_SIZE((char *)(bp) + SIZE(bp) - WSIZE))

/* Given block ptr bp, compute previous/next block */
#define PREV_ALLOC(bp)  (GET_ALLOC((char *)(bp) - DSIZE))
#define NEXT_ALLOC(bp)  (GET_ALLOC((char *)(bp) + SIZE(bp) - WSIZE))
#define PREV(bp)        ((char *)(bp) - GET(bp))
#define NEXT(bp)        ((char *)(bp) + GET((char *)(bp) + WSIZE))

/* Given block ptr bp, set previous/next block */
#define SET_PREV(bp, prev)  \
    (SET(bp, (unsigned int)((char *)(bp) - (char *)(prev))))
#define SET_NEXT(bp, next)  \
    (SET((char *)(bp) + WSIZE, (unsigned int)((char *)(next) - (char *)(bp))))

/* Functional prototypes for internal helper routines */
static void *extend_heap(size_t asize);    
static void *place(void *bp, size_t asize);
static void *find_fit(size_t asize); 
static void *coalesce(void *bp);  
     
/* Functional prototypes for  Segregated free list helper rountines */      
static size_t find_index(size_t size);
static void *insert_in_free_list(void *bp);
static void delete_from_free_list(void *bp);


/* Segregated SIZE class */
#define SIZE_CLASS_1     (1 << 4)
#define SIZE_CLASS_2     (1 << 5)
#define SIZE_CLASS_3     (1 << 6)
#define SIZE_CLASS_4     (1 << 7)
#define SIZE_CLASS_5     (1 << 8)
#define SIZE_CLASS_6     (1 << 9)
#define SIZE_CLASS_7     (1 << 10)           
#define SIZE_CLASS_8     (1 << 11)
#define SIZE_CLASS_9     (1 << 12)
#define SIZE_CLASS_10    (1 << 13)
#define SIZE_CLASS_11    (1 << 14)
#define SIZE_CLASS_12    (1 << 15)
#define SIZE_CLASS_13    (1 << 16)
#define SIZE_CLASS_14    (1 << 17)
#define SIZE_CLASS_15    (1 << 18)
#define SIZE_CLASS_16    (1 << 19)
#define SIZE_CLASS_17    (1 << 20)          
#define SIZE_CLASS_18    (1 << 21)
#define SIZE_CLASS_19    (1 << 22)

/* Global variables */
static char *heap_listp = NULL;  
static size_t *free_list_head;  
static size_t *free_list_tail;  

/* 
 * mm_init - initialize the malloc package.
 */
int mm_init(void)
{
    /* Create the initial empty heap */
    if ((heap_listp = mem_sbrk(84 * WSIZE)) == (void *)-1) 
		return -1;
    
	free_list_head = (size_t *)heap_listp;
	free_list_tail = (size_t *)(heap_listp + 40 * WSIZE);
	
	int i;
	for (i = 0; i < 20; ++i)
		free_list_head[i] = free_list_tail[i] = (size_t)NULL;
	
	heap_listp += 80 * WSIZE;   

	SET(heap_listp, 0);                             /* Alignment padding */
    SET(heap_listp + 1 * WSIZE, PACK(DSIZE, 1));    /* Prologue header */ 
    SET(heap_listp + 2 * WSIZE, PACK(DSIZE, 1));    /* Prologue footer */ 
    SET(heap_listp + 3 * WSIZE, PACK(0, 1));        /* Epilogue header */  
	heap_listp += DSIZE;
	
	/* Extend the empty heap with a free block of CHUNKSIZE bytes */
    if (extend_heap(CHUNKSIZE) == NULL) 
		return -1;
	return 0;
}

/* 
 * mm_malloc - Allocate a block by incrementing the brk pointer.
 *     Always allocate a block whose size is a multiple of the alignment.
 */
void *mm_malloc(size_t size)
{
    size_t blk_size, extend_size;     
    char *bp;      
    
	/* Adjust block size  */
    if (size + DSIZE <= MIN_BLK_SIZE)                    
		blk_size = MIN_BLK_SIZE;                                     
    else
		blk_size = ALIGN(size + DSIZE); 
	
	/* Found a fit */
	if ((bp = find_fit(blk_size)))
		return place(bp, blk_size);


    /* Found no fit, extend the heap*/
	extend_size = MAX(blk_size, CHUNKSIZE);
    if (!(bp = extend_heap(extend_size)))
        return NULL;
    
    return place(bp, blk_size);
}

/*
 * mm_free - Freeing a block does nothing.
 */
void mm_free(void *ptr)
{
    if(!(ptr)) 
		return;

    size_t size = SIZE(ptr);

    SET_HDR(ptr, PACK(size, 0));
    SET_FTR(ptr, PACK(size, 0));
    
    ptr = insert_in_free_list(ptr);
	coalesce(ptr);
}

/*
 * mm_realloc - Implemented simply in terms of mm_malloc and mm_free
 */
void *mm_realloc(void *ptr, size_t size)
{   
    /* ptr == NULL, mm_malloc */
    if(!(ptr)) 
        return mm_malloc(size);
        
    if((int)size < 0)
        return NULL;
    
    /* size == 0, mm_free */    
    if(!size) { 
        mm_free(ptr); 
        return NULL;
    }
    
    size_t old_size = SIZE(ptr);
	size_t new_size = ALIGN(size + DSIZE);
	size_t remaining = old_size - new_size;
	char *next_blk_ptr;
	    
    if (old_size < new_size &&!NEXT_ALLOC(ptr) 
                            && NEXT_SIZE(ptr) + old_size >= new_size){
		next_blk_ptr = NEXT_BLK(ptr);
		remaining = SIZE(next_blk_ptr) + old_size - new_size;
		delete_from_free_list(next_blk_ptr);
		if (remaining >= MIN_BLK_SIZE){ /* Merge and split */
			SET_HDR(ptr, PACK(new_size, 1));
			SET_FTR(ptr, PACK(new_size, 1));
			next_blk_ptr = NEXT_BLK(ptr);
			SET_HDR(next_blk_ptr, PACK(remaining, 0));
			SET_FTR(next_blk_ptr, PACK(remaining, 0));
			insert_in_free_list(next_blk_ptr);
		}
		else{
		    SET_HDR(ptr, PACK(SIZE(next_blk_ptr) + old_size, 1));
			SET_FTR(ptr, PACK(SIZE(next_blk_ptr) + old_size, 1));
		}
		return ptr;
	}
	else if (old_size >= new_size){ 
		if (remaining >= MIN_BLK_SIZE){  
			if (!NEXT_ALLOC(ptr)){
				old_size += NEXT_SIZE(ptr);
				delete_from_free_list(NEXT_BLK(ptr));
			}
			SET_HDR(ptr, PACK(new_size, 1));
			SET_FTR(ptr, PACK(new_size, 1));
			next_blk_ptr = NEXT_BLK(ptr);
			SET_HDR(next_blk_ptr, PACK(remaining, 0));
			SET_FTR(next_blk_ptr, PACK(remaining, 0));
			insert_in_free_list(next_blk_ptr);
		}
		else{
            SET_HDR(ptr, PACK(old_size, 1));
            SET_FTR(ptr, PACK(old_size, 1));
        }
		return ptr;
	} 
    else {
	    void *new_ptr = mm_malloc(size);
	    memcpy(new_ptr, ptr, size);
	    mm_free(ptr);
	    return new_ptr;
    }

}

static void *extend_heap(size_t asize) 
{
	char *bp;

	asize = (asize % 2) ? (asize+1): asize;  
    if (((bp = mem_sbrk(asize)) == (void *)-1)) 
        return NULL;                                        

    /* Initialize free block header/footer and the epilogue header */
    SET_HDR(bp, PACK(asize, 0));         
    SET_FTR(bp, PACK(asize, 0));            
    SET_HDR(NEXT_BLK(bp), PACK(0, 1));
    
    /* Insert a new extend free block */
    return insert_in_free_list(bp);  
}

static void *place(void *bp, size_t asize)
{
	size_t csize = SIZE(bp);
	size_t remaining = csize - asize;
	
	delete_from_free_list(bp);
	
	if (remaining >= MIN_BLK_SIZE) { /* split the block */
		SET_HDR(bp, PACK(asize, 1));
		SET_FTR(bp, PACK(asize, 1));
		void *next_blk_ptr = NEXT_BLK(bp);
		SET_HDR(next_blk_ptr, PACK(remaining, 0));
		SET_FTR(next_blk_ptr, PACK(remaining, 0));
		insert_in_free_list(next_blk_ptr);
   	 }
   	 
   	 else {
		SET_HDR(bp, PACK(csize, 1));
		SET_FTR(bp, PACK(csize, 1));
   	 }
   	 
	return bp;
}

static void *find_fit(size_t asize)
{
	void *bp;
	
	size_t i = find_index(asize);
	for(;i<=19;++i){
		if ((void *)free_list_head[i] == NULL)
			continue;
        
		bp = (char *)free_list_head[i];
		while(1){
			if (SIZE(bp) >= asize){
				return bp;
			}
			
			if (bp == (void *)free_list_tail[i])
				break;
				
			bp=NEXT(bp); /*next free block*/
		}
	}
	return NULL;
}

static inline size_t find_index(size_t size){
	if (size <= SIZE_CLASS_1)
        return 0;
    else if (size <= SIZE_CLASS_2)
        return 1;
    else if (size <= SIZE_CLASS_3)
        return 2;
    else if (size <= SIZE_CLASS_4)
        return 3;
    else if (size <= SIZE_CLASS_5)
        return 4;
    else if (size <= SIZE_CLASS_6)
        return 5;
    else if (size <= SIZE_CLASS_7)
        return 6;
    else if (size <= SIZE_CLASS_8)
        return 7;
    else if (size <= SIZE_CLASS_9)
        return 8;
    else if (size <= SIZE_CLASS_10)
        return 9;
    else if (size <= SIZE_CLASS_11)
        return 10;
    else if (size <= SIZE_CLASS_12)
        return 11;
    else if (size <= SIZE_CLASS_13)
        return 12;
    else if (size <= SIZE_CLASS_14)
        return 13;
    else if (size <= SIZE_CLASS_15)
        return 14;
    else if (size <= SIZE_CLASS_16)
        return 15;
    else if (size <= SIZE_CLASS_17)
        return 16;
    else if (size <= SIZE_CLASS_18)
        return 17;
    else if (size <= SIZE_CLASS_19)
        return 18;
    else
        return 19;
}

static void *coalesce(void *bp) 
{	
	size_t pre_alloc = PREV_ALLOC(bp);
    size_t next_alloc = NEXT_ALLOC(bp);
    size_t size = SIZE(bp);
    
    /* Next block is free */   
    if (pre_alloc && !next_alloc) {                  
        size += SIZE(NEXT_BLK(bp));
        delete_from_free_list(bp);
        delete_from_free_list(NEXT_BLK(bp));
        SET_HDR(bp, PACK(size, 0));
        SET_FTR(bp, PACK(size, 0));
        return insert_in_free_list(bp);

    }
    
    /* Prev block is free */  
    else if (!pre_alloc && next_alloc) {               
        size += SIZE(PREV_BLK(bp));
        delete_from_free_list(PREV_BLK(bp));
        delete_from_free_list(bp);
        SET_HDR(PREV_BLK(bp), PACK(size, 0));
        SET_FTR(bp, PACK(size, 0));
        return insert_in_free_list(PREV_BLK(bp));
    }
    
    /* Both blocks are free */ 
    else if (!pre_alloc && !next_alloc) {                
        size += SIZE(PREV_BLK(bp)) + SIZE(NEXT_BLK(bp));
        delete_from_free_list(PREV_BLK(bp));
        delete_from_free_list(bp);
        delete_from_free_list(NEXT_BLK(bp));
        SET_HDR(PREV_BLK(bp), PACK(size, 0));
        SET_FTR(NEXT_BLK(bp), PACK(size, 0));
        return insert_in_free_list(PREV_BLK(bp));
    } 
    
    else { return bp; }
}

static void *insert_in_free_list(void *bp){
	/* Find the right SIZE class */
	size_t i = find_index(SIZE(bp));
	
	if ((void *)free_list_head[i] == NULL) /* Empty list */
		free_list_head[i] = free_list_tail[i] = (size_t)bp;
	else{
		/* insert the free block */
		if ((size_t)(bp) < free_list_head[i]){
			SET_NEXT(bp, free_list_head[i]);
			SET_PREV(free_list_head[i], bp);
			free_list_head[i] = (size_t)bp;
		}
		
		/* append the free block */
		else if ((size_t)(bp) > free_list_tail[i]){
			SET_PREV(bp, free_list_tail[i]);
			SET_NEXT(free_list_tail[i], bp);
			free_list_tail[i] = (size_t)bp;
		}
		
		else{
			char *tmp = (char *)free_list_head[i];
			while (NEXT(tmp) < (char *)bp)
				tmp = NEXT(tmp);
			SET_PREV(bp, tmp);
			SET_NEXT(bp, NEXT(tmp));
			SET_PREV(NEXT(tmp), bp);
			SET_NEXT(tmp, bp);
		}
	}
	return bp;
}

static void delete_from_free_list(void *bp){
        /* Find the right SIZE class */
        size_t i = find_index(SIZE(bp));
        
        /* Only one block */
        if (free_list_head[i] == free_list_tail[i]){ 
                free_list_head[i] = free_list_tail[i] = (size_t)NULL;
        }
        /* Delete the head */
        else if ((size_t)(bp) == free_list_head[i])
                free_list_head[i] = (size_t)NEXT(bp);
        /* Delete the tail*/        
        else if ((size_t)(bp) == free_list_tail[i])
                free_list_tail[i] = (size_t)PREV(bp);
        /* Delete arbitrary one between head & tail */
        else{                 
            SET_PREV(NEXT(bp), PREV(bp));
            SET_NEXT(PREV(bp), NEXT(bp));
        }
}
