(** CS 440, F20
  * Project1: DFA Simulator
  * Christopher Weicong Hong implemented reading DFA description and input string functionality
  * Daniel Jablonski implemented parsing input string and outputing final state functionality
  * Reza Shahbazi Debugged and tested the code through check-correctness & code-tracing method
  *)

(** Ref variables to store DFA descriptions *)
let number_of_states = ref 0
let number_of_accepting_states = ref 0
let accepting_states = ref []
let number_of_symbols = ref 0
let alphabet = ref []
let number_of_transition_cases = ref 0
let transition_cases = ref []
let input_to_dfa = ref []
let state = ref 0

(** Empty line exception *)
exception Empty of string
let empty = "Empty line detected. "

(** [read_num line description] reads a number from a line.
    Raise exception if the line contains invalid description. *)
let read_num line description =
    (* Print description *)
    print_endline line;
    (* Remove leading and trailing whitespace *) 
    let line' = String.trim line in
        (* Empty line or negative number ? *)
        if String.length line' = 0
            then raise (Empty (empty ^ "Invalid description: " ^ description))
        else if (int_of_string line') < 0
            then raise (Failure "Invalid number of states")
        else
            (* Return int format of string number *)
            int_of_string line'

(** [is_valid_state state num_states] a helper function 
    checks if state is in range 0 to number of states - 1 *)
let is_valid_state state num_states = 
    if (state >= 0) && (state < num_states)
        then true
    else
        raise (Failure "Invalid state")

(** [read_accpeting_state line] a helper fuinction 
    reads acceting state description.
    Raise exception if the line contains invalid description. *)
let read_accepting_state line description num_states =
    let state = read_num line description in
        if not (is_valid_state state num_states)
            then raise (Failure "Invalid state. It should be in range 0 to N-1")
        else
            (* Return string format of the int number *)
            string_of_int state

(** [read_all_accepting_states in_channel num_accepting_states num_states]
    read accepting states line by line into a string list.
    Raise exception if the length of the list <> number of accepting states. *)
let read_all_accepting_states in_channel num_accepting_states num_states = 
    let states = ref [] in
        for i = 0 to num_accepting_states - 1 do
            states := 
                (!states) @ 
                    [read_accepting_state (input_line in_channel) "accepting state" num_states] 
        done;
        if List.length !states <> num_accepting_states
            then raise (Failure "Number of accpeting states mismatched")
        else
            !states
   
(** [read_symbols line] reads symbols into a list of string of length one *)
let read_symbols line num_symbols =
    print_endline line;
    let lst = ref [] in 
        if String.length line < 1
            then raise (Failure "Invalid number of alphabet (<1)")
        else
            for i = 0 to String.length line - 1 do
                lst := (!lst) @ [Char.escaped line.[i]]
            done;
            if List.length !lst <> num_symbols
                then raise (Failure "number of symbols mismatched with that of alphabet")
            else
                !lst

(** [is_valid_symbol symbol alphabet] a helper function check if symbol is in the alphabet *)
let is_valid_symbol symbol alphabet = 
    if List.mem symbol alphabet
        then true
    else
        raise (Failure "Invalid symbol")

(** [trim_element lst] a helper function trim each element in lst *)
let rec trim_element lst = 
    match lst with
        | [] -> []
        | h :: t -> 
            (* None or more than one space ? *)
            if String.trim h = "" 
                then " " :: trim_element t 
            else
                (String.trim h) :: trim_element t

(** [is_valid_transition_case lst] a helper function
    checks if lst contains valid transition case elements
    (state -> symbol -> state *)
let is_valid_transition_case lst = 
    match lst with
        | source::symbol::terminal::[] ->
             (is_valid_state (int_of_string source) (!number_of_states)) &&
             (is_valid_symbol symbol (!alphabet)) &&
             (is_valid_state (int_of_string terminal) (!number_of_states))
        | _ -> false
   
(** [read_transition_case line] a helper function
    reads a transition case into a list of length 3
    i.e [state, symbol, state].
    Raise exception if invalid transition case. *)
let read_transition_case line =
    print_endline line;
    (* Split the transition case line by ',' *)
    let triple = ref (String.split_on_char ',' (String.trim line)) in
        triple := trim_element (!triple);
        if is_valid_transition_case (!triple)
            then !triple
        else
            raise (Failure "Invalid argument: transition case")

(** [read_all_transition_cases in_channel num_cases]
    reads all transitino cases into a transition case list.
    Raise exception if the length of the cast list matches with number of cases *)
let read_all_transition_cases in_channel num_cases = 
    let cases = ref [] in
        for i = 0 to num_cases - 1 do
            cases := !cases @ [read_transition_case (input_line in_channel)]
        done;
        if List.length !cases <> num_cases
            then raise (Failure "Number of transition cases mismatched")
        else
            !cases

(** [read_input line] reads input string into a list of string of length one.
    Raise exception if char of the input is not in alphabet. *)
let read_input line =
    print_endline line;
    let input = ref [] in
        for i = 0 to String.length line - 1 do
            if is_valid_symbol (Char.escaped line.[i]) !alphabet
                then input := (Char.escaped line.[i]) :: (!input)
            else
                raise (Failure "Invalid argument: input string to the DFA")
        done; List.rev (!input)

(** [transition transition_cases input_char] recursively moves through the list of transition cases
    and transition case against the provided character. If it evaluates to true then 
    it will print the letter and the new state *)
let rec transition transition_cases input_char = match transition_cases with 
    | [] -> ()
    | h :: t -> match h with
        | [] -> ()
        | source :: symbol :: terminal::[] ->
            if (int_of_string source) = (!state) && symbol = input_char then begin 
                state := int_of_string terminal; 
                Printf.printf "%s %d " input_char (!state); 
            end else transition t input_char

(** [loop_input input] loops through the list of one character strings (Input string) and 
    checks the transition cases against the character and when it gets to the end of the 
    list it determines if the final state is one of the accepted states and will print out
    the result *)
let rec loop_input input = match input with
    | [] -> if List.mem (string_of_int !state) !accepting_states then Printf.printf "\nAccepted\n" else  Printf.printf "\nRejected\n"
    | h::t -> begin
        transition !transition_cases h;
        loop_input t;
    end;;
                                       
(** [read_dfa] reads DFA descriptions and an input string
    and save them into the above variables, respectively *)
let read_dfa in_channel = 
    number_of_states := read_num (input_line in_channel) "number of states";
    number_of_accepting_states := read_num (input_line in_channel) "number of accepting states";
    accepting_states := read_all_accepting_states in_channel !number_of_accepting_states !number_of_states;
    number_of_symbols := read_num (input_line in_channel) "number of symbols";
    alphabet := read_symbols (input_line in_channel) !number_of_symbols;
    number_of_transition_cases := read_num (input_line in_channel) "number of transition cases";
    transition_cases := read_all_transition_cases in_channel !number_of_transition_cases;
    input_to_dfa := read_input (input_line in_channel)

(** [simulate_dfa filename] reads filename and print out DFA description and the trace to the DFA *)
let simulate_dfa filename = 
    let in_channel = open_in filename in
    	read_dfa in_channel;
    	state := 0;
    	Printf.printf "%d " !state;
    	loop_input !input_to_dfa	
