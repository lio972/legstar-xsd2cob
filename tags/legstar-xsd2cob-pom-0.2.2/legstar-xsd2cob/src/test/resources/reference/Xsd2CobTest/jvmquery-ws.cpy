      * ----------------------------------------------------------------
      * Generated copybook for JVMQueryException
      * ----------------------------------------------------------------
       01  JVMQueryException.
           03  R-message PIC X(32).
      * ----------------------------------------------------------------
      * Generated copybook for queryJvm
      * ----------------------------------------------------------------
       01  queryJvm.
           03  OCCURS-COUNTERS--C.
             05  envVarNames--C PIC 9(9) COMP-5.
           03  arg0.
             05  envVarNames OCCURS 0 TO 10 DEPENDING ON envVarNames--C 
                PIC X(32).
      * ----------------------------------------------------------------
      * Generated copybook for queryJvmResponse
      * ----------------------------------------------------------------
       01  queryJvmResponse.
           03  OCCURS-COUNTERS--C.
             05  envVarValues--C PIC 9(9) COMP-5.
           03  R-return.
             05  country PIC X(32).
             05  currencySymbol PIC X(32).
             05  envVarValues OCCURS 0 TO 10 DEPENDING ON 
                 envVarValues--C PIC X(32).
             05  formattedDate PIC X(32).
             05  language PIC X(32).
