(** CS 440, F20
  * Project 2: NFA Simulator
  * Christopher Weicong Hong
  * Daniel Jablonski
  * Panthi Patel
  *)

(** Ref variables to store NFA descriptions *)
let number_of_states = ref 0
let number_of_accepting_states = ref 0
let accepting_states = ref []
let number_of_symbols = ref 0
let alphabet = ref []
let number_of_transition_cases = ref 0
let transition_cases = ref []
let number_of_epsilon_cases = ref 0
let epsilon_cases = ref []
let input_to_nfa = ref []

(** [check_num s] a helper function
    checks if s contains valid digits only *)
let check_num s = 
    try
        int_of_string s |> ignore; true
    with 
        Failure _ -> false

(** [read_num line] reads a number from a line.
    Raise exception if invalid line description. *)
let read_num line =
    (* Print line description *)
    print_endline line;
    (* Remove leading and trailing whitespace *) 
    let line' = String.trim line in
        if String.length line' = 0 then
	    raise (Invalid_argument "Empty line")
        else if (not (check_num line')) then
            raise (Invalid_argument "Expect a number but get a string")
        else if (int_of_string line') < 0 then
	    raise (Invalid_argument "Invalid number")
        else
            int_of_string line'

(** [is_valid_state state num_states] a helper function 
    checks if state is in range 0 to number of states - 1 *)
let is_valid_state state num_states = 
    if (state >= 0) && (state < num_states) then
        true
    else
        false

(** [read_accpeting_state line] a helper fuinction 
    reads acceting state description.
    Raise exception if invalid state. *)
let read_accepting_state line num_states =
    let state = read_num line in 
        if is_valid_state state num_states then
            string_of_int state
        else
            raise (Failure "Invalid state")

(** [read_all_accepting_states in_channel num_accepting_states num_states]
    reads accepting states line by line into a string list.
    Raise exception if the length of the list <> number of accepting states. *)
let read_all_accepting_states in_channel num_accepting_states num_states = 
    let states = ref [] in
        for i = 0 to num_accepting_states - 1 do
            states := 
                (!states) @ 
                    [read_accepting_state (input_line in_channel) num_states] 
        done;
        if List.length !states <> num_accepting_states then
	    raise (Failure "Mismatched number of accpeting states")
        else
            !states

(** [dedulicate lst] a helper function
    removes duplicate elements from [lst] *)
let deduplicate lst =
    let rec aux lst acc = 
        match lst with
            | [] -> acc
	    | h :: t -> 
                if List.mem h acc then
		    aux t acc
		else
		    aux t (acc @ [h])
    in aux lst []
   
(** [read_symbols line] reads symbols into a list of string of length one *)
let read_symbols line num_symbols =
    print_endline line;
    let lst = ref [] in 
        if String.length line < 1 then
	    raise (Invalid_argument "Empty line")
        else
            for i = 0 to String.length line - 1 do
                lst := (!lst) @ [Char.escaped line.[i]]
            done;
            if List.length !lst <> num_symbols then
		raise (Invalid_argument "Mismatched number of characters with that of alphabet")
            else
                (deduplicate !lst)

(** [is_valid_symbol symbol alphabet] a helper function
    checks if [symbol] is in [alphabet] *)
let is_valid_symbol symbol alphabet = 
    if List.mem symbol alphabet then
	true
    else
        raise (Failure "Invalid symbol")

(** [trim_element lst] a helper function trims each element in [lst] *)
let rec trim_element lst = 
    match lst with
        | [] -> []
        | h :: t -> 
            (* None or more than one space ? *)
            if String.trim h = "" 
                then " " :: trim_element t 
            else
                (String.trim h) :: trim_element t

(** [is_valid_transition_case case] a helper function
    checks if [case] is valid i.e. (state -> symbol -> state *)
let is_valid_transition_case case alphabet num_states = 
    match case with
        | source::symbol::terminal::[] ->
             (is_valid_state (int_of_string source) num_states) &&
             (is_valid_symbol symbol alphabet) &&
             (is_valid_state (int_of_string terminal) num_states)
        | _ -> false
   
(** [read_transition_case line] a helper function
    reads [line] into a list of length 3
    i.e [state, symbol, state].
    Raise exception if invalid transition case. *)
let read_transition_case line alphabet num_states =
    print_endline line;
    (* Split the transition case line by ',' *)
    let triple = ref (String.split_on_char ',' (String.trim line)) in
        triple := trim_element (!triple);
        if is_valid_transition_case (!triple) alphabet num_states then
	    !triple
        else
            raise (Invalid_argument "Invalid transition case")

(** [read_all_transition_cases in_channel num_cases]
    reads all transitino cases into a transition case list.
    Raise exception if the length of the cast list matches with number of cases *)
let read_all_transition_cases in_channel num_cases alphabet num_states = 
    let cases = ref [] in
        for i = 0 to num_cases - 1 do
            cases := !cases @ [read_transition_case (input_line in_channel) alphabet num_states]
        done;
        if List.length !cases <> num_cases then
	    raise (Failure "Mismatched number of transition cases")
        else
            !cases

(** [is_valid_epsilon_case case num_states] a helper function
    checks if [case] is valid (old state -> new state) *)
let is_valid_epsilon_case case num_states = 
    match case with
        | source :: terminal :: [] ->
            (is_valid_state (int_of_string source) num_states) &&
            (is_valid_state (int_of_string terminal) num_states)
        | _ -> false

(** [read_epsilon_case line] a helper function
    reads [line] into a list of length 2 i.e [old state, new state].
    Raise exception if invalid epsilon case. *)
let read_epsilon_case line num_states = 
    print_endline line;
    (* Split the epsilon case line by ',' *)
    let tuple = ref (String.split_on_char ',' (String.trim line)) in
        tuple := trim_element (!tuple);
        if is_valid_epsilon_case (!tuple) num_states then
	    !tuple
        else
            raise (Invalid_argument "Invalid epsilon case")

(** [read_all_epsilon_cases in_channel num_cases num_states]
    reads all epsilon cases into a epsilon case list.
    Raise exception if the length of the case list mismatches the number of epsilon cases. *)
let read_all_epsilon_cases in_channel num_cases num_states = 
    let cases = ref [] in
        for i = 0 to num_cases - 1 do
            cases := !cases @[read_epsilon_case (input_line in_channel) num_states]
        done;
    if List.length !cases <> num_cases then
	raise (Failure "Mismatched number of epsilon cases")
    else
        !cases

(** [read_input line] reads input string into a list of string of length one.
    Raise exception if char not in alphabet. *)
let read_input line alphabet =
    print_endline line;
    let input = ref [] in
        for i = 0 to String.length line - 1 do
            if is_valid_symbol (Char.escaped line.[i]) alphabet then
		input := (Char.escaped line.[i]) :: (!input)
            else
                raise (Invalid_argument "Invalid character")
        done; 
        List.rev (!input)

(** [dedup_sort lst] a helper function removes duplicates and sort [lst]. *)
let dedup_sort lst = 
    let int_list = List.map int_of_string lst in
    let sorted_list = List.sort_uniq compare int_list in
    List.map string_of_int sorted_list

(** [regular_transition cases old_state symbol] a helper function
    finds a set of new states given [old_state] and [symbol]. *)
let regular_transition cases old_state symbol =
    let rec aux acc cases old_state symbol =
        match cases with
            | [] -> dedup_sort acc
	    | h :: t -> 
		if ((List.nth h 0 = old_state) && (List.nth h 1 = symbol)) then
		    aux (List.nth h 2 :: acc) t old_state symbol
		else
		    aux acc t old_state symbol
    in aux [] cases old_state symbol

(** [epsilon_transition cases old_state] a help function
    finds a set of new states given [old_state] via epsilon transition. *)
let epsilon_transition cases old_state =
    let rec aux acc cases old_state = 
	match cases with
	    | [] -> dedup_sort acc
	    | h :: t ->
		if List.nth h 0 = old_state then
		    aux (List.nth h 1 :: acc) t old_state
		else
		    aux acc t old_state
    in aux [] cases old_state

(** [move cases states symbol] a helper function
    finds a set of states to which there is a transition
    on [symbol] from [states]. *)
let move cases states symbol = 
    let rec aux acc cases states symbol = 
	match states with
	    | [] -> dedup_sort acc
	    | state :: t -> aux (acc @ (regular_transition cases state symbol)) cases t symbol
    in aux [] cases states symbol

(** [epsilon_closure_inner acc cases states] a function interatively
    finds a set of states from [states] on epsilon-transitions alone. *)
let rec epsilon_closure_inner acc cases states = 
    match states with
        | [] -> dedup_sort acc
        | state :: t -> epsilon_closure_inner (acc @ (epsilon_transition cases state)) cases t

(** [epsilon_closure cases states] a function iteratively
    finds a set of states from [states] on epsilon-transitions alone. *)
let epsilon_closure cases states =
    let states' = ref states in
    let final_states = ref states in
    while (!states' <> []) do
        states' := epsilon_closure_inner [] cases !states';
	final_states := !final_states @ !states'
    done;
    dedup_sort !final_states

(** [print_set set] a helper function print element(s) in [set]. *)
let print_set set =
    let rec print_elements set =
        match set with
	    | [] -> ()
	    | [x] -> print_string x
	    | h :: t -> print_string h; print_string ","; print_elements t
    in 
    print_string "{";
    print_elements set;
    print_string "}"
 
(** [check_result acc_states states] a helper function
    checks if [states] contain the [acc_state]. *)
let rec check_result acc_states states =
    match acc_states with
	| [] -> false
        | h :: t -> List.mem h states || check_result t states
            
(** [parse_input transition_cases epsilon_cases input] reads input and return final result ("accepted | rejected"). *)
let parse_input transition_cases epsilon_cases acc_states input = 
    let init_states = epsilon_closure epsilon_cases ["0"] in print_set init_states;
    let inter_states = ref init_states in
    let symbol = ref "" in
        for i = 0 to List.length input - 1 do
	    symbol := List.nth input i; 
	    inter_states := move transition_cases !inter_states !symbol;
	    inter_states := epsilon_closure epsilon_cases !inter_states;
	    print_string "->";
	    print_string !symbol;
	    print_string "->";
	    print_set !inter_states
	done;
        if check_result acc_states !inter_states then
	    print_endline " accepted"
	else
	    print_endline " rejected"		
                                          
(** [read_nfa] reads NFA descriptions and an input string
    and save them into the above variables, respectively *)
let read_nfa in_channel = 
    number_of_states := read_num (input_line in_channel);
    number_of_accepting_states := read_num (input_line in_channel);
    accepting_states := read_all_accepting_states in_channel !number_of_accepting_states !number_of_states;
    number_of_symbols := read_num (input_line in_channel);
    alphabet := read_symbols (input_line in_channel) !number_of_symbols;
    number_of_transition_cases := read_num (input_line in_channel);
    transition_cases := read_all_transition_cases in_channel !number_of_transition_cases !alphabet !number_of_states;
    number_of_epsilon_cases := read_num (input_line in_channel);
    epsilon_cases := read_all_epsilon_cases in_channel !number_of_epsilon_cases !number_of_states;
    input_to_nfa := read_input (input_line in_channel) !alphabet

let simulate_nfa filename = 
    let in_channel = open_in filename in
	read_nfa in_channel;
        parse_input !transition_cases !epsilon_cases !accepting_states !input_to_nfa;
	close_in in_channel
