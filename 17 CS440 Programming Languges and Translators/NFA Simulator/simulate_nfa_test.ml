open OUnit2
open Simulate_nfa

let in_channel = open_in "accepting_states.txt"
let in_channel2 = open_in "transition_cases.txt"
let in_channel3 = open_in "epsilon_cases.txt"
let alphabet = ["a";"b";"c";" "]

(* test case 2 *)
let regular_cases = [["2"; "a"; "3"]; ["4"; "b"; "5"]; ["7"; "a"; "8"]; ["8"; "b"; "9"]; ["9"; "b"; "10"]]
let epsilon_cases = [["0"; "7"]; ["0"; "1"]; ["1"; "2"]; ["1"; "4"]; ["3"; "6"]; ["5"; "6"]; ["6"; "1"]; ["6"; "7"]]
let a_set = ["0"; "1"; "2"; "4"; "7"]
let b_set = ["1"; "2"; "3"; "4"; "6"; "7"; "8"]
let c_set = ["1"; "2"; "4"; "5"; "6"; "7"]
let d_set = ["1"; "2"; "4"; "5"; "6"; "7"; "9"]
let e_set = ["1"; "2"; "4"; "5"; "6"; "7"; "10"]
let dtran_A_a = b_set
let dtran_A_b = c_set
let dtran_B_a = b_set
let dtran_B_b = d_set
let dtran_C_a = b_set
let dtran_C_b = c_set
let dtran_D_a = b_set
let dtran_D_b = e_set
let dtran_E_a = b_set
let dtran_E_b = c_set

let test_check_num = "test suite for check_num" >::: [
    "false"		>:: (fun _ -> assert_equal false (check_num "a"));
    "true"		>:: (fun _ -> assert_equal true (check_num "1"));
]

let test_read_num = "test suite for read_num" >::: [
    "empty"		>:: (fun _ -> assert_raises ~msg:"Empty line" (Invalid_argument "Empty line") (fun () -> read_num " "));
    "negative"		>:: (fun _ -> assert_raises ~msg:"Invalid number" (Invalid_argument "Invalid number") (fun () -> read_num "-1"));
    "string"		>:: (fun _ -> assert_raises ~msg:"Expect a number but get a string" (Invalid_argument "Expect a number but get a string") (fun () -> read_num "a"));
    "zero"		>:: (fun _ -> assert_equal 0 (read_num " 0 "));
    "one"		>:: (fun _ -> assert_equal 1 (read_num "1 "));
    "twelve"		>:: (fun _ -> assert_equal 12 (read_num "  12  "));
]

let test_is_valid_state = "test suite for is_valid_state" >::: [
    "less than 0"	>:: (fun _ -> assert_equal false (is_valid_state (-1) 5));
    "greater than N-1"	>:: (fun _ -> assert_equal false (is_valid_state 5 5));
    "zero"		>:: (fun _ -> assert_equal true (is_valid_state 0 5));
    "four"		>:: (fun _ -> assert_equal true (is_valid_state 4 5));
]

let test_read_accepting_state = "test suite for read_accepting_state" >::: [
    "less than 0"	>:: (fun _ -> assert_raises ~msg:"Invalid number" (Invalid_argument "Invalid number") (fun () -> read_accepting_state "-1" 5));
    "greater than N-1"	>:: (fun _ -> assert_raises ~msg:"Invalid state" (Failure "Invalid state") (fun () -> read_accepting_state "5" 5));
    "zero"		>:: (fun _ -> assert_equal "0" (read_accepting_state "0" 5));
    "four"		>:: (fun _ -> assert_equal "4" (read_accepting_state "4" 5));
]

let test_read_all_accepting_states = "test suite for read_all_accpeting_states" >::: [
    "four states"	>:: (fun _ -> assert_equal ["1";"2";"3";"4"] (read_all_accepting_states in_channel 4 5));
]

let test_read_symbols = "test suite for read_symbols" >::: [
    "zero"		>:: (fun _ -> assert_raises ~msg:"Empty line" (Invalid_argument "Empty line") (fun () -> read_symbols "" 4));
    "three"		>:: (fun _ -> assert_raises ~msg:"Mismatched number of characters with that of alphabet" (Invalid_argument "Mismatched number of characters with that of alphabet") (fun () -> read_symbols "abc" 4));
    "four"		>:: (fun _ -> assert_equal ["a";"b";"c";" "] (read_symbols "abc " 4));
]

let test_deduplicate = "test suite for deduplicate" >::: [
   "empty"		>:: (fun _ -> assert_equal [] (deduplicate []));
   "one"		>:: (fun _ -> assert_equal [1] (deduplicate [1]));
   "two"		>:: (fun _ -> assert_equal [1;2] (deduplicate [1;1;2;2]));
]

let test_is_valid_symbol = "test suite for is_valid_symbol" >::: [
    "invalid"		>:: (fun _ -> assert_raises ~msg:"Invalid symbol" (Failure "Invalid symbol") (fun () -> is_valid_symbol "d" ["a";"b";"c";" "]));
    "valid"		>:: (fun _ -> assert_equal true (is_valid_symbol "a" ["a";"b";"c";" "]));
]

let test_trim_element = "test suite for trim_element" >::: [
   "trim"		>:: (fun _ -> assert_equal ["a";"b";"c";" "] (trim_element ["  a";"b   ";"  c  "; "   "]));
]

let test_is_valid_transition_case = "test suite for is_valid_transition_case" >::: [
   "invalid"		>:: (fun _ -> assert_equal false (is_valid_transition_case ["10";"d";"1"] ["a";"b";"c";" "] 5));
   "valid"		>:: (fun _ -> assert_equal true (is_valid_transition_case ["0";"a";"1"] ["a";"b";"c";" "] 5));
]

let test_read_transition_case = "test suite for read_transition_case" >::: [
    "invalid"		>:: (fun _ -> assert_raises ~msg:"Invalid transition case" (Invalid_argument "Invalid transition case") (fun () -> read_transition_case "10,d,0" ["a";"b";"c";" "] 5));
    "valid"		>:: (fun _ -> assert_equal ["0";"a";"1"] (read_transition_case "0,a,1" ["a";"b";"c";" "] 5));
]

let test_read_all_transition_cases = "test suite for read_all_transition_cases" >::: [
    "valid"		>:: (fun _ -> assert_equal [["0";"a";"1"];["1";" ";"2"];["3";"b";"2"]] (read_all_transition_cases in_channel2 3 alphabet 5));
]

let test_is_valid_epsilon_case = "test suite for is_valid_epsilon_case" >::: [
    "invalid"		>:: (fun _ -> assert_equal false (is_valid_epsilon_case ["10";"1"] 5));
    "valid"		>:: (fun _ -> assert_equal true (is_valid_epsilon_case ["0";"1"] 5));
]

let test_read_epsilon_case = "test suite for read_epsilon_case" >::: [
    "invalid"		>:: (fun _ -> assert_raises ~msg:"Invalid epsilon case" (Invalid_argument "Invalid epsilon case") (fun () -> read_epsilon_case "10,20" 5));
    "valid"		>:: (fun _ -> assert_equal ["0";"1"] (read_epsilon_case "0,1" 5));
]

let test_read_all_epsilon_cases  = "test suite for read_all_epsilon_cases" >::: [
    "valid"		>:: (fun _ -> assert_equal [["0";"1"];["0";"3"];["3";"4"]] (read_all_epsilon_cases in_channel3 3 5));
]

let test_read_input = "test suite for read_input" >::: [
    "invalid"		>:: (fun _ -> assert_raises ~msg:"Invalid symbol" (Failure "Invalid symbol") (fun () -> read_input "fffaa" alphabet));
    "valid"		>:: (fun _ -> assert_equal ["a";"b";"c"] (read_input "abc" alphabet));
]

let test_regular_transition = "test suite for regular_transition" >::: [
    "2, a"		>:: (fun _ -> assert_equal ["3"] (regular_transition regular_cases "2" "a"));
    "7, a"		>:: (fun _ -> assert_equal ["8"] (regular_transition regular_cases "7" "a"));
    "2, b"		>:: (fun _ -> assert_equal [] (regular_transition regular_cases "2" "b"));
    "7, b"		>:: (fun _ -> assert_equal [] (regular_transition regular_cases "7" "b"));
    "4, b"		>:: (fun _ -> assert_equal ["5"] (regular_transition regular_cases "4" "b"));
    "8, b"		>:: (fun _ -> assert_equal ["9"] (regular_transition regular_cases "8" "b"));
    "9, b"		>:: (fun _ -> assert_equal ["10"] (regular_transition regular_cases "9" "b"));
    "4, a"		>:: (fun _ -> assert_equal [] (regular_transition regular_cases "4" "a"));
    "8, a"		>:: (fun _ -> assert_equal [] (regular_transition regular_cases "8" "a"));
    "9, a"		>:: (fun _ -> assert_equal [] (regular_transition regular_cases "9" "a"));
   
]

let test_epsilon_transition = "test suite for epsilon_transition" >::: [
    "0"			>:: (fun _ -> assert_equal ["1"; "7"] (epsilon_transition epsilon_cases "0"));
    "1"			>:: (fun _ -> assert_equal ["2"; "4"] (epsilon_transition epsilon_cases "1"));
    "2"			>:: (fun _ -> assert_equal [] (epsilon_transition epsilon_cases "2"));
    "3"			>:: (fun _ -> assert_equal ["6"] (epsilon_transition epsilon_cases "3"));
    "4"			>:: (fun _ -> assert_equal [] (epsilon_transition epsilon_cases "4"));
    "5"			>:: (fun _ -> assert_equal ["6"] (epsilon_transition epsilon_cases "5"));
    "6"			>:: (fun _ -> assert_equal ["1"; "7"] (epsilon_transition epsilon_cases "6"));
    "7"			>:: (fun _ -> assert_equal [] (epsilon_transition epsilon_cases "7"));
    "8"			>:: (fun _ -> assert_equal [] (epsilon_transition epsilon_cases "8"));
    "9"			>:: (fun _ -> assert_equal [] (epsilon_transition epsilon_cases "9"));
    "10"		>:: (fun _ -> assert_equal [] (epsilon_transition epsilon_cases "10"));
]

let test_move = "test suite for move" >::: [
    "A, a"		>:: (fun _ -> assert_equal ["3"; "8"] 	(move regular_cases a_set "a"));
    "A, b"		>:: (fun _ -> assert_equal ["5"] 	(move regular_cases a_set "b"));
    "B, a"		>:: (fun _ -> assert_equal ["3"; "8"] 	(move regular_cases b_set "a"));
    "B, b"		>:: (fun _ -> assert_equal ["5"; "9"] 	(move regular_cases b_set "b"));
    "C, a"		>:: (fun _ -> assert_equal ["3"; "8"]   (move regular_cases c_set "a"));
    "C, b"		>:: (fun _ -> assert_equal ["5"]	(move regular_cases c_set "b"));
    "D, a"		>:: (fun _ -> assert_equal ["3"; "8"]	(move regular_cases d_set "a"));
    "D, b"		>:: (fun _ -> assert_equal ["5"; "10"]	(move regular_cases d_set "b"));
    "E, a"		>:: (fun _ -> assert_equal ["3"; "8"]	(move regular_cases e_set "a"));
    "E, b"		>:: (fun _ -> assert_equal ["5"]	(move regular_cases e_set "b"));
    "A, a"		>:: (fun _ -> assert_equal ["3"; "8"] (move regular_cases a_set "a"));
    "A, b"		>:: (fun _ -> assert_equal ["5"] (move regular_cases a_set "b"));
    "B, a"		>:: (fun _ -> assert_equal ["3"; "8"] (move regular_cases b_set "a"));
    "B, b"		>:: (fun _ -> assert_equal ["5"; "9"] (move regular_cases b_set "b"));
    "C, a"		>:: (fun _ -> assert_equal ["3"; "8"] (move regular_cases c_set "a"));
    "C, b"		>:: (fun _ -> assert_equal ["5"] (move regular_cases c_set "b"));
    "D, a"		>:: (fun _ -> assert_equal ["3"; "8"] (move regular_cases d_set "a"));
    "D, b"		>:: (fun _ -> assert_equal ["5"; "10"] (move regular_cases d_set "b"));
    "E, a"		>:: (fun _ -> assert_equal ["3"; "8"] (move regular_cases e_set "a"));
    "E, b"		>:: (fun _ -> assert_equal ["5"] (move regular_cases e_set "b"));
]

let test_epsilon_closure = "test suite for epsilon_closure" >::: [
    "0"			>:: (fun _ -> assert_equal a_set (epsilon_closure epsilon_cases ["0"]));
    "A, a"		>:: (fun _ -> assert_equal dtran_A_a (epsilon_closure epsilon_cases (move regular_cases a_set "a")));
    "A, b"		>:: (fun _ -> assert_equal dtran_A_b (epsilon_closure epsilon_cases (move regular_cases a_set "b")));
    "B, a"		>:: (fun _ -> assert_equal dtran_B_a (epsilon_closure epsilon_cases (move regular_cases b_set "a")));
    "B, b"		>:: (fun _ -> assert_equal dtran_B_b (epsilon_closure epsilon_cases (move regular_cases b_set "b")));
    "C, a"		>:: (fun _ -> assert_equal dtran_C_a (epsilon_closure epsilon_cases (move regular_cases c_set "a")));
    "C, b"		>:: (fun _ -> assert_equal dtran_C_b (epsilon_closure epsilon_cases (move regular_cases c_set "b")));
    "D, a"		>:: (fun _ -> assert_equal dtran_D_a (epsilon_closure epsilon_cases (move regular_cases d_set "a")));
    "D, b"		>:: (fun _ -> assert_equal dtran_D_b (epsilon_closure epsilon_cases (move regular_cases d_set "b")));
    "E, a"		>:: (fun _ -> assert_equal dtran_E_a (epsilon_closure epsilon_cases (move regular_cases e_set "a")));
    "E, b"		>:: (fun _ -> assert_equal dtran_E_b (epsilon_closure epsilon_cases (move regular_cases e_set "b")));
]

let test_check_result = "test suite for check_result" >::: [
    "false"		>:: (fun _ -> assert_equal false (check_result ["1"] ["2"; "3"; "4"]));
    "true"		>:: (fun _ -> assert_equal true (check_result ["1"] ["1"; "2"; "3"; "4"]));
]

let _ = run_test_tt_main test_check_num 
let _ = run_test_tt_main test_read_num
let _ = run_test_tt_main test_is_valid_state
let _ = run_test_tt_main test_read_accepting_state
let _ = run_test_tt_main test_read_all_accepting_states
let _ = run_test_tt_main test_read_symbols
let _ = run_test_tt_main test_deduplicate
let _ = run_test_tt_main test_is_valid_symbol
let _ = run_test_tt_main test_trim_element
let _ = run_test_tt_main test_is_valid_transition_case
let _ = run_test_tt_main test_read_transition_case
let _ = run_test_tt_main test_read_all_transition_cases
let _ = run_test_tt_main test_is_valid_epsilon_case
let _ = run_test_tt_main test_read_epsilon_case
let _ = run_test_tt_main test_read_all_epsilon_cases
let _ = run_test_tt_main test_read_input
let _ = run_test_tt_main test_regular_transition
let _ = run_test_tt_main test_epsilon_transition
let _ = run_test_tt_main test_move
let _ = run_test_tt_main test_epsilon_closure
let _ = run_test_tt_main test_check_result
