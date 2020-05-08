/* 
 * trans.c - Matrix transpose B = A^T
 *
 * Each transpose function must have a prototype of the form:
 * void trans(int M, int N, int A[N][M], int B[M][N]);
 *
 * A transpose function is evaluated by counting the number of misses
 * on a 1KB direct mapped cache with a block size of 32 bytes.
 */ 
#include <stdio.h>
#include "cachelab.h"


int is_transpose(int M, int N, int A[N][M], int B[M][N]);

/* 
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded. 
 */
char transpose_submit_desc[] = "Transpose submission";
void transpose_submit(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, i1, j1;
    int diagonal_index, diagonal_val;
    
    int R0, R1, R2, R3, R4, R5, R6, R7, k;
    
    if (N == 32 && M == 32) {
        for (i = 0; i < N; i += 8) {
            for (j = 0; j < M; j += 8) {
                for (i1 = i; i1 < i + 8; i1++) {
                    for (j1 = j; j1 < j + 8; j1++) {
                        if (i1 != j1)
                            B[j1][i1] = A[i1][j1];
                        else {
                            diagonal_val = A[i1][j1];
                            diagonal_index = i1;
                        }
                    }
                    if (j == i)
                        B[diagonal_index][diagonal_index] = diagonal_val;
                }
            }
        }   
    } else if (N == 64 && M == 64) {
        for (i = 0; i < N; i += 8){
		    for (j = 0; j < M; j += 8){	
			    for(k = 0; k < 8; k++){
			         if(k == 0){
					    R4 = A[j][i+4];
					    R5 = A[j][i+5];
                        R6 = A[j][i+6];
					    R7 = A[j][i+7];
				    }
				    
			        R0 = A[j+k][i+0];
                    R1 = A[j+k][i+1];
                    R2 = A[j+k][i+2];
                    R3 = A[j+k][i+3];
                    B[i+0][j+k] = R0;
                    B[i+1][j+k] = R1;
                    B[i+2][j+k] = R2;
                    B[i+3][j+k] = R3;
			    }
			    B[i+4][j] = R4;
                B[i+5][j] = R5;
                B[i+6][j] = R6;
                B[i+7][j] = R7;
		
			    for(k = 7; k > 0; k--){
				    R0 = A[j+k][i+4];
				    R1 = A[j+k][i+5];
				    R2 = A[j+k][i+6];
				    R3 = A[j+k][i+7];
				    B[i+4][j+k] = R0;
				    B[i+5][j+k] = R1;
				    B[i+6][j+k] = R2;
				    B[i+7][j+k] = R3;
			    }
		    }
		}
    } else {
        for (i = 0; i < N; i += 23) {
            for (j = 0; j < M; j += 23) {
                for (i1 = i; i1 < i + 23 && i1 < N; i1++) {
                    for (j1 = j; j1 < j + 23 && j1 < M; j1++) {
                        if (i1 != j1)
                            B[j1][i1] = A[i1][j1];
                        else {
                            diagonal_val = A[i1][j1];
                            diagonal_index = i1;
                        }
                    }
                    if (j == i)
                        B[diagonal_index][diagonal_index] = diagonal_val;
                }
            }
        }   
    }
}

/* 
 * You can define additional transpose functions below. We've defined
 * a simple one below to help you get started. 
 */ 

/* 
 * trans - A simple baseline transpose function, not optimized for the cache.
 */
char trans_desc[] = "Simple row-wise scan transpose";
void trans(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, tmp;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; j++) {
            tmp = A[i][j];
            B[j][i] = tmp;
        }
    }    

}

/*
 * registerFunctions - This function registers your transpose
 *     functions with the driver.  At runtime, the driver will
 *     evaluate each of the registered functions and summarize their
 *     performance. This is a handy way to experiment with different
 *     transpose strategies.
 */
void registerFunctions()
{
    /* Register your solution function */
    registerTransFunction(transpose_submit, transpose_submit_desc); 

    /* Register any additional transpose functions */
    registerTransFunction(trans, trans_desc); 

}

/* 
 * is_transpose - This helper function checks if B is the transpose of
 *     A. You can check the correctness of your transpose by calling
 *     it before returning from the transpose function.
 */
int is_transpose(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; ++j) {
            if (A[i][j] != B[j][i]) {
                return 0;
            }
        }
    }
    return 1;
}

