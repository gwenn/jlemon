/*
** 2000-05-29
**
** The author disclaims copyright to this source code.  In place of
** a legal notice, here is a blessing:
**
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
**
*************************************************************************
** Driver template for the LEMON parser generator.
**
** The "lemon" program processes an LALR(1) input grammar file, then uses
** this template to construct a parser.  The "lemon" program inserts text
** at each "%%" line.  Also, any "P-a-r-s-e" identifer prefix (without the
** interstitial "-" characters) contained in this template is changed into
** the value of the %name directive from the grammar.  Otherwise, the content
** of this template is copied straight through into the generate parser
** source file.
**
** The following is the concatenation of all %include directives from the
** input grammar file:
*/
/************ Begin %include sections from the grammar ************************/
%%
/**************** End of %include directives **********************************/
/* These constants specify the various numeric values for terminal symbols
** in a format understandable to "makeheaders".  This section is blank unless
** "lemon" is run with the "-m" command-line option.
***************** Begin makeheaders token definitions *************************/
%%
/**************** End makeheaders token definitions ***************************/

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EmptyStackException;
#ifndef NDEBUG
import java.lang.StringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
#endif /* NDEBUG */

public class yyParser {
#ifndef NDEBUG
  private static final Logger logger = LoggerFactory.getLogger(yyParser.class);
#endif /* NDEBUG */

/* The next sections is a series of control #defines.
** various aspects of the generated parser.
**    YYCODETYPE         is the data type used to store the integer codes
**                       that represent terminal and non-terminal symbols.
**                       "unsigned char" is used if there are fewer than
**                       256 symbols.  Larger types otherwise.
**    YYNOCODE           is a number of type YYCODETYPE that is not used for
**                       any terminal or nonterminal symbol.
**    YYFALLBACK         If defined, this indicates that one or more tokens
**                       (also known as: "terminal symbols") have fall-back
**                       values which should be used if the original symbol
**                       would not parse.  This permits keywords to sometimes
**                       be used as identifiers, for example.
**    YYACTIONTYPE       is the data type used for "action codes" - numbers
**                       that indicate what to do in response to the next
**                       token.
**    ParseTOKENTYPE     is the data type used for minor type for terminal
**                       symbols.  Background: A "minor type" is a semantic
**                       value associated with a terminal or non-terminal
**                       symbols.  For example, for an "ID" terminal symbol,
**                       the minor type might be the name of the identifier.
**                       Each non-terminal can have a different minor type.
**                       Terminal symbols all have the same minor type, though.
**                       This macros defines the minor type for terminal 
**                       symbols.
**    YYMINORTYPE        is the data type used for all minor types.
**                       This is typically a union of many types, one of
**                       which is ParseTOKENTYPE.  The entry in the union
**                       for terminal symbols is called "yy0".
**    YYSTACKDEPTH       is the maximum depth of the parser's stack.  If
**                       zero the stack is dynamically sized using realloc()
**    ParseARG_SDECL     A static variable declaration for the %extra_argument
**    ParseARG_PDECL     A parameter declaration for the %extra_argument
**    ParseARG_STORE     Code to store %extra_argument into yypParser
**    ParseARG_FETCH     Code to extract %extra_argument from yypParser
**    YYERRORSYMBOL      is the code number of the error symbol.  If not
**                       defined, then do no error processing.
**    YYNSTATE           the combined number of states.
**    YYNRULE            the number of rules in the grammar
**    YY_MAX_SHIFT       Maximum value for shift actions
**    YY_MIN_SHIFTREDUCE Minimum value for shift-reduce actions
**    YY_MAX_SHIFTREDUCE Maximum value for shift-reduce actions
**    YY_MIN_REDUCE      Maximum value for reduce actions
**    YY_ERROR_ACTION    The yy_action[] code for syntax error
**    YY_ACCEPT_ACTION   The yy_action[] code for accept
**    YY_NO_ACTION       The yy_action[] code for no-op
*/
#ifndef INTERFACE
# define INTERFACE 1
#endif
/************* Begin control #defines *****************************************/
%%
/************* End control #defines *******************************************/

/* Define the yytestcase() macro to be a no-op if is not already defined
** otherwise.
**
** Applications can choose to define yytestcase() in the %include section
** to a macro that can assist in verifying code coverage.  For production
** code the yytestcase() macro should be turned off.  But it is useful
** for testing.
*/
#ifndef yytestcase
# define yytestcase(X)
#endif


/* Next are the tables used to determine what action to take based on the
** current state and lookahead token.  These tables are used to implement
** functions that take a state number and lookahead value and return an
** action integer.  
**
** Suppose the action integer is N.  Then the action is determined as
** follows
**
**   0 <= N <= YY_MAX_SHIFT             Shift N.  That is, push the lookahead
**                                      token onto the stack and goto state N.
**
**   N between YY_MIN_SHIFTREDUCE       Shift to an arbitrary state then
**     and YY_MAX_SHIFTREDUCE           reduce by rule N-YY_MIN_SHIFTREDUCE.
**
**   N between YY_MIN_REDUCE            Reduce by rule N-YY_MIN_REDUCE
**     and YY_MAX_REDUCE
**
**   N == YY_ERROR_ACTION               A syntax error has occurred.
**
**   N == YY_ACCEPT_ACTION              The parser accepts its input.
**
**   N == YY_NO_ACTION                  No such action.  Denotes unused
**                                      slots in the yy_action[] table.
**
** The action table is constructed as a single large table named yy_action[].
** Given state S and lookahead X, the action is computed as either:
**
**    (A)   N = yy_action[ yy_shift_ofst[S] + X ]
**    (B)   N = yy_default[S]
**
** The (A) formula is preferred.  The B formula is used instead if:
**    (1)  The yy_shift_ofst[S]+X value is out of range, or
**    (2)  yy_lookahead[yy_shift_ofst[S]+X] is not equal to X, or
**    (3)  yy_shift_ofst[S] equal YY_SHIFT_USE_DFLT.
** (Implementation note: YY_SHIFT_USE_DFLT is chosen so that
** YY_SHIFT_USE_DFLT+X will be out of range for all possible lookaheads X.
** Hence only tests (1) and (2) need to be evaluated.)
**
** The formulas above are for computing the action when the lookahead is
** a terminal symbol.  If the lookahead is a non-terminal (as occurs after
** a reduce action) then the yy_reduce_ofst[] array is used in place of
** the yy_shift_ofst[] array and YY_REDUCE_USE_DFLT is used in place of
** YY_SHIFT_USE_DFLT.
**
** The following are the tables generated in this section:
**
**  yy_action[]        A single table containing all actions.
**  yy_lookahead[]     A table containing the lookahead for each entry in
**                     yy_action.  Used to detect hash collisions.
**  yy_shift_ofst[]    For each state, the offset into yy_action for
**                     shifting terminals.
**  yy_reduce_ofst[]   For each state, the offset into yy_action for
**                     shifting non-terminals after a reduce.
**  yy_default[]       Default action for each state.
**
*********** Begin parsing tables **********************************************/
%%
/********** End of lemon-generated parsing tables *****************************/

/* The next table maps tokens (terminal symbols) into fallback tokens.  
** If a construct like the following:
** 
**      %fallback ID X Y Z.
**
** appears in the grammar, then ID becomes a fallback token for X, Y,
** and Z.  Whenever one of the tokens X, Y, or Z is input to the parser
** but it does not parse, the type of the token is changed to ID and
** the parse is retried before an error is thrown.
**
** This feature can be used, for example, to cause some keywords in a language
** to revert to identifiers if they keyword does not apply in the context where
** it appears.
*/
#ifdef YYFALLBACK
private static final YYCODETYPE yyFallback[] = {
%%
};
#endif /* YYFALLBACK */

/* The following structure represents a single element of the
** parser's stack.  Information stored includes:
**
**   +  The state number for the parser at this level of the stack.
**
**   +  The value of the token stored at this level of the stack.
**      (In other words, the "major" token.)
**
**   +  The semantic value stored at this level of the stack.  This is
**      the information used by the action routines in the grammar.
**      It is sometimes called the "minor" token.
**
** After the "shift" half of a SHIFTREDUCE action, the stateno field
** actually contains the reduce action for the second half of the
** SHIFTREDUCE.
*/
private static class yyStackEntry {
  YYACTIONTYPE stateno;  /* The state-number, or reduce action in SHIFTREDUCE */
  YYCODETYPE major;      /* The major token value.  This is the code
                         ** number for the token at this stack level */
  YYMINORTYPE minor;     /* The user-supplied minor token value.  This
                         ** is the value of the token  */
  yyStackEntry() {
    minor = new YYMINORTYPE();
  }
};

/* The state of the parser is completely contained in an instance of
** the following structure */
//  yyStackEntry *yytos;          /* Pointer to top element of the stack */
#ifdef YYTRACKMAXSTACKDEPTH
  private int yyhwm;                    /* High-water mark of the stack */
#endif
#ifndef YYNOERRORRECOVERY
  private int yyerrcnt;                 /* Shifts left before out of the error */
#endif
  ParseARG_SDECL                /* A place to hold %extra_argument */
#if YYSTACKDEPTH<=0
  //int yystksz;                  /* Current side of the stack */
  private final Stack<yyStackEntry> yystack;        /* The parser's stack */
  //yyStackEntry yystk0;          /* First stack entry */
#else
  private final Stack<yyStackEntry> yystack;  /* The parser's stack */
#endif

#ifndef NDEBUG
#endif /* NDEBUG */

#ifndef NDEBUG
/* 
** Turn parser tracing on by giving a stream to which to write the trace
** and a prompt to preface each trace message.  Tracing is turned off
** by making either argument NULL 
**
** Inputs:
** <ul>
** <li> A FILE* to which trace output should be written.
**      If NULL, then tracing is turned off.
** <li> A prefix string written at the beginning of every
**      line of trace output.  If NULL, then tracing is
**      turned off.
** </ul>
**
** Outputs:
** None.
*/
#endif /* NDEBUG */

#ifndef NDEBUG
/* For tracing shifts, the names of all terminals and nonterminals
** are required.  The following table supplies these names */
private static final String yyTokenName[] = {
%%
};
#endif /* NDEBUG */

#ifndef NDEBUG
/* For tracing reduce actions, the names of all rules are required.
*/
private static final String yyRuleName[] = {
%%
};
#endif /* NDEBUG */


#if YYSTACKDEPTH<=0
/*
** Try to increase the size of the parser stack.  Return the number
** of errors.  Return 0 on success.
*/
private boolean yyGrowStack(){
#ifndef NDEBUG
      logger.debug("Stack grows from {} entries.",
              yystack.size());
#endif
  return false;
}
#endif

/* Datatype of the argument to the memory allocated passed as the
** second argument to ParseAlloc() below.  This can be changed by
** putting an appropriate #define in the %include section of the input
** grammar.
*/
#ifndef YYMALLOCARGTYPE
#endif

/* Initialize a new parser that has already been allocated.
*/
public yyParser() {
#ifdef YYTRACKMAXSTACKDEPTH
  yyhwm = 0;
#endif
#if YYSTACKDEPTH<=0
  yystack = new Stack<>(100);
#else
  yystack = new Stack<>(YYSTACKDEPTH);
#endif
#ifndef YYNOERRORRECOVERY
  yyerrcnt = -1;
#endif
  yystack.push(new yyStackEntry());
}

#ifndef Parse_ENGINEALWAYSONSTACK
/* 
** This function allocates a new parser.
** The only argument is a pointer to a function which works like
** malloc.
**
** Inputs:
** A pointer to the function used to allocate memory.
**
** Outputs:
** A pointer to a parser.  This pointer is used in subsequent calls
** to Parse and ParseFree.
*/
#endif /* Parse_ENGINEALWAYSONSTACK */


/* The following function deletes the "minor type" or semantic value
** associated with a symbol.  The symbol can be either a terminal
** or nonterminal. "yymajor" is the symbol code, and "yypminor" is
** a pointer to the value to be deleted.  The code used to do the 
** deletions is derived from the %destructor and/or %token_destructor
** directives of the input grammar.
*/
private void yy_destructor(
  YYCODETYPE yymajor,     /* Type code for object to destroy */
  YYMINORTYPE yypminor   /* The object to be destroyed */
){
  switch( yymajor ){
    /* Here is inserted the actions which take place when a
    ** terminal or non-terminal is destroyed.  This can happen
    ** when the symbol is popped from the stack during a
    ** reduce or during error processing or when a parser is 
    ** being destroyed before it is finished parsing.
    **
    ** Note: during a reduce, the only symbols destroyed are those
    ** which appear on the RHS of the rule, but which are *not* used
    ** inside the C code.
    */
/********* Begin destructor definitions ***************************************/
%%
/********* End destructor definitions *****************************************/
    default:  break;   /* If no destructor action specified: do nothing */
  }
}

/*
** Pop the parser's stack once.
**
** If there is a destructor routine associated with the token which
** is popped from the stack, then call it.
*/
private void yy_pop_parser_stack(){
  yyStackEntry yytos;
  yytos = yystack.pop();
#ifndef NDEBUG
    logger.debug("Popping {}",
      yyTokenName[yytos.major]);
#endif
  yy_destructor(yytos.major, yytos.minor);
}

/*
** Clear all secondary memory allocations from the parser
*/
public void ParseFinalize(){
  while( !yystack.isEmpty() ) yy_pop_parser_stack();
}

#ifndef Parse_ENGINEALWAYSONSTACK
/* 
** Deallocate and destroy a parser.  Destructors are called for
** all stack elements before shutting the parser down.
**
** If the YYPARSEFREENEVERNULL macro exists (for example because it
** is defined in a %include section of the input grammar) then it is
** assumed that the input pointer is never NULL.
*/
#endif /* Parse_ENGINEALWAYSONSTACK */

/*
** Return the peak depth of the stack for a parser.
*/
#ifdef YYTRACKMAXSTACKDEPTH
public int ParseStackPeak(){
  return yyhwm;
}
#endif

/*
** Find the appropriate action for a parser given the terminal
** look-ahead token iLookAhead.
*/
private int yy_find_shift_action(
  YYCODETYPE iLookAhead     /* The look-ahead token */
){
  int i;
  yyStackEntry yytos = yystack.peek();
  int stateno = yytos.stateno;
 
  if( stateno>=YY_MIN_REDUCE ) return stateno;
  assert( stateno <= YY_SHIFT_COUNT );
  do{
    i = yy_shift_ofst[stateno];
    assert( iLookAhead!=YYNOCODE );
    i += iLookAhead;
    if( i<0 || i>=YY_ACTTAB_COUNT || yy_lookahead[i]!=iLookAhead ){
#ifdef YYFALLBACK
      YYCODETYPE iFallback;            /* Fallback token */
      if( iLookAhead<yyFallback.length
             && (iFallback = yyFallback[iLookAhead])!=0 ){
#ifndef NDEBUG
          logger.debug("FALLBACK {} => {}",
             yyTokenName[iLookAhead], yyTokenName[iFallback]);
#endif
        assert( yyFallback[iFallback]==0 ); /* Fallback loop must terminate */
        iLookAhead = iFallback;
        continue;
      }
#endif
#ifdef YYWILDCARD
      {
        int j = i - iLookAhead + YYWILDCARD;
        if( 
#if YY_SHIFT_MIN+YYWILDCARD<0
          j>=0 &&
#endif
#if YY_SHIFT_MAX+YYWILDCARD>=YY_ACTTAB_COUNT
          j<YY_ACTTAB_COUNT &&
#endif
          yy_lookahead[j]==YYWILDCARD && iLookAhead>0
        ){
#ifndef NDEBUG
            logger.debug("WILDCARD {} => {}",
               yyTokenName[iLookAhead],
               yyTokenName[YYWILDCARD]);
#endif /* NDEBUG */
          return yy_action[j];
        }
      }
#endif /* YYWILDCARD */
      return yy_default[stateno];
    }else{
      return yy_action[i];
    }
  }while(true);
}

/*
** Find the appropriate action for a parser given the non-terminal
** look-ahead token iLookAhead.
*/
private static int yy_find_reduce_action(
  int stateno,              /* Current state number */
  YYCODETYPE iLookAhead     /* The look-ahead token */
){
  int i;
#ifdef YYERRORSYMBOL
  if( stateno>YY_REDUCE_COUNT ){
    return yy_default[stateno];
  }
#else
  assert( stateno<=YY_REDUCE_COUNT );
#endif
  i = yy_reduce_ofst[stateno];
  assert( i!=YY_REDUCE_USE_DFLT );
  assert( iLookAhead!=YYNOCODE );
  i += iLookAhead;
#ifdef YYERRORSYMBOL
  if( i<0 || i>=YY_ACTTAB_COUNT || yy_lookahead[i]!=iLookAhead ){
    return yy_default[stateno];
  }
#else
  assert( i>=0 && i<YY_ACTTAB_COUNT );
  assert( yy_lookahead[i]==iLookAhead );
#endif
  return yy_action[i];
}

/*
** The following routine is called if the stack overflows.
*/
private void yyStackOverflow(){
#ifndef NDEBUG
     logger.error("Stack Overflow!");
#endif
   while( !yystack.isEmpty() ) yy_pop_parser_stack();
   /* Here code is inserted which will execute if the parser
   ** stack every overflows */
/******** Begin %stack_overflow code ******************************************/
%%
/******** End %stack_overflow code ********************************************/
}

/*
** Print tracing information for a SHIFT action
*/
#ifndef NDEBUG
private void yyTraceShift(int yyNewState){
    if( yyNewState<YYNSTATE ){
      logger.trace("Shift '{}', go to state {}",
         yyTokenName[yystack.peek().major],
         yyNewState);
    }else{
      logger.trace("Shift '{}'",
         yyTokenName[yystack.peek().major]);
    }
}
#else
# define yyTraceShift(X,Y)
#endif

/*
** Perform a shift action.
*/
private void yy_shift(
  int yyNewState,               /* The new state to shift in */
  int yyMajor,                  /* The major token to shift in */
  ParseTOKENTYPE yyMinor        /* The minor token to shift in */
){
  yyStackEntry yytos = new yyStackEntry();
#ifdef YYTRACKMAXSTACKDEPTH
  if( yystack.size()>yyhwm ){
    yyhwm++;
    assert( yyhwm == yystack.size()+1 );
  }
#endif
#if YYSTACKDEPTH>0 
  if( yystack.size() >= YYSTACKDEPTH ){
    yyStackOverflow();
    return;
  }
#else
  if( false ){
    if( yyGrowStack() ){
      yyStackOverflow();
      return;
    }
  }
#endif
  if( yyNewState > YY_MAX_SHIFT ){
    yyNewState += YY_MIN_REDUCE - YY_MIN_SHIFTREDUCE;
  }
  yystack.push(yytos);
  yytos.stateno = (YYACTIONTYPE)yyNewState;
  yytos.major = (YYCODETYPE)yyMajor;
  yytos.minor.yy0(yyMinor);
  yyTraceShift(yyNewState);
}

/* The following table contains information about every rule that
** is used during the reduce.
*/
private static class ruleInfoEntry {
  YYCODETYPE lhs;         /* Symbol on the left-hand side of the rule */
  byte nrhs;     /* Number of right-hand side symbols in the rule */

  ruleInfoEntry(int lhs, int nrhs) {
    this.lhs = (YYCODETYPE)lhs;
    this.nrhs = (byte)nrhs;
  }
}
private static final ruleInfoEntry
 yyRuleInfo[] = {
%%
};

/*
** Perform a reduce action and the shift that must immediately
** follow the reduce.
*/
private void yy_reduce(
  int yyruleno        /* Number of the rule by which to reduce */
){
  int yygoto;                     /* The next state */
  int yyact;                      /* The next action */
  yyStackEntry yymsp;             /* The top of the parser's stack */
  int yysize;                     /* Amount to pop the stack */
  yymsp = yystack.peek();
#ifndef NDEBUG
  if( yyruleno<yyRuleName.length ){
    yysize = yyRuleInfo[yyruleno].nrhs;
    logger.debug("Reduce [{}], go to state {}.",
      yyRuleName[yyruleno], yystack.peek(-yysize).stateno);
  }
#endif /* NDEBUG */

  /* Check that the stack is large enough to grow by a single entry
  ** if the RHS of the rule is empty.  This ensures that there is room
  ** enough on the stack to push the LHS value */
  if( yyRuleInfo[yyruleno].nrhs==0 ){
#ifdef YYTRACKMAXSTACKDEPTH
    if( yystack.size()>yyhwm ){
      yyhwm++;
      assert( yyhwm == yystack.size()+1);
    }
#endif
#if YYSTACKDEPTH>0 
    if( yystack.size() >= YYSTACKDEPTH-1 ){
      yyStackOverflow();
      return;
    }
#else
    if( false ){
      if( yyGrowStack() ){
        yyStackOverflow();
        return;
      }
    }
#endif
  }

  YYMINORTYPE yylhsminor = new YYMINORTYPE();
  switch( yyruleno ){
  /* Beginning here are the reduction cases.  A typical example
  ** follows:
  **   case 0:
  **  #line <lineno> <grammarfile>
  **     { ... }           // User supplied code
  **  #line <lineno> <thisfile>
  **     break;
  */
/********** Begin reduce actions **********************************************/
%%
/********** End reduce actions ************************************************/
  };
  assert( yyruleno<yyRuleInfo.length );
  yygoto = yyRuleInfo[yyruleno].lhs;
  yysize = yyRuleInfo[yyruleno].nrhs;
  yyact = yy_find_reduce_action(yystack.peek(-yysize).stateno,(YYCODETYPE)yygoto);
  if( yyact <= YY_MAX_SHIFTREDUCE ){
    if( yyact>YY_MAX_SHIFT ){
      yyact += YY_MIN_REDUCE - YY_MIN_SHIFTREDUCE;
    }

    yystack.popN(yysize-1);
    yymsp = yystack.peek();
    yymsp.stateno = (YYACTIONTYPE)yyact;
    yymsp.major = (YYCODETYPE)yygoto;
    yyTraceShift(yyact);
  }else{
    assert( yyact == YY_ACCEPT_ACTION );
    yystack.popN(yysize);
    yy_accept();
  }
}

/*
** The following code executes when the parse fails
*/
#ifndef YYNOERRORRECOVERY
private void yy_parse_failed(
){
#ifndef NDEBUG
    logger.error("Fail!");
#endif
  while( !yystack.isEmpty() ) yy_pop_parser_stack();
  /* Here code is inserted which will be executed whenever the
  ** parser fails */
/************ Begin %parse_failure code ***************************************/
%%
/************ End %parse_failure code *****************************************/
}
#endif /* YYNOERRORRECOVERY */

/*
** The following code executes when a syntax error first occurs.
*/
private void yy_syntax_error(
  int yymajor,                   /* The major type of the error token */
  ParseTOKENTYPE yyminor         /* The minor type of the error token */
){
#define TOKEN yyminor
/************ Begin %syntax_error code ****************************************/
%%
/************ End %syntax_error code ******************************************/
}

/*
** The following is executed when the parser accepts
*/
private void yy_accept(
){
#ifndef NDEBUG
    logger.debug("Accept!");
#endif
#ifndef YYNOERRORRECOVERY
  yyerrcnt = -1;
#endif
  assert( yystack.size() == 1 );
  /* Here code is inserted which will be executed whenever the
  ** parser accepts */
/*********** Begin %parse_accept code *****************************************/
%%
/*********** End %parse_accept code *******************************************/
}

/* The main parser program.
** The first argument is a pointer to a structure obtained from
** "ParseAlloc" which describes the current state of the parser.
** The second argument is the major token number.  The third is
** the minor token.  The fourth optional argument is whatever the
** user wants (and specified in the grammar) and is available for
** use by the action routines.
**
** Inputs:
** <ul>
** <li> A pointer to the parser (an opaque structure.)
** <li> The major token number.
** <li> The minor token number.
** <li> An option argument of a grammar-specified type.
** </ul>
**
** Outputs:
** None.
*/
void Parse(
  int yymajor,                 /* The major token code number */
  ParseTOKENTYPE yyminor       /* The value for the token */
  ParseARG_PDECL               /* Optional %extra_argument parameter */
){
  YYMINORTYPE yyminorunion = new YYMINORTYPE();
  int yyact;   /* The parser action. */
#if !defined(YYERRORSYMBOL) && !defined(YYNOERRORRECOVERY)
  boolean yyendofinput;     /* True if we are at the end of input */
#endif
#ifdef YYERRORSYMBOL
  boolean yyerrorhit = false;   /* True if yymajor has invoked an error */
#endif

  assert( yystack.size() != 0 );
#if !defined(YYERRORSYMBOL) && !defined(YYNOERRORRECOVERY)
  yyendofinput = (yymajor==0);
#endif
  ParseARG_STORE;

#ifndef NDEBUG
    logger.debug("Input '{}'",yyTokenName[yymajor]);
#endif

  do{
    yyact = yy_find_shift_action((YYCODETYPE)yymajor);
    if( yyact <= YY_MAX_SHIFTREDUCE ){
      yy_shift(yyact,yymajor,yyminor);
#ifndef YYNOERRORRECOVERY
      yyerrcnt--;
#endif
      yymajor = YYNOCODE;
    }else if( yyact <= YY_MAX_REDUCE ){
      yy_reduce(yyact-YY_MIN_REDUCE);
    }else{
      assert( yyact == YY_ERROR_ACTION );
      yyminorunion.yy0(yyminor);
#ifdef YYERRORSYMBOL
      int yymx;
#endif
#ifndef NDEBUG
        logger.debug("Syntax Error!");
#endif
#ifdef YYERRORSYMBOL
      /* A syntax error has occurred.
      ** The response to an error depends upon whether or not the
      ** grammar defines an error token "ERROR".  
      **
      ** This is what we do if the grammar does define ERROR:
      **
      **  * Call the %syntax_error function.
      **
      **  * Begin popping the stack until we enter a state where
      **    it is legal to shift the error symbol, then shift
      **    the error symbol.
      **
      **  * Set the error count to three.
      **
      **  * Begin accepting and shifting new tokens.  No new error
      **    processing will occur until three tokens have been
      **    shifted successfully.
      **
      */
      if( yyerrcnt<0 ){
        yy_syntax_error(yymajor,yyminor);
      }
      yymx = yystack.peek().major;
      if( yymx==YYERRORSYMBOL || yyerrorhit ){
#ifndef NDEBUG
          logger.debug("Discard input token {}",
             yyTokenName[yymajor]);
#endif
        yy_destructor((YYCODETYPE)yymajor, yyminorunion);
        yymajor = YYNOCODE;
      }else{
        while( !yystack.isEmpty()
            && yymx != YYERRORSYMBOL
            && (yyact = yy_find_reduce_action(
                        yystack.peek().stateno,
                        YYERRORSYMBOL)) >= YY_MIN_REDUCE
        ){
          yy_pop_parser_stack();
        }
        if( yystack.isEmpty() || yymajor==0 ){
          yy_destructor((YYCODETYPE)yymajor, yyminorunion);
          yy_parse_failed();
#ifndef YYNOERRORRECOVERY
          yyerrcnt = -1;
#endif
          yymajor = YYNOCODE;
        }else if( yymx!=YYERRORSYMBOL ){
          yy_shift(yyact,YYERRORSYMBOL,yyminor);
        }
      }
      yyerrcnt = 3;
      yyerrorhit = true;
#elif defined(YYNOERRORRECOVERY)
      /* If the YYNOERRORRECOVERY macro is defined, then do not attempt to
      ** do any kind of error recovery.  Instead, simply invoke the syntax
      ** error routine and continue going as if nothing had happened.
      **
      ** Applications can set this macro (for example inside %include) if
      ** they intend to abandon the parse upon the first syntax error seen.
      */
      yy_syntax_error(yymajor, yyminor);
      yy_destructor((YYCODETYPE)yymajor, yyminorunion);
      yymajor = YYNOCODE;
      
#else  /* YYERRORSYMBOL is not defined */
      /* This is what we do if the grammar does not define ERROR:
      **
      **  * Report an error message, and throw away the input token.
      **
      **  * If the input token is $, then fail the parse.
      **
      ** As before, subsequent error messages are suppressed until
      ** three input tokens have been successfully shifted.
      */
      if( yyerrcnt<=0 ){
        yy_syntax_error(yymajor, yyminor);
      }
      yyerrcnt = 3;
      yy_destructor((YYCODETYPE)yymajor, yyminorunion);
      if( yyendofinput ){
        yy_parse_failed();
#ifndef YYNOERRORRECOVERY
        yyerrcnt = -1;
#endif
      }
      yymajor = YYNOCODE;
#endif
    }
  }while( yymajor!=YYNOCODE && !yystack.isEmpty() );
#ifndef NDEBUG
    int i;
    char cDiv = '[';
    StringBuilder msg = new StringBuilder("Return. Stack=");
    for(i=1; i< yystack.size(); i++){
      msg.append(cDiv).append(yyTokenName[yystack.get(i).major]);
      cDiv = ' ';
    }
    msg.append(']');
    logger.debug(msg.toString());
#endif
  return;
}

  private static class Stack<E> extends ArrayList<E> {
    Stack(int initialCapacity) {
      super(initialCapacity);
    }
    E push(E item) {
      add(item);
      return item;
    }
    E pop() {
      if (isEmpty()) {
        throw new EmptyStackException();
      }
      return remove(size() - 1);
    }
    E peek() {
      if (isEmpty()) {
        throw new EmptyStackException();
      }
      return get(size() - 1);
    }
    void popN(int times) {
      while (times > 0 && !isEmpty()) {
        pop();
        times--;
      }
    }
    E peek(int i) {
      return get(size() - 1 + i);
    }
  }
} /*class Parse*/