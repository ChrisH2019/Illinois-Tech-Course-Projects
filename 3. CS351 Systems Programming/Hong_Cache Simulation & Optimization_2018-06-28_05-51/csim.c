#include "cachelab.h"
#include<getopt.h>
#include<stdlib.h>
#include<unistd.h>
#include<string.h>
#include<stdio.h>

#define MAXLINE 1024

typedef unsigned long long int uint64_t;

struct line_t {
    int valid;
    uint64_t tag;
    uint64_t LRU;
};

void printUsageMsg(char **argv);
void makeCache();
void readTraceFile(char *trace_file);
void getCacheData(uint64_t address);
void freeCache();

int s = 0;
int b = 0;
int E = 0;
int S = 0;
int B = 0;
    
int hit_count = 0;
int miss_count = 0;
int eviction_count = 0;

int verbose = 0;

struct line_t **cache = NULL;


int main(int argc, char **argv)
{   
    char *trace_file;
    int opt;
    while ((opt = getopt(argc, argv, "hvs:E:b:t:")) != -1) {
        switch (opt) {
            case 's':
                s = atoi(optarg);
                break;
            case 'E':
                E = atoi(optarg);
                break;
            case 'b':
                b = atoi(optarg);
                break;
            case 't':
                trace_file = optarg;
                break;
            case 'v':
                verbose = 1;
                break;
            case 'h':
                printUsageMsg(argv);
                exit(0);
            default:
                printUsageMsg(argv);
                exit(1);
        }
    }
    
    if (!s || !E || !b || !trace_file) {
        printf("%s: Missing required command line argument\n", argv[0]);
        printUsageMsg(argv);
        exit(1);
    }
    
    S = 1<<s;
    B = 1<<b;
    
    makeCache();
    
    readTraceFile(trace_file);
    
    freeCache();
    
    printSummary(hit_count, miss_count, eviction_count);
    
    return 0;
}


void printUsageMsg(char **argv)
{   
    printf("Usage: %s [-hv] -s <num> -E <num> -b <num> -t <file>\n", argv[0]);
    printf("Options:\n");
    printf("  -h         Print this help message.\n");
    printf("  -v         Optional verbose flag.\n");
    printf("  -s <num>   Number of set index bits.\n");
    printf("  -E <num>   Number of lines per set.\n");
    printf("  -b <num>   Number of block offset bits.\n");
    printf("  -t <file>  Trace file.\n");
    printf("\n");
    printf("Exambples:\n");
    printf("  linux>  %s -s 4 -E 1 -b 4 -t traces/yi.trace\n", argv[0]);
    printf("  linux>  %s -v -s 8 -E 2 -b 4 -t traces/yi.trace\n", argv[0]);
    exit(0);
}


void makeCache()
{
    if (!(cache =  malloc(sizeof(struct line_t *) * S))) {
        printf("malloc failed!\n");
        exit(1);
    }
    
    for (int i = 0; i < S; i++) {
        cache[i] = malloc(sizeof(struct line_t) * E);
        for (int j = 0; j < E; j++) {
            cache[i][j].valid = 0;
            cache[i][j].tag = 0;
            cache[i][j].LRU = 0;
        }
    }
}


void readTraceFile(char *trace_file)
{
    
    char operation;
    uint64_t address;
    int size;
    char buf[MAXLINE];
    
    
    FILE *fp = fopen(trace_file, "r");
    if (!fp) {
        printf("%s: No such file or directory\n", trace_file);
        exit(1);
    } else {
        while (fgets(buf, MAXLINE, fp)) {
            if (sscanf(buf, " %c %llx,%d", &operation, &address, &size) == 3)
            switch (operation) {
                case 'I':
                    break;
                case 'L':
                    if (verbose)
                        printf("%c %llx,%d ", operation, address, size);
                    getCacheData(address);
                    break;
                case 'S':
                    if (verbose)
                        printf("%c %llx,%d ", operation, address, size);
                    getCacheData(address);
                    break;
                case 'M':
                    if (verbose)
                        printf("%c %llx,%d ", operation, address, size);
                    getCacheData(address);
                    getCacheData(address);
                    break;
                default:
                    break;
            }
            printf("\n");
        }
    }
    fclose(fp);
} 


void getCacheData(uint64_t address) {
    int i;
    int cache_full = 1;
    int old_hit_count = hit_count;
    
    uint64_t set = address << (64 - (s + b)) >> (64 - s);
    uint64_t tag = address >> (s + b);
    
    for (i = 0; i < E; i++) {
        if (cache[set][i].valid) {
            if (cache[set][i].tag == tag) {
                if (verbose)
                    printf("hit ");
                hit_count++;
                cache[set][i].LRU++;
                break;
            }
        } else if (!(cache[set][i].valid) && cache_full)
                cache_full = 0;
    }
    
    
    if (old_hit_count == hit_count) {
        miss_count++;
        if (verbose)
            printf("miss ");
    } else { return; }
    
    int max_LRU = cache[set][0].LRU;
    int min_LRU = cache[set][0].LRU;
    int evicted_line = 0;
    
    for (i = 1; i < E; i++) {
        if (min_LRU >cache[set][i].LRU) {
            evicted_line = i;
            min_LRU = cache[set][i].LRU;
        }
        
        if (max_LRU < cache[set][i].LRU) 
            max_LRU = cache[set][i].LRU;
    }
    
    if (cache_full) {
        eviction_count++;
        if (verbose)
            printf("eviction ");
    }
    
    cache[set][evicted_line].valid = 1;
    cache[set][evicted_line].tag = tag;
    cache[set][evicted_line].LRU = max_LRU + 1;
    
    return;
    
}


void freeCache() {
    for (int i = 0; i < S; i++) 
        free(cache[i]);
        
    free(cache);
}