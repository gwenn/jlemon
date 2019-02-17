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

#ifndef NDEBUG
import java.util.StringJoiner;
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
**    ParseCTX_*         As ParseARG_ except for %extra_context
**    YYERRORSYMBOL      is the code number of the error symbol.  If not
**                       defined, then do no error processing.
**    YYNSTATE           the combined number of states.
**    YYNRULE            the number of rules in the grammar
**    YYNTOKEN           Number of terminal symbols
**    YY_MAX_SHIFT       Maximum value for shift actions
**    YY_MIN_SHIFTREDUCE Minimum value for shift-reduce actions
**    YY_MAX_SHIFTREDUCE Maximum value for shift-reduce actions
**    YY_ERROR_ACTION    The yy_action[] code for syntax error
**    YY_ACCEPT_ACTION   The yy_action[] code for accept
**    YY_NO_ACTION       The yy_action[] code for no-op
**    YY_MIN_REDUCE      Minimum value for reduce actions
**    YY_MAX_REDUCE      Maximum value for reduce actions
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
**   N == YY_ERROR_ACTION               A syntax error has occurred.
**
**   N == YY_ACCEPT_ACTION              The parser accepts its input.
**
**   N == YY_NO_ACTION                  No such action.  Denotes unused
**                                      slots in the yy_action[] table.
**
**   N between YY_MIN_REDUCE            Reduce by rule N-YY_MIN_REDUCE
**     and YY_MAX_REDUCE
**
** The action table is constructed as a single large table named yy_action[].
** Given state S and lookahead X, the action is computed as either:
**
**    (A)   N = yy_action[ yy_shift_ofst[S] + X ]
**    (B)   N = yy_default[S]
**
** The (A) formula is preferred.  The B formula is used instead if
** yy_lookahead[yy_shift_ofst[S]+X] is not equal to X.
**
** The formulas above are for computing the action when the lookahead is
** a terminal symbol.  If the lookahead is a non-terminal (as occurs after
** a reduce action) then the yy_reduce_ofst[] array is used in place of
** the yy_shift_ofst[] array.
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
  final YYMINORTYPE minor;     /* The user-supplied minor token value.  This
                         ** is the value of the token  */
  yyStackEntry() {
    minor = new YYMINORTYPE();
  }
}

/* The state of the parser is completely contained in an instance of
** the following structure */
//  yyStackEntry *yytos;          /* Pointer to top element of the stack */
  private int yyidx;              /* Index to top element of the stack */
#ifdef YYTRACKMAXSTACKDEPTH
  private int yyhwm;                    /* High-water mark of the stack */
#endif
#ifndef YYNOERRORRECOVERY
  private int yyerrcnt;                 /* Shifts left before out of the error */
#endif
  private final ParseARG_SDECL                /* A place to hold %extra_argument */
  //private final ParseCTX_SDECL                /* A place to hold %extra_context */
#if YYSTACKDEPTH<=0
  //int yystksz;                  /* Current side of the stack */
  private final yyStackEntry[] yystack;        /* The parser's stack */
  //yyStackEntry yystk0;          /* First stack entry */
#else
  private final yyStackEntry[] yystack;  /* The parser's stack */
#endif

#ifndef NDEBUG
#endif /* NDEBUG */

#ifndef NDEBUG
#endif /* NDEBUG */

#if defined(YYCOVERAGE) || !defined(NDEBUG)
/* For tracing shifts, the names of all terminals and nonterminals
** are required.  The following table supplies these names */
private static final String yyTokenName[] = {
%%
};
#endif /* defined(YYCOVERAGE) || !defined(NDEBUG) */

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
  int newSize;
  yyStackEntry[] pNew;

  newSize = yystack.length * 2 + 100;
  pNew = new yyStackEntry[newSize];
  System.arraycopy(yystack, 0, pNew, 0, yystack.length);
#ifndef NDEBUG
      logger.trace("Stack grows from {} to {} entries.",
              yystack.length, newSize);
#endif
  yystack = newStack;
  return false;
}
#endif

/* Initialize a new parser that has already been allocated.
*/
public yyParser(
  ParseARG_PDECL               /* Optional %extra_argument parameter */
  ParseCTX_PDECL
  ) {
  ParseCTX_STORE
  yyidx = 0;
#ifdef YYTRACKMAXSTACKDEPTH
  yyhwm = 0;
#endif
#if YYSTACKDEPTH<=0
  yystack = new yyStackEntry[100];
#else
  yystack = new yyStackEntry[YYSTACKDEPTH];
#endif
#ifndef YYNOERRORRECOVERY
  yyerrcnt = -1;
#endif
  yystack[yyidx] = new yyStackEntry();
  ParseARG_STORE
}

/*
** Pop the parser's stack once.
*/
private void yy_pop_parser_stack(){
  yyStackEntry yytos;
  yytos = yystack[yyidx];
  yystack[yyidx] = null;
  yyidx--;
  assert(yyidx >= 0);
#ifndef NDEBUG
    logger.trace("Popping {}",
      yyTokenName[yytos.major]);
#endif
}

/*
** Clear all secondary memory allocations from the parser
*/
public void ParseFinalize(){
  while( yyidx > 0 ) yy_pop_parser_stack();
}

/*
** Return the peak depth of the stack for a parser.
*/
#ifdef YYTRACKMAXSTACKDEPTH
public int ParseStackPeak(){
  return yyhwm;
}
#endif

/* This array of booleans keeps track of the parser statement
** coverage.  The element yycoverage[X][Y] is set when the parser
** is in state X and has a lookahead token Y.  In a well-tested
** systems, every element of this matrix should end up being set.
*/
#if defined(YYCOVERAGE)
private static final boolean[YYNSTATE][YYNTOKEN] yycoverage;
#endif

/*
** Write into out a description of every state/lookahead combination that
**
**   (1)  has not been used by the parser, and
**   (2)  is not a syntax error.
**
** Return the number of missed state/lookahead combinations.
*/
#if defined(YYCOVERAGE)
int ParseCoverage(FILE *out){
  int stateno, iLookAhead, i;
  int nMissed = 0;
  for(stateno=0; stateno<YYNSTATE; stateno++){
    i = yy_shift_ofst[stateno];
    for(iLookAhead=0; iLookAhead<YYNTOKEN; iLookAhead++){
      if( yy_lookahead[i+iLookAhead]!=iLookAhead ) continue;
      if( yycoverage[stateno][iLookAhead]==0 ) nMissed++;
      if( out ){
        fprintf(out,"State %d lookahead %s %s\n", stateno,
                yyTokenName[iLookAhead],
                yycoverage[stateno][iLookAhead] ? "ok" : "missed");
      }
    }
  }
  return nMissed;
}
#endif

/*
** Find the appropriate action for a parser given the terminal
** look-ahead token iLookAhead.
*/
private YYACTIONTYPE yy_find_shift_action(
  YYCODETYPE iLookAhead,    /* The look-ahead token */
  YYACTIONTYPE stateno      /* Current state number */
){
  assert(iLookAhead >= 0);
  int i;

  if( stateno>YY_MAX_SHIFT ) return stateno;
  assert( stateno <= YY_SHIFT_COUNT );
#if defined(YYCOVERAGE)
  yycoverage[stateno][iLookAhead] = true;
#endif
  do{
    i = yy_shift_ofst[stateno];
    assert( i>=0 );
    /* assert( i+YYNTOKEN<=yy_lookahead.length ); */
    assert( iLookAhead!=YYNOCODE );
    assert( iLookAhead < YYNTOKEN );
    i += iLookAhead;
    if( i>=yy_lookahead.length || yy_lookahead[i]!=iLookAhead ){
#ifdef YYFALLBACK
      YYCODETYPE iFallback;            /* Fallback token */
      if( iLookAhead<yyFallback.length
             && (iFallback = yyFallback[iLookAhead])!=0 ){
#ifndef NDEBUG
          logger.trace("FALLBACK {} => {}",
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
          j<yy_lookahead.length &&
          yy_lookahead[j]==YYWILDCARD && iLookAhead>0
        ){
#ifndef NDEBUG
            logger.trace("WILDCARD {} => {}",
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
private static YYACTIONTYPE yy_find_reduce_action(
  YYACTIONTYPE stateno,     /* Current state number */
  YYCODETYPE iLookAhead     /* The look-ahead token */
){
  assert(iLookAhead >= 0);
  assert(stateno >= 0);
  int i;
#ifdef YYERRORSYMBOL
  if( stateno>YY_REDUCE_COUNT ){
    return yy_default[stateno];
  }
#else
  assert( stateno<=YY_REDUCE_COUNT );
#endif
  i = yy_reduce_ofst[stateno];
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
   ParseCTX_FETCH
#ifndef NDEBUG
     logger.error("Stack Overflow!");
#endif
   while( yyidx > 0 ) yy_pop_parser_stack();
   /* Here code is inserted which will execute if the parser
   ** stack every overflows */
/******** Begin %stack_overflow code ******************************************/
%%
/******** End %stack_overflow code ********************************************/
   ParseCTX_STORE
}

/*
** Print tracing information for a SHIFT action
*/
#ifndef NDEBUG
private void yyTraceShift(YYACTIONTYPE yyNewState, String zTag){
    assert(yyNewState >= 0);
    yyStackEntry yytos = yystack(0);
    if( yyNewState<YYNSTATE ){
      logger.trace("Shift '{}', go to state {}",
         yyTokenName[yytos.major],
         yyNewState);
    }else{
      logger.trace("{} '{}', pending reduce %d",
         zTag, yyTokenName[yytos.major],
         yyNewState - YY_MIN_REDUCE);
    }
}
#else
# define yyTraceShift(X,Y,Z)
#endif

/*
** Perform a shift action.
*/
private void yy_shift(
  YYACTIONTYPE yyNewState,      /* The new state to shift in */
  YYCODETYPE yyMajor,           /* The major token to shift in */
  ParseTOKENTYPE yyMinor        /* The minor token to shift in */
){
  assert(yyNewState >= 0);
  assert(yyMajor >= 0);
  yyidx++;
  yyStackEntry yytos = new yyStackEntry();
#ifdef YYTRACKMAXSTACKDEPTH
  if( yyidx>yyhwm ){
    yyhwm++;
    assert( yyhwm == yyidx );
  }
#endif
#if YYSTACKDEPTH>0 
  if( yyidx >= YYSTACKDEPTH ){
    yyidx--;
    //assert(yyidx >= 0);
    yyStackOverflow();
    return;
  }
#else
  if( yyidx >= yystack.length ){
    if( yyGrowStack() ){
      yyStackOverflow();
      return;
    }
  }
#endif
  if( yyNewState > YY_MAX_SHIFT ){
    yyNewState += YY_MIN_REDUCE - YY_MIN_SHIFTREDUCE;
  }
  yystack[yyidx] = yytos;
  assert(yyNewState >= 0);
  yytos.stateno = yyNewState;
  yytos.major = yyMajor;
  yytos.minor.yy0(yyMinor);
  yyTraceShift(yyNewState, "Shift");
}

/* For rule J, yyRuleInfoLhs[J] contains the symbol on the left-hand side
** of that rule */
private static final YYCODETYPE yyRuleInfoLhs[] = {
%%
};

/* For rule J, yyRuleInfoNRhs[J] contains the negative of the number
** of symbols on the right-hand side of that rule. */
private static final byte yyRuleInfoNRhs[] = {
%%
};

/*
** Perform a reduce action and the shift that must immediately
** follow the reduce.
**
** The yyLookahead and yyLookaheadToken parameters provide reduce actions
** access to the lookahead token (if any).  The yyLookahead will be YYNOCODE
** if the lookahead token has already been consumed.  As this procedure is
** only called from one place, optimizing compilers will in-line it, which
** means that the extra parameters have no performance impact.
*/
@SuppressWarnings({"UnnecessarySemicolon", "PointlessArithmeticExpression"})
private YYACTIONTYPE yy_reduce(
  int yyruleno,        /* Number of the rule by which to reduce */
  int yyLookahead,             /* Lookahead token, or YYNOCODE if none */
  ParseTOKENTYPE yyLookaheadToken  /* Value of the lookahead token */
  ParseCTX_PDECL                   /* %extra_context */
){
  assert(yyruleno >= 0);
  YYCODETYPE yygoto;              /* The next state */
  YYACTIONTYPE yyact;             /* The next action */
  yyStackEntry yymsp;             /* The top of the parser's stack */
  byte yysize;                     /* Amount to pop the stack */
#ifndef NDEBUG
  if( yyruleno<yyRuleName.length ){
    yysize = yyRuleInfoNRhs[yyruleno];
    if( yysize != 0 ){
    	logger.trace("Reduce {} [{}], go to state {}.",
      	yyruleno, yyRuleName[yyruleno], yystack(yysize).stateno);
     } else {
    	logger.trace("Reduce {} [{}].",
      	yyruleno, yyRuleName[yyruleno]);
     }
  }
#endif /* NDEBUG */

  /* Check that the stack is large enough to grow by a single entry
  ** if the RHS of the rule is empty.  This ensures that there is room
  ** enough on the stack to push the LHS value */
  if( yyRuleInfoNRhs[yyruleno]==0 ){
#ifdef YYTRACKMAXSTACKDEPTH
    if( yyidx>yyhwm ){
      yyhwm++;
      assert( yyhwm == yyidx );
    }
#endif
#if YYSTACKDEPTH>0 
    if( yyidx >= YYSTACKDEPTH-1 ){
      yyStackOverflow();
      /* The call to yyStackOverflow() above pops the stack until it is
      ** empty, causing the main parser loop to exit.  So the return value
      ** is never used and does not matter. */
      return 0;
    }
#else
    if( yyidx >= yystack.length-1 ){
      if( yyGrowStack() ){
        yyStackOverflow();
        /* The call to yyStackOverflow() above pops the stack until it is
        ** empty, causing the main parser loop to exit.  So the return value
        ** is never used and does not matter. */
        return 0;
      }
    }
#endif
    // yystack is not prefilled with zero value like in C.
    if (yystack[yyidx] == null) {
        yystack[yyidx] = new yyStackEntry();
    } else if (yystack[yyidx+1] == null) {
        yystack[yyidx+1] = new yyStackEntry();
    }
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
  }
  assert( yyruleno<yyRuleInfoLhs.length );
  yygoto = yyRuleInfoLhs[yyruleno];
  yysize = yyRuleInfoNRhs[yyruleno];
  yyact = yy_find_reduce_action(yystack(yysize).stateno, yygoto);
  assert(yyact >= 0);

  /* There are no SHIFTREDUCE actions on nonterminals because the table
  ** generator has simplified them to pure REDUCE actions. */
  assert( !(yyact>YY_MAX_SHIFT && yyact<=YY_MAX_SHIFTREDUCE) );

  /* It is not possible for a REDUCE to be followed by an error */
  assert( yyact!=YY_ERROR_ACTION );

	yyidx += yysize+1;
	assert(yyidx >= 0);
	yymsp = yystack(0);
	yymsp.stateno = yyact;
	assert(yygoto >= 0);
	yymsp.major = yygoto;
	yyTraceShift(yyact, "... then shift");
	return yyact;
}

/*
** The following code executes when the parse fails
*/
#ifndef YYNOERRORRECOVERY
private void yy_parse_failed(
){
  ParseCTX_FETCH
#ifndef NDEBUG
    logger.error("Fail!");
#endif
  while( yyidx > 0 ) yy_pop_parser_stack();
  /* Here code is inserted which will be executed whenever the
  ** parser fails */
/************ Begin %parse_failure code ***************************************/
%%
/************ End %parse_failure code *****************************************/
  ParseCTX_STORE
}
#endif /* YYNOERRORRECOVERY */

/*
** The following code executes when a syntax error first occurs.
*/
@SuppressWarnings("unused")
private void yy_syntax_error(
  YYCODETYPE yymajor,            /* The major type of the error token */
  ParseTOKENTYPE yyminor         /* The minor type of the error token */
){
  ParseCTX_FETCH
#define TOKEN yyminor
/************ Begin %syntax_error code ****************************************/
%%
/************ End %syntax_error code ******************************************/
  ParseCTX_STORE
}

/*
** The following is executed when the parser accepts
*/
private void yy_accept(
){
  ParseCTX_FETCH
#ifndef NDEBUG
    logger.trace("Accept!");
#endif
#ifndef YYNOERRORRECOVERY
  yyerrcnt = -1;
#endif
  assert( yyidx == 0 );
  /* Here code is inserted which will be executed whenever the
  ** parser accepts */
/*********** Begin %parse_accept code *****************************************/
%%
/*********** End %parse_accept code *******************************************/
  ParseCTX_STORE
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
public void Parse(
  YYCODETYPE yymajor,          /* The major token code number */
  ParseTOKENTYPE yyminor       /* The value for the token */
){
  YYACTIONTYPE yyact;   /* The parser action. */
#if !defined(YYERRORSYMBOL) && !defined(YYNOERRORRECOVERY)
  boolean yyendofinput;     /* True if we are at the end of input */
#endif
#ifdef YYERRORSYMBOL
  boolean yyerrorhit = false;   /* True if yymajor has invoked an error */
#endif
  ParseCTX_FETCH

  assert( yystack(0) != null );
#if !defined(YYERRORSYMBOL) && !defined(YYNOERRORRECOVERY)
  yyendofinput = (yymajor==0);
#endif

  yyact = yystack(0).stateno;
#ifndef NDEBUG
    if( yyact < YY_MIN_REDUCE ){
	    logger.trace("Input '{}' in state {}",yyTokenName[yymajor],yyact);
    }else{
	    logger.trace("Input '{}' with pending reduce {}",
	    				yyTokenName[yymajor],yyact-YY_MIN_REDUCE);
    }
#endif

  do{
    assert( yyact==yystack(0).stateno );
    yyact = yy_find_shift_action(yymajor,yyact);
    if( yyact >= YY_MIN_REDUCE ){
      yyact = yy_reduce(yyact-YY_MIN_REDUCE,yymajor,
                        yyminor ParseCTX_PARAM);
    }else if( yyact <= YY_MAX_SHIFTREDUCE ){
      yy_shift(yyact,yymajor,yyminor);
#ifndef YYNOERRORRECOVERY
      yyerrcnt--;
#endif
      break;
    }else if( yyact==YY_ACCEPT_ACTION ){
			yyidx--;
			yy_accept();
      return;
    }else{
      assert( yyact == YY_ERROR_ACTION );
#ifdef YYERRORSYMBOL
      int yymx;
#endif
#ifndef NDEBUG
        logger.trace("Syntax Error!");
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
      yymx = yystack(0).major;
      if( yymx==YYERRORSYMBOL || yyerrorhit ){
#ifndef NDEBUG
          logger.trace("Discard input token {}",
             yyTokenName[yymajor]);
#endif
        yymajor = YYNOCODE;
      }else{
        while( yyidx >= 0
            && (yyact = yy_find_reduce_action(
                        yystack(0).stateno,
                        YYERRORSYMBOL)) > YY_MAX_SHIFTREDUCE
        ){
          yy_pop_parser_stack();
        }
        if( yyidx < 0 || yymajor==0 ){
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
      if( yymajor==YYNOCODE ) break;
      yyact = yystack(0).stateno;
#elif defined(YYNOERRORRECOVERY)
      /* If the YYNOERRORRECOVERY macro is defined, then do not attempt to
      ** do any kind of error recovery.  Instead, simply invoke the syntax
      ** error routine and continue going as if nothing had happened.
      **
      ** Applications can set this macro (for example inside %include) if
      ** they intend to abandon the parse upon the first syntax error seen.
      */
      yy_syntax_error(yymajor, yyminor);
      break;
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
      if( yyendofinput ){
        yy_parse_failed();
#ifndef YYNOERRORRECOVERY
        yyerrcnt = -1;
#endif
      }
      break;
#endif
    }
  }while( yyidx > 0 );
#ifndef NDEBUG
    if (logger.isTraceEnabled()) {
    StringJoiner msg = new StringJoiner(" ", "Return. Stack=[", "]");
    for(int i=1; i <= yyidx; i++){
      msg.add(yyTokenName[yystack[i].major]);
    }
    logger.trace(msg.toString());
    }
#endif
}

/*
** Return the fallback token corresponding to canonical token iToken, or
** 0 if iToken has no fallback.
*/
public static YYCODETYPE ParseFallback(YYCODETYPE iToken){
#ifdef YYFALLBACK
  if( iToken<yyFallback.length ){
    return yyFallback[iToken];
  }
#else
  //(void)iToken;
#endif
  return 0;
}

  // Access relative to the top: {@code yystack(0)} = top, {@code yystack(-1)} = top-1, ...
  private yyStackEntry yystack(int i) {
    assert(i <= 1);
    yyStackEntry entry = yystack[yyidx+i];
    return entry;
  }
} /*class Parse*/
